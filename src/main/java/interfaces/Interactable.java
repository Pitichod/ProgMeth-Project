package interfaces;

import logic.game.Player;

/**
 * Represents an object that the player can interact with on the game board.
 * The interaction effect is defined by each implementing class.
 */
public interface Interactable {
    /**
     * Performs the interaction between this object and the player.
     *
     * @param player the player who interacts with this object
     */
    void interact(Player player);
}