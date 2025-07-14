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

package tank1990.player;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import tank1990.core.Direction;
import tank1990.core.GameLevel;
import tank1990.core.GlobalConstants;
import tank1990.core.GridLocation;
import tank1990.core.Location;
import tank1990.projectiles.Bullet;
import tank1990.tank.PlayerTank;
import tank1990.tank.TankFactory;
import tank1990.tank.TankType;
import tank1990.tile.Tile;
import tank1990.tile.TileBricks;
import tank1990.tile.TileEagle;
import tank1990.tile.TileIce;
import tank1990.tile.TileSea;
import tank1990.tile.TileSteel;
import tank1990.tile.TileTrees;

public class Player {
    private int life;
    private int dx, dy;   // Player's position and movement direction
    private Direction dir = null;
    private int speed = 0;

    private PlayerTank myTank = null;
    private PlayerType playerType = PlayerType.PLAYER_1;

    public Player(PlayerType playerType) {
        this.life = GlobalConstants.INITAL_PLAYER_HEALTH;
        this.dx = 0;
        this.dy = 0;
        this.speed = GlobalConstants.PLAYER_MOVEMENT_SPEED;

        if (playerType == PlayerType.PLAYER_1) {
            this.dir = GlobalConstants.INITIAL_PLAYER_1_DIR;
            myTank = (PlayerTank) TankFactory.createTank(TankType.PLAYER_TANK, 
                                                         GlobalConstants.gridLoc2Loc(GlobalConstants.INITIAL_PLAYER_1_LOC, 720,720).x(), 
                                                         GlobalConstants.gridLoc2Loc(GlobalConstants.INITIAL_PLAYER_1_LOC, 720,720).y(), 
                                                         GlobalConstants.INITIAL_PLAYER_1_DIR);
            myTank.setPlayerType(playerType);
        } else {
            this.dir = GlobalConstants.INITIAL_PLAYER_2_DIR;
            myTank = (PlayerTank) TankFactory.createTank(TankType.PLAYER_TANK, 
                                                         GlobalConstants.gridLoc2Loc(GlobalConstants.INITIAL_PLAYER_2_LOC, 720,720).x(), 
                                                         GlobalConstants.gridLoc2Loc(GlobalConstants.INITIAL_PLAYER_2_LOC, 720,720).y(), 
                                                         GlobalConstants.INITIAL_PLAYER_2_DIR);
            myTank.setPlayerType(playerType);
        }
    }

    public void draw(Graphics g) {
        myTank.draw(g);

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();
        g2d.setColor(Color.RED);
        g2d.translate(myTank.getX(), myTank.getY());
        g2d.fillRect(-5, -5, 5, 5);
        g2d.setTransform(oldTransform);
    }

    public void update(GameLevel level) {
        // Get tank dimensions for boundary calculations
        int tankWidth = (int) myTank.getSize().getWidth();
        int tankHeight = (int) myTank.getSize().getHeight();
        
        Dimension gameAreaSize = level.getGameAreaSize();

        // If tank size is not set, calculate it based on grid
        if (tankWidth == 0 || tankHeight == 0) {
            int cellWidth = (int) gameAreaSize.getWidth() / GlobalConstants.COL_TILE_COUNT;
            int cellHeight = (int) gameAreaSize.getHeight() / GlobalConstants.ROW_TILE_COUNT;
            tankWidth = cellWidth - 2;
            tankHeight = cellHeight - 2;
        }
        
        // Since tank position is center point, calculate proper boundaries
        int halfWidth = tankWidth / 2;
        int halfHeight = tankHeight / 2;
        
        // Calculate new position with boundary checking (accounting for center position)
        int newX = Math.max(halfWidth, Math.min(myTank.getX() + this.dx, (int) gameAreaSize.getWidth() - halfWidth));
        int newY = Math.max(halfHeight, Math.min(myTank.getY() + this.dy, (int) gameAreaSize.getHeight() - halfHeight));
        //System.out.println("newX:" + newX + " newY:" + newY + " maxWidth:" + maxWidth + " maxHeight:" + maxHeight);

        // Check map constraints by checking neighbor tiles of the player tank
        boolean isMovable = checkMovable(newX, newY, level, true);
        if (isMovable) {
            // Update tank position and direction
            myTank.setX(newX);
            myTank.setY(newY);
        } else {}

        myTank.setDir(dir);
    }

    public boolean checkMovable(int x, int y, GameLevel level, boolean isRecursive) {
        int maxWidth = level.getGameAreaSize().width;
        int maxHeight = level.getGameAreaSize().height;

        Tile[][] map = level.getMap();

        // Get tank dimensions
        int tankWidth = (int) myTank.getSize().getWidth();
        int tankHeight = (int) myTank.getSize().getHeight();
        
        // If tank size is not set, calculate it based on grid (80% of cell size)
        if (tankWidth == 0 || tankHeight == 0) {
            int cellWidth = maxWidth / GlobalConstants.COL_TILE_COUNT;
            int cellHeight = maxHeight / GlobalConstants.ROW_TILE_COUNT;
            tankWidth = (int)(cellWidth - 2);
            tankHeight = (int)(cellHeight - 2);
        }
        
        // x and y are the CENTER coordinates of the tank, so we need to calculate the actual corners
        int halfWidth = tankWidth / 2;
        int halfHeight = tankHeight / 2;
        
        // Calculate the actual bounding box corners from the center point
        int topLeftX = x - halfWidth;
        int topLeftY = y - halfHeight;
        int bottomRightX = x + halfWidth - 1;
        int bottomRightY = y + halfHeight - 1;
        
        // Check key points around the tank's perimeter
        int[] checkX = {
            topLeftX,               // Top-left corner
            bottomRightX,           // Top-right corner  
            topLeftX,               // Bottom-left corner
            bottomRightX,           // Bottom-right corner
            x                       // Center point for good measure
        };
        int[] checkY = {
            topLeftY,               // Top-left corner
            topLeftY,               // Top-right corner
            bottomRightY,           // Bottom-left corner
            bottomRightY,           // Bottom-right corner
            y                       // Center point for good measure
        };
        
        for (int i = 0; i < checkX.length; i++) {
            GridLocation gLoc = GlobalConstants.Loc2gridLoc(new Location(checkX[i], checkY[i]), maxWidth, maxHeight);
            
            int r = gLoc.rowIndex();
            int c = gLoc.colIndex();
            
            // Check bounds
            if (r < 0 || r >= GlobalConstants.ROW_TILE_COUNT || c < 0 || c >= GlobalConstants.COL_TILE_COUNT) {
                return false;
            }
            
            //System.out.println("Checking point " + i + " at row:" + r + " col:" + c + " (coords: " + checkX[i] + "," + checkY[i] + ")");

            if (map[r][c] instanceof TileBricks) { return false; }
            else if (map[r][c] instanceof TileSteel) { return false; /* player cannot move through steel*/ }
            else if (map[r][c] instanceof TileTrees) { /* player can move through trees*/ }
            else if (map[r][c] instanceof TileSea) { return false; /* player cannot move through sea unless it has boat*/ }
            else if (map[r][c] instanceof TileIce && isRecursive != false && this.dx == 0 && this.dy == 0) { 
                /* player can move through ice with skid*/
                int new_x = (myTank.getDir()==Direction.DIRECTION_LEFT || myTank.getDir()==Direction.DIRECTION_RIGHT) ? x + GlobalConstants.SKID_DISTANCE: x;
                int new_y = (myTank.getDir()==Direction.DIRECTION_DOWNWARDS || myTank.getDir()==Direction.DIRECTION_UPWARDS)?  y + GlobalConstants.SKID_DISTANCE: y;
                return checkMovable(new_x, new_y, level, false);
            }
            else if (map[r][c] instanceof TileEagle) { return false; /* player cannot move through eagle*/ }
        }
        return true;
    }

    public void decrementDx() { resetDy(); this.dx = -this.speed < -GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED ? -GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED: -this.speed; this.dir = Direction.DIRECTION_LEFT;}

    public void incrementDx() { resetDy(); this.dx = this.speed > GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED ? GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED: this.speed; this.dir = Direction.DIRECTION_RIGHT;}
    
    public void decrementDy() { resetDx(); this.dy = -this.speed < GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED ? -GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED: -this.speed; this.dir = Direction.DIRECTION_UPWARDS;}

    public void incrementDy() { resetDx(); this.dy = this.speed > GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED ? GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED: this.speed; this.dir = Direction.DIRECTION_DOWNWARDS;}

    public void resetDx() { this.dx=0; }

    public void resetDy() { this.dy=0; }

    public Bullet shoot() {
        return myTank.shoot();
    }

    public void getDamage() {
        life = life>0? --life: 0;
    }

    public int getRemainingLife() {
        return life;
    }

    public PlayerType getPlayerType() {return this.playerType;}

}
