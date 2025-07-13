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
            Tile[][] tiles = MapGenerator.createSubdividedMap(sourceTxtPath);

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
        System.out.println("\n=== TANK 1990 SUBDIVIDED MAP (52x52 Grid) ===");
        System.out.println("Legend: B=Bricks  S=Steel  T=Trees  W=Sea  I=Ice  E=Eagle  .=Empty\n");
        
        // Print column numbers header (every 5th column for readability)
        System.out.print("  ");
        for (int i = 0; i < GlobalConstants.COL_TILE_COUNT; i++) {
            if (i % 5 == 0) {
                System.out.printf("%d", i / 10);
            } else {
                System.out.print(" ");
            }
        }
        System.out.println();
        
        System.out.print("  ");
        for (int i = 0; i < GlobalConstants.COL_TILE_COUNT; i++) {
            if (i % 5 == 0) {
                System.out.printf("%d", i % 10);
            } else {
                System.out.print(" ");
            }
        }
        System.out.println();
        
        // Print each subdivided row
        for (int row = 0; row < GlobalConstants.ROW_TILE_COUNT; row++) {
            System.out.printf("%2d", row);
            
            for (int col = 0; col < GlobalConstants.COL_TILE_COUNT; col++) {
                String tileChar = "."; // Default empty tile
                
                if (grid[row][col] != null) {
                    Tile tile = grid[row][col];
                    switch(tile.getType()) {
                        case TileType.TILE_BRICKS: tileChar = "B"; break;
                        case TileType.TILE_STEEL: tileChar = "S"; break;
                        case TileType.TILE_TREES: tileChar = "T"; break;
                        case TileType.TILE_SEA: tileChar = "W"; break; // W for Water/Sea
                        case TileType.TILE_ICE: tileChar = "I"; break;
                        case TileType.TILE_EAGLE: tileChar = "E"; break;
                        default: tileChar = "?"; break;
                    }
                }
                
                System.out.print(tileChar);
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void printCompactGrid(Tile[][] grid) {        
        for (int baseRow = 0; baseRow < GlobalConstants.BASE_ROW_TILE_COUNT; baseRow++) {
            for (int baseCol = 0; baseCol < GlobalConstants.BASE_COL_TILE_COUNT; baseCol++) {
                // Check the top-left tile of each 4x4 subdivision
                int subdividedRow = baseRow * GlobalConstants.GRID_SUBDIVISION;
                int subdividedCol = baseCol * GlobalConstants.GRID_SUBDIVISION;
                
                String type = " ";
                if (subdividedRow < GlobalConstants.ROW_TILE_COUNT && 
                    subdividedCol < GlobalConstants.COL_TILE_COUNT) {
                    Tile tile = grid[subdividedRow][subdividedCol];
                    if (tile != null) {
                        switch(tile.getType()) {
                            case TileType.TILE_BRICKS: type = "B"; break;
                            case TileType.TILE_STEEL: type = "S"; break;
                            case TileType.TILE_TREES: type = "T"; break;
                            case TileType.TILE_SEA: type = "s"; break;
                            case TileType.TILE_ICE: type = "I"; break;
                            case TileType.TILE_EAGLE: type = "E"; break;
                            default: type = "?"; break;
                        }
                    }
                }
                System.out.print(type + " ");
            }
            System.out.println();
        }
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

    public static Tile[][] createFromTextOriginal(String filePath) throws FileNotFoundException {
        Tile[][] grid = new Tile[GlobalConstants.BASE_ROW_TILE_COUNT][GlobalConstants.BASE_COL_TILE_COUNT];

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

    /**
     * Creates a subdivided map from a text file.
     * Each original tile position is expanded into a 4x4 subdivision grid.
     */
    public static Tile[][] createSubdividedMap(String filePath) throws FileNotFoundException {
        // First create the base map using the original format
        Tile[][] baseGrid = createFromTextOriginal(filePath);
        
        // Create the subdivided grid
        Tile[][] subdividedGrid = new Tile[GlobalConstants.ROW_TILE_COUNT][GlobalConstants.COL_TILE_COUNT];
        
        // Fill the subdivided grid based on the base grid
        for (int baseRow = 0; baseRow < GlobalConstants.BASE_ROW_TILE_COUNT; baseRow++) {
            for (int baseCol = 0; baseCol < GlobalConstants.BASE_COL_TILE_COUNT; baseCol++) {
                Tile baseTile = baseGrid[baseRow][baseCol];
                
                if (baseTile != null) {
                    // Fill the 4x4 subdivision area with the same tile type
                    fillSubdivision(subdividedGrid, baseRow, baseCol, baseTile.getType(), baseTile.getBlockConf());
                }
            }
        }
        
        return subdividedGrid;
    }
    
    /**
     * Fills a 4x4 subdivision area in the subdivided grid with the specified tile type.
     */
    private static void fillSubdivision(Tile[][] subdividedGrid, int baseRow, int baseCol, 
                                      TileType tileType, BlockConfiguration blockConf) {
        int startRow = baseRow * GlobalConstants.GRID_SUBDIVISION;
        int startCol = baseCol * GlobalConstants.GRID_SUBDIVISION;
        
        for (int subRow = 0; subRow < GlobalConstants.GRID_SUBDIVISION; subRow++) {
            for (int subCol = 0; subCol < GlobalConstants.GRID_SUBDIVISION; subCol++) {
                int actualRow = startRow + subRow;
                int actualCol = startCol + subCol;
                
                if (actualRow < GlobalConstants.ROW_TILE_COUNT && actualCol < GlobalConstants.COL_TILE_COUNT) {
                    subdividedGrid[actualRow][actualCol] = TileFactory.createTile(tileType, actualCol, actualRow, blockConf);
                }
            }
        }
    }
    
    /**
     * Converts subdivided coordinates back to original base coordinates.
     */
    public static int[] subdividedToBase(int subdividedRow, int subdividedCol) {
        return new int[]{
            subdividedRow / GlobalConstants.GRID_SUBDIVISION,
            subdividedCol / GlobalConstants.GRID_SUBDIVISION
        };
    }
    
    /**
     * Converts base coordinates to the top-left subdivided coordinates.
     */
    public static int[] baseToSubdivided(int baseRow, int baseCol) {
        return new int[]{
            baseRow * GlobalConstants.GRID_SUBDIVISION,
            baseCol * GlobalConstants.GRID_SUBDIVISION
        };
    }

    /**
     * Creates a subdivided map from a text file (new default method).
     * This is the main entry point for creating maps with 4x4 subdivisions.
     */
    public static Tile[][] createFromText(String filePath) throws FileNotFoundException {
        return createSubdividedMap(filePath);
    }
    
    /**
     * Prints a demonstration of the new grid format with sample data.
     */
    public static void printGridFormatDemo() {
        System.out.println("=== GRID FORMAT EXAMPLES ===");
        System.out.println("Multiple display styles available for 52x52 subdivided grid:\n");
        
        System.out.println("1. FULL SUBDIVIDED GRID (each character = 1 subdivision):");
        System.out.println("  012345678901234567890123456789012345678901234567890123");
        System.out.println(" 0....SSSS........BBBB.............................");
        System.out.println(" 1....SSSS........BBBB.............................");
        System.out.println(" 2....SSSS........BBBB.............................");
        System.out.println(" 3....SSSS........BBBB.............................");
        System.out.println(" 4TTTTBBBB................................BBBB......");
        System.out.println(" 5TTTTBBBB................................BBBB......");
        System.out.println(" 6TTTTBBBB................................BBBB......");
        System.out.println(" 7TTTTBBBB................................BBBB......");
        
        System.out.println("\n2. WITH TILE BOUNDARIES:");
        System.out.println("    0   1   2   3   4   5   6   7   8   9  10  11  12");
        System.out.println("   +----+----+----+----+----+----+----+----+----+----+----+----+----+");
        System.out.println(" 0 |....|SSSS|....|BBBB|....|....|....|....|....|....|....|....|....|");
        System.out.println("   |....|SSSS|....|BBBB|....|....|....|....|....|....|....|....|....|");
        System.out.println("   |....|SSSS|....|BBBB|....|....|....|....|....|....|....|....|....|");
        System.out.println("   |....|SSSS|....|BBBB|....|....|....|....|....|....|....|....|....|");
        System.out.println("   +----+----+----+----+----+----+----+----+----+----+----+----+----+");
        
        System.out.println("\nLegend:");
        System.out.println("B=Bricks, S=Steel, T=Trees, W=Sea, I=Ice, E=Eagle, .=Empty");
        System.out.println("Each character represents one subdivision (1/16th of original tile)");
        System.out.println();
    }
    
    /**
     * Prints a visual grid using Unicode block characters for better representation.
     */
    public static void printVisualGrid(Tile[][] grid) {
        System.out.println("\n=== VISUAL MAP REPRESENTATION (52x52) ===");
        System.out.println("█ = Bricks  ▓ = Steel  ░ = Trees  ~ = Sea  ❄ = Ice  ★ = Eagle  · = Empty\n");
        
        for (int row = 0; row < GlobalConstants.ROW_TILE_COUNT; row++) {
            for (int col = 0; col < GlobalConstants.COL_TILE_COUNT; col++) {
                String tileChar = "·"; // Default empty tile
                
                if (grid[row][col] != null) {
                    Tile tile = grid[row][col];
                    switch(tile.getType()) {
                        case TileType.TILE_BRICKS: tileChar = "█"; break;
                        case TileType.TILE_STEEL: tileChar = "▓"; break;
                        case TileType.TILE_TREES: tileChar = "░"; break;
                        case TileType.TILE_SEA: tileChar = "~"; break;
                        case TileType.TILE_ICE: tileChar = "❄"; break;
                        case TileType.TILE_EAGLE: tileChar = "★"; break;
                        default: tileChar = "?"; break;
                    }
                }
                
                System.out.print(tileChar);
            }
            System.out.println();
        }
        System.out.println();
    }
    
    /**
     * Prints a detailed grid showing subdivision information.
     */
    public static void printDetailedGrid(Tile[][] grid) {
        System.out.println("\n=== DETAILED SUBDIVISION MAP ===");
        System.out.println("Format: Each base tile shows [TYPE] with subdivision count\n");
        
        for (int baseRow = 0; baseRow < GlobalConstants.BASE_ROW_TILE_COUNT; baseRow++) {
            for (int baseCol = 0; baseCol < GlobalConstants.BASE_COL_TILE_COUNT; baseCol++) {
                // Get the tile type from the subdivided grid
                int subdividedRow = baseRow * GlobalConstants.GRID_SUBDIVISION;
                int subdividedCol = baseCol * GlobalConstants.GRID_SUBDIVISION;
                
                String tileInfo = "[    ]"; // Default empty
                if (subdividedRow < GlobalConstants.ROW_TILE_COUNT && 
                    subdividedCol < GlobalConstants.COL_TILE_COUNT && 
                    grid[subdividedRow][subdividedCol] != null) {
                    
                    Tile tile = grid[subdividedRow][subdividedCol];
                    // Count how many subdivision tiles are actually filled
                    int filledCount = 0;
                    for (int subRow = 0; subRow < GlobalConstants.GRID_SUBDIVISION; subRow++) {
                        for (int subCol = 0; subCol < GlobalConstants.GRID_SUBDIVISION; subCol++) {
                            int actualRow = subdividedRow + subRow;
                            int actualCol = subdividedCol + subCol;
                            if (actualRow < GlobalConstants.ROW_TILE_COUNT && 
                                actualCol < GlobalConstants.COL_TILE_COUNT && 
                                grid[actualRow][actualCol] != null) {
                                filledCount++;
                            }
                        }
                    }
                    
                    String typeCode = "";
                    switch(tile.getType()) {
                        case TileType.TILE_BRICKS: typeCode = "BR"; break;
                        case TileType.TILE_STEEL: typeCode = "ST"; break;
                        case TileType.TILE_TREES: typeCode = "TR"; break;
                        case TileType.TILE_SEA: typeCode = "SE"; break;
                        case TileType.TILE_ICE: typeCode = "IC"; break;
                        case TileType.TILE_EAGLE: typeCode = "EA"; break;
                        default: typeCode = "??"; break;
                    }
                    tileInfo = String.format("[%s%2d]", typeCode, filledCount);
                }
                
                System.out.print(tileInfo + " ");
            }
            System.out.println();
        }
        System.out.println("Number after type code indicates filled subdivisions (max 16)");
        System.out.println();
    }
    
    /**
     * Prints the subdivided grid with visual separators showing original tile boundaries.
     */
    public static void printGridWithBoundaries(Tile[][] grid) {
        System.out.println("\n=== SUBDIVIDED MAP WITH TILE BOUNDARIES ===");
        System.out.println("Legend: B=Bricks  S=Steel  T=Trees  W=Sea  I=Ice  E=Eagle  .=Empty");
        System.out.println("| and - show original 13x13 tile boundaries\n");
        
        // Print column header with base tile numbers
        System.out.print("   ");
        for (int baseCol = 0; baseCol < GlobalConstants.BASE_COL_TILE_COUNT; baseCol++) {
            System.out.printf(" %2d  ", baseCol);
        }
        System.out.println();
        
        System.out.print("   ");
        for (int baseCol = 0; baseCol < GlobalConstants.BASE_COL_TILE_COUNT; baseCol++) {
            System.out.print("+----");
        }
        System.out.println("+");
        
        // Print each subdivided row with boundaries
        for (int row = 0; row < GlobalConstants.ROW_TILE_COUNT; row++) {
            // Print row number for every 4th row (base tile boundary)
            if (row % GlobalConstants.GRID_SUBDIVISION == 0) {
                System.out.printf("%2d ", row / GlobalConstants.GRID_SUBDIVISION);
            } else {
                System.out.print("   ");
            }
            
            System.out.print("|");
            for (int col = 0; col < GlobalConstants.COL_TILE_COUNT; col++) {
                String tileChar = "."; // Default empty tile
                
                if (grid[row][col] != null) {
                    Tile tile = grid[row][col];
                    switch(tile.getType()) {
                        case TileType.TILE_BRICKS: tileChar = "B"; break;
                        case TileType.TILE_STEEL: tileChar = "S"; break;
                        case TileType.TILE_TREES: tileChar = "T"; break;
                        case TileType.TILE_SEA: tileChar = "W"; break;
                        case TileType.TILE_ICE: tileChar = "I"; break;
                        case TileType.TILE_EAGLE: tileChar = "E"; break;
                        default: tileChar = "?"; break;
                    }
                }
                
                System.out.print(tileChar);
                
                // Add vertical boundary every 4 columns
                if ((col + 1) % GlobalConstants.GRID_SUBDIVISION == 0) {
                    System.out.print("|");
                }
            }
            System.out.println();
            
            // Add horizontal boundary every 4 rows
            if ((row + 1) % GlobalConstants.GRID_SUBDIVISION == 0) {
                System.out.print("   ");
                for (int baseCol = 0; baseCol < GlobalConstants.BASE_COL_TILE_COUNT; baseCol++) {
                    System.out.print("+----");
                }
                System.out.println("+");
            }
        }
        System.out.println();
    }
}
