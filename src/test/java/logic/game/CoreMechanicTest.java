package logic.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import objects.Extrovert;
import objects.TA;
import objects.items.Caffeine;
import objects.items.Parabola;
import objects.items.RobuxGiftCard;
import org.junit.jupiter.api.Test;

/**
 * Tests core mechanics (interactions between different classes).
 * Covers: starting a level, attacking enemies (Extrovert, TA), picking up items (Caffeine, Parabola, RobuxGiftCard).
 *
 * Reason: This is a unit-level integration test that verifies Player + Enemy/Item
 * work together correctly according to the game rules.
 */
class CoreMechanicTest {

    @Test
    void startingLevelShouldUseConfigHealth() {
        // Verify startLevel uses HP from LevelConfig (ISCALE_403 = 5 HP, 20 Stamina)
        GameSession session = new GameSession();

        session.startLevel(LevelId.ISCALE_403);

        assertEquals(5, session.getPlayer().getHealth());
        // Match current LevelConfig for ISCALE_403 (stamina configured by game)
        assertEquals(20, session.getPlayer().getStamina());
    }

    @Test
    void attackingExtrovertShouldApplyPenalty() {
        // Extrovert interaction: player loses 3 HP and 1 stamina, extrovert is deactivated
        Player player = new Player(5, 10, 0, 0);
        Extrovert extrovert = new Extrovert(1, 0);

        player.attack(extrovert);

        assertFalse(extrovert.isActive());
        assertEquals(2, player.getHealth());
        assertEquals(9, player.getStamina());
    }

    @Test
    void pickingCaffeineShouldIncreaseStaminaByTwo() {
        // Caffeine item grants +2 stamina and is consumed
        Player player = new Player(5, 7, 0, 0);
        Caffeine caffeine = new Caffeine(1, 1);

        caffeine.onPick(player);

        assertEquals(9, player.getStamina());
        assertTrue(caffeine.isConsumed());
    }

    @Test
    void pickingParabolaShouldIncreaseHealthByTwo() {
        // Parabola item heals +2 HP and is consumed
        Player player = new Player(5, 7, 0, 0);
        player.setHealth(2);
        Parabola parabola = new Parabola(1, 1);

        parabola.onPick(player);

        assertEquals(4, player.getHealth());
        assertTrue(parabola.isConsumed());
    }

    @Test
    void pickingRobuxGiftCardShouldDecreaseStaminaByOne() {
        // RobuxGiftCard is a trap item: costs 1 stamina and is consumed
        Player player = new Player(5, 7, 0, 0);
        RobuxGiftCard robuxGiftCard = new RobuxGiftCard(1, 1);

        robuxGiftCard.onPick(player);

        assertEquals(6, player.getStamina());
        assertTrue(robuxGiftCard.isConsumed());
    }

    @Test
    void taCollisionAtFullHealthShouldReducePlayerToOneAndRemainActive() {
        // TA at full HP: reduces player to 1 HP, TA stays active (not killable)
        Player player = new Player(5, 10, 0, 0);
        TA ta = new TA(1, 0);

        player.attack(ta);

        assertEquals(1, player.getHealth());
        assertTrue(ta.isActive());
    }

    @Test
    void taCollisionAtNotFullHealthShouldDefeatPlayerAndRemainActive() {
        // TA when player is not full HP: instantly kills the player, TA stays active
        Player player = new Player(5, 10, 0, 0);
        player.setHealth(4);
        TA ta = new TA(1, 0);

        player.attack(ta);

        assertEquals(0, player.getHealth());
        assertTrue(ta.isActive());
    }
}