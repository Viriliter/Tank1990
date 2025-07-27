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
import java.util.*;

import tank1990.powerup.AbstractPowerup;
import tank1990.powerup.PowerupType;
import tank1990.powerup.PowerupFactory;
import tank1990.tank.AbstractTank;
import tank1990.tank.TankFactory;
import tank1990.tank.TankType;
import tank1990.tile.*;

public class GameLevel {
    // Create a min-heap for spawn location which spawn location anbd timestamp of last enemy tank created.
    PriorityQueue<Map.Entry<GridLocation, Long>> spawnLocations = new PriorityQueue<>(Comparator.comparingLong(Map.Entry::getValue));

    GridLocation[] SPAWN_LOCATIONS = new GridLocation[] {
        new GridLocation(0, 0), new GridLocation(0, 6), new GridLocation(0, 12)
    };

    private Map<TankType, Integer> enemyTankCounts; // Map of tank types to their counts in the level

    private LevelState currentState;

    private LevelInfo levelInfo;

    private Dimension gameAreaSize;

    private int activeEnemyTankCount;

    private GridLocation eagleLocation = null;
    private GridLocation playerLocation = null;

    private final int totalEnemyTankCount; // Total number of enemy tanks in the level

    private final List<Integer> MAGIC_NUMBERS = List.of(4, 11, 18); // Magic numbers used to determine which enemy tanks are spawned as red tanks in the level

    private List<Tile> tilesAroundEagle = new ArrayList<>();

    public GameLevel(String levelPath) {
        this.currentState = LevelState.NOT_LOADED;
        this.levelInfo = MapGenerator.readLevelInfo(levelPath);
        this.enemyTankCounts = new HashMap<TankType, Integer>();
        this.activeEnemyTankCount = 0;

        // Set default size values for now, it will be updated on draw method
        this.gameAreaSize = new Dimension(Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT);

        Map<TankType, Integer> enemyTankCount = this.levelInfo.enemyTankCount;
        this.setEnemyTankCount(enemyTankCount);
        this.totalEnemyTankCount = enemyTankCount.values().stream().mapToInt(Integer::intValue).sum();

        this.eagleLocation = findEagleLocation();

        // Default timestamp for spawn locations is -1 which means invalid
        spawnLocations.add(new AbstractMap.SimpleEntry<>(new GridLocation(0, 0), -1L));
        spawnLocations.add(new AbstractMap.SimpleEntry<>(new GridLocation(0, 6), -1L));
        spawnLocations.add(new AbstractMap.SimpleEntry<>(new GridLocation(0, 12), -1L));
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

    public void decreaseActiveEnemyTank() {
        this.activeEnemyTankCount = this.activeEnemyTankCount>0 ? this.activeEnemyTankCount - 1 : 0;
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

    public void update() {
        // Check timestamp of spawn time. Set the timestamp to invalid if it is too old
        Map.Entry<GridLocation, Long> oldestSpawnLocationEntry = this.spawnLocations.poll();

        if (oldestSpawnLocationEntry!=null) {
            if (System.currentTimeMillis() - oldestSpawnLocationEntry.getValue() > Globals.ENEMY_TANK_SPAWN_DELAY_MS) {
                // Set the timestamp to invalid
                oldestSpawnLocationEntry.setValue(-1L);
            }
            // Update the list
            this.spawnLocations.add(oldestSpawnLocationEntry);
        }

        for (int row = 0; row < this.levelInfo.levelGrid.length; row++) {
            for (int col = 0; col < this.levelInfo.levelGrid[row].length; col++) {
                Tile tile = this.levelInfo.levelGrid[row][col];
                if (tile == null) continue;

                if (tile.isDestroyed()) {
                    this.levelInfo.levelGrid[row][col] = null; // Set the array element to null
                } else {
                    tile.update();
                }
            }
        }
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
        for (Tile[] tileRows: this.levelInfo.levelGrid) {
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
        return this.levelInfo.levelGrid;
    }

    public Tile[] getNeighbors(GridLocation gloc) {
        if (gloc == null) return null;

        Tile[] neighbors = new Tile[9];
        int row = gloc.rowIndex();
        int col = gloc.colIndex();

        // Add the center tile
        neighbors[0] = this.levelInfo.levelGrid[row][col];

        // Check bounds and get all 8 neighbors (including corners)
        if (row > 0) neighbors[1] = this.levelInfo.levelGrid[row - 1][col]; // Up
        if (col < Globals.COL_TILE_COUNT - 1) neighbors[2] = this.levelInfo.levelGrid[row][col + 1]; // Right
        if (row < Globals.ROW_TILE_COUNT - 1) neighbors[3] = this.levelInfo.levelGrid[row + 1][col]; // Down
        if (col > 0) neighbors[4] = this.levelInfo.levelGrid[row][col - 1]; // Left
        if (row > 0 && col > 0) neighbors[5] = this.levelInfo.levelGrid[row - 1][col - 1]; // Top-Left
        if (row > 0 && col < Globals.COL_TILE_COUNT - 1) neighbors[6] = this.levelInfo.levelGrid[row - 1][col + 1]; // Top-Right
        if (row < Globals.ROW_TILE_COUNT - 1 && col < Globals.COL_TILE_COUNT - 1) neighbors[7] = this.levelInfo.levelGrid[row + 1][col + 1]; // Bottom-Right
        if (row < Globals.ROW_TILE_COUNT - 1 && col > 0) neighbors[8] = this.levelInfo.levelGrid[row + 1][col - 1]; // Bottom-Left

        return neighbors;
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
    public void setEnemyTankCount(Map<TankType, Integer> enemyTankCounts) {
        this.enemyTankCounts = enemyTankCounts;
    }

    public AbstractPowerup spawnPowerup() {
        Random random = new Random();
        PowerupType powerupType = PowerupType.valueOf(random.nextInt(PowerupType.values().length));

        Dimension gameArea = getGameAreaSize();

        // Ensure the powerup spawns within the game area, leaving space for its dimensions
        int x = random.nextInt(gameArea.width - Globals.POWERUP_WIDTH);
        int y = random.nextInt(gameArea.height - Globals.POWERUP_HEIGHT);

        AbstractPowerup powerup = PowerupFactory.createPowerup(powerupType, x, y);
        if (powerup != null) {
            System.out.println("Spawning powerup: " + powerupType + " at (" + x + ", " + y + ")");
        }
        return powerup;
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

        Map.Entry<GridLocation, Long> spawnLocationEntry = this.spawnLocations.poll();

        if (spawnLocationEntry==null) return null;

        //If spawn time is not old enough, do not spawn any tank
        if (spawnLocationEntry.getValue()>0) {
            //Update the list
            this.spawnLocations.add(spawnLocationEntry);
            return null;
        }

        // Set timestamp of spawn time
        spawnLocationEntry.setValue(System.currentTimeMillis());

        // Update the list
        this.spawnLocations.add(spawnLocationEntry);

        Direction[] directions = Direction.values();
        Direction spawnDir = directions[random.nextInt(1,directions.length)];

        this.activeEnemyTankCount++;
        enemyTankCounts.put(currentTankType, enemyTankCounts.get(currentTankType) - 1);
        System.out.println("Spawning enemy tank: " + currentTankType + " at " + spawnLocationEntry.getKey() + " facing " + spawnDir + ". Remaining: " + enemyTankCounts.get(currentTankType) + " Active: " + activeEnemyTankCount);
        Location loc = Utils.gridLoc2Loc(spawnLocationEntry.getKey());

        AbstractTank enemyTank = TankFactory.createTank(currentTankType, loc.x(), loc.y(), spawnDir);

        int nThTank = this.totalEnemyTankCount - getRemainingEnemyTanks() + 1;
        if (MAGIC_NUMBERS.contains(nThTank)) {
            assert enemyTank != null;
            enemyTank.setAsRed();
        } else {
            assert enemyTank != null;
            // There is a 30% chance to spawn a red tank
            if (Utils.getRandomProbability(30)) enemyTank.setAsRed();
        }

        return enemyTank;
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
                Tile tile = this.levelInfo.levelGrid[i][j];
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
        return this.eagleLocation != null && this.levelInfo.levelGrid[this.eagleLocation.rowIndex()][this.eagleLocation.colIndex()] != null;
    }

    public boolean checkMovable(RectangleBound tankBound) {
        GridLocation tankGLoc = Utils.Loc2GridLoc(new Location(tankBound.getOriginX() ,tankBound.getOriginY()));
        Tile[] neighbors = getNeighbors(tankGLoc);

        for (Tile neighbor : neighbors) {
            if (neighbor == null) continue;

            if (neighbor.getType() == TileType.TILE_ICE) continue;
            if (neighbor.getType() == TileType.TILE_TREES) continue;

            RectangleBound tileBound = neighbor.getBoundingBox();
            // Check if the neighbor tile is occupied by a tank
            boolean isCollided = RectangleBound.isCollided(tileBound, tankBound);
            if (isCollided) return false;
        }
        return true;
    }

    public void shovelAroundEagle() {
        if (this.eagleLocation == null) return;

        //int row = this.eagleLocation.rowIndex();
        //int col = this.eagleLocation.colIndex();
//
        //// Check bounds and convert tiles around the eagle to steel
        //for (int i = -1; i <= 1; i++) {
        //    for (int j = -1; j <= 1; j++) {
        //        int newRow = row + i;
        //        int newCol = col + j;
//
        //        if (newRow >= 0 && newRow < Globals.ROW_TILE_COUNT && newCol >= 0 && newCol < Globals.COL_TILE_COUNT) {
        //            // Convert any tile (including null) to steel tiles
        //            Location tileLocation = Utils.gridLoc2Loc(new GridLocation(newRow, newCol));
        //            this.levelInfo.levelGrid[newRow][newCol] = TileFactory.createTile(TileType.TILE_STEEL, tileLocation.x(), tileLocation.y(), BlockConfiguration.BLOCK_CONF_FULL);
        //        }
        //    }
        //}
    }

    public void removeShovelAroundEagle() {
    }

    public void removeEagleProtection() {}

    public void enableEagleProtection() {}

}
