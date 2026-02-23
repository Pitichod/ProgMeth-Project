package interfaces;

import logic.game.Player;

public interface Attackable {
    void onAttackedBy(Player player);
}