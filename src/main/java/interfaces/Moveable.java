package interfaces;

import logic.components.Direction;

public interface Moveable {
    int getMoveCost();

    void move(Direction direction);
}