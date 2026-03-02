package objects;

import interfaces.Attackable;
import interfaces.Interactable;
import logic.components.Direction;
import logic.game.Player;

public abstract class Human extends BaseObject implements Interactable, Attackable {
    private int health;
    private boolean active = true;

    // Current facing direction for idle/random facing behavior
    private Direction facing = Direction.DOWN;
    // Next time (nanos) when facing should randomly change
    private long nextFaceChangeAt = 0L;

    protected Human(String name, int health, int x, int y) {
        super(name, x, y);
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(0, health);
        if (this.health == 0) {
            this.active = false;
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void interact(Player player) {
        onAttackedBy(player);
    }

    public Direction getFacing() {
        return facing;
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public long getNextFaceChangeAt() {
        return nextFaceChangeAt;
    }

    public void setNextFaceChangeAt(long timeNanos) {
        this.nextFaceChangeAt = timeNanos;
    }
}