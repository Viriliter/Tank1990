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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import tank1990.core.*;
import tank1990.projectiles.Bullet;
import tank1990.tank.PlayerTank;
import tank1990.tank.TankFactory;
import tank1990.tank.TankType;

public class Player {
    private int life;

    private PlayerTank myTank = null;
    private final PlayerType playerType = PlayerType.PLAYER_1;

    public Player(PlayerType playerType) {
        this.life = Globals.INITAL_PLAYER_HEALTH;

        if (playerType == PlayerType.PLAYER_1) {
            myTank = (PlayerTank) TankFactory.createTank(TankType.PLAYER_TANK,
                                                         Utils.gridLoc2Loc(Globals.INITIAL_PLAYER_1_LOC).x(),
                                                         Utils.gridLoc2Loc(Globals.INITIAL_PLAYER_1_LOC).y(),
                                                         Globals.INITIAL_PLAYER_1_DIR);
            assert myTank== null : "PlayerTank is null for player type: " + playerType;
            myTank.setDir(Globals.INITIAL_PLAYER_1_DIR);
            myTank.setPlayerType(playerType);
        } else {
            myTank = (PlayerTank) TankFactory.createTank(TankType.PLAYER_TANK,
                                                         Utils.gridLoc2Loc(Globals.INITIAL_PLAYER_2_LOC).x(),
                                                         Utils.gridLoc2Loc(Globals.INITIAL_PLAYER_2_LOC).y(),
                                                         Globals.INITIAL_PLAYER_2_DIR);
            assert myTank== null : "PlayerTank is null for player type: " + playerType;
            myTank.setDir(Globals.INITIAL_PLAYER_2_DIR);
            myTank.setPlayerType(playerType);
        }
    }

    public void draw(Graphics g) {
        myTank.draw(g);
        //System.out.println("x:" + myTank.getX() + " y:" + myTank.getY() + " dir:" + myTank.getDir() + " speed:" + this.speed);

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();
        g2d.setColor(Color.RED);
        g2d.translate(myTank.getX(), myTank.getY());
        g2d.fillRect(-5, -5, 5, 5);
        g2d.setTransform(oldTransform);
    }

    public void update(GameLevel level) {
        myTank.update(level);

        // Update player location in the level
        GridLocation gLoc = Utils.Loc2GridLoc(new Location(myTank.getX(), myTank.getY()));
        level.setPlayerLocation(gLoc);
    }

    public void decrementDx() { this.myTank.decrementDx(); }

    public void incrementDx() { this.myTank.incrementDx(); }

    public void decrementDy() { this.myTank.decrementDy(); }

    public void incrementDy() { this.myTank.incrementDy(); }

    public void resetDx() { this.myTank.resetDx(); }

    public void resetDy() { this.myTank.resetDy(); }

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
