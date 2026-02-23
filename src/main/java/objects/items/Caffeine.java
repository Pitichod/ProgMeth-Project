package objects.items;

import logic.game.Player;

public class Caffeine extends BaseItem {
    public Caffeine(int x, int y) {
        super("Caffeine", x, y);
    }

    @Override
    protected void consume(Player player) {
        player.gainStamina(1);
    }
}