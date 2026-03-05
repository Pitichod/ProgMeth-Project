package objects.items;

import logic.game.Player;

/**
 * A collectible Parabola item that heals the player for 2 HP when picked up.
 */
public class Parabola extends BaseItem {
    /**
     * Constructs a Parabola item at the given grid position.
     *
     * @param x the column position
     * @param y the row position
     */
    public Parabola(int x, int y) {
        super("Parabola", x, y);
    }

    /**
     * Heals the player for 2 HP.
     *
     * @param player the player who consumes this item
     */
    @Override
    protected void consume(Player player) {
        player.heal(2);
    }
}