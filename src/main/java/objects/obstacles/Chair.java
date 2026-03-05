package objects.obstacles;

import interfaces.Moveable;
import logic.components.Direction;
import objects.BaseObject;

/**
 * A chair obstacle that the player can push on the game board.
 * Implements {@link Moveable} and costs 1 stamina to push one tile.
 */
public class Chair extends BaseObject implements Moveable {
    /** The visual orientation of this chair (e.g. "Front", "Back", "Left", "Right"). */
    private final String orientation;

    /**
     * Constructs a Chair with a specific orientation at the given position.
     *
     * @param x           the column position
     * @param y           the row position
     * @param orientation the visual orientation
     */
    public Chair(int x, int y, String orientation) {
        super("Chair", x, y);
        this.orientation = orientation;
    }

    /**
     * Constructs a Chair with the default "Front" orientation.
     *
     * @param x the column position
     * @param y the row position
     */
    public Chair(int x, int y) {
        this(x, y, "Front");
    }

    /**
     * Returns the visual orientation of this chair.
     *
     * @return the orientation string
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * Returns the stamina cost to push this chair (always 1).
     *
     * @return 1
     */
    @Override
    public int getMoveCost() {
        return 1;
    }

    /**
     * Moves this chair one tile in the given direction.
     *
     * @param direction the direction to push
     */
    @Override
    public void move(Direction direction) {
        setX(getX() + direction.getDx());
        setY(getY() + direction.getDy());
    }
}