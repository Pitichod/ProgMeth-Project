package objects;

import logic.game.Player;

public class Extrovert extends Human {
    public Extrovert(int x, int y) {
        super("Extrovert", 1, x, y);
    }

    @Override
    public void onAttackedBy(Player player) {
        setActive(false);
        player.consumeStamina(1);
        player.takeDamage(3);
    }
}