package gui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import logic.components.Direction;
import logic.game.GameBoard;
import logic.game.GameEngine;
import logic.game.GameSession;
import logic.game.LevelId;
import objects.Human;
import objects.items.BaseItem;
import objects.obstacles.Cable;
import objects.obstacles.Chair;
import objects.obstacles.Table;
import rewards.Reward;

/**
 * Main JavaFX {@link Application} class that drives the entire game UI.
 * <p>
 * Manages all scene transitions (start, how-to-play, opening, level-select,
 * game, reward, lose, and completion screens), renders the tile-based game
 * board on a {@link Canvas}, and delegates game logic to {@link GameEngine}
 * and {@link GameSession}.
 * </p>
 */
public class GameApp extends Application {
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 900;
    private static final int TILE = 64;
    private static final int HUMAN_REACTION_RANGE = 1;
    private static final long PLAYER_MOVE_ANIM_NANOS = 120_000_000L;
    private static final long EASTER_EGG_MAX_GAP_NANOS = 1_200_000_000L;

    private Stage stage;
    private GameEngine engine;
    private GameSession session;

    private Label hpLabel;
    private Label staminaLabel;
    private Label levelLabel;
    private Label statusLabel;
    private ProgressBar hpBar;
    private ProgressBar staminaBar;
    private Canvas gameCanvas;
    private AnimationTimer renderTimer;
    private boolean gameOverHandled = false;
    private logic.game.LevelId currentLevelId;
    private long animationStartNanos;
    private double renderPlayerX;
    private double renderPlayerY;
    private double moveFromX;
    private double moveFromY;
    private double moveToX;
    private double moveToY;
    private long playerMoveStartNanos;
    private boolean playerMoveAnimating;
    private int easterEggProgress;
    private long easterEggLastInputNanos;

    private double currentWidth() {
        return stage.isShowing() && stage.getScene() != null ? stage.getScene().getWidth() : WINDOW_WIDTH;
    }

    private double currentHeight() {
        return stage.isShowing() && stage.getScene() != null ? stage.getScene().getHeight() : WINDOW_HEIGHT;
    }

    /**
     * {@inheritDoc}
     * Initializes the primary stage and shows the start scene.
     */
    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setTitle("Project Progmeth");
        showStartScene();
        stage.show();
    }

    /** Displays the main-menu / start scene with background music. */
    private void showStartScene() {
        stopRenderLoop();
        SoundManager.playBackgroundLoop();
        // Main screen uses provided Main image and two buttons: Press to Start and How to Play
        stopRenderLoop();
        Image mainImage = ImageLoader.loadImage("/Page/Main.png");
        javafx.scene.image.ImageView mainView = null;
        if (mainImage != null) {
            mainView = new javafx.scene.image.ImageView(mainImage);
            mainView.setPreserveRatio(true);
        }

        Button pressToStart = new Button();
        Image pImg = ImageLoader.loadImage("/Page/PressToStart.png");
        if (pImg != null) {
            javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(pImg);
            iv.setPreserveRatio(true);
            iv.setFitWidth(260);
            pressToStart.setGraphic(iv);
            pressToStart.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        } else {
            pressToStart.setText("Press to Start");
        }
        pressToStart.setOnAction(e -> {
            SoundManager.playOpening(1);
            showOpeningScene(1);
        });
        applyHoverEffect(pressToStart);

        Button howTo = new Button();
        Image hImg = ImageLoader.loadImage("/Page/HowtoPlay.png");
        if (hImg != null) {
            javafx.scene.image.ImageView iv2 = new javafx.scene.image.ImageView(hImg);
            iv2.setPreserveRatio(true);
            iv2.setFitWidth(260);
            howTo.setGraphic(iv2);
            howTo.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        } else {
            howTo.setText("How to Play");
        }
        howTo.setOnAction(e -> showHowToPlayScene());
        applyHoverEffect(howTo);

        HBox buttons = new HBox(36, pressToStart, howTo);
        buttons.setAlignment(Pos.CENTER);

        // Create mute button for main menu
        Button mainMenuMuteButton = new Button();
        mainMenuMuteButton.setPrefSize(40, 40);
        mainMenuMuteButton.setStyle("-fx-font-size: 18; -fx-cursor: hand;");
        updateMuteButtonText(mainMenuMuteButton);
        mainMenuMuteButton.setOnAction(e -> {
            SoundManager.toggleMute();
            updateMuteButtonText(mainMenuMuteButton);
            SoundManager.playClick();
        });

        StackPane root = new StackPane();
        // remove explicit background so image can fill entire window
        if (mainView != null) root.getChildren().add(mainView);

        // framed box containing the buttons so they appear 'in the box' under title
        StackPane buttonBox = new StackPane(buttons);
        buttonBox.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-border-color: black; -fx-border-width: 3; -fx-padding: 18;");
        buttonBox.setMaxWidth(800);

        // Create top-right mute button area
        HBox topRightBox = new HBox(mainMenuMuteButton);
        topRightBox.setAlignment(Pos.TOP_RIGHT);
        topRightBox.setPadding(new Insets(15, 20, 0, 0));

        VBox overlay = new VBox();
        overlay.setAlignment(Pos.TOP_CENTER);
        overlay.getChildren().add(topRightBox);
        overlay.getChildren().add(buttonBox);
        root.getChildren().add(overlay);

        Scene scene = new Scene(root, currentWidth(), currentHeight());
        stage.setScene(scene);

        if (mainView != null) {
            // make image fill the window completely
            mainView.setPreserveRatio(false);
            mainView.fitWidthProperty().bind(scene.widthProperty());
            mainView.fitHeightProperty().bind(scene.heightProperty());

            // position the button box relative to the image: below the title area
            // try to place at about 62% of image height (tweakable)
            buttonBox.translateYProperty().bind(scene.heightProperty().multiply(0.62));
            // scale button box width relative to scene
            buttonBox.maxWidthProperty().bind(scene.widthProperty().multiply(0.6));
        }
        // make button images responsive to scene width
        if (pressToStart.getGraphic() instanceof javafx.scene.image.ImageView pIv) {
            pIv.fitWidthProperty().bind(scene.widthProperty().multiply(0.22));
        }
        if (howTo.getGraphic() instanceof javafx.scene.image.ImageView hIv) {
            hIv.fitWidthProperty().bind(scene.widthProperty().multiply(0.22));
        }
        // click sound handled by button ActionEvent (keyboard Enter/Space also triggers)
    }

    /** Displays the how-to-play tutorial scene. */
    private void showHowToPlayScene() {
        stopRenderLoop();

        // Show HowToPlay pages in order using images in resources/HowToPlay/1.png..6.png
        javafx.scene.image.ImageView pageView = new javafx.scene.image.ImageView();
        pageView.setPreserveRatio(true);
        pageView.setFitWidth(WINDOW_WIDTH * 0.8);

        final int[] page = {1};
        Runnable updateView = () -> {
            String path = "/HowToPlay/" + page[0] + ".png";
            Image img = ImageLoader.loadImage(path);
            if (img != null) pageView.setImage(img);
        };

        updateView.run();

        Button next = new Button();
        Image nextImg = ImageLoader.loadImage("/HowToPlay/Next.png");
        if (nextImg != null) {
            javafx.scene.image.ImageView niv = new javafx.scene.image.ImageView(nextImg);
            niv.setPreserveRatio(true);
            niv.setFitWidth(160);
            next.setGraphic(niv);
        } else {
            next.setText("Next");
        }

        Button letsPlay = new Button();
        Image lpImg = ImageLoader.loadImage("/HowToPlay/LetsPlay.png");
        if (lpImg != null) {
            javafx.scene.image.ImageView liv = new javafx.scene.image.ImageView(lpImg);
            liv.setPreserveRatio(true);
            liv.setFitWidth(220);
            letsPlay.setGraphic(liv);
        } else {
            letsPlay.setText("Let's play");
        }

        next.setOnAction(e -> {
            if (page[0] < 6) {
                page[0]++;
                updateView.run();
            } else {
                // at end of HowToPlay, show the opening image sequence before starting level 1
                showOpeningScene(1);
            }
        });
        applyHoverEffect(next);

        // When clicking "Let's play", play opening sound then show the opening image sequence
        letsPlay.setOnAction(e -> {
            SoundManager.playOpening(1);
            showOpeningScene(1);
        });
        applyHoverEffect(letsPlay);

        HBox bottom = new HBox(12, next);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(12));

        BorderPane root = new BorderPane();
        root.setCenter(pageView);
        root.setBottom(bottom);
        root.setStyle("-fx-background-color: #FFFFFF;");

        // Rebuild bottom area when page changes to show letsPlay on final page
        pageView.imageProperty().addListener((obs, old, nw) -> {
            HBox b;
            if (page[0] < 6) {
                b = new HBox(12, next);
            } else {
                b = new HBox(12, letsPlay);
            }
            b.setAlignment(Pos.CENTER_RIGHT);
            b.setPadding(new Insets(12));
            root.setBottom(b);
        });

        Scene scene = new Scene(root, currentWidth(), currentHeight());
        stage.setScene(scene);
        pageView.fitWidthProperty().bind(scene.widthProperty().multiply(0.8));
    }

    /**
     * Displays the opening narration scene for the given level.
     *
     * @param levelNum the 1-based level number
     */
    private void showOpeningScene(int levelNum) {
        stopRenderLoop();
        SoundManager.stopBackground();

        Image openingBg = ImageLoader.loadImage("/Openning/bg.png");
        javafx.scene.image.ImageView bgView = null;
        if (openingBg != null) {
            bgView = new javafx.scene.image.ImageView(openingBg);
            bgView.setPreserveRatio(false);
        }

        javafx.scene.image.ImageView pageView = new javafx.scene.image.ImageView();
        pageView.setPreserveRatio(true);
        pageView.setFitWidth(WINDOW_WIDTH * 0.8);

        final int MAX_OPENING_PAGES = 3;
        final int[] page = {1};
        Runnable updateView = () -> {
            String path = "/Openning/" + page[0] + ".png";
            Image img = ImageLoader.loadImage(path);
            if (img != null) pageView.setImage(img);
        };

        updateView.run();

        Button next = new Button();
        Image nextImg = ImageLoader.loadImage("/Openning/Next.png");
        if (nextImg != null) {
            javafx.scene.image.ImageView niv = new javafx.scene.image.ImageView(nextImg);
            niv.setPreserveRatio(true);
            niv.setFitWidth(160);
            next.setGraphic(niv);
        } else {
            next.setText("Next");
        }

        Button letsPlay = new Button();
        Image lpImg = ImageLoader.loadImage("/Openning/LetsPlay.png");
        if (lpImg != null) {
            javafx.scene.image.ImageView liv = new javafx.scene.image.ImageView(lpImg);
            liv.setPreserveRatio(true);
            liv.setFitWidth(220);
            letsPlay.setGraphic(liv);
        } else {
            letsPlay.setText("Let's play");
        }

        next.setOnAction(e -> {
            if (page[0] < MAX_OPENING_PAGES) {
                page[0]++;
                updateView.run();
                SoundManager.playOpening(page[0]);
            } else {
                // stop opening sound before starting
                SoundManager.stopOpening();
                startGameWithLevel(levelNum);
            }
        });
        applyHoverEffect(next);

        letsPlay.setOnAction(e -> {
            SoundManager.stopOpening();
            startGameWithLevel(levelNum);
        });
        applyHoverEffect(letsPlay);

        HBox bottom = new HBox(12, next);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(12));

        BorderPane content = new BorderPane();
        content.setCenter(pageView);
        content.setBottom(bottom);
        content.setStyle("-fx-background-color: transparent;");

        StackPane root = new StackPane();
        if (bgView != null) {
            root.getChildren().add(bgView);
        }
        root.getChildren().add(content);

        // Rebuild bottom area when page changes to show letsPlay on final page
        pageView.imageProperty().addListener((obs, old, nw) -> {
            HBox b;
            if (page[0] < MAX_OPENING_PAGES) {
                b = new HBox(12, next);
            } else {
                b = new HBox(12, letsPlay);
            }
            b.setAlignment(Pos.CENTER_RIGHT);
            b.setPadding(new Insets(12));
            content.setBottom(b);
        });

        Scene scene = new Scene(root, currentWidth(), currentHeight());
        stage.setScene(scene);
        if (bgView != null) {
            bgView.fitWidthProperty().bind(scene.widthProperty());
            bgView.fitHeightProperty().bind(scene.heightProperty());
        }
        pageView.fitWidthProperty().bind(scene.widthProperty().multiply(0.8));
    }

    /** Displays the level-select scene. */
    private void showLevelSelectScene() {
        stopRenderLoop();
        SoundManager.stopBackground();

        Label title = new Label("Select Level");
        title.setFont(Font.font("System", 56));
        title.setTextFill(Color.BLACK);

        GridPane levelGrid = new GridPane();
        levelGrid.setHgap(20);
        levelGrid.setVgap(20);

        for (int i = 1; i <= 5; i++) {
            final int levelNum = i;
            Button levelButton = new Button("Level " + i);
            levelButton.setStyle("-fx-font-size: 24; -fx-padding: 20; -fx-background-color: #000000; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2; -fx-border-radius: 10;");
            levelButton.setPrefWidth(200);
            levelButton.setPrefHeight(100);
            levelButton.setOnAction(e -> startGameWithLevel(levelNum));
            applyHoverEffect(levelButton);
            levelGrid.add(levelButton, (i - 1) % 5, (i - 1) / 5);
        }

        Button backButton = createMainButton("Back");
        backButton.setOnAction(e -> showStartScene());

        VBox root = new VBox(20, title, levelGrid, backButton);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #FFFFFF;");

        Scene scene = new Scene(root, currentWidth(), currentHeight());
        stage.setScene(scene);
    }

    /**
     * Initializes the game engine for the given level and transitions to the game scene.
     *
     * @param levelNum the 1-based level number to start
     */
    private void startGameWithLevel(int levelNum) {
        LevelId levelId = switch (levelNum) {
            case 1 -> LevelId.ISCALE_401;
            case 2 -> LevelId.ISCALE_402;
            case 3 -> LevelId.ISCALE_403;
            case 4 -> LevelId.ISCALE_404;
            case 5 -> LevelId.ISCALE_405;
            default -> LevelId.ISCALE_401;
        };
        session = new GameSession();
        showGameScene(levelId);
    }

    private VBox createHowToCard(String heading, String text) {
        Rectangle imageBox = new Rectangle(320, 140);
        imageBox.setArcWidth(40);
        imageBox.setArcHeight(40);
        imageBox.setFill(Color.WHITE);
        imageBox.setStroke(Color.BLACK);
        imageBox.setStrokeWidth(2);

        Label headingLabel = new Label(heading);
        headingLabel.setFont(Font.font(28));
        headingLabel.setTextFill(Color.BLACK);

        Label textLabel = new Label(text);
        textLabel.setFont(Font.font(20));
        textLabel.setTextFill(Color.BLACK);

        VBox card = new VBox(10, imageBox, headingLabel, textLabel);
        card.setPrefWidth(360);
        return card;
    }

    private Button createMainButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font(48));
        button.setPrefWidth(400);
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: #000000; -fx-background-radius: 40; -fx-border-radius: 40; -fx-border-color: white; -fx-border-width: 2;");
        applyHoverEffect(button);
        
        return button;
    }

    private void applyHoverEffect(Button button) {
        // scale up slightly on hover with a smooth transition
        button.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(120), button);
            st.setToX(1.08);
            st.setToY(1.08);
            st.playFromStart();
        });
        button.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(120), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
        });
        // action-based click sound: covers mouse click and keyboard activation
        button.addEventHandler(ActionEvent.ACTION, ev -> SoundManager.playClick());
    }

    /**
     * Sets up and displays the main game scene for the specified level.
     *
     * @param levelId the level to show
     */
    private void showGameScene(LevelId levelId) {
        stopRenderLoop();
        engine = new GameEngine(session, levelId);
        animationStartNanos = System.nanoTime();
        easterEggProgress = 0;
        easterEggLastInputNanos = 0L;
        // reset game-over flag for this level and start background music
        this.currentLevelId = levelId;
        this.gameOverHandled = false;
        SoundManager.playBackgroundLoop();

        hpBar = new ProgressBar(1.0);
        staminaBar = new ProgressBar(1.0);
        hpBar.setPrefWidth(250);
        staminaBar.setPrefWidth(250);

        hpLabel = new Label();
        staminaLabel = new Label();
        levelLabel = new Label();
        statusLabel = new Label();

        // Left-aligned compact HUD showing only remaining values
        Label hpText = new Label("HP : ");
        hpText.setFont(Font.loadFont(GameApp.class.getResourceAsStream("/fonts/Grandstander-Bold.ttf"), 28));
        hpLabel.setFont(Font.loadFont(GameApp.class.getResourceAsStream("/fonts/Grandstander-Bold.ttf"), 28));
        hpLabel.setTextFill(Color.BLACK);

        Label stText = new Label("Stamina : ");
        stText.setFont(Font.loadFont(GameApp.class.getResourceAsStream("/fonts/Grandstander-Bold.ttf"), 28));
        staminaLabel.setFont(Font.loadFont(GameApp.class.getResourceAsStream("/fonts/Grandstander-Bold.ttf"), 28));
        staminaLabel.setTextFill(Color.BLACK);

        HBox stRow = new HBox(6, stText, staminaLabel);
        stRow.setAlignment(Pos.CENTER_LEFT);

        HBox hpRow = new HBox(6, hpText, hpLabel);
        hpRow.setAlignment(Pos.CENTER_LEFT);

        VBox leftHud = new VBox(4, stRow, hpRow);
        leftHud.setAlignment(Pos.CENTER_LEFT);
        leftHud.setPadding(new Insets(8, 0, 8, 20));

        // Create mute button
        Button muteButton = new Button();
        muteButton.setPrefSize(40, 40);
        muteButton.setStyle("-fx-font-size: 18; -fx-cursor: hand;");
        muteButton.setFocusTraversable(false);
        updateMuteButtonText(muteButton);
        muteButton.setOnAction(e -> {
            SoundManager.toggleMute();
            updateMuteButtonText(muteButton);
            SoundManager.playClick();
            gameCanvas.requestFocus();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topHud = new HBox(leftHud, spacer, levelLabel, muteButton);
        topHud.setAlignment(Pos.CENTER_LEFT);
        topHud.setPadding(new Insets(4, 20, 4, 0));
        topHud.setSpacing(10);

        // Size canvas to fit the board if the board is larger than the default view
        GameBoard board = engine.getBoard();
        double boardPixelWidth = board.getWidth() * TILE;
        double boardPixelHeight = board.getHeight() * TILE;
        double defaultCanvasWidth = WINDOW_WIDTH - 60;
        double defaultCanvasHeight = WINDOW_HEIGHT - 170;
        double canvasWidth = Math.max(defaultCanvasWidth, boardPixelWidth + 40); // add small padding
        double canvasHeight = Math.max(defaultCanvasHeight, boardPixelHeight + 40);

        gameCanvas = new Canvas(canvasWidth, canvasHeight);
        StackPane boardPane = new StackPane(gameCanvas);
        boardPane.setPadding(new Insets(6, 20, 6, 20));

        statusLabel.setFont(Font.font(22));
        statusLabel.setPadding(new Insets(4, 22, 12, 22));
        hpLabel.setTextFill(Color.BLACK);
        staminaLabel.setTextFill(Color.BLACK);
        levelLabel.setFont(Font.loadFont(GameApp.class.getResourceAsStream("/fonts/Grandstander-Bold.ttf"), 32));
        levelLabel.setTextFill(Color.BLACK);
        statusLabel.setTextFill(Color.BLACK);

        BorderPane root = new BorderPane();
        root.setTop(topHud);
        root.setCenter(boardPane);
        root.setBottom(statusLabel);
        root.setStyle("-fx-background-color: #FFFFFF;");

        Scene scene = new Scene(root, currentWidth(), currentHeight());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            if (handleEasterEggKey(code)) {
                return;
            }
            if (code == KeyCode.UP || code == KeyCode.W) {
                handlePlayerMove(Direction.UP);
            } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                handlePlayerMove(Direction.DOWN);
            } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                handlePlayerMove(Direction.LEFT);
            } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                handlePlayerMove(Direction.RIGHT);
            } else if (code == KeyCode.R) {
                engine.restartLevel();
                syncPlayerRenderPosition();
            } else if (code == KeyCode.ESCAPE) {
                showStartScene();
                return;
            }
            refreshGameView();
            
            if (engine.isCompleted() && engine.getPlayer().getHealth() > 0) {
                handleWin();
            }
        });

        stage.setScene(scene);

        // Make canvas responsive to window size: keep at least board pixel size but
        // expand/shrink with scene while preserving padding.
        scene.widthProperty().addListener((obs, oldV, newV) -> {
            double dw = newV.doubleValue() - 60;
            double newWidth = Math.max(dw, boardPixelWidth + 40);
            gameCanvas.setWidth(newWidth);
            refreshGameView();
        });
        scene.heightProperty().addListener((obs, oldV, newV) -> {
            double dh = newV.doubleValue() - 170;
            double newHeight = Math.max(dh, boardPixelHeight + 40);
            gameCanvas.setHeight(newHeight);
            refreshGameView();
        });

        // initialize canvas size to current scene size
        gameCanvas.setWidth(Math.max(scene.getWidth() - 60, boardPixelWidth + 40));
        gameCanvas.setHeight(Math.max(scene.getHeight() - 170, boardPixelHeight + 40));

        syncPlayerRenderPosition();
        refreshGameView();
        startRenderLoop();
        gameCanvas.setFocusTraversable(true);
        gameCanvas.requestFocus();
    }

    private boolean handleEasterEggKey(KeyCode code) {
        long now = System.nanoTime();
        if (easterEggProgress > 0 && (now - easterEggLastInputNanos) > EASTER_EGG_MAX_GAP_NANOS) {
            easterEggProgress = 0;
        }

        if (matchesEasterEggStep(easterEggProgress, code)) {
            easterEggProgress++;
            easterEggLastInputNanos = now;

            if (easterEggProgress >= 10) {
                easterEggProgress = 0;
                easterEggLastInputNanos = 0L;
                SoundManager.playEnding(1);
                showGameCompletionScene();
                return true;
            }
            return false;
        }

        if (matchesEasterEggStep(0, code)) {
            easterEggProgress = 1;
            easterEggLastInputNanos = now;
        } else {
            easterEggProgress = 0;
            easterEggLastInputNanos = 0L;
        }
        return false;
    }

    private boolean matchesEasterEggStep(int step, KeyCode code) {
        return switch (step) {
            case 0, 1 -> code == KeyCode.UP || code == KeyCode.W;
            case 2, 3 -> code == KeyCode.DOWN || code == KeyCode.S;
            case 4, 6 -> code == KeyCode.LEFT || code == KeyCode.A;
            case 5, 7 -> code == KeyCode.RIGHT || code == KeyCode.D;
            case 8 -> code == KeyCode.A;
            case 9 -> code == KeyCode.B;
            default -> false;
        };
    }

    /**
     * Handles a player movement request in the given direction,
     * updating the game engine and triggering animations.
     *
     * @param direction the direction to move
     */
    private void handlePlayerMove(Direction direction) {
        int oldX = engine.getPlayer().getX();
        int oldY = engine.getPlayer().getY();
        int oldHp = engine.getPlayer().getHealth();
        int oldStamina = engine.getPlayer().getStamina();
        String prevStatus = engine.getStatusMessage();

        updatePlayerAnimation();

        engine.move(direction);

        int newX = engine.getPlayer().getX();
        int newY = engine.getPlayer().getY();
        int newHp = engine.getPlayer().getHealth();
        int newStamina = engine.getPlayer().getStamina();
        String newStatus = engine.getStatusMessage();

        // play walk only if player actually moved
        if (newX != oldX || newY != oldY) {
            startPlayerMoveAnimation(newX, newY);
            SoundManager.playWalk();
        }

        // play hurt when HP decreased
        if (newHp < oldHp) {
            SoundManager.playHurt();
        }

        // play pick-up when status indicates item picked
        if (newStatus != null && newStatus.startsWith("Picked ") && !newStatus.equals(prevStatus)) {
            SoundManager.playPickUp();
        }
    }

    private void startPlayerMoveAnimation(int targetX, int targetY) {
        moveFromX = renderPlayerX;
        moveFromY = renderPlayerY;
        moveToX = targetX;
        moveToY = targetY;
        playerMoveStartNanos = System.nanoTime();
        playerMoveAnimating = true;
    }

    private void updatePlayerAnimation() {
        if (!playerMoveAnimating) {
            return;
        }

        double elapsed = System.nanoTime() - playerMoveStartNanos;
        double progress = Math.max(0.0, Math.min(1.0, elapsed / PLAYER_MOVE_ANIM_NANOS));
        renderPlayerX = moveFromX + ((moveToX - moveFromX) * progress);
        renderPlayerY = moveFromY + ((moveToY - moveFromY) * progress);

        if (progress >= 1.0) {
            playerMoveAnimating = false;
            renderPlayerX = moveToX;
            renderPlayerY = moveToY;
        }
    }

    private void syncPlayerRenderPosition() {
        int playerX = engine.getPlayer().getX();
        int playerY = engine.getPlayer().getY();
        renderPlayerX = playerX;
        renderPlayerY = playerY;
        moveFromX = playerX;
        moveFromY = playerY;
        moveToX = playerX;
        moveToY = playerY;
        playerMoveAnimating = false;
    }

    private void startRenderLoop() {
        renderTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updatePlayerAnimation();
                drawBoard();
            }
        };
        renderTimer.start();
    }

    private void stopRenderLoop() {
        if (renderTimer != null) {
            renderTimer.stop();
            renderTimer = null;
        }
    }

    /** Handles the win condition — stops the render loop and shows the reward screen. */
    private void handleWin() {
        if (gameOverHandled) return;
        gameOverHandled = true;
        SoundManager.stopBackground();
        SoundManager.playWin();
        // show reward screen (this will take over the scene)
        showRewardScreen();
    }

    /** Handles the lose condition — stops the render loop and shows the lose scene. */
    private void handleLose() {
        if (gameOverHandled) return;
        gameOverHandled = true;
        SoundManager.stopBackground();
        SoundManager.playLose();
        stopRenderLoop();
        showLoseScene();
    }

    /** Displays the lose / game-over scene. */
    private void showLoseScene() {
        stopRenderLoop();
        SoundManager.stopBackground();

        javafx.scene.image.ImageView pageView = new javafx.scene.image.ImageView();
        pageView.setPreserveRatio(true);
        Image img = ImageLoader.loadImage("/PlayAgain/PlayAgain.png");
        if (img != null) pageView.setImage(img);

        Button again = new Button();
        Image againImg = ImageLoader.loadImage("/PlayAgain/Play_Again_Button.png");
        if (againImg != null) {
            javafx.scene.image.ImageView aiv = new javafx.scene.image.ImageView(againImg);
            aiv.setPreserveRatio(true);
            aiv.setFitWidth(220);
            again.setGraphic(aiv);
        } else {
            again.setText("Play Again");
        }
        again.setOnAction(e -> showGameScene(currentLevelId));
        applyHoverEffect(again);

        Button exit = new Button();
        Image exitImg = ImageLoader.loadImage("/PlayAgain/Exist.png");
        if (exitImg != null) {
            javafx.scene.image.ImageView eiv = new javafx.scene.image.ImageView(exitImg);
            eiv.setPreserveRatio(true);
            eiv.setFitWidth(220);
            exit.setGraphic(eiv);
        } else {
            exit.setText("Exit");
        }
        exit.setOnAction(e -> showStartScene());
        applyHoverEffect(exit);

        HBox buttonRow = new HBox(12, again, exit);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setTranslateY(80);

        StackPane imageWithButtons = new StackPane(pageView, buttonRow);
        imageWithButtons.setAlignment(Pos.CENTER);
        StackPane.setAlignment(buttonRow, Pos.BOTTOM_CENTER);

        StackPane centerPane = new StackPane(imageWithButtons);
        centerPane.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(centerPane);
        root.setStyle("-fx-background-color: #FFFFFF;");

        Scene scene = new Scene(root, currentWidth(), currentHeight());
        stage.setScene(scene);
        pageView.fitWidthProperty().bind(scene.widthProperty().multiply(0.6));
        pageView.fitHeightProperty().bind(scene.heightProperty().multiply(0.6));
    }

    private HBox createHudRow(String icon, ProgressBar bar, Label text) {
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(26));
        iconLabel.setTextFill(Color.BLACK);

        text.setFont(Font.font(28));

        HBox row = new HBox(8, iconLabel, bar, text);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** Updates the HUD labels and progress bars and redraws the board. */
    private void refreshGameView() {
        int hp = engine.getPlayer().getHealth();
        int stamina = engine.getPlayer().getStamina();

        // show only remaining values
        hpLabel.setText(String.valueOf(hp));
        staminaLabel.setText(String.valueOf(stamina));
        levelLabel.setText(engine.getLevelLabel());
        statusLabel.setText(engine.getStatusMessage() + " (ESC: menu)");

        hpBar.setProgress(Math.max(0.0, Math.min(1.0, hp / (double) engine.getMaxHealth())));
        staminaBar.setProgress(Math.max(0.0, Math.min(1.0, stamina / (double) engine.getMaxStamina())));

        // Check win / lose conditions once per refresh
        int playerX = engine.getPlayer().getX();
        int playerY = engine.getPlayer().getY();
        if (!gameOverHandled) {
            if (engine.isCompleted() && hp > 0) {
                handleWin();
                return;
            }
            // lose when HP or stamina zero and player is NOT on the door/exit
            if ((hp <= 0 || stamina <= 0) && !engine.getBoard().isDoor(playerX, playerY)) {
                handleLose();
                return;
            }
        }

        drawBoard();
    }

    /** Renders the entire tile-based game board onto the canvas. */
    private void drawBoard() {
        GraphicsContext g = gameCanvas.getGraphicsContext2D();
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        GameBoard board = engine.getBoard();
        double boardWidth = board.getWidth() * TILE;
        double boardHeight = board.getHeight() * TILE;
        double originX = (gameCanvas.getWidth() - boardWidth) / 2.0;
        double originY = (gameCanvas.getHeight() - boardHeight) / 2.0;

        g.setFill(Color.web("#dbdbdb"));
        g.fillRoundRect(originX, originY, boardWidth, boardHeight, 30, 30);

        // Draw floor tiles
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                double px = originX + x * TILE;
                double py = originY + y * TILE;

                if (!board.isWall(x, y)) {
                    boolean isStartOrEnd = (x == board.getPlayerStartX() && y == board.getPlayerStartY()) || 
                                          (x == board.getDoorX() && y == board.getDoorY());
                    String floorPath = isStartOrEnd ? "/images/Object/FloorStartEnd.png" : "/images/Object/FloorBasic.png";
                    Image floorImage = ImageLoader.loadImage(floorPath, TILE, TILE);
                    if (floorImage != null) {
                        g.drawImage(floorImage, px, py, TILE, TILE);
                    }
                }

                g.setStroke(Color.web("#aaaaaa"));
                g.strokeRect(px, py, TILE, TILE);

                if (board.isWall(x, y)) {
                    g.setFill(Color.web("#7f7f7f"));
                    g.fillRect(px + 2, py + 2, TILE - 4, TILE - 4);
                }
            }
        }

        int playerRow = Math.max(0, Math.min(board.getHeight() - 1, (int) Math.round(renderPlayerY)));

        for (int row = 0; row < board.getHeight(); row++) {
            for (objects.BaseObject obstacle : board.getMoveableObstacles()) {
                if (obstacle.getY() == row) {
                    drawObstacle(g, originX, originY, obstacle);
                }
            }

            for (Cable cable : board.getCables()) {
                if (cable.getY() == row) {
                    drawCableToken(g, originX, originY, cable);
                }
            }

            for (BaseItem item : board.getItems()) {
                if (!item.isConsumed() && item.getY() == row) {
                    drawItem(g, originX, originY, item.getX(), item.getY(), item.getName());
                }
            }

            for (Human human : board.getHumans()) {
                if (human.isActive() && human.getY() == row) {
                    // Update random facing when player not nearby
                    int playerX = engine.getPlayer().getX();
                    int playerY = engine.getPlayer().getY();
                    int dx = Math.abs(playerX - human.getX());
                    int dy = Math.abs(playerY - human.getY());
                    int distance = dx + dy;
                    long now = System.nanoTime();

                    if (distance <= HUMAN_REACTION_RANGE) {
                        // When player nearby, face player (cancel random idle)
                        human.setFacing(getHumanFacingDirection(human.getX(), human.getY()));
                        // schedule next random change a bit later
                        human.setNextFaceChangeAt(now + 2_000_000_000L);
                    } else {
                        // If it's time, pick a new random facing direction
                        if (now >= human.getNextFaceChangeAt()) {
                            int r = (int) (Math.random() * 4);
                            logic.components.Direction d = switch (r) {
                                case 0 -> logic.components.Direction.UP;
                                case 1 -> logic.components.Direction.DOWN;
                                case 2 -> logic.components.Direction.LEFT;
                                default -> logic.components.Direction.RIGHT;
                            };
                            human.setFacing(d);
                            // next change in 1-5 seconds
                            long delay = (1 + (long)(Math.random() * 5)) * 1_000_000_000L;
                            human.setNextFaceChangeAt(now + delay);
                        }
                    }

                    drawHuman(g, originX, originY, human.getX(), human.getY(), human.getName(), human.getFacing(), distance);
                }
            }

            if (playerRow == row) {
                drawPlayer(g, originX, originY, renderPlayerX, renderPlayerY);
            }
        }
    }

    private void drawObstacle(GraphicsContext g, double originX, double originY, objects.BaseObject obstacle) {
        double x = originX + obstacle.getX() * TILE;
        double y = originY + obstacle.getY() * TILE;

        String imagePath = null;
        if (obstacle instanceof Table) {
            Table table = (Table) obstacle;
            String orientation = table.getOrientation();
            imagePath = switch (orientation) {
                case "Up" -> "/images/Object/TableUp.png";
                case "Down" -> "/images/Object/TableDown.png";
                case "Left" -> "/images/Object/TableLeft.png";
                case "Right" -> "/images/Object/TableRight.png";
                default -> "/images/Object/TableDown.png";
            };
        } else if (obstacle instanceof Chair) {
            Chair chair = (Chair) obstacle;
            String orientation = chair.getOrientation();
            imagePath = switch (orientation) {
                case "Back" -> "/images/Object/ChairBack.png";
                case "Front" -> "/images/Object/ChairFront.png";
                case "Left" -> "/images/Object/ChairLeft.png";
                case "Right" -> "/images/Object/ChairRight.png";
                default -> "/images/Object/ChairFront.png";
            };
        } else {
            imagePath = "/images/Object/ChairFront.png";
        }

        Image obstacleImage = ImageLoader.loadImage(imagePath);
        if (obstacleImage != null) {
            double imageWidth = obstacleImage.getWidth();
            double imageHeight = obstacleImage.getHeight();
            if (imageWidth > 0 && imageHeight > 0) {
                double maxSize = (obstacle instanceof Table) ? TILE : TILE * 0.9;
                double scale = Math.min(maxSize / imageWidth, maxSize / imageHeight);
                double scaledWidth = imageWidth * scale;
                double scaledHeight = imageHeight * scale;
                
                double drawX, drawY;
                if (obstacle instanceof Table) {
                    Table table = (Table) obstacle;
                    String orientation = table.getOrientation();
                    drawX = switch (orientation) {
                        case "Left" -> x + (TILE - scaledWidth);
                        case "Right" -> x;
                        default -> x + (TILE - scaledWidth) / 2.0;
                    };
                    drawY = switch (orientation) {
                        case "Up" -> y + (TILE - scaledHeight);
                        case "Down" -> y;
                        default -> y + (TILE - scaledHeight) / 2.0;
                    };
                } else {
                    drawX = x + (TILE - scaledWidth) / 2.0;
                    drawY = y + (TILE - scaledHeight) / 2.0;
                }
                
                g.drawImage(obstacleImage, drawX, drawY, scaledWidth, scaledHeight);
                return;
            }
        }

        g.setFill(Color.web("#ECEFD1"));
        g.fillRoundRect(x + 3, y + 3, TILE - 6, TILE - 6, 20, 20);
        g.setStroke(Color.BLACK);
        g.strokeRoundRect(x + 3, y + 3, TILE - 6, TILE - 6, 20, 20);
    }

    private void drawCableToken(GraphicsContext g, double originX, double originY, objects.obstacles.Cable cable) {
        double x = originX + cable.getX() * TILE;
        double y = originY + cable.getY() * TILE;

        String sprite = null;
        try {
            sprite = cable.getSpriteName();
        } catch (Exception ignored) {
        }

        Image cableImage = null;
        if (sprite != null) {
            cableImage = ImageLoader.loadImage("/images/Object/" + sprite + ".png");
        }

        if (cableImage == null) {
            cableImage = ImageLoader.loadImage("/images/Object/HPlugDown.png");
        }

        if (cableImage != null) {
            double imageWidth = cableImage.getWidth();
            double imageHeight = cableImage.getHeight();
            if (imageWidth > 0 && imageHeight > 0) {
                double maxSize = TILE;
                double scale = Math.min(maxSize / imageWidth, maxSize / imageHeight);
                double scaledWidth = imageWidth * scale;
                double scaledHeight = imageHeight * scale;
                double drawX = x + (TILE - scaledWidth) / 2.0;
                double drawY = y + (TILE - scaledHeight) / 2.0;
                g.drawImage(cableImage, drawX, drawY, scaledWidth, scaledHeight);
                return;
            }
        }

        g.setFill(Color.web("#ff2f2f"));
        g.fillRoundRect(x + 5, y + 5, TILE - 10, TILE - 10, 18, 18);
        g.setStroke(Color.BLACK);
        g.strokeRoundRect(x + 5, y + 5, TILE - 10, TILE - 10, 18, 18);
    }

    private void drawItem(GraphicsContext g, double originX, double originY, int gridX, int gridY, String name) {
        double x = originX + gridX * TILE;
        double y = originY + gridY * TILE;
        // Idle bobbing for items (up/down)
        double time = (System.nanoTime() - animationStartNanos) / 1_000_000_000.0;
        double bob = Math.sin((time * 2.0) + (gridX + gridY) * 0.5) * 6.0;

        String imagePath = switch (name) {
            case "Parabola" -> "/images/Object/Parabola.png";
            case "Caffeine" -> "/images/Object/Coffee.png";
            case "RobuxGiftCard" -> "/images/Object/Roblox.png";
            default -> null;
        };

        if (imagePath != null) {
            Image itemImage = ImageLoader.loadImage(imagePath);
            if (itemImage != null) {
                double imageWidth = itemImage.getWidth();
                double imageHeight = itemImage.getHeight();
                if (imageWidth > 0 && imageHeight > 0) {
                    double maxSize = TILE * 0.8;
                    double scale = Math.min(maxSize / imageWidth, maxSize / imageHeight);
                    double scaledWidth = imageWidth * scale;
                    double scaledHeight = imageHeight * scale;
                    double drawX = x + (TILE - scaledWidth) / 2.0;
                    double drawY = y + (TILE - scaledHeight) / 2.0 + bob;
                    g.drawImage(itemImage, drawX, drawY, scaledWidth, scaledHeight);
                    return;
                }
            }
        }

        g.setFill(Color.web("#ffcf66"));
        g.fillOval(x + 12, y + 12, TILE - 24, TILE - 24);
        g.setStroke(Color.BLACK);
        g.strokeOval(x + 12, y + 12, TILE - 24, TILE - 24);

        g.setFill(Color.BLACK);
        String token = switch (name) {
            case "Parabola" -> "P";
            case "Caffeine" -> "C";
            case "RobuxGiftCard" -> "R";
            default -> "?";
        };
        g.fillText(token, x + 28, y + 38 + bob);
    }

    private void drawHuman(GraphicsContext g, double originX, double originY, int gridX, int gridY, String name, logic.components.Direction facing, int distanceToPlayer) {
        double x = originX + gridX * TILE;
        double y = originY + gridY * TILE;
        // humans do not bob; keep static position

        String imagePath = getHumanImagePath(name, facing);

        if (imagePath != null) {
            Image humanImage = ImageLoader.loadImage(imagePath);
            if (humanImage != null) {
                double imageWidth = humanImage.getWidth();
                double imageHeight = humanImage.getHeight();
                if (imageWidth > 0 && imageHeight > 0) {
                    double targetHeight = TILE * 1.25;
                    double targetWidth = targetHeight * (imageWidth / imageHeight);
                    double drawX = x + (TILE - targetWidth) / 2.0;
                    double drawY = y + TILE - targetHeight;
                    g.drawImage(humanImage, drawX, drawY, targetWidth, targetHeight);
                    return;
                }
            }
        }

        g.setFill(Color.web("#f57c2f"));
        g.fillOval(x + 10, y + 10, TILE - 20, TILE - 20);
        g.setStroke(Color.BLACK);
        g.strokeOval(x + 10, y + 10, TILE - 20, TILE - 20);

        g.setFill(Color.BLACK);
        String token = switch (name) {
            case "Introvert" -> "I";
            case "Extrovert" -> "E";
            case "TA" -> "A";
            case "Teacher" -> "R";
            default -> "H";
        };
        g.fillText(token, x + 28, y + 38);
    }

    private String getHumanImagePath(String name, Direction facing) {
        String humanPrefix = switch (name) {
            case "Introvert" -> "Introvert";
            case "Extrovert" -> "Extrovert";
            case "TA" -> "TA";
            case "Teacher" -> "Teacher";
            default -> null;
        };

        if (humanPrefix == null) {
            return null;
        }

        String suffix = switch (facing) {
            case UP -> "Back.png";
            case DOWN -> "Front.png";
            case LEFT -> "Left.png";
            case RIGHT -> "Right.png";
        };

        return "/images/People/" + humanPrefix + suffix;
    }

    private Direction getHumanFacingDirection(int humanX, int humanY) {
        int playerX = engine.getPlayer().getX();
        int playerY = engine.getPlayer().getY();

        int dx = playerX - humanX;
        int dy = playerY - humanY;
        int distance = Math.abs(dx) + Math.abs(dy);

        if (distance == 0 || distance > HUMAN_REACTION_RANGE) {
            return Direction.DOWN;
        }

        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? Direction.RIGHT : Direction.LEFT;
        }

        return dy > 0 ? Direction.DOWN : Direction.UP;
    }

    /** Shows the reward screen after a level is completed. */
    private void showRewardScreen() {
        stopRenderLoop();

        int currentLevel = switch (engine.getLevelLabel()) {
            case "LEVEL 1 (ISCALE 401)" -> 1;
            case "LEVEL 2 (ISCALE 402)" -> 2;
            case "LEVEL 3 (ISCALE 403)" -> 3;
            case "LEVEL 4 (ISCALE 404)" -> 4;
            case "LEVEL 5 (ISCALE 405)" -> 5;
            default -> 1;
        };

        java.util.List<rewards.Reward> collected = session.getCollectedRewards();
        java.util.List<rewards.Reward> toShow;
        if (collected == null || collected.isEmpty()) {
            toShow = java.util.List.of();
        } else {
            toShow = java.util.List.of(collected.get(collected.size() - 1));
        }
        RewardScreen rewardScreen = new RewardScreen(stage, toShow, currentLevel);

        rewardScreen.setOnNextLevel(() -> {
            LevelId nextLevel = engine.getNextLevelId();
            if (nextLevel != null) {
                showGameScene(nextLevel);
            } else {
                // play ending sound 1 when advancing from last reward
                SoundManager.playEnding(1);
                showGameCompletionScene();
            }
        });

        rewardScreen.setOnMainMenu(() -> showStartScene());

        stage.setScene(rewardScreen.createScene());
    }

    /** Shows the game-completion scene after all levels are finished. */
    private void showGameCompletionScene() {
        stopRenderLoop();
        SoundManager.stopBackground();

        Image endingBg = ImageLoader.loadImage("/Ending/bg.png");
        javafx.scene.image.ImageView bgView = null;
        if (endingBg != null) {
            bgView = new javafx.scene.image.ImageView(endingBg);
            bgView.setPreserveRatio(false);
        }

        javafx.scene.image.ImageView pageView = new javafx.scene.image.ImageView();
        pageView.setPreserveRatio(true);
        pageView.setFitWidth(WINDOW_WIDTH * 0.8);

        final int MAX_ENDING_PAGES = 2;
        final int[] page = {1};
        Runnable updateView = () -> {
            String path = "/Ending/" + page[0] + ".png";
            Image img = ImageLoader.loadImage(path);
            if (img != null) pageView.setImage(img);
        };

        updateView.run();

        Button next = new Button();
        Image nextImg = ImageLoader.loadImage("/Ending/Next.png");
        if (nextImg != null) {
            javafx.scene.image.ImageView niv = new javafx.scene.image.ImageView(nextImg);
            niv.setPreserveRatio(true);
            niv.setFitWidth(160);
            next.setGraphic(niv);
        } else {
            next.setText("Next");
        }

        Button end = new Button();
        Image endImg = ImageLoader.loadImage("/Ending/End.png");
        if (endImg != null) {
            javafx.scene.image.ImageView eiv = new javafx.scene.image.ImageView(endImg);
            eiv.setPreserveRatio(true);
            eiv.setFitWidth(220);
            end.setGraphic(eiv);
        } else {
            end.setText("End");
        }

        next.setOnAction(e -> {
            if (page[0] < MAX_ENDING_PAGES) {
                page[0]++;
                updateView.run();
                SoundManager.playEnding(page[0]);
            } else {
                SoundManager.stopEnding();
                showStartScene();
            }
        });
        applyHoverEffect(next);

        end.setOnAction(e -> {
            SoundManager.stopEnding();
            showStartScene();
        });
        applyHoverEffect(end);

        HBox bottom = new HBox(12, next);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(12));

        BorderPane content = new BorderPane();
        content.setCenter(pageView);
        content.setBottom(bottom);
        content.setStyle("-fx-background-color: transparent;");

        StackPane root = new StackPane();
        if (bgView != null) {
            root.getChildren().add(bgView);
        }
        root.getChildren().add(content);

        pageView.imageProperty().addListener((obs, old, nw) -> {
            HBox b;
            if (page[0] < MAX_ENDING_PAGES) {
                b = new HBox(12, next);
            } else {
                b = new HBox(12, end);
            }
            b.setAlignment(Pos.CENTER_RIGHT);
            b.setPadding(new Insets(12));
            content.setBottom(b);
        });

        Scene scene = new Scene(root, currentWidth(), currentHeight());
        stage.setScene(scene);
        if (bgView != null) {
            bgView.fitWidthProperty().bind(scene.widthProperty());
            bgView.fitHeightProperty().bind(scene.heightProperty());
        }
        pageView.fitWidthProperty().bind(scene.widthProperty().multiply(0.8));
    }

    private double getIdleBobOffset(int gridX, int gridY) {
        double time = (System.nanoTime() - animationStartNanos) / 1_000_000_000.0;
        double phaseOffset = (gridX + gridY) * 0.35;
        return Math.sin((time * 4.0) + phaseOffset) * 2.0;
    }

    /**
     * Draws the player sprite at the interpolated render position.
     *
     * @param g             the graphics context
     * @param originX       board origin X on canvas
     * @param originY       board origin Y on canvas
     * @param renderPlayerX interpolated player X in grid units
     * @param renderPlayerY interpolated player Y in grid units
     */
    private void drawPlayer(GraphicsContext g, double originX, double originY, double renderPlayerX, double renderPlayerY) {
        double x = originX + renderPlayerX * TILE;
        double y = originY + renderPlayerY * TILE;
        double bobOffset = getIdleBobOffset((int)Math.round(renderPlayerX), (int)Math.round(renderPlayerY));

        logic.game.Player player = engine.getPlayer();
        String suffix = switch (player != null ? player.getLastDirection() : null) {
            case UP -> "Back.png";
            case DOWN -> "Front.png";
            case LEFT -> "Left.png";
            case RIGHT -> "Right.png";
            default -> "Front.png";
        };

        String imagePath = "/images/People/Player" + suffix;
        Image playerImage = ImageLoader.loadImage(imagePath);

        if (playerImage != null) {
            double imageWidth = playerImage.getWidth();
            double imageHeight = playerImage.getHeight();
            if (imageWidth > 0 && imageHeight > 0) {
                double targetHeight = TILE * 1.25;
                double targetWidth = targetHeight * (imageWidth / imageHeight);
                double drawX = x + (TILE - targetWidth) / 2.0;
                double drawY = y + TILE - targetHeight + bobOffset;
                g.drawImage(playerImage, drawX, drawY, targetWidth, targetHeight);
                return;
            }
        }

        g.setFill(Color.web("#3b5ce4"));
        g.fillRoundRect(x + 8, y + 8 + bobOffset, TILE - 16, TILE - 16, 18, 18);
        g.setStroke(Color.BLACK);
        g.strokeRoundRect(x + 8, y + 8 + bobOffset, TILE - 16, TILE - 16, 18, 18);
        g.setFill(Color.WHITE);
        g.fillText("P", x + 28, y + 38 + bobOffset);
    }

    private void updateMuteButtonText(Button muteButton) {
        if (SoundManager.isMutedStatus()) {
            // Sound off - muted
            Image speakerOffImg = ImageLoader.loadImage("/images/icons/SoundOff.png");
            if (speakerOffImg != null) {
                javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(speakerOffImg);
                iv.setPreserveRatio(true);
                iv.setFitWidth(30);
                iv.setFitHeight(30);
                muteButton.setGraphic(iv);
                muteButton.setText("");
            } else {
                muteButton.setText("🔇");
            }
            muteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-opacity: 0.6; -fx-padding: 5;");
        } else {
            // Sound on - unmuted
            Image speakerOnImg = ImageLoader.loadImage("/images/icons/soundOn.png");
            if (speakerOnImg != null) {
                javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(speakerOnImg);
                iv.setPreserveRatio(true);
                iv.setFitWidth(30);
                iv.setFitHeight(30);
                muteButton.setGraphic(iv);
                muteButton.setText("");
            } else {
                muteButton.setText("🔊");
            }
            muteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-opacity: 1.0; -fx-padding: 5;");
        }
    }
}