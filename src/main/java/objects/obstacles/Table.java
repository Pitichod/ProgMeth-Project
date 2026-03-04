package objects.obstacles;

import objects.BaseObject;

public class Table extends BaseObject {
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

}