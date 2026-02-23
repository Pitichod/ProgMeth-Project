package logic.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import objects.Extrovert;
import objects.items.Caffeine;
import org.junit.jupiter.api.Test;

class CoreMechanicTest {

    @Test
    void startingLevelShouldResetHealthToFive() {
        GameSession session = new GameSession();

        session.startLevel(LevelId.ISCALE_403);

        assertEquals(5, session.getPlayer().getHealth());
        assertEquals(9, session.getPlayer().getStamina());
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
    void pickingCaffeineShouldIncreaseStaminaByOne() {
        Player player = new Player(5, 7, 0, 0);
        Caffeine caffeine = new Caffeine(1, 1);

        caffeine.onPick(player);

        assertEquals(8, player.getStamina());
        assertTrue(caffeine.isConsumed());
    }
}