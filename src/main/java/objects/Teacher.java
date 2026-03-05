package objects;

import logic.game.Player;

/**
 * A Teacher enemy on the game board.
 * When the player attacks a Teacher, the player is instantly killed (health set to 0).
 */
public class Teacher extends Human {
    /**
     * Constructs a Teacher at the given grid position.
     *
     * @param x the column position
     * @param y the row position
     */
    public Teacher(int x, int y) {
        super("Teacher", 1, x, y);
    }

    /**
     * When attacked by the player, the player's health is set to 0 (instant defeat).
     *
     * @param player the player who attacked this teacher
     */
    @Override
    public void onAttackedBy(Player player) {
        player.setHealth(0);
    }
}