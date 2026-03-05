package logic.components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the Direction enum, which is the most fundamental unit in the logic layer.
 * Every movement in the game depends on dx/dy values from Direction.
 * If these values are wrong, it will affect Player, GameEngine, and all Obstacles.
 */
class DirectionTest {

    // --- Verify delta values for each direction ---

    @Test
    void upShouldHaveDxZeroAndDyNegativeOne() {
        // UP must have dx=0, dy=-1 because the Y-axis increases downward in the game grid
        assertEquals(0, Direction.UP.getDx());
        assertEquals(-1, Direction.UP.getDy());
    }

    @Test
    void downShouldHaveDxZeroAndDyPositiveOne() {
        // DOWN must have dx=0, dy=+1
        assertEquals(0, Direction.DOWN.getDx());
        assertEquals(1, Direction.DOWN.getDy());
    }

    @Test
    void leftShouldHaveDxNegativeOneAndDyZero() {
        // LEFT must have dx=-1, dy=0
        assertEquals(-1, Direction.LEFT.getDx());
        assertEquals(0, Direction.LEFT.getDy());
    }

    @Test
    void rightShouldHaveDxPositiveOneAndDyZero() {
        // RIGHT must have dx=+1, dy=0
        assertEquals(1, Direction.RIGHT.getDx());
        assertEquals(0, Direction.RIGHT.getDy());
    }

    // --- Verify enum member count ---

    @Test
    void shouldHaveExactlyFourDirections() {
        // The game only supports 4 directions; adding more requires a full logic review
        assertEquals(4, Direction.values().length);
    }
}
