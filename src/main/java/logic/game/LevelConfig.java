package logic.game;

import rewards.Backpack;
import rewards.ChatGPTPro;
import rewards.Glasses;
import rewards.Mouse;
import rewards.Notebook;
import rewards.Reward;

/**
 * Immutable configuration for a single game level.
 * Holds the level identifier, default player stats, and the reward
 * granted upon completion. Use {@link #fromLevel(LevelId)} to obtain
 * the predefined configuration for each level.
 */
public class LevelConfig {
    /** The level identifier. */
    private final LevelId levelId;
    /** The default stamina the player starts with on this level. */
    private final int defaultStamina;
    /** The default health the player starts with on this level. */
    private final int defaultHealth;
    /** The reward granted when this level is completed. */
    private final Reward reward;

    /**
     * Constructs a LevelConfig with the given parameters.
     *
     * @param levelId        the level identifier
     * @param defaultHealth  the player's starting health
     * @param defaultStamina the player's starting stamina
     * @param reward         the reward for completing this level
     */
    public LevelConfig(LevelId levelId, int defaultHealth, int defaultStamina, Reward reward) {
        this.levelId = levelId;
        this.defaultHealth = defaultHealth;
        this.defaultStamina = defaultStamina;
        this.reward = reward;
    }

    /**
     * Returns the level identifier.
     *
     * @return the {@link LevelId}
     */
    public LevelId getLevelId() {
        return levelId;
    }

    /**
     * Returns the default stamina for this level.
     *
     * @return the starting stamina
     */
    public int getDefaultStamina() {
        return defaultStamina;
    }

    /**
     * Returns the default health for this level.
     *
     * @return the starting health
     */
    public int getDefaultHealth() {
        return defaultHealth;
    }

    /**
     * Returns the reward for completing this level.
     *
     * @return the {@link Reward}
     */
    public Reward getReward() {
        return reward;
    }

    /**
     * Factory method that returns the predefined configuration for the given level.
     *
     * @param levelId the level to get the configuration for
     * @return the corresponding {@link LevelConfig}
     */
    public static LevelConfig fromLevel(LevelId levelId) {
        return switch (levelId) {
            case ISCALE_401 -> new LevelConfig(levelId, 10, 15, new Glasses());
            case ISCALE_402 -> new LevelConfig(levelId, 5, 15, new Notebook());
            case ISCALE_403 -> new LevelConfig(levelId, 5, 20, new Mouse());
            case ISCALE_404 -> new LevelConfig(levelId, 5, 15, new Backpack());
            case ISCALE_405 -> new LevelConfig(levelId, 10, 100, new ChatGPTPro());
        };
    }
}