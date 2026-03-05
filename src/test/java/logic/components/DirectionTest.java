package logic.components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * ทดสอบ enum Direction ซึ่งเป็นหน่วยพื้นฐานที่สุดของระบบ logic
 * ทุกการเคลื่อนที่ในเกมพึ่งพา dx, dy ของ Direction
 * หากค่าเหล่านี้ผิดจะกระทบ Player, GameEngine, และ Obstacle ทั้งหมด
 */
class DirectionTest {

    // --- ตรวจค่า delta ของแต่ละทิศทาง ---

    @Test
    void upShouldHaveDxZeroAndDyNegativeOne() {
        // UP ต้อง dx=0, dy=-1 เพราะแกน Y ในเกมเพิ่มลงล่าง
        assertEquals(0, Direction.UP.getDx());
        assertEquals(-1, Direction.UP.getDy());
    }

    @Test
    void downShouldHaveDxZeroAndDyPositiveOne() {
        // DOWN ต้อง dx=0, dy=+1
        assertEquals(0, Direction.DOWN.getDx());
        assertEquals(1, Direction.DOWN.getDy());
    }

    @Test
    void leftShouldHaveDxNegativeOneAndDyZero() {
        // LEFT ต้อง dx=-1, dy=0
        assertEquals(-1, Direction.LEFT.getDx());
        assertEquals(0, Direction.LEFT.getDy());
    }

    @Test
    void rightShouldHaveDxPositiveOneAndDyZero() {
        // RIGHT ต้อง dx=+1, dy=0
        assertEquals(1, Direction.RIGHT.getDx());
        assertEquals(0, Direction.RIGHT.getDy());
    }

    // --- ตรวจจำนวนสมาชิกของ enum ---

    @Test
    void shouldHaveExactlyFourDirections() {
        // เกมรองรับ 4 ทิศเท่านั้น ถ้ามีเพิ่มต้อง review logic ทั้งระบบ
        assertEquals(4, Direction.values().length);
    }
}
