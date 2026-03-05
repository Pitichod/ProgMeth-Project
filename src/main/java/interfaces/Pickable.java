package interfaces;

import logic.game.Player;

/**
 * Represents an object that can be picked up by the player.
 * Implementing classes define what happens when the player collects the object.
 */
public interface Pickable {
    /**
     * Called when the player picks up this object.
     *
     * @param player the player who picks up the object
     */
    void onPick(Player player);
}