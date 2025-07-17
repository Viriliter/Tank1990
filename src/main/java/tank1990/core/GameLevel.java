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
import tank1990.tile.Tile;

public class GameLevel {
    // Define game level status here
    public static final int STAGE_NOT_LOADED = 0;
    public static final int STAGE_LOADED = 1;
    public static final int STAGE_GET_READY = 2;
    public static final int STAGE_PLAYING = 3;
    public static final int STAGE_PAUSED = 4;
    public static final int STAGE_GAME_OVER = 5;

    GridLocation[] SPAWN_LOCATIONS = new GridLocation[] {
        new GridLocation(0, 0), new GridLocation(0, 1), new GridLocation(1, 0), new GridLocation(1, 1),
        new GridLocation(2, 0), new GridLocation(2, 1), new GridLocation(3, 0), new GridLocation(3, 1)
    };

    private Map<TankType, Integer> enemyTankCounts; // Map of tank types to their counts in the level

    private LevelState currentState;

    private Tile[][] map;

    private Dimension gameAreaSize;
    
    public GameLevel(String levelPath) {
        this.currentState = LevelState.NOT_LOADED;
        this.map = MapGenerator.createMap(levelPath);
        this.enemyTankCounts = new HashMap<TankType, Integer>();

        // Set default size values for now, it will updated on draw method
        this.gameAreaSize = new Dimension(Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT);
        //enemyTankCounts = loadenemyTankCounts(levelPath);
    }

    public LevelState getCurrentState() {
        return this.currentState;
    }

    public Dimension getGameAreaSize() {
        return this.gameAreaSize;
    }

    public void setCurrentState(LevelState state) {
        this.currentState = state;
    }

    public void draw(Graphics g, int minDepth) {
        for (Tile[] tileRows: this.map) {
            for (Tile tile: tileRows) {
                if (tile!=null && tile.depth>=minDepth) tile.draw(g);
            }
        }

        this.gameAreaSize.width = g.getClipBounds().width;
        this.gameAreaSize.height = g.getClipBounds().height;
    }

    public void update() {
        
    }

    public Tile[][] getMap() {
        return this.map;
    }

    public Map<TankType, Integer> getEnemyTankCounts() {
        return enemyTankCounts;
    }

    public void setEnemyTankCounts(Map<TankType, Integer> enemyTankCounts) {
        this.enemyTankCounts = enemyTankCounts;
    }

    public AbstractTank spawnEnemyTank() {
      // Check if there are any zombies left to spawn
        if (getRemainingEnemyTanks() <= 0) {
            return null;
        }

        Random random = new Random();

        // Get tank type from randomized list
        TankType[] tankTypes = new TankType[] {TankType.BASIC_TANK, TankType.FAST_TANK, TankType.POWER_TANK, TankType.ARMOR_TANK };
        TankType currentTankType;
        try {
            currentTankType = tankTypes[random.nextInt(tankTypes.length)];
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }

        // Randomly choose a spawn location
        int spawnIndex = random.nextInt(SPAWN_LOCATIONS.length);
        GridLocation spawnLocation = SPAWN_LOCATIONS[spawnIndex];
        // Randomly choose a spawn direction
        Direction[] directions = Direction.values();
        Direction spawnDir = directions[random.nextInt(directions.length)];

        // Spawn an enemy tank
        switch(currentTankType) {
            case BASIC_TANK:
                this.enemyTankCounts.put(TankType.BASIC_TANK, this.enemyTankCounts.get(TankType.BASIC_TANK) - 1);
                return TankFactory.createTank(TankType.BASIC_TANK, spawnLocation.colIndex(), spawnLocation.rowIndex(), spawnDir);
            case FAST_TANK:
                this.enemyTankCounts.put(TankType.FAST_TANK, this.enemyTankCounts.get(TankType.FAST_TANK) - 1);
                return TankFactory.createTank(TankType.FAST_TANK, spawnLocation.colIndex(), spawnLocation.rowIndex(), spawnDir);
            case POWER_TANK:
                this.enemyTankCounts.put(TankType.POWER_TANK, this.enemyTankCounts.get(TankType.POWER_TANK) - 1);
                return TankFactory.createTank(TankType.POWER_TANK, spawnLocation.colIndex(), spawnLocation.rowIndex(), spawnDir);
            case ARMOR_TANK:
                this.enemyTankCounts.put(TankType.ARMOR_TANK, this.enemyTankCounts.get(TankType.ARMOR_TANK) - 1);
                return TankFactory.createTank(TankType.ARMOR_TANK, spawnLocation.colIndex(), spawnLocation.rowIndex(), spawnDir);
            default:
                return null;
        }    
    }

    public int getRemainingEnemyTanks() {
        return enemyTankCounts.values().stream().mapToInt(Integer::intValue).sum();
    }
}
