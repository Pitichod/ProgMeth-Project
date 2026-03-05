package objects.obstacles;

import objects.BaseObject;

/**
 * A table obstacle on the game board. Tables occupy two grid cells and
 * cannot be pushed by the player (they do not implement {@link interfaces.Moveable}).
 */
public class Table extends BaseObject {
    /** The number of grid cells this table occupies. */
    private final int sizeInBlocks;
    /** The visual orientation of this table (e.g. "Down", "Up", "Left", "Right"). */
    private final String orientation;

    /**
     * Constructs a Table with a specific orientation at the given position.
     *
     * @param x           the column position
     * @param y           the row position
     * @param orientation the visual orientation
     */
    public Table(int x, int y, String orientation) {
        super("Table", x, y);
        this.sizeInBlocks = 2;
        this.orientation = orientation;
    }

    /**
     * Constructs a Table with the default "Down" orientation.
     *
     * @param x the column position
     * @param y the row position
     */
    public Table(int x, int y) {
        this(x, y, "Down");
    }

    /**
     * Returns the number of grid cells this table occupies.
     *
     * @return the size in blocks
     */
    public int getSizeInBlocks() {
        return sizeInBlocks;
    }

    /**
     * Returns the visual orientation of this table.
     *
     * @return the orientation string
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * Returns the stamina cost that would be required to push this table.
     *
     * @return 2
     */
    public int getMoveCost() {
        return 2;
    }

}