package objects.obstacles;

public class Table extends Chair {
    private final int sizeInBlocks;
    private final String orientation;

    public Table(int x, int y, String orientation) {
        super(x, y);
        setName("Table");
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

    @Override
    public int getMoveCost() {
        return 2;
    }
}