package objects.obstacles;

import interfaces.Moveable;
import logic.components.Direction;
import objects.BaseObject;

public class Chair extends BaseObject implements Moveable {
    public Chair(int x, int y) {
        super("Chair", x, y);
    }

    @Override
    public int getMoveCost() {
        return 1;
    }

    @Override
    public void move(Direction direction) {
        setX(getX() + direction.getDx());
        setY(getY() + direction.getDy());
    }
}