package logic.game;

import static org.junit.jupiter.api.Assertions.*;

import objects.BaseObject;
import objects.Human;
import objects.obstacles.Cable;
import objects.items.BaseItem;
import org.junit.jupiter.api.Test;

/**
 * Tests GameBoard which is responsible for loading maps, detecting walls, and locating objects.
 *
 * Reason: GameBoard is the foundation of every level.
 * If map parsing is wrong, walls/objects/doors will be misplaced, making the game unplayable.
 */
class GameBoardTest {

    // --- Map Loading ---

    @Test
    void fromResourceShouldParseSimpleMap() {
        // test_simple map: 5x3, contains P and D
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertEquals(5, board.getWidth());
        assertEquals(3, board.getHeight());
    }

    @Test
    void fromResourceShouldThrowForMissingMap() {
        // Must throw when the map file is not found
        assertThrows(IllegalStateException.class,
                () -> GameBoard.fromResource("/maps/nonexistent.txt"));
    }

    // --- Starting Position ---

    @Test
    void playerStartPositionShouldMatchPSymbol() {
        // P in test_simple is at (1,1)
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertEquals(1, board.getPlayerStartX());
        assertEquals(1, board.getPlayerStartY());
    }

    @Test
    void doorPositionShouldMatchDSymbol() {
        // D in test_simple is at (3,1)
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertEquals(3, board.getDoorX());
        assertEquals(1, board.getDoorY());
    }

    // --- Walls ---

    @Test
    void wallsShouldBeDetected() {
        // Border tiles (#) must be detected as walls
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertTrue(board.isWall(0, 0));
        assertTrue(board.isWall(4, 2));
    }

    @Test
    void emptyTileShouldNotBeWall() {
        // A '.' tile must not be a wall
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertFalse(board.isWall(2, 1));
    }

    @Test
    void outOfBoundsShouldBeTreatedAsWall() {
        // Out-of-bounds positions must return true (prevent player from leaving the map)
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertTrue(board.isWall(-1, 0));
        assertTrue(board.isWall(0, -1));
        assertTrue(board.isWall(100, 0));
    }

    // --- isInBounds ---

    @Test
    void isInBoundsShouldReturnTrueForValidCoordinates() {
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertTrue(board.isInBounds(0, 0));
        assertTrue(board.isInBounds(4, 2));
    }

    @Test
    void isInBoundsShouldReturnFalseForInvalidCoordinates() {
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertFalse(board.isInBounds(-1, 0));
        assertFalse(board.isInBounds(5, 0));
        assertFalse(board.isInBounds(0, 3));
    }

    // --- isDoor ---

    @Test
    void isDoorShouldReturnTrueAtDoorPosition() {
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertTrue(board.isDoor(3, 1));
    }

    @Test
    void isDoorShouldReturnFalseElsewhere() {
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertFalse(board.isDoor(1, 1));
    }

    // --- Objects on the Board ---

    @Test
    void obstacleMapShouldParseChair() {
        // test_obstacles has 'C' at (3,1) -> Chair
        GameBoard board = GameBoard.fromResource("/maps/test_obstacles.txt");
        BaseObject chair = board.findMoveableObstacleAt(3, 1);
        assertNotNull(chair);
        assertEquals("Chair", chair.getName());
    }

    @Test
    void cableMapShouldParseCable() {
        // test_obstacles has 'W' at (5,1) -> Cable
        GameBoard board = GameBoard.fromResource("/maps/test_obstacles.txt");
        Cable cable = board.findCableAt(5, 1);
        assertNotNull(cable);
    }

    @Test
    void itemMapShouldParseAllThreeItems() {
        // test_items has 1(Parabola), 2(Caffeine), 3(RobuxGiftCard)
        GameBoard board = GameBoard.fromResource("/maps/test_items.txt");
        assertNotNull(board.findItemAt(2, 1)); // Parabola
        assertNotNull(board.findItemAt(3, 1)); // Caffeine
        assertNotNull(board.findItemAt(4, 1)); // RobuxGiftCard
        assertEquals(3, board.getItems().size());
    }

    @Test
    void enemyMapShouldParseExtrovert() {
        // test_enemies has 'E' at (3,1)
        GameBoard board = GameBoard.fromResource("/maps/test_enemies.txt");
        Human enemy = board.findHumanAt(3, 1);
        assertNotNull(enemy);
        assertEquals("Extrovert", enemy.getName());
    }

    // --- isBlockingCell ---

    @Test
    void blockingCellShouldIncludeWallAndObstacle() {
        GameBoard board = GameBoard.fromResource("/maps/test_obstacles.txt");
        assertTrue(board.isBlockingCell(0, 0));  // wall
        assertTrue(board.isBlockingCell(3, 1));  // chair
    }

    @Test
    void emptyFloorShouldNotBeBlocking() {
        GameBoard board = GameBoard.fromResource("/maps/test_obstacles.txt");
        assertFalse(board.isBlockingCell(2, 1));  // empty tile
    }

    // --- find methods return null when empty ---

    @Test
    void findMethodsShouldReturnNullWhenNothingAtPosition() {
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertNull(board.findMoveableObstacleAt(2, 1));
        assertNull(board.findCableAt(2, 1));
        assertNull(board.findItemAt(2, 1));
        assertNull(board.findHumanAt(2, 1));
    }
}
