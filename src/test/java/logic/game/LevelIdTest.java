package logic.game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests the LevelId enum that identifies all levels in the game.
 *
 * Reason: LevelId is the link between LevelConfig, GameEngine, and GameSession.
 * If the member count or names are wrong, switch-expressions used everywhere will break.
 */
class LevelIdTest {

    @Test
    void shouldHaveExactlyFiveLevels() {
        // The game has 5 levels; adding/removing requires reviewing switch in LevelConfig and GameEngine
        assertEquals(5, LevelId.values().length);
    }

    @Test
    void valuesShouldBeInCorrectOrder() {
        // Enum order matters when using ordinal() or iteration
        LevelId[] ids = LevelId.values();
        assertEquals(LevelId.ISCALE_401, ids[0]);
        assertEquals(LevelId.ISCALE_402, ids[1]);
        assertEquals(LevelId.ISCALE_403, ids[2]);
        assertEquals(LevelId.ISCALE_404, ids[3]);
        assertEquals(LevelId.ISCALE_405, ids[4]);
    }

    @Test
    void valueOfShouldReturnCorrectEnum() {
        // Verify String-to-enum conversion (used for serialization)
        assertEquals(LevelId.ISCALE_401, LevelId.valueOf("ISCALE_401"));
        assertEquals(LevelId.ISCALE_405, LevelId.valueOf("ISCALE_405"));
    }
}
