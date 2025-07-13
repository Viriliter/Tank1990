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

public class TileFactory {
    public static Tile createTile(TileType type, int row, int col, BlockConfiguration blockConf) {
        if (type == null) {
            throw new IllegalArgumentException("Tank type cannot be null!");
        }

        return switch (type) {
            case TILE_BRICKS -> new TileBricks(row, col, blockConf);
            case TILE_STEEL -> new TileSteel(row, col, blockConf);
            case TILE_TREES -> new TileTrees(row, col, blockConf);
            case TILE_SEA -> new TileSea(row, col, blockConf);
            case TILE_ICE -> new TileIce(row, col, blockConf);
            case TILE_EAGLE -> new TileEagle(row, col, blockConf);
            default -> null;
        };
    }
}
