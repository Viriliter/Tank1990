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

package tank1990.tile;

public enum TileType {
    TILE_NONE,
    TILE_BRICKS,
    TILE_STEEL,
    TILE_TREES,
    TILE_SEA,
    TILE_ICE,
    TILE_EAGLE;

    public static TileType valueOf(int val) {
        return switch (val) {
            case (0) -> TILE_NONE;
            case (1) -> TILE_BRICKS;
            case (2) -> TILE_STEEL;
            case (3) -> TILE_TREES;
            case (4) -> TILE_SEA;
            case (5) -> TILE_ICE;
            case (6) -> TILE_EAGLE;
            default -> throw new RuntimeException("Invalid Value for TileType");
        };
    }

    /**
     * Checks if a tile of the given type is passable.
     * @param type The type of the tile.
     * @return True if the tile is passable, false otherwise.
     */
    public static boolean isPassable(TileType type) {
        return switch (type) {
            case TILE_NONE, TILE_BRICKS, TILE_TREES, TILE_ICE, TILE_EAGLE-> true;
            case TILE_STEEL, TILE_SEA  -> false;
        };
    }

    /**
     * Returns the cost of moving through a tile of the given type.
     * @param type The type of the tile.
     * @return The cost associated with the tile type.
     */
    public static int getCost(TileType type) {
        return switch (type) {
            case TILE_NONE -> 1;
            case TILE_BRICKS -> 10;
            case TILE_TREES -> 1;
            case TILE_ICE -> 2;
            case TILE_EAGLE -> 1;
            case TILE_STEEL, TILE_SEA -> Integer.MAX_VALUE; // Not passable
        };
    }
}
