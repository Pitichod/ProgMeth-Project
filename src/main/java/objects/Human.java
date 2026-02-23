package objects;

import interfaces.Attackable;
import interfaces.Interactable;
import logic.game.Player;

public abstract class Human extends BaseObject implements Interactable, Attackable {
    private int health;
    private boolean active = true;

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
}