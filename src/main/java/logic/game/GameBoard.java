package logic.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import objects.Human;
import objects.Introvert;
import objects.Extrovert;
import objects.TA;
import objects.Teacher;
import objects.items.BaseItem;
import objects.items.Caffeine;
import objects.items.Parabola;
import objects.items.RobuxGiftCard;
import objects.obstacles.Cable;
import objects.obstacles.Chair;
import objects.obstacles.Table;
import objects.BaseObject;

/**
 * Represents the game board (map) for a single level.
 * Parses a text-based map file from resources and stores walls, obstacles,
 * cables, items, enemies, the player start position, and the exit door position.
 * Provides lookup methods for finding objects at specific grid coordinates.
 */
public class GameBoard {
    private final int width;
    private final int height;
    private final boolean[][] walls;
    private final List<BaseObject> moveableObstacles = new ArrayList<>();
    private final List<Cable> cables = new ArrayList<>();
    private final List<Human> humans = new ArrayList<>();
    private final List<BaseItem> items = new ArrayList<>();
    private int playerStartX;
    private int playerStartY;
    private int doorX;
    private int doorY;

    private GameBoard(int width, int height) {
        this.width = width;
        this.height = height;
        this.walls = new boolean[height][width];
    }

    /**
     * Loads and parses a map file from the classpath.
     * Each character in the text file represents a tile (wall, floor, enemy, item, etc.).
     *
     * @param resourcePath the classpath resource path to the map file
     * @return the parsed {@link GameBoard}
     * @throws IllegalStateException if the file cannot be found, read, or contains invalid data
     */
    public static GameBoard fromResource(String resourcePath) {
        InputStream in = GameBoard.class.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalStateException("Map file not found: " + resourcePath);
        }

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read map file: " + resourcePath, e);
        }

        if (lines.isEmpty()) {
            throw new IllegalStateException("Map is empty: " + resourcePath);
        }

        int height = lines.size();
        int width = lines.get(0).length();
        GameBoard board = new GameBoard(width, height);

        for (int y = 0; y < height; y++) {
            String row = lines.get(y);
            if (row.length() != width) {
                throw new IllegalStateException("Map rows must have equal width: " + resourcePath);
            }
            for (int x = 0; x < width; x++) {
                board.parseSymbol(row.charAt(x), x, y);
            }
        }

        return board;
    }

    private void parseSymbol(char symbol, int x, int y) {
        switch (symbol) {
            case '#':
                walls[y][x] = true;
                break;
            case 'P':
                playerStartX = x;
                playerStartY = y;
                break;
            case 'D':
                doorX = x;
                doorY = y;
                break;
            case 'C':
            case 'c':
                moveableObstacles.add(new Chair(x, y, "Front"));
                break;
            case 'B':
            case 'b':
                moveableObstacles.add(new Chair(x, y, "Back"));
                break;
            case 'J':
            case 'j':
                moveableObstacles.add(new Chair(x, y, "Left"));
                break;
            case 'K':
            case 'k':
                moveableObstacles.add(new Chair(x, y, "Right"));
                break;
            case 'T':
            case 't':
                moveableObstacles.add(new Table(x, y, "Down"));
                break;
            case 'U':
            case 'u':
                moveableObstacles.add(new Table(x, y, "Up"));
                break;
            case 'L':
            case 'l':
                moveableObstacles.add(new Table(x, y, "Left"));
                break;
            case 'H':
            case 'h':
                moveableObstacles.add(new Table(x, y, "Right"));
                break;
            case 'W':
            case 'w':
                cables.add(new Cable(x, y));
                break;
            // Plugs (single-char map symbols) -> choose mapping here
            case '<':
                cables.add(new Cable(x, y, "PlugLeft"));
                break;
            case '>':
                cables.add(new Cable(x, y, "PlugRight"));
                break;
            case '^':
                cables.add(new Cable(x, y, "PlugUp"));
                break;
            case 'v':
            case 'V':
                cables.add(new Cable(x, y, "PlugDown"));
                break;
            // Horizontal / vertical wires
            case '-':
                cables.add(new Cable(x, y, "WireH"));
                break;
            case '|':
                cables.add(new Cable(x, y, "WireV"));
                break;
            // HPlug variants (alternate plug visuals) - use digits to avoid conflicting with existing letters
            case '4':
                cables.add(new Cable(x, y, "HPlugLeft"));
                break;
            case '5':
                cables.add(new Cable(x, y, "HPlugRight"));
                break;
            case '6':
                cables.add(new Cable(x, y, "HPlugUp"));
                break;
            case '7':
                cables.add(new Cable(x, y, "HPlugDown"));
                break;
            case 'I':
            case 'i':
                humans.add(new Introvert(x, y));
                break;
            case 'E':
            case 'e':
                humans.add(new Extrovert(x, y));
                break;
            case 'A':
            case 'a':
                humans.add(new TA(x, y));
                break;
            case 'R':
            case 'r':
                humans.add(new Teacher(x, y));
                break;
            case '1':
                items.add(new Parabola(x, y));
                break;
            case '2':
                items.add(new Caffeine(x, y));
                break;
            case '3':
                items.add(new RobuxGiftCard(x, y));
                break;
            case '.':
                break;
            default:
                throw new IllegalStateException("Unknown map symbol: " + symbol + " at (" + x + ", " + y + ")");
        }
    }

    /**
     * Returns the width of the board in tiles.
     *
     * @return the number of columns
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the board in tiles.
     *
     * @return the number of rows
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns whether the given cell is a wall or out of bounds.
     *
     * @param x the column
     * @param y the row
     * @return {@code true} if the cell is a wall or out of bounds
     */
    public boolean isWall(int x, int y) {
        return !isInBounds(x, y) || walls[y][x];
    }

    /**
     * Returns whether the given coordinates are within the board boundaries.
     *
     * @param x the column
     * @param y the row
     * @return {@code true} if in bounds
     */
    public boolean isInBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    /**
     * Returns the player's starting column.
     *
     * @return the start X coordinate
     */
    public int getPlayerStartX() {
        return playerStartX;
    }

    /**
     * Returns the player's starting row.
     *
     * @return the start Y coordinate
     */
    public int getPlayerStartY() {
        return playerStartY;
    }

    /**
     * Returns the exit door column.
     *
     * @return the door X coordinate
     */
    public int getDoorX() {
        return doorX;
    }

    /**
     * Returns the exit door row.
     *
     * @return the door Y coordinate
     */
    public int getDoorY() {
        return doorY;
    }

    /**
     * Returns the list of moveable obstacles (chairs and tables) on the board.
     *
     * @return the list of moveable obstacles
     */
    public List<BaseObject> getMoveableObstacles() {
        return moveableObstacles;
    }

    /**
     * Returns the list of cables on the board.
     *
     * @return the list of cables
     */
    public List<Cable> getCables() {
        return cables;
    }

    /**
     * Returns the list of human enemies on the board.
     *
     * @return the list of humans
     */
    public List<Human> getHumans() {
        return humans;
    }

    /**
     * Returns the list of collectible items on the board.
     *
     * @return the list of items
     */
    public List<BaseItem> getItems() {
        return items;
    }

    /**
     * Finds an active human at the given grid position.
     *
     * @param x the column
     * @param y the row
     * @return the {@link Human} at that position, or {@code null} if none
     */
    public Human findHumanAt(int x, int y) {
        for (Human human : humans) {
            if (human.isActive() && human.getX() == x && human.getY() == y) {
                return human;
            }
        }
        return null;
    }

    /**
     * Finds a moveable obstacle at the given grid position.
     *
     * @param x the column
     * @param y the row
     * @return the obstacle at that position, or {@code null} if none
     */
    public BaseObject findMoveableObstacleAt(int x, int y) {
        for (BaseObject obstacle : moveableObstacles) {
            if (obstacle.getX() == x && obstacle.getY() == y) {
                return obstacle;
            }
        }
        return null;
    }

    /**
     * Finds a cable at the given grid position.
     *
     * @param x the column
     * @param y the row
     * @return the {@link Cable} at that position, or {@code null} if none
     */
    public Cable findCableAt(int x, int y) {
        for (Cable cable : cables) {
            if (cable.getX() == x && cable.getY() == y) {
                return cable;
            }
        }
        return null;
    }

    /**
     * Finds an unconsumed item at the given grid position.
     *
     * @param x the column
     * @param y the row
     * @return the {@link BaseItem} at that position, or {@code null} if none
     */
    public BaseItem findItemAt(int x, int y) {
        for (BaseItem item : items) {
            if (!item.isConsumed() && item.getX() == x && item.getY() == y) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns whether the given cell is blocked (wall, obstacle, or active human).
     *
     * @param x the column
     * @param y the row
     * @return {@code true} if the cell cannot be entered
     */
    public boolean isBlockingCell(int x, int y) {
        if (isWall(x, y)) {
            return true;
        }
        if (findMoveableObstacleAt(x, y) != null) {
            return true;
        }
        return findHumanAt(x, y) != null;
    }

    /**
     * Returns whether the given cell is the exit door.
     *
     * @param x the column
     * @param y the row
     * @return {@code true} if this is the door position
     */
    public boolean isDoor(int x, int y) {
        return x == doorX && y == doorY;
    }
}