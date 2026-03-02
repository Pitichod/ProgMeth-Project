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
    private final int defaultHealth;
    private final Reward reward;

    public LevelConfig(LevelId levelId, int defaultHealth, int defaultStamina, Reward reward) {
        this.levelId = levelId;
        this.defaultHealth = defaultHealth;
        this.defaultStamina = defaultStamina;
        this.reward = reward;
    }

    public LevelId getLevelId() {
        return levelId;
    }

    public int getDefaultStamina() {
        return defaultStamina;
    }

    public int getDefaultHealth() {
        return defaultHealth;
    }

    public Reward getReward() {
        return reward;
    }

    public static LevelConfig fromLevel(LevelId levelId) {
        return switch (levelId) {
            // Default stamina per original tests: 12,10,9,8,7 (health values kept but not used by tests)
            case ISCALE_401 -> new LevelConfig(levelId, 10, 15, new Glasses());
            case ISCALE_402 -> new LevelConfig(levelId, 5, 15, new Notebook());
            case ISCALE_403 -> new LevelConfig(levelId, 5, 20, new Mouse());
            case ISCALE_404 -> new LevelConfig(levelId, 5, 15, new Backpack());
            case ISCALE_405 -> new LevelConfig(levelId, 10, 100, new ChatGPTPro());
        };
    }
}