package objects.obstacles;

import interfaces.Moveable;
import logic.components.Direction;
import objects.BaseObject;

public class Chair extends BaseObject implements Moveable {
    private final String orientation;

    public Chair(int x, int y, String orientation) {
        super("Chair", x, y);
        this.orientation = orientation;
    }

    public Chair(int x, int y) {
        this(x, y, "Front");
    }

    public String getOrientation() {
        return orientation;
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