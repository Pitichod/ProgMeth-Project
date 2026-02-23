package logic.game;

import logic.components.Direction;
import objects.Human;
import objects.items.BaseItem;
import objects.obstacles.Cable;
import objects.obstacles.Chair;
import rewards.Reward;

public class GameEngine {
    private final GameSession session;
    private final LevelId levelId;
    private final int maxHealth = 5;
    private final int maxStamina;
    private GameBoard board;
    private String statusMessage = "Move with Arrow keys or WASD.";
    private boolean completed;

    public GameEngine(GameSession session, LevelId levelId) {
        this.session = session;
        this.levelId = levelId;
        session.startLevel(levelId);
        this.maxStamina = session.getCurrentLevel().getDefaultStamina();
        reloadBoard();
    }

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

        Chair obstacle = board.findMoveableObstacleAt(targetX, targetY);
        if (obstacle != null) {
            int pushToX = targetX + direction.getDx();
            int pushToY = targetY + direction.getDy();
            if (!board.isInBounds(pushToX, pushToY) || board.isBlockingCell(pushToX, pushToY) || board.isDoor(pushToX, pushToY)) {
                statusMessage = "Cannot push this obstacle.";
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

    public GameBoard getBoard() {
        return board;
    }

    public Player getPlayer() {
        return session.getPlayer();
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public String getLevelLabel() {
        return switch (levelId) {
            case ISCALE_401 -> "LEVEL 1 (ISCALE 401)";
            case ISCALE_402 -> "LEVEL 2 (ISCALE 402)";
            case ISCALE_403 -> "LEVEL 3 (ISCALE 403)";
            case ISCALE_404 -> "LEVEL 4 (ISCALE 404)";
            case ISCALE_405 -> "LEVEL 5 (ISCALE 405)";
        };
    }

    public LevelId getNextLevelId() {
        return switch (levelId) {
            case ISCALE_401 -> LevelId.ISCALE_402;
            case ISCALE_402 -> LevelId.ISCALE_403;
            case ISCALE_403 -> LevelId.ISCALE_404;
            case ISCALE_404 -> LevelId.ISCALE_405;
            case ISCALE_405 -> null;
        };
    }

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