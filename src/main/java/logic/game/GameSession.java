package logic.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rewards.Reward;

public class GameSession {
    private final List<Reward> collectedRewards = new ArrayList<>();
    private Player player;
    private LevelConfig currentLevel;

    public void startLevel(LevelId levelId) {
        currentLevel = LevelConfig.fromLevel(levelId);
        player = new Player(5, currentLevel.getDefaultStamina(), 0, 0);
    }

    public Player getPlayer() {
        return player;
    }

    public LevelConfig getCurrentLevel() {
        return currentLevel;
    }

    public Reward completeCurrentLevel() {
        if (currentLevel == null || player == null || player.getHealth() <= 0) {
            return null;
        }
        Reward reward = currentLevel.getReward();
        collectedRewards.add(reward);
        return reward;
    }

    public List<Reward> getCollectedRewards() {
        return Collections.unmodifiableList(collectedRewards);
    }

    public boolean hasFinishedAllLevels() {
        return collectedRewards.size() >= 5;
    }
}