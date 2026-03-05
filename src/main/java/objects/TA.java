package objects;

import logic.game.Player;

/**
 * A Teaching Assistant (TA) enemy on the game board.
 * If the player attacks a TA while already damaged, the player is killed.
 * If the player is at full health, the player's health is reduced to 1.
 * The TA remains active after being attacked.
 */
public class TA extends Human {
    /**
     * Constructs a TA at the given grid position.
     *
     * @param x the column position
     * @param y the row position
     */
    public TA(int x, int y) {
        super("TA", 1, x, y);
    }

    /**
     * Handles being attacked by the player.
     * If the player's health is below maximum, the player is killed.
     * Otherwise the player's health is reduced to 1. The TA stays active.
     *
     * @param player the player who attacked this TA
     */
    @Override
    public void onAttackedBy(Player player) {
        setActive(true);
        if (player.getHealth() < player.getMaxHealth()) {
            player.setHealth(0);
        } else {
            player.setHealth(1);
        }
    }
}