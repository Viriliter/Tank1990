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

import java.awt.Graphics;
import java.io.Serializable;

import tank1990.core.*;
import tank1990.powerup.AbstractPowerup;
import tank1990.projectiles.Blast;
import tank1990.projectiles.Bullet;
import tank1990.tank.AbstractTank;
import tank1990.tank.PlayerTank;
import tank1990.tank.TankFactory;
import tank1990.tank.TankType;

public class Player implements Serializable {
    private int remainingLife;

    private PlayerTank myTank = null;
    private PlayerType playerType;

    public Player(PlayerType playerType) {
        this.playerType = playerType;
        this.remainingLife = Globals.INITAL_PLAYER_HEALTH;

        // While creating a player, we spawn the tank
        spawnTank();
    }

    public void draw(Graphics g) {
        myTank.draw(g);
        //System.out.println("x:" + myTank.getX() + " y:" + myTank.getY() + " dir:" + myTank.getDir() + " speed:" + this.speed);
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

    public boolean getDamage() {
        boolean isDamaged = myTank.getDamage();
        if (isDamaged) {
            --remainingLife;
        }

        return isDamaged;
    }

    public Blast destroy() {
        --remainingLife;
        return myTank.destroy();
    }

    public int getRemainingLives() {
        return remainingLife;
    }

    public void collectPowerup(AbstractPowerup powerup) {
        if (powerup == null) return;

        myTank.collectPowerup(powerup);

        // Collect the powerup and apply its effects
        switch (powerup.getPowerupType()) {
            case POWERUP_GRENADE -> {
                // No specific action for timer powerup in players
            }
            case POWERUP_HELMET -> {
                // No specific action for timer powerup in players
            }
            case POWERUP_SHOVEL -> {
                // No specific action for timer powerup in players
            }
            case POWERUP_STAR -> {
                // No specific action for timer powerup in players
            }
            case POWERUP_TANK -> {
                remainingLife++; // Increase the player's life by one
            }
            case POWERUP_TIMER -> {
                // No specific action for timer powerup in players
            }
            default -> {
                System.err.println("Unknown powerup type: " + powerup.getPowerupType());
            }
        }

        // Add powerup's points to the player's score
        GameLevelManager.getInstance().addPlayerScore(powerup.getPoints());
    }

    public boolean isTankDestroyed() { return myTank.isDestroyed(); }

    public PlayerType getPlayerType() {return this.playerType;}

    public void isFrozen() {
        myTank.isFrozen();
    }

    public void setFrozen(boolean frozen) {
        myTank.setFrozen(frozen);
    }

    public void setRemainingLives(int lives) {
        this.remainingLife = lives;
    }

    public RectangleBound getBoundingBox() { return myTank.getBoundingBox(); }

    public void spawnTank() {
        remainingLife = remainingLife>0? --remainingLife: 0;

        if (remainingLife >= 0) {
            if (playerType == PlayerType.PLAYER_1) {
                myTank = (PlayerTank) TankFactory.createTank(TankType.PLAYER_TANK,
                                                             Utils.gridLoc2Loc(Globals.INITIAL_PLAYER_1_LOC).x(),
                                                             Utils.gridLoc2Loc(Globals.INITIAL_PLAYER_1_LOC).y(),
                                                             Globals.INITIAL_PLAYER_1_DIR);
                myTank.setDir(Globals.INITIAL_PLAYER_1_DIR);
                myTank.setPlayerType(playerType);
            } else {
                myTank = (PlayerTank) TankFactory.createTank(TankType.PLAYER_TANK,
                                                             Utils.gridLoc2Loc(Globals.INITIAL_PLAYER_2_LOC).x(),
                                                             Utils.gridLoc2Loc(Globals.INITIAL_PLAYER_2_LOC).y(),
                                                             Globals.INITIAL_PLAYER_2_DIR);
                myTank.setDir(Globals.INITIAL_PLAYER_2_DIR);
                myTank.setPlayerType(playerType);
            }
        }
    }
}
