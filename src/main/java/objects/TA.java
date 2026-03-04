package objects;

import logic.game.Player;

public class TA extends Human {
    public TA(int x, int y) {
        super("TA", 1, x, y);
    }

    @Override
    public void onAttackedBy(Player player) {
        setActive(true);
        if (player.getHealth() < player.getMaxHealth()) {
            player.setHealth(0);
        } else {
            player.setHealth(1);
        }
    }
}