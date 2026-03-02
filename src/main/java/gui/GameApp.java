package gui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
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

public class GameApp extends Application {
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 700;
    private static final int TILE = 64;
    private static final int HUMAN_REACTION_RANGE = 1;
    private static final long PLAYER_MOVE_ANIM_NANOS = 120_000_000L;

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
    private long animationStartNanos;
    private double renderPlayerX;
    private double renderPlayerY;
    private double moveFromX;
    private double moveFromY;
    private double moveToX;
    private double moveToY;
    private long playerMoveStartNanos;
    private boolean playerMoveAnimating;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setTitle("Project Progmeth");
        showStartScene();
        stage.show();
    }

    private void showStartScene() {
        stopRenderLoop();
        // Main screen uses provided Main image and two buttons: Press to Start and How to Play
        stopRenderLoop();

        Image mainImage = ImageLoader.loadImage("/Page/Main.png");
        javafx.scene.image.ImageView mainView = null;
        if (mainImage != null) {
            mainView = new javafx.scene.image.ImageView(mainImage);
            mainView.setPreserveRatio(true);
            mainView.setFitWidth(WINDOW_WIDTH * 0.7);
        }

        Button pressToStart = new Button();
        Image pImg = ImageLoader.loadImage("/Page/PressToStart.png");
        if (pImg != null) {
            javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(pImg);
            iv.setPreserveRatio(true);
            iv.setFitWidth(220);
            pressToStart.setGraphic(iv);
        } else {
            pressToStart.setText("Press to Start");
        }
        pressToStart.setOnAction(e -> startGameWithLevel(1));

        Button howTo = new Button();
        Image hImg = ImageLoader.loadImage("/Page/HowtoPlay.png");
        if (hImg != null) {
            javafx.scene.image.ImageView iv2 = new javafx.scene.image.ImageView(hImg);
            iv2.setPreserveRatio(true);
            iv2.setFitWidth(220);
            howTo.setGraphic(iv2);
        } else {
            howTo.setText("How to Play");
        }
        howTo.setOnAction(e -> showHowToPlayScene());

        HBox buttons = new HBox(24, pressToStart, howTo);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(24);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #FFFFFF;");
        if (mainView != null) root.getChildren().add(mainView);
        root.getChildren().add(buttons);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
    }

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
            }
        });

        letsPlay.setOnAction(e -> startGameWithLevel(1));

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

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
    }

    private void showLevelSelectScene() {
        stopRenderLoop();

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
            levelGrid.add(levelButton, (i - 1) % 5, (i - 1) / 5);
        }

        Button backButton = createMainButton("Back");
        backButton.setOnAction(e -> showStartScene());

        VBox root = new VBox(20, title, levelGrid, backButton);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #FFFFFF;");

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
    }

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
        return button;
    }

    private void showGameScene(LevelId levelId) {
        stopRenderLoop();
        engine = new GameEngine(session, levelId);
        animationStartNanos = System.nanoTime();

        hpBar = new ProgressBar(1.0);
        staminaBar = new ProgressBar(1.0);
        hpBar.setPrefWidth(250);
        staminaBar.setPrefWidth(250);

        hpLabel = new Label();
        staminaLabel = new Label();
        levelLabel = new Label();
        statusLabel = new Label();

        HBox topHud = new HBox(22,
                createHudRow("HP", hpBar, hpLabel),
                createHudRow("ST", staminaBar, staminaLabel),
                levelLabel
        );
        topHud.setPadding(new Insets(10, 20, 10, 20));
        topHud.setAlignment(Pos.CENTER_LEFT);

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
        levelLabel.setTextFill(Color.BLACK);
        statusLabel.setTextFill(Color.BLACK);

        BorderPane root = new BorderPane();
        root.setTop(topHud);
        root.setCenter(boardPane);
        root.setBottom(statusLabel);
        root.setStyle("-fx-background-color: #FFFFFF;");

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
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
                showRewardScreen();
            }
        });

        stage.setScene(scene);
        syncPlayerRenderPosition();
        refreshGameView();
        startRenderLoop();
        gameCanvas.setFocusTraversable(true);
        gameCanvas.requestFocus();
    }

    private void handlePlayerMove(Direction direction) {
        int oldX = engine.getPlayer().getX();
        int oldY = engine.getPlayer().getY();
        updatePlayerAnimation();

        engine.move(direction);

        int newX = engine.getPlayer().getX();
        int newY = engine.getPlayer().getY();
        if (newX != oldX || newY != oldY) {
            startPlayerMoveAnimation(newX, newY);
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

    private HBox createHudRow(String icon, ProgressBar bar, Label text) {
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(26));
        iconLabel.setTextFill(Color.BLACK);

        text.setFont(Font.font(28));

        HBox row = new HBox(8, iconLabel, bar, text);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void refreshGameView() {
        int hp = engine.getPlayer().getHealth();
        int stamina = engine.getPlayer().getStamina();

        hpLabel.setText(hp + "/" + engine.getMaxHealth());
        staminaLabel.setText(stamina + "/" + engine.getMaxStamina());
        levelLabel.setText(engine.getLevelLabel());
        statusLabel.setText(engine.getStatusMessage() + " (ESC: menu)");

        hpBar.setProgress(Math.max(0.0, Math.min(1.0, hp / (double) engine.getMaxHealth())));
        staminaBar.setProgress(Math.max(0.0, Math.min(1.0, stamina / (double) engine.getMaxStamina())));

        drawBoard();
    }

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
            for (Chair obstacle : board.getMoveableObstacles()) {
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
                    drawHuman(g, originX, originY, human.getX(), human.getY(), human.getName());
                }
            }

            if (playerRow == row) {
                drawPlayer(g, originX, originY, renderPlayerX, renderPlayerY);
            }
        }
    }

    private void drawObstacle(GraphicsContext g, double originX, double originY, Chair obstacle) {
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
                    double drawY = y + (TILE - scaledHeight) / 2.0;
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
        g.fillText(token, x + 28, y + 38);
    }

    private void drawHuman(GraphicsContext g, double originX, double originY, int gridX, int gridY, String name) {
        double x = originX + gridX * TILE;
        double y = originY + gridY * TILE;
        double bobOffset = getIdleBobOffset(gridX, gridY);

        String imagePath = getHumanImagePath(name, gridX, gridY);

        if (imagePath != null) {
            Image humanImage = ImageLoader.loadImage(imagePath);
            if (humanImage != null) {
                double imageWidth = humanImage.getWidth();
                double imageHeight = humanImage.getHeight();
                if (imageWidth > 0 && imageHeight > 0) {
                    double targetHeight = TILE * 1.25;
                    double targetWidth = targetHeight * (imageWidth / imageHeight);
                    double drawX = x + (TILE - targetWidth) / 2.0;
                    double drawY = y + TILE - targetHeight + bobOffset;
                    g.drawImage(humanImage, drawX, drawY, targetWidth, targetHeight);
                    return;
                }
            }
        }

        g.setFill(Color.web("#f57c2f"));
        g.fillOval(x + 10, y + 10 + bobOffset, TILE - 20, TILE - 20);
        g.setStroke(Color.BLACK);
        g.strokeOval(x + 10, y + 10 + bobOffset, TILE - 20, TILE - 20);

        g.setFill(Color.BLACK);
        String token = switch (name) {
            case "Introvert" -> "I";
            case "Extrovert" -> "E";
            case "TA" -> "A";
            case "Teacher" -> "R";
            default -> "H";
        };
        g.fillText(token, x + 28, y + 38 + bobOffset);
    }

    private String getHumanImagePath(String name, int humanX, int humanY) {
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

        Direction facing = getHumanFacingDirection(humanX, humanY);
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

        RewardScreen rewardScreen = new RewardScreen(stage, session.getCollectedRewards(), currentLevel);

        rewardScreen.setOnNextLevel(() -> {
            LevelId nextLevel = engine.getNextLevelId();
            if (nextLevel != null) {
                showGameScene(nextLevel);
            } else {
                showGameCompletionScene();
            }
        });

        rewardScreen.setOnMainMenu(() -> showStartScene());

        stage.setScene(rewardScreen.createScene());
    }

    private void showGameCompletionScene() {
        stopRenderLoop();

        Label titleLabel = new Label("Congratulations!");
        titleLabel.setFont(Font.font("System", 72));
        titleLabel.setTextFill(Color.BLACK);

        Label messageLabel = new Label("You have completed all levels!\nYou are now a true CEDT member!");
        messageLabel.setFont(Font.font("System", 36));
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setTextFill(Color.BLACK);

        VBox rewardDisplay = new VBox(20);
        rewardDisplay.setAlignment(Pos.CENTER);
        rewardDisplay.setPadding(new Insets(30));

        Label rewardLabel = new Label("Final Reward Collection:");
        rewardLabel.setFont(Font.font("System", 32));
        rewardLabel.setTextFill(Color.BLACK);

        HBox rewardsBox = new HBox(30);
        rewardsBox.setAlignment(Pos.CENTER);
        for (Reward reward : session.getCollectedRewards()) {
            Label rewardName = new Label(reward.getName());
            rewardName.setTextFill(Color.BLACK);
            VBox rewardItem = new VBox(10, 
                rewardName);
            rewardItem.setAlignment(Pos.CENTER);
            rewardItem.setPadding(new Insets(10));
            rewardItem.setStyle("-fx-border-color: black; -fx-border-radius: 10; -fx-padding: 10;");
            rewardsBox.getChildren().add(rewardItem);
        }

        rewardDisplay.getChildren().addAll(rewardLabel, rewardsBox);

        Button menuButton = new Button("Back to Menu");
        menuButton.setFont(Font.font(32));
        menuButton.setPrefWidth(300);
        menuButton.setTextFill(Color.WHITE);
        menuButton.setStyle("-fx-background-color: #000000; -fx-background-radius: 20; -fx-border-color: white; -fx-border-width: 2;");
        menuButton.setOnAction(e -> showStartScene());

        VBox root = new VBox(40, titleLabel, messageLabel, rewardDisplay, menuButton);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #FFFFFF;");

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
    }

    private void drawPlayer(GraphicsContext g, double originX, double originY, double gridX, double gridY) {
        double x = originX + gridX * TILE;
        double y = originY + gridY * TILE;
        double bobOffset = getIdleBobOffset((int) Math.round(gridX), (int) Math.round(gridY));

        String directionSuffix = switch (engine.getPlayer().getLastDirection()) {
            case UP -> "Back.png";
            case DOWN -> "Front.png";
            case LEFT -> "Left.png";
            case RIGHT -> "Right.png";
        };

        String imagePath = "/images/People/Player" + directionSuffix;
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
        } else {
            g.setFill(Color.web("#3b5ce4"));
            g.fillRoundRect(x + 8, y + 8 + bobOffset, TILE - 16, TILE - 16, 18, 18);
            g.setStroke(Color.BLACK);
            g.strokeRoundRect(x + 8, y + 8 + bobOffset, TILE - 16, TILE - 16, 18, 18);
            g.setFill(Color.WHITE);
            g.fillText("P", x + 28, y + 38 + bobOffset);
        }
    }

    private double getIdleBobOffset(int gridX, int gridY) {
        double time = (System.nanoTime() - animationStartNanos) / 1_000_000_000.0;
        double phaseOffset = (gridX + gridY) * 0.35;
        return Math.sin((time * 4.0) + phaseOffset) * 2.0;
    }
}