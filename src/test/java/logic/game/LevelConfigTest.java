package logic.game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import rewards.*;

/**
 * ทดสอบ LevelConfig ที่กำหนดค่าเริ่มต้น (HP, Stamina, Reward) ของแต่ละด่าน
 *
 * เหตุผล: ค่าเหล่านี้กระทบ game balance โดยตรง
 * การเปลี่ยน config ผิดจะทำให้ด่านง่ายหรือยากเกินไป
 * หรือให้รางวัลผิดตัวเมื่อผ่านด่าน
 */
class LevelConfigTest {

    // --- ตรวจ config ของแต่ละด่าน ---

    @Test
    void level401ShouldHaveCorrectConfig() {
        // ด่าน 1: HP 10, Stamina 15, รางวัล Glasses
        LevelConfig cfg = LevelConfig.fromLevel(LevelId.ISCALE_401);
        assertEquals(LevelId.ISCALE_401, cfg.getLevelId());
        assertEquals(10, cfg.getDefaultHealth());
        assertEquals(15, cfg.getDefaultStamina());
        assertInstanceOf(Glasses.class, cfg.getReward());
    }

    @Test
    void level402ShouldHaveCorrectConfig() {
        // ด่าน 2: HP 5, Stamina 15, รางวัล Notebook
        LevelConfig cfg = LevelConfig.fromLevel(LevelId.ISCALE_402);
        assertEquals(5, cfg.getDefaultHealth());
        assertEquals(15, cfg.getDefaultStamina());
        assertInstanceOf(Notebook.class, cfg.getReward());
    }

    @Test
    void level403ShouldHaveCorrectConfig() {
        // ด่าน 3: HP 5, Stamina 20, รางวัล Mouse
        LevelConfig cfg = LevelConfig.fromLevel(LevelId.ISCALE_403);
        assertEquals(5, cfg.getDefaultHealth());
        assertEquals(20, cfg.getDefaultStamina());
        assertInstanceOf(Mouse.class, cfg.getReward());
    }

    @Test
    void level404ShouldHaveCorrectConfig() {
        // ด่าน 4: HP 5, Stamina 15, รางวัล Backpack
        LevelConfig cfg = LevelConfig.fromLevel(LevelId.ISCALE_404);
        assertEquals(5, cfg.getDefaultHealth());
        assertEquals(15, cfg.getDefaultStamina());
        assertInstanceOf(Backpack.class, cfg.getReward());
    }

    @Test
    void level405ShouldHaveCorrectConfig() {
        // ด่าน 5: HP 10, Stamina 100, รางวัล ChatGPTPro (ด่านสุดท้ายให้ stamina มากสุด)
        LevelConfig cfg = LevelConfig.fromLevel(LevelId.ISCALE_405);
        assertEquals(10, cfg.getDefaultHealth());
        assertEquals(100, cfg.getDefaultStamina());
        assertInstanceOf(ChatGPTPro.class, cfg.getReward());
    }

    // --- ตรวจ getter ทั่วไป ---

    @Test
    void customConstructorShouldSetAllFields() {
        // ตรวจว่า constructor กำหนดค่าได้ถูกสำหรับกรณี custom
        Glasses reward = new Glasses();
        LevelConfig cfg = new LevelConfig(LevelId.ISCALE_401, 7, 12, reward);
        assertEquals(LevelId.ISCALE_401, cfg.getLevelId());
        assertEquals(7, cfg.getDefaultHealth());
        assertEquals(12, cfg.getDefaultStamina());
        assertSame(reward, cfg.getReward());
    }
}
