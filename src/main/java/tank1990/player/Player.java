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

import tank1990.core.ConfigHandler.PlayerProperties;
import tank1990.core.Direction;
import tank1990.core.GlobalConstants;
import tank1990.projectiles.Bullet;
import tank1990.tank.PlayerTank;
import tank1990.tank.TankFactory;
import tank1990.tank.TankType;

public class Player {
    int health = 0;
    PlayerTank myTank = null;
    PlayerType playerType = PlayerType.PLAYER_1;

    public Player(PlayerProperties properties, PlayerType playerType) {
        this.health = properties.initialHealth();

        if (playerType == PlayerType.PLAYER_1)
            myTank = (PlayerTank) TankFactory.createTank(TankType.PLAYER_TANK, GlobalConstants.INITIAL_PLAYER_1_LOC.x(), GlobalConstants.INITIAL_PLAYER_1_LOC.y(), GlobalConstants.INITIAL_PLAYER_1_DIR);
        else
            myTank = (PlayerTank) TankFactory.createTank(TankType.PLAYER_TANK, GlobalConstants.INITIAL_PLAYER_2_LOC.x(), GlobalConstants.INITIAL_PLAYER_2_LOC.y(), GlobalConstants.INITIAL_PLAYER_2_DIR);
    }

    public void draw() {

    }

    public void incrementDx() {

    }

    public void decrementDx() {

    }

    public void incrementDy() {

    }

    public void decrementDy() {

    }

    public void resetDx() {}

    public void resetDy() {}

    public Bullet shoot() {
        return myTank.shoot();
    }

}
