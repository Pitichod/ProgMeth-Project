package logic.game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import rewards.*;

/**
 * Tests LevelConfig which defines the initial values (HP, Stamina, Reward) for each level.
 *
 * Reason: These values directly affect game balance.
 * A wrong config could make a level too easy or too hard,
 * or award the wrong reward upon completion.
 */
class LevelConfigTest {

    // --- Verify config for each level ---

    @Test
    void level401ShouldHaveCorrectConfig() {
        // Level 1: HP 10, Stamina 15, reward Glasses
        LevelConfig cfg = LevelConfig.fromLevel(LevelId.ISCALE_401);
        assertEquals(LevelId.ISCALE_401, cfg.getLevelId());
        assertEquals(10, cfg.getDefaultHealth());
        assertEquals(15, cfg.getDefaultStamina());
        assertInstanceOf(Glasses.class, cfg.getReward());
    }

    @Test
    void level402ShouldHaveCorrectConfig() {
        // Level 2: HP 5, Stamina 15, reward Notebook
        LevelConfig cfg = LevelConfig.fromLevel(LevelId.ISCALE_402);
        assertEquals(5, cfg.getDefaultHealth());
        assertEquals(15, cfg.getDefaultStamina());
        assertInstanceOf(Notebook.class, cfg.getReward());
    }

    @Test
    void level403ShouldHaveCorrectConfig() {
        // Level 3: HP 5, Stamina 20, reward Mouse
        LevelConfig cfg = LevelConfig.fromLevel(LevelId.ISCALE_403);
        assertEquals(5, cfg.getDefaultHealth());
        assertEquals(20, cfg.getDefaultStamina());
        assertInstanceOf(Mouse.class, cfg.getReward());
    }

    @Test
    void level404ShouldHaveCorrectConfig() {
        // Level 4: HP 5, Stamina 15, reward Backpack
        LevelConfig cfg = LevelConfig.fromLevel(LevelId.ISCALE_404);
        assertEquals(5, cfg.getDefaultHealth());
        assertEquals(15, cfg.getDefaultStamina());
        assertInstanceOf(Backpack.class, cfg.getReward());
    }

    @Test
    void level405ShouldHaveCorrectConfig() {
        // Level 5: HP 10, Stamina 100, reward ChatGPTPro (final level grants max stamina)
        LevelConfig cfg = LevelConfig.fromLevel(LevelId.ISCALE_405);
        assertEquals(10, cfg.getDefaultHealth());
        assertEquals(100, cfg.getDefaultStamina());
        assertInstanceOf(ChatGPTPro.class, cfg.getReward());
    }

    // --- Verify general getters ---

    @Test
    void customConstructorShouldSetAllFields() {
        // Verify that the constructor sets all fields correctly for custom configs
        Glasses reward = new Glasses();
        LevelConfig cfg = new LevelConfig(LevelId.ISCALE_401, 7, 12, reward);
        assertEquals(LevelId.ISCALE_401, cfg.getLevelId());
        assertEquals(7, cfg.getDefaultHealth());
        assertEquals(12, cfg.getDefaultStamina());
        assertSame(reward, cfg.getReward());
    }
}
