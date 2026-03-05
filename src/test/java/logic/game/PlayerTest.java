package logic.game;

import static org.junit.jupiter.api.Assertions.*;

import logic.components.Direction;
import org.junit.jupiter.api.Test;

/**
 * ทดสอบ Player ซึ่งเป็นตัวละครหลักที่ผู้เล่นควบคุม
 * ครอบคลุม: การสร้าง, stamina, health, damage, heal, attack, direction
 *
 * เหตุผล: Player เป็นแกนกลางของ logic ทั้งหมด
 * ถ้า setter/getter ผิดจะกระทบ GameEngine, items, enemies ทุกตัว
 */
class PlayerTest {

    // === การสร้าง Player ===

    @Test
    void constructorShouldSetMaxHealthAndStamina() {
        // ตรวจว่า constructor ตั้งค่าเริ่มต้นถูกต้อง
        Player p = new Player(5, 10, 2, 3);
        assertEquals(5, p.getMaxHealth());
        assertEquals(5, p.getHealth());
        assertEquals(10, p.getStamina());
        assertEquals(2, p.getX());
        assertEquals(3, p.getY());
    }

    @Test
    void constructorShouldClampMaxHealthToAtLeastOne() {
        // maxHealth ≤ 0 ต้องถูก clamp เป็น 1 ป้องกัน division-by-zero ใน HUD
        Player p = new Player(0, 5, 0, 0);
        assertEquals(1, p.getMaxHealth());
    }

    @Test
    void constructorShouldClampNegativeStaminaToZero() {
        // stamina ติดลบต้องถูก clamp เป็น 0
        Player p = new Player(5, -3, 0, 0);
        assertEquals(0, p.getStamina());
    }

    // === Stamina ===

    @Test
    void consumeStaminaShouldDecreasAndReturnTrue() {
        // ใช้ stamina สำเร็จเมื่อมีเพียงพอ
        Player p = new Player(5, 10, 0, 0);
        assertTrue(p.consumeStamina(3));
        assertEquals(7, p.getStamina());
    }

    @Test
    void consumeStaminaShouldReturnFalseWhenInsufficient() {
        // ใช้ stamina ไม่สำเร็จเมื่อไม่พอ ค่า stamina ต้องไม่เปลี่ยน
        Player p = new Player(5, 2, 0, 0);
        assertFalse(p.consumeStamina(5));
        assertEquals(2, p.getStamina());
    }

    @Test
    void consumeStaminaShouldRejectNegativeAmount() {
        // ป้องกันการ exploit ด้วยค่าลบ
        Player p = new Player(5, 10, 0, 0);
        assertFalse(p.consumeStamina(-1));
        assertEquals(10, p.getStamina());
    }

    @Test
    void gainStaminaShouldIncrease() {
        // เพิ่ม stamina เมื่อกินไอเทม
        Player p = new Player(5, 5, 0, 0);
        p.gainStamina(3);
        assertEquals(8, p.getStamina());
    }

    @Test
    void gainStaminaShouldIgnoreNonPositiveAmount() {
        // gainStamina(0) หรือลบต้องไม่เปลี่ยนค่า
        Player p = new Player(5, 5, 0, 0);
        p.gainStamina(0);
        p.gainStamina(-2);
        assertEquals(5, p.getStamina());
    }

    // === Health / Damage / Heal ===

    @Test
    void takeDamageShouldReduceHealth() {
        // ตรวจกลไกรับดาเมจพื้นฐาน
        Player p = new Player(5, 10, 0, 0);
        p.takeDamage(2);
        assertEquals(3, p.getHealth());
    }

    @Test
    void takeDamageShouldNotGoBelowZero() {
        // HP ต้องไม่ติดลบ (Human.setHealth clamps to 0)
        Player p = new Player(3, 10, 0, 0);
        p.takeDamage(10);
        assertEquals(0, p.getHealth());
    }

    @Test
    void takeDamageShouldIgnoreNonPositiveAmount() {
        // damage ≤ 0 ต้องไม่มีผล
        Player p = new Player(5, 10, 0, 0);
        p.takeDamage(0);
        p.takeDamage(-1);
        assertEquals(5, p.getHealth());
    }

    @Test
    void healShouldIncreaseHealthUpToMax() {
        // heal ต้องไม่เกิน maxHealth
        Player p = new Player(5, 10, 0, 0);
        p.takeDamage(3);
        assertEquals(2, p.getHealth());

        p.heal(10); // heal เกิน max
        assertEquals(5, p.getHealth());
    }

    @Test
    void healShouldReactivateDeadPlayer() {
        // heal หลังตายต้องทำให้กลับมา active
        Player p = new Player(5, 10, 0, 0);
        p.takeDamage(5);
        assertFalse(p.isActive());

        p.heal(1);
        assertTrue(p.isActive());
        assertEquals(1, p.getHealth());
    }

    @Test
    void healShouldIgnoreNonPositiveAmount() {
        Player p = new Player(5, 10, 0, 0);
        p.takeDamage(2);
        p.heal(0);
        p.heal(-1);
        assertEquals(3, p.getHealth());
    }

    // === Direction ===

    @Test
    void defaultLastDirectionShouldBeDown() {
        // ทิศเริ่มต้นที่ตัวละครหัน = DOWN (ใช้เลือกภาพ sprite)
        Player p = new Player(5, 10, 0, 0);
        assertEquals(Direction.DOWN, p.getLastDirection());
    }

    @Test
    void setLastDirectionShouldUpdate() {
        Player p = new Player(5, 10, 0, 0);
        p.setLastDirection(Direction.LEFT);
        assertEquals(Direction.LEFT, p.getLastDirection());
    }

    // === Attack ===

    @Test
    void attackShouldNotActWhenPlayerIsDead() {
        // ผู้เล่นที่ตายแล้วต้องไม่สามารถโจมตีได้
        Player p = new Player(5, 10, 0, 0);
        p.takeDamage(5);

        objects.Extrovert e = new objects.Extrovert(1, 0);
        p.attack(e);
        assertTrue(e.isActive()); // ศัตรูไม่ถูกกระทบ
    }

    @Test
    void attackNullShouldNotThrow() {
        // attack(null) ต้องไม่พัง
        Player p = new Player(5, 10, 0, 0);
        assertDoesNotThrow(() -> p.attack(null));
    }
}
