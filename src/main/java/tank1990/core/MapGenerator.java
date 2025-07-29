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

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Random;

import tank1990.tile.BlockConfiguration;
import tank1990.tile.Tile;
import tank1990.tile.TileFactory;
import tank1990.tile.TileType;
import tank1990.tank.TankType;

public class MapGenerator {

    public static LevelInfo readLevelInfo(String filePath) {
        LevelInfo levelInfo = loadFromBinary(filePath);

        return levelInfo;
    }

    public static void saveToBinary(String sourceTxtPath, String targetBinPath) {
        ObjectOutputStream os = null;
        try {
            LevelInfo levelInfo = MapGenerator.createFromText(sourceTxtPath);

            os = new ObjectOutputStream(new FileOutputStream(targetBinPath));
            os.writeObject(levelInfo);
            os.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }    
    }

    public static void printGrid(Tile[][] grid) {
        System.out.print("||===|===|===|===|===|===|===|===|===|===|===|===|===||\n");
        for (int i = 0; i< Globals.ROW_TILE_COUNT; i++) {
            for (int j = 0; j< Globals.COL_TILE_COUNT; j++) {
                if (j==0) System.out.print("|");

                String type = "";
                Tile tile = grid[i][j];
                if (tile==null) {
                    System.out.printf("|- -", type);
                } else {
                    switch(tile.getType()) {
                        case TileType.TILE_BRICKS: type = "B"; break;
                        case TileType.TILE_STEEL: type = "S"; break;
                        case TileType.TILE_TREES: type = "T"; break;
                        case TileType.TILE_SEA: type = "s"; break;
                        case TileType.TILE_ICE: type = "I"; break;
                        case TileType.TILE_EAGLE: type = "E"; break;
                        default: type = "?"; break;
                    }
                    System.out.printf("|-%s-", type);
                }

                if (j== Globals.COL_TILE_COUNT-1) System.out.print("||\n");
            }

            if (i!= Globals.ROW_TILE_COUNT-1) System.out.print("||---|---|---|---|---|---|---|---|---|---|---|---|---||\n");
        }
        System.out.print("||===|===|===|===|===|===|===|===|===|===|===|===|===||\n");
    }

    private static LevelInfo loadFromBinary(String filePath) {
        try {
            InputStream inputStream = MapGenerator.class.getClassLoader().getResourceAsStream(filePath);
            ObjectInputStream os = new ObjectInputStream(inputStream);
            LevelInfo levelInfo = (LevelInfo) os.readObject();
            os.close();
            return levelInfo;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static LevelInfo createFromText(String filePath) throws FileNotFoundException {
        Tile[][] grid = new Tile[Globals.ROW_TILE_COUNT][Globals.COL_TILE_COUNT];
        HashMap<TankType, Integer> enemyTankCount = new HashMap<>();

        FileInputStream inputStream = new FileInputStream(filePath);
        assert inputStream != null;
        Scanner scanner = new Scanner(inputStream);

        boolean isFirstLine = true;
        try {
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] params = line.split("\\s+");

                // First line of the text file contains number of enemy tanks
                if (isFirstLine) {
                    int i=0;

                    TankType tankType = null;
                    for (String param : params) {
                        if (i%2 == 0) {
                            tankType = TankType.valueOf(param);
                        } else {
                            int tankCount = Integer.parseInt(param);
                            enemyTankCount.put(tankType, tankCount);
                        }
                        i++;
                    }

                    isFirstLine = false;
                    continue;
                }

                if (params.length<3) continue;

                TileType tileType = TileType.valueOf(params[0]);
                int rowIndex = Integer.parseInt(params[1]);
                int colIndex = Integer.parseInt(params[2]);

                // If extra parameter provided for block configuration set it, otherwise set as full block
                BlockConfiguration blockConf = BlockConfiguration.BLOCK_CONF_FULL;
                if (params.length==4) {
                    blockConf = BlockConfiguration.valueOf(Integer.parseInt(params[3]));
                }
                
                grid[rowIndex][colIndex] = TileFactory.createTile(tileType, colIndex, rowIndex, blockConf);
            }
        } finally {
            scanner.close();
        }

        return new LevelInfo(grid, enemyTankCount);
    }
    
    public static HashMap<TankType, Integer> generateRandomEnemies() {
        Random random = new Random();

        HashMap<TankType, Integer> enemyMap = new HashMap<TankType, Integer>();
        for (int i=0; i<TankType.values().length; i++) {
            TankType key = TankType.valueOf(i);

            if (key == TankType.PLAYER_TANK) continue;

            enemyMap.put(key, 0);
        }

        for (int i=0; i<20; i++) {
            int tankTypeId = random.nextInt(TankType.values().length);
            TankType key = TankType.valueOf(tankTypeId);

            if (key == TankType.PLAYER_TANK) {i--; continue;}
            
            enemyMap.put(key, enemyMap.get(key)+1);
        }

        return enemyMap;
    }

    public static Tile[] getNeighbors(Tile[][] grid, GridLocation gLoc) {
        Tile[] neighbors = new Tile[5]; 
        for (int i=-1; i<=1; i++) {
            for (int j=-1; j<=1; j++) {
                if (gLoc.rowIndex()==i && gLoc.colIndex()==j) continue;

                if (gLoc.rowIndex()+i<0 || gLoc.colIndex()+j<0) continue;

                if (gLoc.rowIndex()+i>=Globals.ROW_TILE_COUNT || gLoc.colIndex()+j>=Globals.COL_TILE_COUNT) continue;

                Tile tile = grid[gLoc.rowIndex()+i][gLoc.colIndex()+j];
                neighbors[i+1+j+1] = tile;
            }   
        }
        return neighbors;
    }

    public static TileType getRandomTileType() {
        Random random = new Random();
        TileType tentativeTileType = TileType.valueOf(random.nextInt(TileType.values().length));

        if (tentativeTileType == TileType.TILE_EAGLE) return getRandomTileType();

        return tentativeTileType;
    }

    public static Tile[][] generateRandomGrid() {
        Random random = new Random();

        final GridLocation PROTECTION_LOCATION_1 = new GridLocation(11, 5);
        final GridLocation PROTECTION_LOCATION_2 = new GridLocation(11, 6);
        final GridLocation PROTECTION_LOCATION_3 = new GridLocation(11, 7);
        final GridLocation PROTECTION_LOCATION_4 = new GridLocation(12, 5);
        final GridLocation EAGLE_LOCATION = new GridLocation(12, 6);
        final GridLocation PROTECTION_LOCATION_5 = new GridLocation(12, 7);
        ArrayList<GridLocation> eagleProtectionGridLocations = new ArrayList<>();
        eagleProtectionGridLocations.add(PROTECTION_LOCATION_1);
        eagleProtectionGridLocations.add(PROTECTION_LOCATION_2);
        eagleProtectionGridLocations.add(PROTECTION_LOCATION_3);
        eagleProtectionGridLocations.add(PROTECTION_LOCATION_4);
        eagleProtectionGridLocations.add(PROTECTION_LOCATION_5);

        final BlockConfiguration PROTECTION_TILE_CONF_1 = BlockConfiguration.BLOCK_CONF_FULL;
        final BlockConfiguration PROTECTION_TILE_CONF_2 = BlockConfiguration.BLOCK_CONF_FULL;
        final BlockConfiguration PROTECTION_TILE_CONF_3 = BlockConfiguration.BLOCK_CONF_FULL;
        final BlockConfiguration PROTECTION_TILE_CONF_4 = BlockConfiguration.BLOCK_CONF_FULL;
        final BlockConfiguration PROTECTION_TILE_CONF_5 = BlockConfiguration.BLOCK_CONF_FULL;
        ArrayList<BlockConfiguration> protectionTileConfiguration = new ArrayList<>();
        protectionTileConfiguration.add(PROTECTION_TILE_CONF_1);
        protectionTileConfiguration.add(PROTECTION_TILE_CONF_2);
        protectionTileConfiguration.add(PROTECTION_TILE_CONF_3);
        protectionTileConfiguration.add(PROTECTION_TILE_CONF_4);
        protectionTileConfiguration.add(PROTECTION_TILE_CONF_5);

        ArrayList<GridLocation> blockedGridLocations = new ArrayList<>();
        blockedGridLocations.add(PROTECTION_LOCATION_1);
        blockedGridLocations.add(PROTECTION_LOCATION_2);
        blockedGridLocations.add(PROTECTION_LOCATION_3);
        blockedGridLocations.add(PROTECTION_LOCATION_4);
        blockedGridLocations.add(EAGLE_LOCATION);
        blockedGridLocations.add(PROTECTION_LOCATION_5);
        blockedGridLocations.add(Globals.ENEMY_SPAWN_LOCATION_1);
        blockedGridLocations.add(Globals.ENEMY_SPAWN_LOCATION_2);
        blockedGridLocations.add(Globals.ENEMY_SPAWN_LOCATION_3);
        blockedGridLocations.add(Globals.INITIAL_PLAYER_1_LOC);
        blockedGridLocations.add(Globals.INITIAL_PLAYER_2_LOC);

        Tile[][] grid = new Tile[Globals.ROW_TILE_COUNT][Globals.COL_TILE_COUNT];

        for (int r=0; r<Globals.ROW_TILE_COUNT; r++){
            for (int c=0; c<Globals.ROW_TILE_COUNT; c++){
                GridLocation gLoc = new GridLocation(r, c);

                if (blockedGridLocations.contains(gLoc)) continue;

                Tile[] neighborTiles = getNeighbors(grid, gLoc);

                Tile neighborTile = neighborTiles[random.nextInt(neighborTiles.length)];
                TileType neighborTileType = (neighborTile==null)? TileType.TILE_NONE: neighborTile.getType();
                
                TileType tentativeTileType = Utils.getRandomProbability(50) ? neighborTileType: getRandomTileType();

                tentativeTileType = Utils.getRandomProbability(40) ? TileType.TILE_NONE: tentativeTileType; 

                BlockConfiguration tentativeBlockConfiguration = Utils.getRandomProbability(80) ? BlockConfiguration.BLOCK_CONF_FULL: BlockConfiguration.valueOf(random.nextInt(0, BlockConfiguration.values().length-1));
                if (tentativeTileType != TileType.TILE_NONE && tentativeBlockConfiguration != BlockConfiguration.BLOCK_CONF_EMPTY) {
                    Tile tile = TileFactory.createTile(tentativeTileType, r, c, tentativeBlockConfiguration);
                    grid[r][c] = tile;
                } else {
                    grid[r][c] = null;
                }
            }
        }
        
        int i=0;
        for (GridLocation gLoc: eagleProtectionGridLocations) {
            grid[gLoc.rowIndex()][gLoc.colIndex()] = TileFactory.createTile(TileType.TILE_BRICKS, gLoc.rowIndex(), gLoc.colIndex(), protectionTileConfiguration.get(i));
            i++;
        }

        grid[EAGLE_LOCATION.rowIndex()][EAGLE_LOCATION.colIndex()] = TileFactory.createTile(TileType.TILE_EAGLE, EAGLE_LOCATION.rowIndex(), EAGLE_LOCATION.colIndex(), BlockConfiguration.BLOCK_CONF_FULL);

        return grid;
    }

    public static LevelInfo generateRandomLevelInfo() {
        HashMap<TankType, Integer> enemyMap = generateRandomEnemies();
        Tile[][] grid = generateRandomGrid();

        return new LevelInfo(grid, enemyMap);
    }

}
