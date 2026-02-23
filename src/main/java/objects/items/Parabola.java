package objects.items;

import logic.game.Player;

public class Parabola extends BaseItem {
    public Parabola(int x, int y) {
        super("Parabola", x, y);
    }

    @Override
    protected void consume(Player player) {
        player.heal(1);
    }
}