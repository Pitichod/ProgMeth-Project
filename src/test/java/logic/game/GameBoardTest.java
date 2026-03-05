package logic.game;

import static org.junit.jupiter.api.Assertions.*;

import objects.BaseObject;
import objects.Human;
import objects.obstacles.Cable;
import objects.items.BaseItem;
import org.junit.jupiter.api.Test;

/**
 * ทดสอบ GameBoard ที่รับผิดชอบการโหลดแผนที่, ตรวจกำแพง, หาวัตถุบนกระดาน
 *
 * เหตุผล: GameBoard เป็นรากฐานของทุกด่าน
 * ถ้า parse map ผิด จะวางกำแพง/วัตถุ/ประตูผิดตำแหน่ง ทำเกมเล่นไม่ได้
 */
class GameBoardTest {

    // --- โหลดแผนที่ ---

    @Test
    void fromResourceShouldParseSimpleMap() {
        // แผนที่ test_simple: 5x3, มี P และ D
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertEquals(5, board.getWidth());
        assertEquals(3, board.getHeight());
    }

    @Test
    void fromResourceShouldThrowForMissingMap() {
        // ต้อง throw เมื่อไม่พบไฟล์แผนที่
        assertThrows(IllegalStateException.class,
                () -> GameBoard.fromResource("/maps/nonexistent.txt"));
    }

    // --- ตำแหน่งเริ่มต้น ---

    @Test
    void playerStartPositionShouldMatchPSymbol() {
        // P ใน test_simple อยู่ที่ (1,1)
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertEquals(1, board.getPlayerStartX());
        assertEquals(1, board.getPlayerStartY());
    }

    @Test
    void doorPositionShouldMatchDSymbol() {
        // D ใน test_simple อยู่ที่ (3,1)
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertEquals(3, board.getDoorX());
        assertEquals(1, board.getDoorY());
    }

    // --- กำแพง ---

    @Test
    void wallsShouldBeDetected() {
        // ขอบแผนที่ (#) ต้องเป็นกำแพง
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertTrue(board.isWall(0, 0));
        assertTrue(board.isWall(4, 2));
    }

    @Test
    void emptyTileShouldNotBeWall() {
        // ช่อง . ต้องไม่ใช่กำแพง
        GameBoard board = GameBoard.fromResource("/maps/test_simple.txt");
        assertFalse(board.isWall(2, 1));
    }

    @Test
    void outOfBoundsShouldBeTreatedAsWall() {
        // ตำแหน่งนอกแผนที่ต้อง return true (กันผู้เล่นออกนอก)
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

    // --- วัตถุบนกระดาน ---

    @Test
    void obstacleMapShouldParseChair() {
        // test_obstacles มี C ที่ (3,1) → Chair
        GameBoard board = GameBoard.fromResource("/maps/test_obstacles.txt");
        BaseObject chair = board.findMoveableObstacleAt(3, 1);
        assertNotNull(chair);
        assertEquals("Chair", chair.getName());
    }

    @Test
    void cableMapShouldParseCable() {
        // test_obstacles มี W ที่ (5,1) → Cable
        GameBoard board = GameBoard.fromResource("/maps/test_obstacles.txt");
        Cable cable = board.findCableAt(5, 1);
        assertNotNull(cable);
    }

    @Test
    void itemMapShouldParseAllThreeItems() {
        // test_items มี 1(Parabola), 2(Caffeine), 3(RobuxGiftCard)
        GameBoard board = GameBoard.fromResource("/maps/test_items.txt");
        assertNotNull(board.findItemAt(2, 1)); // Parabola
        assertNotNull(board.findItemAt(3, 1)); // Caffeine
        assertNotNull(board.findItemAt(4, 1)); // RobuxGiftCard
        assertEquals(3, board.getItems().size());
    }

    @Test
    void enemyMapShouldParseExtrovert() {
        // test_enemies มี E ที่ (3,1)
        GameBoard board = GameBoard.fromResource("/maps/test_enemies.txt");
        Human enemy = board.findHumanAt(3, 1);
        assertNotNull(enemy);
        assertEquals("Extrovert", enemy.getName());
    }

    // --- isBlockingCell ---

    @Test
    void blockingCellShouldIncludeWallAndObstacle() {
        GameBoard board = GameBoard.fromResource("/maps/test_obstacles.txt");
        assertTrue(board.isBlockingCell(0, 0));  // กำแพง
        assertTrue(board.isBlockingCell(3, 1));  // เก้าอี้
    }

    @Test
    void emptyFloorShouldNotBeBlocking() {
        GameBoard board = GameBoard.fromResource("/maps/test_obstacles.txt");
        assertFalse(board.isBlockingCell(2, 1));  // ช่องว่าง
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
