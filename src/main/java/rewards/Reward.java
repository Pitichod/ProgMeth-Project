package rewards;

public abstract class Reward {
    private final String name;
    private final int stageNumber;

    protected Reward(String name, int stageNumber) {
        this.name = name;
        this.stageNumber = stageNumber;
    }

    public String getName() {
        return name;
    }

    public int getStageNumber() {
        return stageNumber;
    }
}