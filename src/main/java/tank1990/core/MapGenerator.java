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
import java.util.Scanner;

import tank1990.tile.BlockConfiguration;
import tank1990.tile.Tile;
import tank1990.tile.TileFactory;
import tank1990.tile.TileType;

public class MapGenerator {

    public static Tile[][] createMap(String filePath) {
        Tile[][] tiles = loadFromBinary(filePath);

        return tiles;//createPanel(tiles);
    }

    public static void saveToBinary(String sourceTxtPath, String targetBinPath) {
        ObjectOutputStream os = null;
        try {
            Tile[][] tiles = MapGenerator.createFromText(sourceTxtPath);

            os = new ObjectOutputStream(new FileOutputStream(targetBinPath));
            os.writeObject(tiles);
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

    private static Tile[][] loadFromBinary(String filePath) {
        try {
            InputStream inputStream = MapGenerator.class.getClassLoader().getResourceAsStream(filePath);
            ObjectInputStream os = new ObjectInputStream(inputStream);
            Tile[][] grid = (Tile[][]) os.readObject();
            os.close();
            return grid;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Tile[][] createFromText(String filePath) throws FileNotFoundException {
        Tile[][] grid = new Tile[Globals.ROW_TILE_COUNT][Globals.COL_TILE_COUNT];

        FileInputStream inputStream = new FileInputStream(filePath);
        assert inputStream != null;
        Scanner scanner = new Scanner(inputStream);
        try {
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] params = line.split("\\s+");

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

        return grid;
    }
}
