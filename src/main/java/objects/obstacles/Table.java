package objects.obstacles;

import interfaces.Moveable;
import logic.components.Direction;
import objects.BaseObject;

public class Table extends BaseObject implements Moveable {
    private final int sizeInBlocks;
    private final String orientation;

    public Table(int x, int y, String orientation) {
        super("Table", x, y);
        this.sizeInBlocks = 2;
        this.orientation = orientation;
    }

    public Table(int x, int y) {
        this(x, y, "Down");
    }

    public int getSizeInBlocks() {
        return sizeInBlocks;
    }

    public String getOrientation() {
        return orientation;
    }

    public int getMoveCost() {
        return 2;
    }

    @Override
    public void move(Direction direction) {
        setX(getX() + direction.getDx());
        setY(getY() + direction.getDy());
    }
}