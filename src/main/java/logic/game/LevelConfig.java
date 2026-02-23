package logic.game;

import rewards.Backpack;
import rewards.ChatGPTPro;
import rewards.Glasses;
import rewards.Mouse;
import rewards.Notebook;
import rewards.Reward;

public class LevelConfig {
    private final LevelId levelId;
    private final int defaultStamina;
    private final Reward reward;

    public LevelConfig(LevelId levelId, int defaultStamina, Reward reward) {
        this.levelId = levelId;
        this.defaultStamina = defaultStamina;
        this.reward = reward;
    }

    public LevelId getLevelId() {
        return levelId;
    }

    public int getDefaultStamina() {
        return defaultStamina;
    }

    public Reward getReward() {
        return reward;
    }

    public static LevelConfig fromLevel(LevelId levelId) {
        return switch (levelId) {
            case ISCALE_401 -> new LevelConfig(levelId, 12, new Glasses());
            case ISCALE_402 -> new LevelConfig(levelId, 10, new Mouse());
            case ISCALE_403 -> new LevelConfig(levelId, 9, new Notebook());
            case ISCALE_404 -> new LevelConfig(levelId, 8, new Backpack());
            case ISCALE_405 -> new LevelConfig(levelId, 7, new ChatGPTPro());
        };
    }
}