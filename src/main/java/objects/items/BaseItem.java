package objects.items;

import interfaces.Pickable;
import logic.game.Player;
import objects.BaseObject;

/**
 * Abstract base class for collectible items on the game board.
 * Extends {@link BaseObject} and implements {@link Pickable}.
 * Uses the <b>Template Method</b> pattern: {@link #onPick(Player)} handles the
 * consumed-state check while subclasses implement {@link #consume(Player)} to define
 * the specific effect.
 */
public abstract class BaseItem extends BaseObject implements Pickable {
    /** Whether this item has already been consumed. */
    private boolean consumed;

    /**
     * Constructs a BaseItem with the given name and position.
     *
     * @param name the display name of the item
     * @param x    the column position
     * @param y    the row position
     */
    protected BaseItem(String name, int x, int y) {
        super(name, x, y);
    }

    /**
     * Returns whether this item has already been consumed.
     *
     * @return {@code true} if consumed, {@code false} otherwise
     */
    public boolean isConsumed() {
        return consumed;
    }

    /**
     * Called when the player picks up this item.
     * If the item has not yet been consumed, {@link #consume(Player)} is invoked
     * and the item is marked as consumed.
     *
     * @param player the player picking up the item
     */
    @Override
    public void onPick(Player player) {
        if (!consumed) {
            consume(player);
            consumed = true;
        }
    }

    /**
     * Applies this item's effect to the player.
     * Subclasses must implement this to define the specific effect.
     *
     * @param player the player who consumes this item
     */
    protected abstract void consume(Player player);
}