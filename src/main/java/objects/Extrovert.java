package objects;

import logic.game.Player;

/**
 * An Extrovert enemy on the game board.
 * When attacked, the Extrovert becomes inactive and the player loses 1 stamina and 3 HP.
 */
public class Extrovert extends Human {
    /**
     * Constructs an Extrovert at the given grid position.
     *
     * @param x the column position
     * @param y the row position
     */
    public Extrovert(int x, int y) {
        super("Extrovert", 1, x, y);
    }

    /**
     * When attacked, this Extrovert is defeated (becomes inactive).
     * The player loses 1 stamina and takes 3 damage.
     *
     * @param player the player who attacked this Extrovert
     */
    @Override
    public void onAttackedBy(Player player) {
        setActive(false);
        player.consumeStamina(1);
        player.takeDamage(3);
    }
}