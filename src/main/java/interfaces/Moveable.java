package interfaces;

import logic.components.Direction;

/**
 * Represents an object that can be pushed or moved on the game board.
 * Implementing classes define their movement cost and movement behavior.
 */
public interface Moveable {
    /**
     * Returns the stamina cost required to push this object.
     *
     * @return the stamina cost of pushing this object
     */
    int getMoveCost();

    /**
     * Moves this object one tile in the given direction.
     *
     * @param direction the direction to move
     */
    void move(Direction direction);
}