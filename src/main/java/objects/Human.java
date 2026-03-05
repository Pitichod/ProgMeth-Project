package objects;

import interfaces.Attackable;
import interfaces.Interactable;
import logic.components.Direction;
import logic.game.Player;

/**
 * Abstract base class for all human characters (NPCs and the player) on the game board.
 * Extends {@link BaseObject} and implements {@link Interactable} and {@link Attackable}.
 * Provides health management, active state tracking, and a facing direction for rendering.
 */
public abstract class Human extends BaseObject implements Interactable, Attackable {
    /** Current health points. When this reaches 0 the human becomes inactive. */
    private int health;
    /** Whether this human is still active on the board. */
    private boolean active = true;

    /** Current facing direction used for idle and reactive sprite rendering. */
    private Direction facing = Direction.DOWN;
    /** Timestamp (in nanoseconds) when the facing direction should next change randomly. */
    private long nextFaceChangeAt = 0L;

    /**
     * Constructs a Human with the given attributes.
     *
     * @param name   the display name
     * @param health the initial health points
     * @param x      the initial column position
     * @param y      the initial row position
     */
    protected Human(String name, int health, int x, int y) {
        super(name, x, y);
        this.health = health;
    }

    /**
     * Returns the current health of this human.
     *
     * @return the current health points
     */
    public int getHealth() {
        return health;
    }

    /**
     * Sets the health of this human, clamped to a minimum of 0.
     * If health reaches 0 the human is automatically deactivated.
     *
     * @param health the new health value
     */
    public void setHealth(int health) {
        this.health = Math.max(0, health);
        if (this.health == 0) {
            this.active = false;
        }
    }

    /**
     * Returns whether this human is still active on the board.
     *
     * @return {@code true} if active, {@code false} otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active state of this human.
     *
     * @param active {@code true} to activate, {@code false} to deactivate
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Interacting with a human triggers the attack behavior.
     *
     * @param player the player who interacts with this human
     */
    @Override
    public void interact(Player player) {
        onAttackedBy(player);
    }

    /**
     * Returns the current facing direction.
     *
     * @return the facing {@link Direction}
     */
    public Direction getFacing() {
        return facing;
    }

    /**
     * Sets the facing direction of this human.
     *
     * @param facing the new facing direction
     */
    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    /**
     * Returns the nanosecond timestamp at which the facing direction should next change.
     *
     * @return the next face-change time in nanoseconds
     */
    public long getNextFaceChangeAt() {
        return nextFaceChangeAt;
    }

    /**
     * Sets the nanosecond timestamp for the next random facing change.
     *
     * @param timeNanos the next face-change time in nanoseconds
     */
    public void setNextFaceChangeAt(long timeNanos) {
        this.nextFaceChangeAt = timeNanos;
    }
}