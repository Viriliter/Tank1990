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

import tank1990.core.*;

import java.util.Random;

public class BasicTank extends AbstractTank implements Enemy {

    public BasicTank(int x, int y, Direction dir) {
        super(x, y, dir);

        setDefaultTankTextureFXs();

        setSpeedUnit(Globals.BASIC_TANK_MOVEMENT_SPEED);
        setMaxSpeedUnit(Globals.BASIC_TANK_MOVEMENT_MAX_SPEED);
    }

    @Override
    protected void setDefaultTankTextureFXs() {
        this.tankTextureFxStruct = Globals.TEXTURE_BASIC_TANK_STRUCT;
        createTextureFXs();
    }

    @Override
    protected void setRedTankTextureFXs() {
        this.tankTextureFxStruct = Globals.TEXTURE_BASIC_TANK_RED_STRUCT;
        createTextureFXs();
    }

    @Override
    public void move(GameLevel level) {
        Random random = new Random();
        int behavior = random.nextInt(1,11);

        if (behavior<=8) { // Move towards to target with %80 percentage
            moveToTarget(level);
        } else {  // Move randomly with %20 percentage
            moveToRandom();
        }
    }

}
