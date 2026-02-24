package gui;

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

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setTitle("Project Progmeth");
        showStartScene();
        stage.show();
    }

    private void showStartScene() {
        Label title = new Label("Born Again\nBecome CEDT Student");
        title.setFont(Font.font("System", 64));
        title.setAlignment(Pos.CENTER);
        title.setTextFill(Color.WHITE);

        Button newGameButton = createMainButton("New Game");
        newGameButton.setOnAction(e -> startGameWithLevel(1));

        Button chapterSelectButton = createMainButton("Chapter Select");
        chapterSelectButton.setOnAction(e -> showLevelSelectScene());

        Button howToPlayButton = createMainButton("How to Play");
        howToPlayButton.setOnAction(e -> showHowToPlayScene());

        Button exitButton = createMainButton("Exit");
        exitButton.setOnAction(e -> stage.close());

        VBox root = new VBox(18, title, newGameButton, chapterSelectButton, howToPlayButton, exitButton);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #000000;");

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
    }

    private void showHowToPlayScene() {
        Label title = new Label("How to play");
        title.setFont(Font.font("System", 56));
        title.setTextFill(Color.WHITE);

        GridPane cardsGrid = new GridPane();
        cardsGrid.setHgap(26);
        cardsGrid.setVgap(20);
        cardsGrid.add(createHowToCard("Movement", "Use Arrow keys or WASD to move.\nEach move consumes 1 stamina."), 0, 0);
        cardsGrid.add(createHowToCard("Combat", "Walk into human to attack.\nIntrovert: HP-1 Stamina-1\nExtrovert: HP-3 Stamina-1"), 1, 0);
        cardsGrid.add(createHowToCard("Obstacles", "Push Chair (cost 1)\nPush Table (cost 2)\nCable hurts HP when stepped."), 0, 1);
        cardsGrid.add(createHowToCard("Goal", "Reach the door D to clear level.\nPress R to restart current level."), 1, 1);

        Button backButton = createMainButton("Back");
        backButton.setOnAction(e -> showStartScene());

        VBox root = new VBox(24, title, cardsGrid, backButton);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #000000;");

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
    }

    private void showLevelSelectScene() {
        Label title = new Label("Select Level");
        title.setFont(Font.font("System", 56));
        title.setTextFill(Color.WHITE);

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
        root.setStyle("-fx-background-color: #000000;");

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
        imageBox.setFill(Color.BLACK);
        imageBox.setStroke(Color.WHITE);
        imageBox.setStrokeWidth(2);

        Label headingLabel = new Label(heading);
        headingLabel.setFont(Font.font(28));
        headingLabel.setTextFill(Color.WHITE);

        Label textLabel = new Label(text);
        textLabel.setFont(Font.font(20));
        textLabel.setTextFill(Color.WHITE);

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
        engine = new GameEngine(session, levelId);

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

        gameCanvas = new Canvas(WINDOW_WIDTH - 60, WINDOW_HEIGHT - 170);
        StackPane boardPane = new StackPane(gameCanvas);
        boardPane.setPadding(new Insets(6, 20, 6, 20));

        statusLabel.setFont(Font.font(22));
        statusLabel.setPadding(new Insets(4, 22, 12, 22));
        hpLabel.setTextFill(Color.WHITE);
        staminaLabel.setTextFill(Color.WHITE);
        levelLabel.setTextFill(Color.WHITE);
        statusLabel.setTextFill(Color.WHITE);

        BorderPane root = new BorderPane();
        root.setTop(topHud);
        root.setCenter(boardPane);
        root.setBottom(statusLabel);
        root.setStyle("-fx-background-color: #000000;");

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.UP || code == KeyCode.W) {
                engine.move(Direction.UP);
            } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                engine.move(Direction.DOWN);
            } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                engine.move(Direction.LEFT);
            } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                engine.move(Direction.RIGHT);
            } else if (code == KeyCode.R) {
                engine.restartLevel();
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
        refreshGameView();
        gameCanvas.setFocusTraversable(true);
        gameCanvas.requestFocus();
    }

    private HBox createHudRow(String icon, ProgressBar bar, Label text) {
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(26));
        iconLabel.setTextFill(Color.WHITE);

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
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        GameBoard board = engine.getBoard();
        double boardWidth = board.getWidth() * TILE;
        double boardHeight = board.getHeight() * TILE;
        double originX = (gameCanvas.getWidth() - boardWidth) / 2.0;
        double originY = (gameCanvas.getHeight() - boardHeight) / 2.0;

        g.setFill(Color.web("#dbdbdb"));
        g.fillRoundRect(originX, originY, boardWidth, boardHeight, 30, 30);

        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                double px = originX + x * TILE;
                double py = originY + y * TILE;

                g.setStroke(Color.web("#aaaaaa"));
                g.strokeRect(px, py, TILE, TILE);

                if (board.isWall(x, y)) {
                    g.setFill(Color.web("#7f7f7f"));
                    g.fillRect(px + 2, py + 2, TILE - 4, TILE - 4);
                }
                if (board.isDoor(x, y)) {
                    g.setFill(Color.WHITE);
                    g.fillRect(px + 8, py + 8, TILE - 16, TILE - 16);
                    g.setStroke(Color.BLACK);
                    g.strokeRect(px + 8, py + 8, TILE - 16, TILE - 16);
                }
            }
        }

        for (Chair obstacle : board.getMoveableObstacles()) {
            drawObstacle(g, originX, originY, obstacle);
        }

        for (Cable cable : board.getCables()) {
            drawCircleToken(g, originX, originY, cable.getX(), cable.getY(), Color.web("#ff2f2f"));
        }

        for (BaseItem item : board.getItems()) {
            if (!item.isConsumed()) {
                drawItem(g, originX, originY, item.getX(), item.getY(), item.getName());
            }
        }

        for (Human human : board.getHumans()) {
            if (human.isActive()) {
                drawHuman(g, originX, originY, human.getX(), human.getY(), human.getName());
            }
        }

        drawPlayer(g, originX, originY, engine.getPlayer().getX(), engine.getPlayer().getY());
    }

    private void drawObstacle(GraphicsContext g, double originX, double originY, Chair obstacle) {
        double x = originX + obstacle.getX() * TILE;
        double y = originY + obstacle.getY() * TILE;

        Image obstacleImage = null;
        if (obstacle instanceof Table) {
            obstacleImage = ImageLoader.loadImage("/images/Object/Table-Horizon.png", TILE, TILE);
        } else {
            obstacleImage = ImageLoader.loadImage("/images/Object/Chair-Front.png", TILE, TILE);
        }

        if (obstacleImage != null) {
            g.drawImage(obstacleImage, x, y, TILE, TILE);
        } else {
            g.setFill(Color.web("#ECEFD1"));
            g.fillRoundRect(x + 3, y + 3, TILE - 6, TILE - 6, 20, 20);
            g.setStroke(Color.BLACK);
            g.strokeRoundRect(x + 3, y + 3, TILE - 6, TILE - 6, 20, 20);
        }
    }

    private void drawCircleToken(GraphicsContext g, double originX, double originY, int gridX, int gridY, Color color) {
        double x = originX + gridX * TILE;
        double y = originY + gridY * TILE;

        Image cableImage = ImageLoader.loadImage("/images/Object/Extended-plug-Down.png", TILE, TILE);
        if (cableImage != null) {
            g.drawImage(cableImage, x, y, TILE, TILE);
        } else {
            g.setFill(color);
            g.fillRoundRect(x + 5, y + 5, TILE - 10, TILE - 10, 18, 18);
            g.setStroke(Color.BLACK);
            g.strokeRoundRect(x + 5, y + 5, TILE - 10, TILE - 10, 18, 18);
        }
    }

    private void drawItem(GraphicsContext g, double originX, double originY, int gridX, int gridY, String name) {
        double x = originX + gridX * TILE;
        double y = originY + gridY * TILE;

        String imagePath = switch (name) {
            case "Parabola" -> "/images/Object/coffee.png";
            case "Caffeine" -> "/images/Object/coffee.png";
            case "RobuxGiftCard" -> "/images/Object/yogurt.png";
            default -> null;
        };

        if (imagePath != null) {
            Image itemImage = ImageLoader.loadImage(imagePath, TILE, TILE);
            if (itemImage != null) {
                g.drawImage(itemImage, x, y, TILE, TILE);
                return;
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

        String imagePath = switch (name) {
            case "Introvert" -> "/images/People/Introvert-Front.png";
            case "Extrovert" -> "/images/People/Extrovert-Front.png";
            case "TA" -> "/images/People/TA-Front.png";
            case "Teacher" -> "/images/People/ArJarn-Front.png";
            default -> null;
        };

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

    private void showRewardScreen() {
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
        Label titleLabel = new Label("Congratulations!");
        titleLabel.setFont(Font.font("System", 72));
        titleLabel.setTextFill(Color.WHITE);

        Label messageLabel = new Label("You have completed all levels!\nYou are now a true CEDT member!");
        messageLabel.setFont(Font.font("System", 36));
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setTextFill(Color.WHITE);

        VBox rewardDisplay = new VBox(20);
        rewardDisplay.setAlignment(Pos.CENTER);
        rewardDisplay.setPadding(new Insets(30));

        Label rewardLabel = new Label("Final Reward Collection:");
        rewardLabel.setFont(Font.font("System", 32));
        rewardLabel.setTextFill(Color.WHITE);

        HBox rewardsBox = new HBox(30);
        rewardsBox.setAlignment(Pos.CENTER);
        for (Reward reward : session.getCollectedRewards()) {
            Label rewardName = new Label(reward.getName());
            rewardName.setTextFill(Color.WHITE);
            VBox rewardItem = new VBox(10, 
                rewardName);
            rewardItem.setAlignment(Pos.CENTER);
            rewardItem.setPadding(new Insets(10));
            rewardItem.setStyle("-fx-border-color: white; -fx-border-radius: 10; -fx-padding: 10;");
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
        root.setStyle("-fx-background-color: #000000;");

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
    }

    private void drawPlayer(GraphicsContext g, double originX, double originY, int gridX, int gridY) {
        double x = originX + gridX * TILE;
        double y = originY + gridY * TILE;

        String directionSuffix = switch (engine.getPlayer().getLastDirection()) {
            case UP -> "-Back.png";
            case DOWN -> "-Front.png";
            case LEFT -> "-Left.png";
            case RIGHT -> "-Right.png";
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
                double drawY = y + TILE - targetHeight;
                g.drawImage(playerImage, drawX, drawY, targetWidth, targetHeight);
                return;
            }
        } else {
            g.setFill(Color.web("#3b5ce4"));
            g.fillRoundRect(x + 8, y + 8, TILE - 16, TILE - 16, 18, 18);
            g.setStroke(Color.BLACK);
            g.strokeRoundRect(x + 8, y + 8, TILE - 16, TILE - 16, 18, 18);
            g.setFill(Color.WHITE);
            g.fillText("P", x + 28, y + 38);
        }
    }
}