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

class CoreMechanicTest {

    @Test
    void startingLevelShouldResetHealthToFive() {
        GameSession session = new GameSession();

        session.startLevel(LevelId.ISCALE_403);

        assertEquals(5, session.getPlayer().getHealth());
        // Match current LevelConfig for ISCALE_403 (stamina configured by game)
        assertEquals(20, session.getPlayer().getStamina());
    }

    @Test
    void attackingExtrovertShouldApplyPenalty() {
        Player player = new Player(5, 10, 0, 0);
        Extrovert extrovert = new Extrovert(1, 0);

        player.attack(extrovert);

        assertFalse(extrovert.isActive());
        assertEquals(2, player.getHealth());
        assertEquals(9, player.getStamina());
    }

    @Test
    void pickingCaffeineShouldIncreaseStaminaByTwo() {
        Player player = new Player(5, 7, 0, 0);
        Caffeine caffeine = new Caffeine(1, 1);

        caffeine.onPick(player);

        assertEquals(9, player.getStamina());
        assertTrue(caffeine.isConsumed());
    }

    @Test
    void pickingParabolaShouldIncreaseHealthByTwo() {
        Player player = new Player(5, 7, 0, 0);
        player.setHealth(2);
        Parabola parabola = new Parabola(1, 1);

        parabola.onPick(player);

        assertEquals(4, player.getHealth());
        assertTrue(parabola.isConsumed());
    }

    @Test
    void pickingRobuxGiftCardShouldDecreaseStaminaByOne() {
        Player player = new Player(5, 7, 0, 0);
        RobuxGiftCard robuxGiftCard = new RobuxGiftCard(1, 1);

        robuxGiftCard.onPick(player);

        assertEquals(6, player.getStamina());
        assertTrue(robuxGiftCard.isConsumed());
    }

    @Test
    void taCollisionAtFullHealthShouldReducePlayerToOneAndRemainActive() {
        Player player = new Player(5, 10, 0, 0);
        TA ta = new TA(1, 0);

        player.attack(ta);

        assertEquals(1, player.getHealth());
        assertTrue(ta.isActive());
    }

    @Test
    void taCollisionAtNotFullHealthShouldDefeatPlayerAndRemainActive() {
        Player player = new Player(5, 10, 0, 0);
        player.setHealth(4);
        TA ta = new TA(1, 0);

        player.attack(ta);

        assertEquals(0, player.getHealth());
        assertTrue(ta.isActive());
    }
}