package rewards;

/**
 * Abstract base class for rewards granted to the player upon completing a level.
 * Each reward has a display name and the stage (level) number it belongs to.
 */
public abstract class Reward {
    /** The display name of this reward. */
    private final String name;
    /** The stage (level) number this reward is associated with. */
    private final int stageNumber;

    /**
     * Constructs a Reward with the given name and stage number.
     *
     * @param name        the display name
     * @param stageNumber the level number (1–5)
     */
    protected Reward(String name, int stageNumber) {
        this.name = name;
        this.stageNumber = stageNumber;
    }

    /**
     * Returns the display name of this reward.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the stage number this reward belongs to.
     *
     * @return the stage number
     */
    public int getStageNumber() {
        return stageNumber;
    }
}