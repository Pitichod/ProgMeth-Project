package logic.game;

import interfaces.Attackable;
import logic.components.Direction;
import objects.Human;

/**
 * Represents the player character controlled by the user.
 * Extends {@link Human} and adds stamina management, damage/healing mechanics,
 * and the ability to attack other {@link Attackable} entities.
 */
public class Player extends Human {
    /** Current stamina points, consumed by movement and pushing obstacles. */
    private int stamina;
    /** The maximum health the player can have (set at construction). */
    private final int maxHealth;
    /** The last direction the player moved, used for sprite rendering. */
    private Direction lastDirection = Direction.DOWN;

    /**
     * Constructs a Player with the given stats and position.
     *
     * @param maxHealth the maximum (and initial) health
     * @param stamina   the initial stamina
     * @param x         the starting column position
     * @param y         the starting row position
     */
    public Player(int maxHealth, int stamina, int x, int y) {
        super("Player", maxHealth, x, y);
        this.maxHealth = Math.max(1, maxHealth);
        this.stamina = Math.max(0, stamina);
    }

    /**
     * Returns the maximum health of this player.
     *
     * @return the maximum health
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Returns the last direction the player moved.
     *
     * @return the last movement {@link Direction}
     */
    public Direction getLastDirection() {
        return lastDirection;
    }

    /**
     * Sets the last direction the player moved (used for sprite rendering).
     *
     * @param direction the direction
     */
    public void setLastDirection(Direction direction) {
        this.lastDirection = direction;
    }

    /**
     * Returns the current stamina.
     *
     * @return the stamina value
     */
    public int getStamina() {
        return stamina;
    }

    /**
     * Sets the stamina, clamped to a minimum of 0.
     *
     * @param stamina the new stamina value
     */
    public void setStamina(int stamina) {
        this.stamina = Math.max(0, stamina);
    }

    /**
     * Attempts to consume the given amount of stamina.
     * Returns {@code false} if the amount is negative or exceeds current stamina.
     *
     * @param amount the stamina to consume
     * @return {@code true} if successful, {@code false} otherwise
     */
    public boolean consumeStamina(int amount) {
        if (amount < 0 || stamina < amount) {
            return false;
        }
        stamina -= amount;
        return true;
    }

    /**
     * Adds the given amount of stamina. Negative values are ignored.
     *
     * @param amount the stamina to gain
     */
    public void gainStamina(int amount) {
        if (amount > 0) {
            stamina += amount;
        }
    }

    /**
     * Reduces the player's health by the given damage amount.
     * Negative values are ignored.
     *
     * @param damage the amount of damage to take
     */
    public void takeDamage(int damage) {
        if (damage > 0) {
            setHealth(getHealth() - damage);
        }
    }

    /**
     * Heals the player by the given amount, up to the maximum health.
     * Also reactivates the player if they were inactive (dead).
     *
     * @param amount the amount to heal
     */
    public void heal(int amount) {
        if (amount > 0) {
            int newHealth = Math.min(getMaxHealth(), getHealth() + amount);
            setHealth(newHealth);
            setActive(true);
        }
    }

    /**
     * Attacks the given target if the player is active.
     *
     * @param target the entity to attack
     */
    public void attack(Attackable target) {
        if (target != null && isActive()) {
            target.onAttackedBy(this);
        }
    }

    /**
     * When the player is attacked by another player, they take 1 damage.
     *
     * @param player the attacking player
     */
    @Override
    public void onAttackedBy(Player player) {
        takeDamage(1);
    }
}