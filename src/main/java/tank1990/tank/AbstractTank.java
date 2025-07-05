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

import tank1990.core.ConfigHandler;
import tank1990.core.Direction;
import tank1990.core.DynamicGameObject;
import tank1990.projectiles.Bullet;

public abstract class AbstractTank extends DynamicGameObject {
    ConfigHandler.TankProperties properties = null;
    boolean isBulletDestroyed = false;

    int health;

    public AbstractTank(ConfigHandler.TankProperties properties, int x, int y, Direction dir) {
        this.properties = properties;
        setX(x);
        setY(y);
        setDir(dir);

        this.health = properties.health();
    }

    @Override
    public void repaint() {
        // TODO implement later
    }

    @Override
    public void move() {

    }

    public Bullet fire() {
        if (!this.isBulletDestroyed) return null;

        return new Bullet();
    }

    public void getDamage() {

    }

    public void getPowerup() {

    }

    public void setBulletStatus(boolean isBulletDestroyed) {
        this.isBulletDestroyed = isBulletDestroyed;
    }

}
