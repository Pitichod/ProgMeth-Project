package logic.game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * ทดสอบ enum LevelId ที่ใช้ระบุด่านทั้งหมดในเกม
 *
 * เหตุผล: LevelId เป็นตัวเชื่อมระหว่าง LevelConfig, GameEngine, GameSession
 * หากจำนวนหรือชื่อสมาชิกผิดจะกระทบ switch-expression ที่ใช้ทุกที่
 */
class LevelIdTest {

    @Test
    void shouldHaveExactlyFiveLevels() {
        // เกมมี 5 ด่าน ถ้ามีเพิ่ม/ลดต้อง review switch ใน LevelConfig, GameEngine
        assertEquals(5, LevelId.values().length);
    }

    @Test
    void valuesShouldBeInCorrectOrder() {
        // ลำดับ enum สำคัญเมื่อใช้ ordinal() หรือ iteration
        LevelId[] ids = LevelId.values();
        assertEquals(LevelId.ISCALE_401, ids[0]);
        assertEquals(LevelId.ISCALE_402, ids[1]);
        assertEquals(LevelId.ISCALE_403, ids[2]);
        assertEquals(LevelId.ISCALE_404, ids[3]);
        assertEquals(LevelId.ISCALE_405, ids[4]);
    }

    @Test
    void valueOfShouldReturnCorrectEnum() {
        // ตรวจการแปลงจากชื่อ String กลับเป็น enum (ใช้ในการ serialize)
        assertEquals(LevelId.ISCALE_401, LevelId.valueOf("ISCALE_401"));
        assertEquals(LevelId.ISCALE_405, LevelId.valueOf("ISCALE_405"));
    }
}
