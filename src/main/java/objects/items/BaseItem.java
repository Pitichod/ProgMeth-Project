package objects.items;

import interfaces.Pickable;
import logic.game.Player;
import objects.BaseObject;

public abstract class BaseItem extends BaseObject implements Pickable {
    private boolean consumed;

    protected BaseItem(String name, int x, int y) {
        super(name, x, y);
    }

    public boolean isConsumed() {
        return consumed;
    }

    @Override
    public void onPick(Player player) {
        if (!consumed) {
            consume(player);
            consumed = true;
        }
    }

    protected abstract void consume(Player player);
}