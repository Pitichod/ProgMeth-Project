package objects.obstacles;

import interfaces.Interactable;
import logic.game.Player;
import objects.BaseObject;

/**
 * A cable obstacle on the game board that damages the player when stepped on.
 * Cables are static and cannot be pushed. Different sprite names represent
 * different visual orientations (e.g., plugs, horizontal/vertical wires).
 */
public class Cable extends BaseObject implements Interactable {
    /** The sprite variant name used for rendering (e.g. "HPlugDown", "WireH"). */
    private final String spriteName;

    /**
     * Constructs a Cable with the default sprite at the given position.
     *
     * @param x the column position
     * @param y the row position
     */
    public Cable(int x, int y) {
        this(x, y, "HPlugDown");
    }

    /**
     * Constructs a Cable with a specific sprite at the given position.
     *
     * @param x          the column position
     * @param y          the row position
     * @param spriteName the sprite variant name for rendering
     */
    public Cable(int x, int y, String spriteName) {
        super("Cable", x, y);
        this.spriteName = spriteName;
    }

    /**
     * Returns the sprite variant name for this cable.
     *
     * @return the sprite name
     */
    public String getSpriteName() {
        return spriteName;
    }

    /**
     * When the player steps on this cable, the player takes 1 damage.
     *
     * @param player the player who stepped on the cable
     */
    @Override
    public void interact(Player player) {
        player.takeDamage(1);
    }
}