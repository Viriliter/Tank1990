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

import tank1990.core.Direction;
import tank1990.core.DynamicGameObject;
import tank1990.core.GlobalConstants;
import tank1990.tank.AbstractTank;

public class Bullet extends DynamicGameObject {

    AbstractTank tankInst = null;
    int speed;

    public Bullet(AbstractTank tankInst, int x, int y, Direction dir, int speed) {
        this.tankInst = tankInst;
        setX(x);
        setY(y);
        setDir(dir);

        this.speed = speed;
    }

    @Override
    public void move() {
        switch (dir) {
            case DIRECTION_UPWARDS:
                setX(x);
                setY(y - this.speed*GlobalConstants.BULLET_SPEED_PER_TICK);
                break;
            case DIRECTION_RIGHT:
                setX(x + this.speed*GlobalConstants.BULLET_SPEED_PER_TICK);
                setY(y);
                break;
            case DIRECTION_DOWNWARDS:
                setX(x);
                setY(y + this.speed*GlobalConstants.BULLET_SPEED_PER_TICK);
                break;
            case DIRECTION_LEFT:
                setX(x - this.speed*GlobalConstants.BULLET_SPEED_PER_TICK);
                setY(y);
                break;
            default:
                System.err.printf("Invalid direction: %s", dir);
                break;
        }

    }

    @Override
    public void repaint() {

    }

    public void destroy() {
        this.tankInst.setBulletStatus(true);
    }

    public boolean checkCollision() {
        return false;
    }



}
