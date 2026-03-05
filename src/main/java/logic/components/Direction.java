package logic.components;

/**
 * Represents the four cardinal movement directions on the game board.
 * Each direction stores its delta-x and delta-y values so that movement
 * calculations can use {@link #getDx()} and {@link #getDy()} directly.
 * The Y-axis increases downward (screen coordinates).
 */
public enum Direction {
    /** Upward movement (dx=0, dy=-1). */
    UP(0, -1),
    /** Downward movement (dx=0, dy=1). */
    DOWN(0, 1),
    /** Leftward movement (dx=-1, dy=0). */
    LEFT(-1, 0),
    /** Rightward movement (dx=1, dy=0). */
    RIGHT(1, 0);

    /** Horizontal displacement per step. */
    private final int dx;
    /** Vertical displacement per step. */
    private final int dy;

    /**
     * Constructs a Direction with the given deltas.
     *
     * @param dx horizontal displacement
     * @param dy vertical displacement
     */
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Returns the horizontal displacement for this direction.
     *
     * @return -1, 0, or 1
     */
    public int getDx() {
        return dx;
    }

    /**
     * Returns the vertical displacement for this direction.
     *
     * @return -1, 0, or 1
     */
    public int getDy() {
        return dy;
    }
}