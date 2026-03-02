package objects.obstacles;

import interfaces.Interactable;
import logic.game.Player;
import objects.BaseObject;

public class Cable extends BaseObject implements Interactable {
    private final String spriteName;

    public Cable(int x, int y) {
        this(x, y, "HPlugDown");
    }

    public Cable(int x, int y, String spriteName) {
        super("Cable", x, y);
        this.spriteName = spriteName;
    }

    public String getSpriteName() {
        return spriteName;
    }

    @Override
    public void interact(Player player) {
        player.takeDamage(1);
    }
}