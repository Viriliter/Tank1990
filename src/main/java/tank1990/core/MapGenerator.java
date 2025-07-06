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

import java.awt.*;
import javax.swing.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import tank1990.tile.Tile;
import tank1990.tile.TileFactory;
import tank1990.tile.TileType;

public class MapGenerator {

    public static JPanel createMap(String filePath) {
        Tile[][] tiles = loadFromBinary(filePath);

        return createPanel(tiles);
    }

    @SuppressWarnings("unchecked")
    private static Tile[][] loadFromBinary(String filePath) {
        Tile[][] grid = new Tile[GlobalConstants.ROW_TILE_COUNT][GlobalConstants.COL_TILE_COUNT];
        try {
            InputStream inputStream = MapGenerator.class.getClassLoader().getResourceAsStream(filePath);
            ObjectInputStream os = new ObjectInputStream(inputStream);
            for(Tile tile: (List<Tile>) os.readObject()) {
                grid[tile.getX()][tile.getY()] = tile;
            }
            return grid;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static JPanel createPanel(Tile[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        JPanel panel = new JPanel(new GridLayout(rows, cols));
        panel.setPreferredSize(new Dimension(ConfigHandler.getInstance().getWindowProperties().windowWidth(),
                                             ConfigHandler.getInstance().getWindowProperties().windowHeight()));

        int tileSize = panel.getWidth() / GlobalConstants.ROW_TILE_COUNT;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Tile tile = grid[row][col];

                if (tile == null) {
                    JLabel emptyLabel = new JLabel();
                    emptyLabel.setPreferredSize(new Dimension(tileSize, tileSize));
                    emptyLabel.setOpaque(true);
                    emptyLabel.setBackground(Color.BLACK);
                    panel.add(emptyLabel);
                    continue;
                }

                JLabel label = null;
                switch (tile.getType()) {
                    case TILE_BRICKS:
                        label = new JLabel(GlobalConstants.loadPNGIcon(GlobalConstants.TEXTURE_TILE_BRICKS_PATH, tileSize, tileSize));
                        break;
                    case TileType.TILE_STEEL:
                        label = new JLabel(GlobalConstants.loadPNGIcon(GlobalConstants.TEXTURE_TILE_BRICKS_PATH, tileSize, tileSize));
                        break;
                    case TileType.TILE_TREES:
                        label = new JLabel(GlobalConstants.loadPNGIcon(GlobalConstants.TEXTURE_TILE_BRICKS_PATH, tileSize, tileSize));
                        break;
                    case TileType.TILE_SEA:
                        label = new JLabel(GlobalConstants.loadPNGIcon(GlobalConstants.TEXTURE_TILE_BRICKS_PATH, tileSize, tileSize));
                        break;
                    case TileType.TILE_ICE:
                        label = new JLabel(GlobalConstants.loadPNGIcon(GlobalConstants.TEXTURE_TILE_BRICKS_PATH, tileSize, tileSize));
                        break;
                    case TileType.TILE_EAGLE:
                        label = new JLabel(GlobalConstants.loadPNGIcon(GlobalConstants.TEXTURE_TILE_BRICKS_PATH, tileSize, tileSize));
                        break;
                    default:
                        JLabel emptyLabel = new JLabel();
                        emptyLabel.setPreferredSize(new Dimension(tileSize, tileSize));
                        emptyLabel.setOpaque(true);
                        emptyLabel.setBackground(Color.BLACK);
                        panel.add(emptyLabel);
                        break;
                }

                assert label != null;
                label.setPreferredSize(new Dimension(tileSize, tileSize));
                panel.add(label);
            }
        }

        return panel;
    }

    public static Tile[][] createFromText(String filePath) throws FileNotFoundException {
        Tile[][] grid = new Tile[GlobalConstants.ROW_TILE_COUNT][GlobalConstants.COL_TILE_COUNT];

        FileInputStream inputStream = new FileInputStream(filePath);
        assert inputStream != null;
        Scanner scanner = new Scanner(inputStream);
         while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] params = line.split("\\s+");

            if (params.length!=3) continue;

            TileType tileType = TileType.valueOf(params[0]);
            int rowIndex = Integer.parseInt(params[1]);
            int colIndex = Integer.parseInt(params[2]);

            grid[rowIndex][colIndex] = TileFactory.createTile(ConfigHandler.getInstance(), tileType, rowIndex, colIndex);
        }

        return grid;
    }

    public static void saveToBinary() {

    }

    public static void printGrid(Tile[][] grid) {
        System.out.print("||===|===|===|===|===|===|===|===|===|===|===|===|===||\n");
        for (int i=0; i<GlobalConstants.ROW_TILE_COUNT; i++) {
            for (int j=0; j<GlobalConstants.COL_TILE_COUNT; j++) {
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

                if (j==GlobalConstants.COL_TILE_COUNT-1) System.out.print("||\n");
            }

            if (i!=GlobalConstants.ROW_TILE_COUNT-1) System.out.print("||---|---|---|---|---|---|---|---|---|---|---|---|---||\n");
        }
        System.out.print("||===|===|===|===|===|===|===|===|===|===|===|===|===||\n");
    }
}
