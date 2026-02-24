package gui;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
    private Runnable onMainMenu;

    public RewardScreen(Stage stage, List<Reward> rewards, int currentLevelNumber) {
        this.stage = stage;
        this.rewards = rewards;
        this.currentLevelNumber = currentLevelNumber;
    }

    public void setOnNextLevel(Runnable callback) {
        this.onNextLevel = callback;
    }

    public void setOnMainMenu(Runnable callback) {
        this.onMainMenu = callback;
    }

    public Scene createScene() {
        Label titleLabel = new Label("Level " + currentLevelNumber + " Clear!");
        titleLabel.setFont(Font.font("System", 64));
        titleLabel.setTextFill(Color.WHITE);

        VBox rewardBox = new VBox(20);
        rewardBox.setAlignment(Pos.CENTER);
        rewardBox.setPadding(new Insets(30));

        Label rewardTitleLabel = new Label("Rewards Unlocked:");
        rewardTitleLabel.setFont(Font.font("System", 32));
        rewardTitleLabel.setTextFill(Color.WHITE);

        HBox rewardImagesBox = new HBox(20);
        rewardImagesBox.setAlignment(Pos.CENTER);
        rewardImagesBox.setPadding(new Insets(20));

        for (Reward reward : rewards) {
            VBox rewardItem = createRewardItem(reward);
            rewardImagesBox.getChildren().add(rewardItem);
        }

        rewardBox.getChildren().addAll(rewardTitleLabel, rewardImagesBox);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));

        Button nextButton = new Button(currentLevelNumber < 5 ? "Next Level" : "Final Reward!");
        nextButton.setFont(Font.font(32));
        nextButton.setPrefWidth(300);
        nextButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-background-radius: 20; -fx-border-color: white; -fx-border-width: 2;");
        nextButton.setOnAction(e -> {
            if (onNextLevel != null) {
                onNextLevel.run();
            }
        });

        Button menuButton = new Button("Main Menu");
        menuButton.setFont(Font.font(32));
        menuButton.setPrefWidth(300);
        menuButton.setTextFill(Color.WHITE);
        menuButton.setStyle("-fx-background-color: #000000; -fx-background-radius: 20; -fx-border-color: white; -fx-border-width: 2;");
        menuButton.setOnAction(e -> {
            if (onMainMenu != null) {
                onMainMenu.run();
            }
        });

        buttonBox.getChildren().addAll(nextButton, menuButton);

        VBox root = new VBox(40, titleLabel, rewardBox, buttonBox);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #000000;");

        return new Scene(root, 1200, 700);
    }

    private VBox createRewardItem(Reward reward) {
        String imagePath = switch (reward.getName()) {
            case "Glasses" -> "/images/rewards/Glasses.png";
            case "Mouse" -> "/images/rewards/Computer.png";
            case "Notebook" -> "/images/rewards/Computer.png";
            case "Backpack" -> "/images/rewards/Bagpack.png";
            case "ChatGPT Pro (3 months)" -> "/images/rewards/Computer.png";
            default -> null;
        };

        StackPane imageBox = new StackPane();
        imageBox.setPrefSize(120, 120);
        imageBox.setStyle("-fx-border-color: white; -fx-border-width: 3; -fx-background-color: #000000; -fx-border-radius: 10;");

        if (imagePath != null) {
            Image rewardImage = ImageLoader.loadImage(imagePath, 120, 120);
            if (rewardImage != null) {
                imageBox.getChildren().add(new javafx.scene.image.ImageView(rewardImage));
            } else {
                Label fallback = new Label(reward.getName().substring(0, 1));
                fallback.setFont(Font.font(32));
                fallback.setTextFill(Color.WHITE);
                imageBox.getChildren().add(fallback);
            }
        }

        Label rewardName = new Label(reward.getName());
        rewardName.setFont(Font.font(18));
        rewardName.setTextFill(Color.WHITE);
        rewardName.setWrapText(true);
        rewardName.setMaxWidth(140);
        rewardName.setAlignment(Pos.CENTER);

        VBox item = new VBox(10, imageBox, rewardName);
        item.setAlignment(Pos.CENTER);
        return item;
    }
}
