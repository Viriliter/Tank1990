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

package tank1990.projectiles;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import tank1990.core.Utils;
import tank1990.core.Direction;
import tank1990.core.DynamicGameObject;
import tank1990.core.GameLevel;
import tank1990.core.Globals;
import tank1990.core.GridLocation;
import tank1990.core.Location;
import tank1990.tank.AbstractTank;
import tank1990.tile.Tile;
import tank1990.tile.TileBricks;
import tank1990.tile.TileEagle;
import tank1990.tile.TileIce;
import tank1990.tile.TileSea;
import tank1990.tile.TileSteel;
import tank1990.tile.TileTrees;

public class Bullet extends DynamicGameObject {

    AbstractTank tankInst = null;
    int speed;

    public Bullet(AbstractTank tankInst, int x, int y, Direction dir, int speed) {
        this.tankInst = tankInst;
        setX(x);
        setY(y);
        setDir(dir);
        setSize(new Dimension(Globals.BULLET_WIDTH, Globals.BULLET_HEIGHT));

        this.speed = speed;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();
        g2d.setColor(Globals.COLOR_GRAY);
        g2d.translate(x, y);
        g2d.fillRect((int) -getSize().getWidth(), (int) -getSize().getHeight(), 
                     (int) getSize().getWidth(), (int) getSize().getHeight());
        g2d.setTransform(oldTransform);
    }

    public void update(GameLevel level) {
        //System.out.printf("Bullet update: x=%d, y=%d, dir=%s, speed=%d%n", x, y, dir, speed);
        switch (this.dir) {
            case DIRECTION_UPWARDS: y -= (int) this.speed; break; 
            case DIRECTION_RIGHT: x += (int) this.speed; break; 
            case DIRECTION_DOWNWARDS: y += (int) this.speed; break; 
            case DIRECTION_LEFT: x -= (int) this.speed; break;
            default: break; 
        }
    }

    public Blast destroy() {
        this.tankInst.setBulletStatus(true);
        // Create an blast at the current bullet position
        return new Blast(x, y);

    }

    /**
     * Checks if the projectile is out of bounds, meaning it has gone beyond the specified area (width and height).
     * 
     * @param width The width of the game area.
     * @param height The height of the game area.
     * @return true if the projectile is out of bounds, false otherwise.
     */
    public boolean isOutOfBounds(int width, int height) {
        return this.x < 0 || this.x > width || this.y < 0 || this.y > height;
    }

    // Implementation of abstract method from DynamicGameObject
    @Override
    public boolean checkCollision() {
        // This method is required by the parent class but we need GameLevel context
        // Return false as we'll use the overloaded method with GameLevel parameter
        return false;
    }
    
    public boolean checkCollision(GameLevel gameLevel) {
        if (gameLevel == null || gameLevel.getMap() == null) {
            return false;
        }
        
        // Get the tile map
        Tile[][] map = gameLevel.getMap();
        
        // Get game area dimensions
        Dimension gameAreaSize = gameLevel.getGameAreaSize();
        int maxWidth = (int) gameAreaSize.getWidth();
        int maxHeight = (int) gameAreaSize.getHeight();
        
        // Convert bullet position to grid location
        GridLocation gLoc = Utils.Loc2GridLoc(new Location(this.x, this.y));
        
        int r = gLoc.rowIndex();
        int c = gLoc.colIndex();
        
        // Check bounds
        if (r < 0 || r >= Globals.ROW_TILE_COUNT || c < 0 || c >= Globals.COL_TILE_COUNT) {
            return false;
        }
        
        Tile currentTile = map[r][c];
        if (currentTile == null) { return false; }
        
        // Handle different tile types
        if (currentTile instanceof TileBricks) {
            // Destroy both bullet and brick tile
            map[r][c] = null; // Remove the brick tile
            return true; // Bullet should be destroyed
        }
        else if (currentTile instanceof TileSteel) { return true; }
        else if (currentTile instanceof TileEagle) {
            // Destroy both bullet and eagle (game over condition could be handled here)
            map[r][c] = null; // Remove the eagle tile
            return true; // Bullet should be destroyed
        }
        else if (currentTile instanceof TileTrees) { return false; }
        else if (currentTile instanceof TileSea) { return false; }
        else if (currentTile instanceof TileIce) { return false; }
        
        // For any other tile types, bullet passes through
        return false;
    }

    public String toString() {
        return String.format("Bullet [x=%d, y=%d, dir=%s, speed=%d]", x, y, dir, speed);
    }


}
