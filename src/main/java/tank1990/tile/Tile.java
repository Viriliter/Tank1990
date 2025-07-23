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

import java.awt.*;

import tank1990.core.*;
import tank1990.projectiles.Bullet;
import tank1990.tank.AbstractTank;

public abstract class Tile extends StaticGameObject {
    protected TileType type = null;
    protected BlockConfiguration blockConf = BlockConfiguration.BLOCK_CONF_FULL;
    protected TextureFX textureFX = null;
    protected boolean isDestroyed = false;

    protected AbstractTank includedTankInst = null;

    GridLocation gloc = null;

    public Tile(int x, int y, TileType type) {
        setX(x);
        setY(y);
        setDir(Direction.DIRECTION_INVALID);

        this.type = type;
    }

    public Tile(int x, int y, TileType type, BlockConfiguration blockConf) {
        setX(x);
        setY(y);
        setDir(Direction.DIRECTION_INVALID);

        this.type = type;
        this.blockConf = blockConf;
    }

    public TileType getType() { return this.type; }

    public BlockConfiguration getBlockConf() { return this.blockConf; }

    public void update() { }
    
    public void draw(Graphics g) {
        if (this.textureFX == null) {
            System.err.println("Tile textureFX is null for tile: " + this.type);
            return;
        }

        setSize(Utils.normalizeDimension(g, Globals.TILE_WIDTH, Globals.TILE_HEIGHT));

        if (this.gloc==null) {
            this.gloc = new GridLocation(y, x);
            Location loc = Utils.gridLoc2Loc(this.gloc);

            setX(loc.x());
            setY(loc.y());
        }

        // Get the clip bounds of the graphics context
        java.awt.Rectangle clipBounds = g.getClipBounds();
        this.textureFX.setTargetSize(getSize().width, getSize().height);
        this.textureFX.draw(g, x, y, 0.0);
    }

    public void addTank(AbstractTank tank) {
        this.includedTankInst = tank;
    }

    public void removeTank() {
        this.includedTankInst = null;
    }

    public boolean includesTank() {
        return this.includedTankInst!=null;
    }

    /**
     * Destroys the tile and returns true if it was successfully destroyed.
     * If the tile cannot be destroyed, it should return false.
     * This method should be overridden by subclasses to implement specific destruction logic.
     * @param b the bullet that is attempting to destroy the tile
     *
     * @return true if the tile was successfully destroyed, false otherwise
     */
    public abstract boolean destroy(Bullet b);

    public boolean isDestroyed() {
        return this.isDestroyed;
    }

    public void setAsDestroyed() {
        this.isDestroyed = true;
    }

    public String toString() {
        return "Tile{" +
                "type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", width=" + getSize().width +
                ", height=" + getSize().height +
                ", LocX=" + Utils.gridLoc2Loc(this.gloc).x() +
                ", LocY=" + Utils.gridLoc2Loc(this.gloc).y() +
                '}';
    }
}
