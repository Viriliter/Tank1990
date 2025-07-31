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

package tank1990.powerup;

import java.awt.Dimension;
import java.awt.Graphics;

import tank1990.core.*;

public abstract class AbstractPowerup extends DynamicGameObject {
    protected PowerupType powerupType;
    protected int lifeTimeMs;
    protected transient TextureFX textureFX;
    protected boolean isActive = true; // Flag to indicate if the powerup is active

    private TimeTick lifeTimeTick; // Tick for managing lifetime

    private TimeTick blinkTick; // Tick for blinking effect

    private boolean isVisible = true;

    protected int points = 500;

    AbstractPowerup(int x, int y, PowerupType powerupType, int lifeTimeMs) {
        setX(x);
        setY(y);
        setSize(new Dimension(16, 16)); // Assuming a default size for powerups
        this.powerupType = powerupType;
        this.lifeTimeMs = lifeTimeMs;

        lifeTimeTick = new TimeTick(Utils.Time2GameTick(this.lifeTimeMs)); // Tick for managing lifetime
        lifeTimeTick.setRepeats(0);  // Do not repeat
        blinkTick = new TimeTick(Utils.Time2GameTick(Globals.POWERUP_BLINK_INTERVAL_MS));
        blinkTick.setRepeats(-1); // Repeat indefinitely
    }

    public void draw(Graphics g) {
        if (blinkTick.isTimeOut()) {
            isVisible = !isVisible;  // Toggle visibility
            blinkTick.reset();
        }

        setSize(Utils.normalizeDimension(g, Globals.POWERUP_WIDTH, Globals.POWERUP_HEIGHT));

        if (!isVisible) {
            this.textureFX.draw(g, getX(), getY(), 0);
        }
    }

    public void update() {
        // Update lifetime of the powerup
        lifeTimeTick.updateTick();
        blinkTick.updateTick();

        if (lifeTimeTick.isTimeOut()) {
            System.out.println("Powerup expired: " + powerupType);
            setActive(false);
        }


    }

    /**
     * Checks if the powerup is expired based on its lifetime and active status.
     * @return true if the powerup is expired, false otherwise.
     */
    public boolean isExpired() {
        return !isActive || lifeTimeTick.isTimeOut();
    }

    /**
     * Sets the active status of the powerup.
     * @param active true to activate the powerup, false to deactivate it.
     */
    public void setActive(boolean active) {
        this.isActive = active;
    }

    /**
     * Gets the type of the powerup.
     * @return the PowerupType of this powerup.
     */
    public PowerupType getPowerupType() {
        return this.powerupType;
    }

    /**
     * Gets the points to be earned from this powerup.
     * @return the points
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * Sets the points for this powerup.
     * @param points the points to set
     */
    protected void setPoints(int points) {
        this.points = points;
    }
}
