/*
 * Copyright (c) 2025.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

 package tank1990.core;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import tank1990.tank.AbstractTank;
import tank1990.tank.TankFactory;
import tank1990.tank.TankType;
import tank1990.tile.*;

public class GameLevel {
    GridLocation[] SPAWN_LOCATIONS = new GridLocation[] {
        new GridLocation(0, 0), new GridLocation(0, 6), new GridLocation(0, 12)
    };

    private Map<TankType, Integer> enemyTankCounts; // Map of tank types to their counts in the level

    private LevelState currentState;

    private Tile[][] map;

    private Dimension gameAreaSize;

    private int activeEnemyTankCount;

    private GridLocation eagleLocation = null;
    private GridLocation playerLocation = null;
    
    public GameLevel(String levelPath) {
        this.currentState = LevelState.NOT_LOADED;
        this.map = MapGenerator.createMap(levelPath);
        this.enemyTankCounts = new HashMap<TankType, Integer>();
        this.activeEnemyTankCount = 0;

        // Set default size values for now, it will be updated on draw method
        this.gameAreaSize = new Dimension(Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT);

        Map<TankType, Integer> enemyTankCounts = new HashMap<>();
        enemyTankCounts.put(TankType.FAST_TANK, 2);
        enemyTankCounts.put(TankType.ARMOR_TANK, 4);
        enemyTankCounts.put(TankType.POWER_TANK, 3);
        enemyTankCounts.put(TankType.BASIC_TANK, 11);

        this.setEnemyTankCounts(enemyTankCounts);

        this.eagleLocation = findEagleLocation();
    }

    /**
     * Gets the current state of the game level.
     * This method returns the current state of the level, which can be used
     * to determine if the level is loaded, in progress, or completed.
     *
     * @return The current LevelState of the game level.
     */
    public LevelState getCurrentState() {
        return this.currentState;
    }

    /**
     * Gets the size of the game area.
     * This method returns the current size of the game area, which is typically
     * updated during the drawing process.
     *
     * @return A Dimension object representing the width and height of the game area.
     */
    public Dimension getGameAreaSize() {
        return this.gameAreaSize;
    }

    /**
     * Gets the count of active enemy tanks in the game level.
     * This method returns the number of enemy tanks that are currently active.
     *
     * @return The count of active enemy tanks.
     */
    public int getActiveEnemyTankCount() {
        return this.activeEnemyTankCount;
    }

    /**
     * Sets the current state of the game level.
     * This method allows the game level to change its state, which can be used
     * to manage transitions between different phases of the game (e.g., loading, playing, completed).
     *
     * @param state The new LevelState to set for the game level.
     */
    public void setCurrentState(LevelState state) {
        this.currentState = state;
    }

    /**
     * Draws the game level on the provided graphics context.
     * This method iterates through all tiles in the map and draws them
     * if their depth is greater than or equal to the specified minimum depth.
     *
     * @param g The graphics context to draw on.
     * @param minDepth The minimum depth of tiles to draw.
     */
    public void draw(Graphics g, int minDepth) {
        for (Tile[] tileRows: this.map) {
            for (Tile tile: tileRows) {
                if (tile!=null && tile.depth>=minDepth) tile.draw(g);
            }
        }

        this.gameAreaSize.width = g.getClipBounds().width;
        this.gameAreaSize.height = g.getClipBounds().height;
    }

    /**
     * Gets the map of tiles for the game level.
     * This method returns a 2D array representing the tiles in the game level.
     *
     * @return A 2D array of Tile objects representing the game level map.
     */
    public Tile[][] getMap() {
        return this.map;
    }

    /**
     * Gets the count of enemy tanks remaining in the game level.
     * This method sums up the counts of all enemy tank types to determine
     * how many enemy tanks are still available to spawn.
     *
     * @return The total number of remaining enemy tanks across all types.
     */
    public int getEnemyTankCounts() {
        int remainingTanks = 0;
        for (Integer count : enemyTankCounts.values()) {
            remainingTanks += count;
        }
        return remainingTanks;
    }

    /**
     * Sets the counts of enemy tanks for the game level.
     * This method allows the game level to define how many of each type of enemy tank
     * should be available for spawning.
     *
     * @param enemyTankCounts A map where keys are TankType and values are the counts of each type.
     */
    public void setEnemyTankCounts(Map<TankType, Integer> enemyTankCounts) {
        this.enemyTankCounts = enemyTankCounts;
    }

    /**
     * Spawns an enemy tank in the game level.
     * This method randomly selects a tank type from the available types
     * and spawns it at a random spawn location with a random direction.
     * It also updates the count of active enemy tanks and the remaining counts of each tank type.
     *
     * @return An instance of AbstractTank representing the spawned enemy tank, or null if no tanks are available to spawn.
     */
    public AbstractTank spawnEnemyTank() {
        if (getRemainingEnemyTanks() <= 0) {
            return null;
        }

        Random random = new Random();
        TankType[] tankTypes = new TankType[] {TankType.BASIC_TANK, TankType.FAST_TANK, TankType.POWER_TANK, TankType.ARMOR_TANK};

        TankType currentTankType = null;
        for (int i = 0; i < tankTypes.length; i++) {
            TankType candidate = tankTypes[random.nextInt(tankTypes.length)];
            if (enemyTankCounts.getOrDefault(candidate, 0) > 0) {
                currentTankType = candidate;
                break;
            }
        }
        if (currentTankType == null) {
            return null;
        }

        int spawnIndex = random.nextInt(SPAWN_LOCATIONS.length);
        GridLocation spawnLocation = SPAWN_LOCATIONS[spawnIndex];
        Direction[] directions = Direction.values();
        Direction spawnDir = directions[random.nextInt(1,directions.length)];

        activeEnemyTankCount++;
        enemyTankCounts.put(currentTankType, enemyTankCounts.get(currentTankType) - 1);
        System.out.println("Spawning enemy tank: " + currentTankType + " at " + spawnLocation + " facing " + spawnDir + ". Remaining: " + enemyTankCounts.get(currentTankType) + " Active: " + activeEnemyTankCount);
        Location loc = Utils.gridLoc2Loc(spawnLocation);

        return TankFactory.createTank(currentTankType, loc.x(), loc.y(), spawnDir);
    }

    /**
     * Gets the count of enemy tanks remaining in the game level.
     * This method sums up the counts of all enemy tank types.
     *
     * @return The total number of remaining enemy tanks.
     */
    public int getRemainingEnemyTanks() {
        return enemyTankCounts.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Finds the eagle location in the game level.
     * This method searches through the map for a tile of type TILE_EAGLE
     * and sets the eagleLocation field accordingly.
     *
     * @return The location of the eagle in grid coordinates, or null if not found.
     */
    public GridLocation findEagleLocation() {
        if (this.eagleLocation != null) {
            return this.eagleLocation;
        }

        for (int i = 0; i < Globals.ROW_TILE_COUNT; i++) {
            for (int j = 0; j < Globals.COL_TILE_COUNT; j++) {
                Tile tile = this.map[i][j];
                if (tile==null) continue;

                if (tile.getType() == TileType.TILE_EAGLE) {
                    this.eagleLocation = new GridLocation(i, j);
                    return this.eagleLocation;
                }
            }
        }
        return null; // No eagle found
    }

    /**
     * Gets the eagle location in the game level.
     * This is typically used to determine where the eagle is positioned for gameplay purposes.
     *
     * @return The current location of the eagle in grid coordinates, or null if not set.
     */
    public GridLocation getEagleLocation() {
        return this.eagleLocation;
    }

    /**
     * Gets the player location in the game level.
     * This is typically used to determine where the player is currently positioned.
     *
     * @return The current location of the player in grid coordinates.
     */
    public GridLocation getPlayerLocation() {
        return this.playerLocation;
    }

    /**
     * Sets the player location in the game level.
     * This is typically used to update the player's position after movement.
     *
     * @param playerLocation The new location of the player in grid coordinates.
     */
    public void setPlayerLocation(GridLocation playerLocation) {
        this.playerLocation = playerLocation;
    }

    /**
     * Calculates the distance from the origin to the eagle location.
     * The distance is defined as the sum of the differences in row and column indices.
     * If the eagle location is not set, returns -1.
     *
     * @param origin The origin location from which to calculate the distance.
     * @return The distance to the eagle location, or -1 if the eagle location is not set.
     */
    public int getEagleDistance(GridLocation origin) {
        if (this.eagleLocation == null) {
            return -1;
        }

        int dx = Math.abs(this.eagleLocation.colIndex() - origin.colIndex());
        int dy = Math.abs(this.eagleLocation.rowIndex() - origin.rowIndex());

        return dx+dy;
    }

    /**
     * Calculates the distance from the origin to the player location.
     * The distance is defined as the sum of the differences in row and column indices.
     * If the player location is not set, returns -1.
     *
     * @param origin The origin location from which to calculate the distance.
     * @return The distance to the player location, or -1 if the player location is not set.
     */
    public int getPlayerDistance(GridLocation origin) {
        if (this.playerLocation == null) {
            return -1;
        }

        int dx = Math.abs(this.playerLocation.colIndex() - origin.colIndex());
        int dy = Math.abs(this.playerLocation.rowIndex() - origin.rowIndex());

        return dx+dy;
    }

    /**
     * Checks if the eagle is alive in the game level.
     * The eagle is considered alive if its location is set and the corresponding tile in the map is not null.
     *
     * @return true if the eagle is alive, false otherwise.
     */
    public boolean isEagleAlive() {
        return this.eagleLocation != null && this.map[this.eagleLocation.rowIndex()][this.eagleLocation.colIndex()] != null;
    }

    public boolean checkMovable(Location location, Dimension size) {
        int maxWidth = getGameAreaSize().width;
        int maxHeight = getGameAreaSize().height;

        Tile[][] map = getMap();

        // Get tank dimensions
        int tankWidth = (int) size.getWidth();
        int tankHeight = (int) size.getHeight();

        // If tank size is not set, calculate it based on grid
        if (tankWidth == 0 || tankHeight == 0) {
            int cellWidth = maxWidth / Globals.COL_TILE_COUNT;
            int cellHeight = maxHeight / Globals.ROW_TILE_COUNT;
            tankWidth = cellWidth - 2;
            tankHeight = cellHeight - 2;
        }

        // x and y are the CENTER coordinates of the tank, so we need to calculate the actual corners
        int halfWidth = tankWidth / 2;
        int halfHeight = tankHeight / 2;

        // Calculate the actual bounding box corners from the center point
        int topLeftX = location.x() - halfWidth;
        int topLeftY = location.y() - halfHeight;
        int bottomRightX = location.x() + halfWidth - 1;
        int bottomRightY = location.y() + halfHeight - 1;

        // Check key points around the tank's perimeter
        int[] checkX = {
                topLeftX,               // Top-left corner
                bottomRightX,           // Top-right corner
                topLeftX,               // Bottom-left corner
                bottomRightX,           // Bottom-right corner
                location.x()            // Center point for good measure
        };
        int[] checkY = {
                topLeftY,               // Top-left corner
                topLeftY,               // Top-right corner
                bottomRightY,           // Bottom-left corner
                bottomRightY,           // Bottom-right corner
                location.y()            // Center point for good measure
        };

        for (int i = 0; i < checkX.length; i++) {
            GridLocation gLoc = Utils.Loc2GridLoc(new Location(checkX[i], checkY[i]));

            int r = gLoc.rowIndex();
            int c = gLoc.colIndex();

            // Check bounds
            if (r < 0 || r >= Globals.ROW_TILE_COUNT || c < 0 || c >= Globals.COL_TILE_COUNT) {
                return false;
            }

            //System.out.println("Checking point " + i + " at row:" + r + " col:" + c + " (coords: " + checkX[i] + "," + checkY[i] + ")");

            if (map[r][c] instanceof TileBricks) { return false; /* player cannot move through bricks*/ }
            else if (map[r][c] instanceof TileSteel) { return false; /* player cannot move through steel*/ }
            else if (map[r][c] instanceof TileTrees) { /* player can move through trees*/ }
            else if (map[r][c] instanceof TileSea) { return false; /* player cannot move through sea unless it has boat*/ }
            else if (map[r][c] instanceof TileIce) {  /* player can move through ice*/  }
            else if (map[r][c] instanceof TileEagle) { return false; /* player cannot move through eagle*/ }
            else { }

            // Check if the tile owns a tank
            if (map[r][c] != null && map[r][c].includesTank()) return false;
        }
        return true;
    }
}
