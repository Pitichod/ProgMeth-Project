package logic.game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import rewards.Reward;

/**
 * Tests GameSession which manages player state across levels.
 * Covers: startLevel, completeCurrentLevel, collectedRewards, hasFinishedAllLevels.
 *
 * Reason: GameSession is the bridge between LevelConfig and Player,
 * and stores accumulated rewards. If wrong, rewards may be lost
 * or starting HP/Stamina may be incorrect.
 */
class GameSessionTest {

    // --- startLevel ---

    @Test
    void startLevelShouldCreatePlayerWithConfigHealth() {
        // Verify startLevel uses HP from LevelConfig, not a hardcoded value
        GameSession session = new GameSession();
        session.startLevel(LevelId.ISCALE_401);
        assertEquals(10, session.getPlayer().getHealth());
        assertEquals(10, session.getPlayer().getMaxHealth());
    }

    @Test
    void startLevelShouldCreatePlayerWithConfigStamina() {
        // Verify starting stamina matches the level config
        GameSession session = new GameSession();
        session.startLevel(LevelId.ISCALE_403);
        assertEquals(20, session.getPlayer().getStamina());
    }

    @Test
    void startLevelShouldResetPlayerOnRestart() {
        // Restarting a level must fully reset player state
        GameSession session = new GameSession();
        session.startLevel(LevelId.ISCALE_402);
        session.getPlayer().takeDamage(3);

        session.startLevel(LevelId.ISCALE_402);
        assertEquals(5, session.getPlayer().getHealth()); // HP restored to full
    }

    @Test
    void startLevelShouldSetCurrentLevelConfig() {
        GameSession session = new GameSession();
        session.startLevel(LevelId.ISCALE_404);
        assertNotNull(session.getCurrentLevel());
        assertEquals(LevelId.ISCALE_404, session.getCurrentLevel().getLevelId());
    }

    // --- completeCurrentLevel ---

    @Test
    void completeCurrentLevelShouldReturnReward() {
        // Completing a level successfully must yield a reward
        GameSession session = new GameSession();
        session.startLevel(LevelId.ISCALE_401);

        Reward reward = session.completeCurrentLevel();
        assertNotNull(reward);
    }

    @Test
    void completeCurrentLevelShouldReturnNullWhenPlayerDead() {
        // A dead player must not receive a reward
        GameSession session = new GameSession();
        session.startLevel(LevelId.ISCALE_401);
        session.getPlayer().setHealth(0);

        assertNull(session.completeCurrentLevel());
    }

    @Test
    void completeCurrentLevelShouldAccumulateRewards() {
        // Rewards must accumulate across levels
        GameSession session = new GameSession();

        session.startLevel(LevelId.ISCALE_401);
        session.completeCurrentLevel();

        session.startLevel(LevelId.ISCALE_402);
        session.completeCurrentLevel();

        assertEquals(2, session.getCollectedRewards().size());
    }

    // --- hasFinishedAllLevels ---

    @Test
    void hasFinishedAllLevelsShouldBeFalseInitially() {
        GameSession session = new GameSession();
        assertFalse(session.hasFinishedAllLevels());
    }

    @Test
    void hasFinishedAllLevelsShouldBeTrueAfterFiveLevels() {
        // Must return true after collecting rewards from all 5 levels
        GameSession session = new GameSession();
        for (LevelId id : LevelId.values()) {
            session.startLevel(id);
            session.completeCurrentLevel();
        }
        assertTrue(session.hasFinishedAllLevels());
    }

    // --- collectedRewards immutability ---

    @Test
    void collectedRewardsShouldBeUnmodifiable() {
        // Prevent external modification of the reward list
        GameSession session = new GameSession();
        session.startLevel(LevelId.ISCALE_401);
        session.completeCurrentLevel();

        List<Reward> rewards = session.getCollectedRewards();
        assertThrows(UnsupportedOperationException.class, () -> rewards.clear());
    }
}
