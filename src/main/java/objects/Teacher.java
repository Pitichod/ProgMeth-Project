package objects;

import logic.game.Player;

public class Teacher extends Human {
    public Teacher(int x, int y) {
        super("Teacher", 1, x, y);
    }

    @Override
    public void onAttackedBy(Player player) {
        player.setHealth(0);
    }
}