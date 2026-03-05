package logic.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rewards.Reward;

/**
 * Manages the overall game session state across multiple levels.
 * Keeps track of the current level, the player instance, and all rewards
 * collected so far. A session is considered complete once all five levels
 * have been finished.
 */
public class GameSession {
    /** Rewards collected from completed levels. */
    private final List<Reward> collectedRewards = new ArrayList<>();
    /** The player for the current level (recreated on each level start). */
    private Player player;
    /** The configuration of the current level. */
    private LevelConfig currentLevel;

    /**
     * Starts (or restarts) the given level by creating a new player with
     * the level's default health and stamina.
     *
     * @param levelId the level to start
     */
    public void startLevel(LevelId levelId) {
        currentLevel = LevelConfig.fromLevel(levelId);
        player = new Player(currentLevel.getDefaultHealth(), currentLevel.getDefaultStamina(), 0, 0);
    }

    /**
     * Returns the player for the current level.
     *
     * @return the current {@link Player}, or {@code null} if no level has been started
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the configuration of the current level.
     *
     * @return the current {@link LevelConfig}, or {@code null} if no level has been started
     */
    public LevelConfig getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Marks the current level as completed and collects its reward.
     * Returns {@code null} if no level is active or the player is dead.
     *
     * @return the {@link Reward} for this level, or {@code null} if completion is invalid
     */
    public Reward completeCurrentLevel() {
        if (currentLevel == null || player == null || player.getHealth() <= 0) {
            return null;
        }
        Reward reward = currentLevel.getReward();
        collectedRewards.add(reward);
        return reward;
    }

    /**
     * Returns an unmodifiable view of all rewards collected so far.
     *
     * @return an unmodifiable list of {@link Reward} objects
     */
    public List<Reward> getCollectedRewards() {
        return Collections.unmodifiableList(collectedRewards);
    }

    /**
     * Returns whether the player has completed all five levels.
     *
     * @return {@code true} if all levels are finished
     */
    public boolean hasFinishedAllLevels() {
        return collectedRewards.size() >= 5;
    }
}