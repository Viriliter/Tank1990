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

import tank1990.core.Globals;
import tank1990.core.TextureFX;
import tank1990.projectiles.Bullet;

public class TileBricks extends Tile {
    public TileBricks(int x, int y, BlockConfiguration blockConf) {
        super(x, y, TileType.TILE_BRICKS, blockConf);

        this.textureFX = new TextureFX(Globals.TEXTURE_TILE_BRICKS);
    }

    private int findSubTile(int subX, int subY, Bullet b) {
        int diffX = subX - b.getX();
        int diffY = subY - b.getY();

        if (diffX > 0 && diffY > 0) {
            return 0;  // Top-left
        } else if (diffX < 0 && diffY > 0) {
            return 1;  // Top-right
        } else if (diffX > 0 && diffY < 0) {
            return 2;  // Bottom-left
        } else if (diffX < 0 && diffY < 0) {
            return 3;  // Bottom-right
        } else {
            return 0;  // Center: Top-left
        }
    }

    private int findHitLocation(Bullet b) {
        int diffX = getX() - b.getX();
        int diffY = getY() - b.getY();

        int subWidth = (int) getSize().getWidth() / Globals.TILE_SUBDIVISION;
        int subHeight = (int) getSize().getHeight() / Globals.TILE_SUBDIVISION;

        /*
            (X:diffX, Y:diffY)

            +---+---+---+---+
            | 0 | 1 |   |   |
            +-(+,+)-+-(-,+)-+
            | 2 | 3 |   |   |
            +---+---+---+---+
            |   |   |   |   |
            +-(+,-)-+-(-,-)-+
            |   |   |   |   |
            +---+---+---+---+

         */

        int subpiece;
        int region;

        if (diffX > 0 && diffY > 0) {  // Top-left
            subpiece = 0;  // Top-left
            region = findSubTile(getX()-subWidth,getY()-subHeight,b);
        } else if (diffX < 0 && diffY > 0) {  // Top-right
            subpiece = 1;  // Top-right
            region = findSubTile(getX()+subWidth,getY()-subHeight,b);
        } else if (diffX > 0 && diffY < 0) {  // Bottom-left
            subpiece = 2;  // Bottom-left
            region = findSubTile(getX()-subWidth,getY()+subHeight,b);
        } else if (diffX < 0 && diffY < 0) {  // Bottom-right
            subpiece = 3;  // Bottom-right
            region = findSubTile(getX()+subWidth,getY()+subHeight,b);
        } else {
            subpiece = 0;  // Top-left
            region = findSubTile(getX()-subWidth,getY()-subHeight,b);
        }

        System.out.println("subpiece: " + subpiece + ", region: " + region);
        return 0;
    }

    public boolean destroy(Bullet b) {
        hit(b.getDir());

        return true;
    }

}
