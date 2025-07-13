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

import java.awt.Graphics;

import tank1990.core.Direction;
import tank1990.core.DynamicGameObject;
import tank1990.core.GlobalConstants;
import tank1990.tank.AbstractTank;

public class Bullet extends DynamicGameObject {

    AbstractTank tankInst = null;
    int speed;

    public Bullet(AbstractTank tankInst, int x, int y, Direction dir, int speed) {
        this.tankInst = tankInst;
        setCol(x);
        setRow(y);
        setDir(dir);

        this.speed = speed;
    }

    @Override
    public void draw(Graphics g) {
        
    }

    public void update() {
        
    }

    @Override
    public void move() {
        switch (dir) {
            case DIRECTION_UPWARDS:
                setCol(col);
                setRow(row - this.speed*GlobalConstants.BULLET_SPEED_PER_TICK);
                break;
            case DIRECTION_RIGHT:
                setCol(col + this.speed*GlobalConstants.BULLET_SPEED_PER_TICK);
                setRow(row);
                break;
            case DIRECTION_DOWNWARDS:
                setCol(col);
                setRow(row + this.speed*GlobalConstants.BULLET_SPEED_PER_TICK);
                break;
            case DIRECTION_LEFT:
                setCol(col - this.speed*GlobalConstants.BULLET_SPEED_PER_TICK);
                setRow(row);
                break;
            default:
                System.err.printf("Invalid direction: %s", dir);
                break;
        }

    }

    public void destroy() {
        this.tankInst.setBulletStatus(true);
    }

    public boolean checkCollision() {
        return false;
    }



}
