package objects.obstacles;

import interfaces.Interactable;
import logic.game.Player;
import objects.BaseObject;

public class Cable extends BaseObject implements Interactable {
    public Cable(int x, int y) {
        super("Cable", x, y);
    }

    @Override
    public void interact(Player player) {
        player.takeDamage(1);
    }
}