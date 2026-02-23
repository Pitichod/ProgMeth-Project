package objects.items;

import logic.game.Player;

public class RobuxGiftCard extends BaseItem {
    public RobuxGiftCard(int x, int y) {
        super("RobuxGiftCard", x, y);
    }

    @Override
    protected void consume(Player player) {
        player.consumeStamina(1);
    }
}