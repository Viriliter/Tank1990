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

package tank1990.tank;

import java.awt.Graphics;

import tank1990.core.ConfigHandler;
import tank1990.core.Direction;
import tank1990.core.DynamicGameObject;
import tank1990.powerup.Powerup;
import tank1990.projectiles.Bullet;

public abstract class AbstractTank extends DynamicGameObject {
    private ConfigHandler.TankProperties properties = null;
    private boolean isBulletDestroyed = false;

    private int points = 200;
    private int armorLevel = 1;
    private int movement = 3;
    private int bullet = 2;

    public AbstractTank(int x, int y, Direction dir) {
        setX(x);
        setY(y);
        setDir(dir);
    }

    @Override
    public void repaint() {
        // TODO implement later
    }

    @Override
    public void draw(Graphics g) {
        // Draw tank animations

    }

    @Override
    public void move() {

    }

    public int getPoints() {return this.points;}
    public int getArmorLevel() {return this.armorLevel;}
    public int getMovement() {return this.movement;}
    public int getBullet() {return this.bullet;}

    public void setPoints(int points) {this.points = points;}
    public void setArmorLevel(int armorLevel) {this.armorLevel = armorLevel;}
    public void setMovement(int movement) {this.movement = movement;}
    public void setBullet(int bullet) {this.bullet = bullet;}

    public Bullet shoot() {
        if (!this.isBulletDestroyed) return null;

        return new Bullet(this, getX(), getY(), getDir(), this.properties.bulletSpeed());
    }

    public void getDamage() {
        // After each damage decrement armor level by one.
        armorLevel = armorLevel>0 ? --armorLevel: 0;
    }

    public void getPowerup(Powerup powerup) {

    }

    public void setBulletStatus(boolean isBulletDestroyed) {
        this.isBulletDestroyed = isBulletDestroyed;
    }

    public boolean checkCollision() {
        return false;
    }

}
