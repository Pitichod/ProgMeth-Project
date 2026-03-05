package objects.items;

import logic.game.Player;

/**
 * A collectible Robux Gift Card item that costs the player 1 stamina when picked up.
 * This is a trap item that penalizes the player.
 */
public class RobuxGiftCard extends BaseItem {
    /**
     * Constructs a RobuxGiftCard item at the given grid position.
     *
     * @param x the column position
     * @param y the row position
     */
    public RobuxGiftCard(int x, int y) {
        super("RobuxGiftCard", x, y);
    }

    /**
     * Deducts 1 stamina from the player.
     *
     * @param player the player who consumes this item
     */
    @Override
    protected void consume(Player player) {
        player.consumeStamina(1);
    }
}