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

import tank1990.core.Direction;
import tank1990.core.GameLevel;
import tank1990.core.Globals;
import tank1990.player.PlayerType;

public class PlayerTank extends AbstractTank {

    public PlayerTank(int x, int y) {
        super(x, y);

        this.tankTextureFxStruct = Globals.TEXTURE_PLAYER1_TANK_STRUCT;
        createTextureFXs();

        setSpeedUnit(Globals.PLAYER_TANK_MOVEMENT_SPEED);
        setMaxSpeedUnit(Globals.PLAYER_TANK_MOVEMENT_MAX_SPEED);
    }

    public PlayerTank(int x, int y, Direction dir) {
        super(x, y, dir);

        this.tankTextureFxStruct = Globals.TEXTURE_PLAYER1_TANK_STRUCT;
        createTextureFXs();

        setSpeedUnit(Globals.PLAYER_TANK_MOVEMENT_SPEED);
        setMaxSpeedUnit(Globals.PLAYER_TANK_MOVEMENT_MAX_SPEED);
    }

    @Override
    protected void setDefaultTankTextureFXs() { }

    @Override
    protected void setRedTankTextureFXs() { }

    public void setPlayerType(PlayerType playerType) {
        if (playerType == PlayerType.PLAYER_1) {
            this.tankTextureFxStruct = Globals.TEXTURE_PLAYER1_TANK_STRUCT;
        } else {
            this.tankTextureFxStruct = Globals.TEXTURE_PLAYER2_TANK_STRUCT;
        }
        createTextureFXs();
    }

    @Override
    public void move(GameLevel level) {}

}
