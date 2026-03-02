package gui;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import rewards.Reward;

public class RewardScreen {
    private final Stage stage;
    private final List<Reward> rewards;
    private final int currentLevelNumber;
    private Runnable onNextLevel;

    public RewardScreen(Stage stage, List<Reward> rewards, int currentLevelNumber) {
        this.stage = stage;
        this.rewards = rewards;
        this.currentLevelNumber = currentLevelNumber;
    }

    public void setOnNextLevel(Runnable callback) {
        this.onNextLevel = callback;
    }

    public void setOnMainMenu(Runnable callback) {
        // intentionally retained for compatibility but not used in new UI
    }

    public Scene createScene() {
//        Label titleLabel = new Label("Level " + currentLevelNumber + " Clear!");
//        titleLabel.setFont(Font.font("System", 64));
//        titleLabel.setTextFill(Color.BLACK);

        javafx.scene.image.ImageView centerView = new javafx.scene.image.ImageView();
        centerView.setPreserveRatio(true);
        centerView.setFitWidth(800);

        final int[] idx = {0};
        Runnable updateCenter = () -> {
            if (idx[0] < 0 || idx[0] >= rewards.size()) return;
            Reward r = rewards.get(idx[0]);
            String path = switch (r.getName()) {
                case "Glasses" -> "/Page/Glasses.png";
                case "Mouse" -> "/Page/Mouse.png";
                case "Notebook" -> "/Page/Laptop.png";
                case "Backpack" -> "/Page/BagPack.png";
                case "ChatGPT Pro (3 months)" -> "/Page/ChaTgpt.png";
                default -> null;
            };
            if (path != null) {
                Image img = ImageLoader.loadImage(path, 800, 600);
                if (img != null) centerView.setImage(img);
            }
        };

        updateCenter.run();

        Button nextButton = new Button();
        Image nextImg = ImageLoader.loadImage("/Page/Next.png");
        if (nextImg != null) {
            javafx.scene.image.ImageView niv = new javafx.scene.image.ImageView(nextImg);
            niv.setPreserveRatio(true);
            niv.setFitWidth(300);
            nextButton.setGraphic(niv);
        } else {
            nextButton.setText("Next");
            nextButton.setFont(Font.font(24));
            nextButton.setPrefWidth(240);
        }

        nextButton.setOnAction(e -> {
            idx[0]++;
            if (idx[0] < rewards.size()) {
                updateCenter.run();
            } else {
                if (onNextLevel != null) onNextLevel.run();
            }
        });
        // hover scale effect
        nextButton.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(120), nextButton);
            st.setToX(1.08);
            st.setToY(1.08);
            st.playFromStart();
        });
        nextButton.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(120), nextButton);
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
        });

        HBox bottom = new HBox(nextButton);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(24));

        VBox root = new VBox(30, centerView, bottom);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #FFFFFF;");

        Scene scene = new Scene(root, 1200, 700);
        // Bind reward image size to scene width for responsiveness
        centerView.fitWidthProperty().bind(scene.widthProperty().multiply(0.6));
        return scene;
    }
}
