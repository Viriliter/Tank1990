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

import tank1990.Game;
import tank1990.core.DynamicGameObject;
import tank1990.core.GameLevel;
import tank1990.core.TextureFX;

public abstract class AbstractPowerup extends DynamicGameObject {
    protected PowerupType powerupType;
    protected int duration;
    protected transient TextureFX textureFX;
    protected boolean isActive = true; // Flag to indicate if the powerup is active

    AbstractPowerup(int x, int y, PowerupType powerupType, int duration) {
        setX(x);
        setY(y);
        setSize(new Dimension(16, 16)); // Assuming a default size for powerups
        this.powerupType = powerupType;
        this.duration = duration;
    }

    public void draw(Graphics g) {
        this.textureFX.draw(g, getX(), getY(), 0);
    }

    public void update() {
        // Update logic for the powerup, e.g., countdown timer
        if (duration > 0) {
            duration--;
        } else {
            // Logic to remove or deactivate the powerup
            setActive(false);
        }
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public PowerupType getPowerupType() {
        return this.powerupType;
    }
}
