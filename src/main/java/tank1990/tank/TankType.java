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

/**
 * @class TankType
 * @brief Enum representing the different types of tanks in the game.
 * @details This enum defines the various tank types available, including basic, fast, power, armor, and player tanks.
 */
public enum TankType {
    BASIC_TANK,
    FAST_TANK,
    POWER_TANK,
    ARMOR_TANK,
    PLAYER_TANK;

    public static TankType valueOf(int val) {
        return switch (val) {
            case (0) -> BASIC_TANK;
            case (1) -> FAST_TANK;
            case (2) -> POWER_TANK;
            case (3) -> ARMOR_TANK;
            case (4) -> PLAYER_TANK;
            default -> throw new RuntimeException("Invalid Value for TankType");
        };
    }
}
