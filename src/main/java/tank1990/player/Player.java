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

import tank1990.core.ConfigHandler.PlayerProperties;

import java.awt.Graphics;

import tank1990.core.Direction;
import tank1990.core.GlobalConstants;
import tank1990.projectiles.Bullet;
import tank1990.tank.PlayerTank;
import tank1990.tank.TankFactory;
import tank1990.tank.TankType;

public class Player {
    private int health;
    private int dx, dy;   // Player's position and movement direction
    private Direction dir = null;
    private int speed = 0;

    private PlayerTank myTank = null;
    private PlayerType playerType = PlayerType.PLAYER_1;

    public Player(PlayerType playerType) {
        this.health = GlobalConstants.INITAL_PLAYER_HEALTH;
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
    }

    public void update(int maxWidth, int maxHeight) {
        // Calculate new position with boundary checking
        int newX = Math.max(0, Math.min(myTank.getX() + this.dx, maxWidth - (int) myTank.getSize().getWidth()));
        int newY = Math.max(0, Math.min(myTank.getY() + this.dy, maxHeight - (int) myTank.getSize().getHeight()));
        
        // Update tank position and direction
        myTank.setX(newX);
        myTank.setY(newY);
        myTank.setDir(dir);
    }

    public void decrementDx() { resetDy(); this.dx = this.dx-this.speed < -GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED ? -GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED: this.dx-this.speed; this.dir = Direction.DIRECTION_LEFT;}

    public void incrementDx() { resetDy(); this.dx = this.dx+this.speed > GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED ? GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED: this.dx+this.speed; this.dir = Direction.DIRECTION_RIGHT;}
    
    public void decrementDy() { resetDx(); this.dy = this.dy-this.speed < GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED ? -GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED: this.dy-this.speed; this.dir = Direction.DIRECTION_UPWARDS;}

    public void incrementDy() { resetDx(); this.dy = this.dy+this.speed > GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED ? GlobalConstants.PLAYER_MOVEMENT_MAX_SPEED: this.dy+this.speed; this.dir = Direction.DIRECTION_DOWNWARDS;}

    public void resetDx() { this.dx=0; }

    public void resetDy() { this.dy=0; }

    public Bullet shoot() {
        return myTank.shoot();
    }

    public PlayerType getPlayerType() {return this.playerType;}

}
