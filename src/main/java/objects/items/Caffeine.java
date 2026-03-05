package objects.items;

import logic.game.Player;

/**
 * A collectible caffeine item that restores 2 stamina to the player when picked up.
 */
public class Caffeine extends BaseItem {
    /**
     * Constructs a Caffeine item at the given grid position.
     *
     * @param x the column position
     * @param y the row position
     */
    public Caffeine(int x, int y) {
        super("Caffeine", x, y);
    }

    /**
     * Grants the player 2 stamina.
     *
     * @param player the player who consumes this item
     */
    @Override
    protected void consume(Player player) {
        player.gainStamina(2);
    }
}