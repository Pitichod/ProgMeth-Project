package interfaces;

import logic.game.Player;

/**
 * Represents an entity that can be attacked by the player.
 * Implementing classes define the consequences of being attacked.
 */
public interface Attackable {
    /**
     * Called when this entity is attacked by the given player.
     * The implementation determines the effect on both the attacker and this entity.
     *
     * @param player the player who attacks this entity
     */
    void onAttackedBy(Player player);
}