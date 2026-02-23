package logic.game;

import interfaces.Attackable;
import logic.components.Direction;
import objects.Human;

public class Player extends Human {
    private int stamina;
    private Direction lastDirection = Direction.DOWN;

    public Player(int health, int stamina, int x, int y) {
        super("Player", health, x, y);
        this.stamina = Math.max(0, stamina);
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(Direction direction) {
        this.lastDirection = direction;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = Math.max(0, stamina);
    }

    public boolean consumeStamina(int amount) {
        if (amount < 0 || stamina < amount) {
            return false;
        }
        stamina -= amount;
        return true;
    }

    public void gainStamina(int amount) {
        if (amount > 0) {
            stamina += amount;
        }
    }

    public void takeDamage(int damage) {
        if (damage > 0) {
            setHealth(getHealth() - damage);
        }
    }

    public void heal(int amount) {
        if (amount > 0) {
            setHealth(getHealth() + amount);
            setActive(true);
        }
    }

    public void attack(Attackable target) {
        if (target != null && isActive()) {
            target.onAttackedBy(this);
        }
    }

    @Override
    public void onAttackedBy(Player player) {
        takeDamage(1);
    }
}