package logic.game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import rewards.Reward;

/**
 * ทดสอบ GameSession ที่จัดการสถานะผู้เล่นข้ามด่าน
 * ครอบคลุม: startLevel, completeCurrentLevel, collectedRewards, hasFinishedAllLevels
 *
 * เหตุผล: GameSession เป็นตัวเชื่อมระหว่าง level config กับ Player
 * และเก็บรางวัลสะสม ถ้าผิดจะเสียของรางวัลหรือ HP/Stamina เริ่มต้นผิด
 */
class GameSessionTest {

    // --- startLevel ---

    @Test
    void startLevelShouldCreatePlayerWithConfigHealth() {
        // ตรวจว่า startLevel ใช้ HP จาก LevelConfig ไม่ใช่ค่า hardcode
        GameSession session = new GameSession();
        session.startLevel(LevelId.ISCALE_401);
        assertEquals(10, session.getPlayer().getHealth());
        assertEquals(10, session.getPlayer().getMaxHealth());
    }

    @Test
    void startLevelShouldCreatePlayerWithConfigStamina() {
        // ตรวจ stamina เริ่มต้นตรงกับ config
        GameSession session = new GameSession();
        session.startLevel(LevelId.ISCALE_403);
        assertEquals(20, session.getPlayer().getStamina());
    }

    @Test
    void startLevelShouldResetPlayerOnRestart() {
        // เริ่มด่านใหม่ต้องรีเซ็ตสถานะผู้เล่นทั้งหมด
        GameSession session = new GameSession();
        session.startLevel(LevelId.ISCALE_402);
        session.getPlayer().takeDamage(3);

        session.startLevel(LevelId.ISCALE_402);
        assertEquals(5, session.getPlayer().getHealth()); // HP กลับเต็ม
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
        // ผ่านด่านสำเร็จต้องได้รางวัล
        GameSession session = new GameSession();
        session.startLevel(LevelId.ISCALE_401);

        Reward reward = session.completeCurrentLevel();
        assertNotNull(reward);
    }

    @Test
    void completeCurrentLevelShouldReturnNullWhenPlayerDead() {
        // ผู้เล่นตายต้องไม่ได้รางวัล
        GameSession session = new GameSession();
        session.startLevel(LevelId.ISCALE_401);
        session.getPlayer().setHealth(0);

        assertNull(session.completeCurrentLevel());
    }

    @Test
    void completeCurrentLevelShouldAccumulateRewards() {
        // รางวัลต้องสะสมข้ามด่าน
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
        // ต้อง return true เมื่อสะสมรางวัลครบ 5 ด่าน
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
        // ป้องกันการแก้ไข list จากภายนอก
        GameSession session = new GameSession();
        session.startLevel(LevelId.ISCALE_401);
        session.completeCurrentLevel();

        List<Reward> rewards = session.getCollectedRewards();
        assertThrows(UnsupportedOperationException.class, () -> rewards.clear());
    }
}
