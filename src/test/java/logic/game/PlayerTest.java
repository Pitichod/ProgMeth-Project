package logic.game;

import static org.junit.jupiter.api.Assertions.*;

import logic.components.Direction;
import org.junit.jupiter.api.Test;

/**
 * Tests the Player class, which is the main character controlled by the user.
 * Covers: construction, stamina, health, damage, heal, attack, direction.
 *
 * Reason: Player is the central piece of all game logic.
 * If any setter/getter is wrong, it will affect GameEngine, items, and enemies.
 */
class PlayerTest {

    // === Player Construction ===

    @Test
    void constructorShouldSetMaxHealthAndStamina() {
        // Verify constructor initializes all fields correctly
        Player p = new Player(5, 10, 2, 3);
        assertEquals(5, p.getMaxHealth());
        assertEquals(5, p.getHealth());
        assertEquals(10, p.getStamina());
        assertEquals(2, p.getX());
        assertEquals(3, p.getY());
    }

    @Test
    void constructorShouldClampMaxHealthToAtLeastOne() {
        // maxHealth <= 0 must be clamped to 1 to prevent division-by-zero in the HUD
        Player p = new Player(0, 5, 0, 0);
        assertEquals(1, p.getMaxHealth());
    }

    @Test
    void constructorShouldClampNegativeStaminaToZero() {
        // Negative stamina must be clamped to 0
        Player p = new Player(5, -3, 0, 0);
        assertEquals(0, p.getStamina());
    }

    // === Stamina ===

    @Test
    void consumeStaminaShouldDecreasAndReturnTrue() {
        // Consuming stamina succeeds when sufficient amount is available
        Player p = new Player(5, 10, 0, 0);
        assertTrue(p.consumeStamina(3));
        assertEquals(7, p.getStamina());
    }

    @Test
    void consumeStaminaShouldReturnFalseWhenInsufficient() {
        // Consuming stamina fails when not enough; stamina must remain unchanged
        Player p = new Player(5, 2, 0, 0);
        assertFalse(p.consumeStamina(5));
        assertEquals(2, p.getStamina());
    }

    @Test
    void consumeStaminaShouldRejectNegativeAmount() {
        // Prevent exploit via negative values
        Player p = new Player(5, 10, 0, 0);
        assertFalse(p.consumeStamina(-1));
        assertEquals(10, p.getStamina());
    }

    @Test
    void gainStaminaShouldIncrease() {
        // Gain stamina when picking up an item
        Player p = new Player(5, 5, 0, 0);
        p.gainStamina(3);
        assertEquals(8, p.getStamina());
    }

    @Test
    void gainStaminaShouldIgnoreNonPositiveAmount() {
        // gainStamina(0) or negative must not change the value
        Player p = new Player(5, 5, 0, 0);
        p.gainStamina(0);
        p.gainStamina(-2);
        assertEquals(5, p.getStamina());
    }

    // === Health / Damage / Heal ===

    @Test
    void takeDamageShouldReduceHealth() {
        // Verify basic damage mechanic
        Player p = new Player(5, 10, 0, 0);
        p.takeDamage(2);
        assertEquals(3, p.getHealth());
    }

    @Test
    void takeDamageShouldNotGoBelowZero() {
        // HP must not go negative (Human.setHealth clamps to 0)
        Player p = new Player(3, 10, 0, 0);
        p.takeDamage(10);
        assertEquals(0, p.getHealth());
    }

    @Test
    void takeDamageShouldIgnoreNonPositiveAmount() {
        // damage <= 0 must have no effect
        Player p = new Player(5, 10, 0, 0);
        p.takeDamage(0);
        p.takeDamage(-1);
        assertEquals(5, p.getHealth());
    }

    @Test
    void healShouldIncreaseHealthUpToMax() {
        // Healing must not exceed maxHealth
        Player p = new Player(5, 10, 0, 0);
        p.takeDamage(3);
        assertEquals(2, p.getHealth());

        p.heal(10); // heal beyond max
        assertEquals(5, p.getHealth());
    }

    @Test
    void healShouldReactivateDeadPlayer() {
        // Healing a dead player must reactivate them
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
        // Default facing direction = DOWN (used for sprite selection)
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
        // A dead player must not be able to attack
        Player p = new Player(5, 10, 0, 0);
        p.takeDamage(5);

        objects.Extrovert e = new objects.Extrovert(1, 0);
        p.attack(e);
        assertTrue(e.isActive()); // enemy is unaffected
    }

    @Test
    void attackNullShouldNotThrow() {
        // attack(null) must not crash
        Player p = new Player(5, 10, 0, 0);
        assertDoesNotThrow(() -> p.attack(null));
    }
}
