package objects;

import logic.game.Player;

/**
 * An Introvert enemy on the game board.
 * When attacked, the Introvert becomes inactive and the player loses 3 stamina and 1 HP.
 */
public class Introvert extends Human {
    /**
     * Constructs an Introvert at the given grid position.
     *
     * @param x the column position
     * @param y the row position
     */
    public Introvert(int x, int y) {
        super("Introvert", 1, x, y);
    }

    /**
     * When attacked, this Introvert is defeated (becomes inactive).
     * The player loses 3 stamina and takes 1 damage.
     *
     * @param player the player who attacked this Introvert
     */
    @Override
    public void onAttackedBy(Player player) {
        setActive(false);
        player.consumeStamina(3);
        player.takeDamage(1);
    }
}