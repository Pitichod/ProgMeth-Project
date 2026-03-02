package objects;

import logic.game.Player;

public class TA extends Human {
    public TA(int x, int y) {
        super("TA", 1, x, y);
    }

    @Override
    public void onAttackedBy(Player player) {
        // If player's health is not full, they immediately lose (set to 0).
        // Otherwise reduce to 1.
        if (player.getHealth() < player.getMaxHealth()) {
            player.setHealth(0);
        } else {
            player.setHealth(1);
        }
    }
}