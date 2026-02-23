package objects.obstacles;

public class Table extends Chair {
    private final int sizeInBlocks;

    public Table(int x, int y) {
        super(x, y);
        setName("Table");
        this.sizeInBlocks = 2;
    }

    public int getSizeInBlocks() {
        return sizeInBlocks;
    }

    @Override
    public int getMoveCost() {
        return 2;
    }
}