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

    /**
     * Update the player tank and its position in the game level.
     * @param level the current game level
     */
    public void update(GameLevel level) {
        myTank.update(level);

        // Update player location in the level
        GridLocation gLoc = Utils.loc2GridLoc(new Location(myTank.getX(), myTank.getY()));
        level.setPlayerLocation(gLoc);
    }

    /**
     * Decrement the tank's horizontal speed (dx).
     */
    public void decrementDx() { this.myTank.decrementDx(); }

    /**
     * Increment the tank's horizontal speed (dx).
     */
    public void incrementDx() { this.myTank.incrementDx(); }

    /**
     * Decrement the tank's vertical speed (dy).
     */
    public void decrementDy() { this.myTank.decrementDy(); }

    /**
     * Increment the tank's vertical speed (dy).
     */
    public void incrementDy() { this.myTank.incrementDy(); }

    /**
     * Reset the tank's horizontal speed (dx) to zero.
     */
    public void resetDx() { this.myTank.resetDx(); }

    /**
     * Reset the tank's vertical speed (dy) to zero.
     */
    public void resetDy() { this.myTank.resetDy(); }

    /**
     * Shoot a bullet from the player's tank.
     * @return the bullet shot by the tank
     */
    public Bullet shoot() {
        return myTank.shoot();
    }

    /**
     * Check if the player's tank is damaged.
     * @return true if the tank is damaged, false otherwise
     */
    public boolean getDamage() {
        boolean isDamaged = myTank.getDamage();
        if (isDamaged) {
            --remainingLife;
        }

        return isDamaged;
    }

    /**
     * Destroy the player's tank and return a blast effect.
     * @return the blast effect from the tank's destruction
     */
    public Blast destroy() {
        --remainingLife;
        return myTank.destroy();
    }

    /**
     * Get the number of remaining lives for the player.
     * @return the number of remaining lives
     */
    public int getRemainingLives() {
        return remainingLife;
    }

    /**
     * Collect a powerup and apply its effects to the player's tank.
     * @param powerup the powerup to collect
     */
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

    /**
     * Check if the player's tank is destroyed.
     * @return true if the tank is destroyed, false otherwise
     */
    public boolean isTankDestroyed() { return myTank.isDestroyed(); }

    /**
     * Get the type of the player (PLAYER_1 or PLAYER_2).
     * @return the PlayerType of this player
     */
    public PlayerType getPlayerType() {return this.playerType;}

    /**
     * Get the player's tank instance.
     * @return the PlayerTank instance associated with this player
     */
    public AbstractTank getTank() { return myTank; }

    /**
     * Check if the player's tank is frozen.
     */
    public void isFrozen() {
        myTank.isFrozen();
    }

    /**
     * Set the frozen state of the player's tank.
     * @param frozen true to freeze the tank, false to unfreeze it
     */
    public void setFrozen(boolean frozen) {
        myTank.setFrozen(frozen);
    }

    /**
     * Set the remaining lives for the player.
     * @param lives the number of lives to set
     */
    public void setRemainingLives(int lives) {
        this.remainingLife = lives;
    }

    /**
     * Get the bounding box of the player's tank.
     * @return the RectangleBound representing the bounding box of the tank
     */
    public RectangleBound getBoundingBox() { return myTank.getBoundingBox(); }

    /**
     * Spawn a new tank for the player.
     * This method is called when the player needs to respawn or start the game.
     */
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
