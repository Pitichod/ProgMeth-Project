package objects;

/**
 * Abstract base class for all objects placed on the game board.
 * Stores the object's name and its grid position (x, y).
 */
public abstract class BaseObject {
    /** The display name of this object. */
    private String name;
    /** The horizontal grid coordinate (column). */
    private int x;
    /** The vertical grid coordinate (row). */
    private int y;

    /**
     * Constructs a BaseObject with the given name and position.
     *
     * @param name the display name of this object
     * @param x    the initial column position
     * @param y    the initial row position
     */
    protected BaseObject(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the display name of this object.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name of this object.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the horizontal grid coordinate.
     *
     * @return the column index
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the horizontal grid coordinate.
     *
     * @param x the new column index
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Returns the vertical grid coordinate.
     *
     * @return the row index
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the vertical grid coordinate.
     *
     * @param y the new row index
     */
    public void setY(int y) {
        this.y = y;
    }
}