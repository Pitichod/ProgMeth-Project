package logic.game;

import logic.components.Direction;
import objects.Human;
import objects.items.BaseItem;
import objects.obstacles.Cable;
import objects.obstacles.Chair;
import objects.BaseObject;
import interfaces.Moveable;
import rewards.Reward;

/**
 * Core game engine that orchestrates player movement, combat, item pickup,
 * obstacle pushing, and win/lose condition checks for a single level.
 * Delegates board state to {@link GameBoard} and session state to {@link GameSession}.
 */
public class GameEngine {
    /** The game session managing player state and rewards. */
    private final GameSession session;
    /** The level being played. */
    private final LevelId levelId;
    /** Default max health fallback. */
    private final int maxHealth = 5;
    /** Maximum stamina for the current level. */
    private final int maxStamina;
    /** The game board containing walls, obstacles, items, and enemies. */
    private GameBoard board;
    /** The current status message displayed to the player. */
    private String statusMessage = "Move with Arrow keys or WASD.";
    /** Whether the current level has been completed. */
    private boolean completed;

    /**
     * Constructs a GameEngine for the given session and level.
     * Initialises the board and places the player at the start position.
     *
     * @param session the game session
     * @param levelId the level to play
     */

    public GameEngine(GameSession session, LevelId levelId) {
        this.session = session;
        this.levelId = levelId;
        session.startLevel(levelId);
        this.maxStamina = session.getCurrentLevel().getDefaultStamina();
        reloadBoard();
    }

    /**
     * Attempts to move the player in the given direction.
     * Handles wall collision, enemy combat, obstacle pushing, item pickup,
     * cable damage, and door (exit) detection.
     *
     * @param direction the direction to move
     */
    public void move(Direction direction) {
        Player player = session.getPlayer();
        if (player == null) {
            return;
        }
        player.setLastDirection(direction);
        if (completed) {
            statusMessage = "Level already completed.";
            return;
        }
        if (player.getHealth() <= 0) {
            statusMessage = "You are defeated. Press R to restart.";
            return;
        }
        if (player.getStamina() <= 0) {
            statusMessage = "Out of stamina. Press R to restart.";
            return;
        }

        int targetX = player.getX() + direction.getDx();
        int targetY = player.getY() + direction.getDy();

        if (!board.isInBounds(targetX, targetY) || board.isWall(targetX, targetY)) {
            statusMessage = "Blocked by wall.";
            return;
        }

        Human human = board.findHumanAt(targetX, targetY);
        if (human != null) {
            player.attack(human);
            if (!human.isActive() && player.getHealth() > 0) {
                player.setX(targetX);
                player.setY(targetY);
                applyAfterMoveEffects();
            } else if (player.getHealth() <= 0) {
                statusMessage = "You lost this level. Press R to restart.";
            } else {
                statusMessage = "That was a bad fight.";
            }
            return;
        }

        BaseObject found = board.findMoveableObstacleAt(targetX, targetY);
        if (found != null) {
            if (!(found instanceof Moveable)) {
                statusMessage = "Cannot push this obstacle.";
                return;
            }
            Moveable obstacle = (Moveable) found;
            int pushToX = targetX + direction.getDx();
            int pushToY = targetY + direction.getDy();
            if (!board.isInBounds(pushToX, pushToY) || board.isBlockingCell(pushToX, pushToY) || board.isDoor(pushToX, pushToY)) {
                statusMessage = "Cannot push this obstacle.";
                return;
            }
            if (found instanceof Chair && board.findCableAt(pushToX, pushToY) != null) {
                statusMessage = "Cannot push chair onto cable.";
                return;
            }
            int moveCost = obstacle.getMoveCost();
            if (!player.consumeStamina(moveCost)) {
                statusMessage = "Not enough stamina to push.";
                return;
            }
            obstacle.move(direction);
            player.setX(targetX);
            player.setY(targetY);
            applyAfterMoveEffects();
            return;
        }

        if (!player.consumeStamina(1)) {
            statusMessage = "Not enough stamina.";
            return;
        }

        player.setX(targetX);
        player.setY(targetY);
        applyAfterMoveEffects();
    }

    private void applyAfterMoveEffects() {
        Player player = session.getPlayer();

        Cable cable = board.findCableAt(player.getX(), player.getY());
        if (cable != null) {
            cable.interact(player);
            statusMessage = "Stepped on cable: HP -1";
        } else {
            statusMessage = "Moved.";
        }

        BaseItem item = board.findItemAt(player.getX(), player.getY());
        if (item != null) {
            item.onPick(player);
            statusMessage = "Picked " + item.getName();
        }

        if (player.getHealth() <= 0) {
            statusMessage = "You lost this level. Press R to restart.";
            return;
        }

        if (board.isDoor(player.getX(), player.getY())) {
            completed = true;
            Reward reward = session.completeCurrentLevel();
            statusMessage = "Level clear! Reward: " + (reward == null ? "-" : reward.getName());
            return;
        }

        if (player.getStamina() <= 0) {
            statusMessage = "Out of stamina. Press R to restart.";
        }
    }

    /**
     * Restarts the current level by reinitialising the player and reloading the board.
     */
    public void restartLevel() {
        session.startLevel(levelId);
        reloadBoard();
        completed = false;
        statusMessage = "Level restarted.";
    }

    private void reloadBoard() {
        this.board = GameBoard.fromResource(getMapResource());
        Player player = session.getPlayer();
        player.setX(board.getPlayerStartX());
        player.setY(board.getPlayerStartY());
    }

    /**
     * Returns the game board for the current level.
     *
     * @return the {@link GameBoard}
     */
    public GameBoard getBoard() {
        return board;
    }

    /**
     * Returns the player from the current session.
     *
     * @return the {@link Player}
     */
    public Player getPlayer() {
        return session.getPlayer();
    }

    /**
     * Returns the current status message to display in the UI.
     *
     * @return the status message
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Returns whether the level has been completed.
     *
     * @return {@code true} if the level is completed
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Returns the player's maximum health for this level.
     *
     * @return the max health
     */
    public int getMaxHealth() {
        Player p = session.getPlayer();
        return p == null ? maxHealth : p.getMaxHealth();
    }

    /**
     * Returns the maximum stamina for this level.
     *
     * @return the max stamina
     */
    public int getMaxStamina() {
        return maxStamina;
    }

    /**
     * Returns a human-readable label for the current level.
     *
     * @return the level label string
     */
    public String getLevelLabel() {
        return switch (levelId) {
            case ISCALE_401 -> "LEVEL 1 (ISCALE 401)";
            case ISCALE_402 -> "LEVEL 2 (ISCALE 402)";
            case ISCALE_403 -> "LEVEL 3 (ISCALE 403)";
            case ISCALE_404 -> "LEVEL 4 (ISCALE 404)";
            case ISCALE_405 -> "LEVEL 5 (ISCALE 405)";
        };
    }

    /**
     * Returns the {@link LevelId} of the next level, or {@code null} if this is the last level.
     *
     * @return the next level ID, or {@code null}
     */
    public LevelId getNextLevelId() {
        return switch (levelId) {
            case ISCALE_401 -> LevelId.ISCALE_402;
            case ISCALE_402 -> LevelId.ISCALE_403;
            case ISCALE_403 -> LevelId.ISCALE_404;
            case ISCALE_404 -> LevelId.ISCALE_405;
            case ISCALE_405 -> null;
        };
    }

    /**
     * Returns the classpath resource path of the map file for this level.
     *
     * @return the resource path string
     */
    public String getMapResource() {
        return switch (levelId) {
            case ISCALE_401 -> "/maps/iscale_401.txt";
            case ISCALE_402 -> "/maps/iscale_402.txt";
            case ISCALE_403 -> "/maps/iscale_403.txt";
            case ISCALE_404 -> "/maps/iscale_404.txt";
            case ISCALE_405 -> "/maps/iscale_405.txt";
        };
    }
}