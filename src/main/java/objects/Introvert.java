package objects;

import logic.game.Player;

public class Introvert extends Human {
    public Introvert(int x, int y) {
        super("Introvert", 1, x, y);
    }

    @Override
    public void onAttackedBy(Player player) {
        setActive(false);
        player.consumeStamina(1);
        player.takeDamage(1);
    }
}