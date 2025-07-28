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

/**
 * @class TankFactory
 * @brief A factory class for creating different types of tanks.
 *
 * This class provides a static method to generate zombies of different types
 * based on a configuration handler.
 */
public class TankFactory {
    /**
     * Creates a new tank based on the given type.
     *
     * This method generates an enemy using the provided configuration
     * and assigns it the given x and y coordinates.
     *
     * @param type The type of tank to create.
     * @param x The x-coordinate of the tank's spawn location.
     * @param y The y-coordinate of the tank's spawn location.
     * @return A newly created Tank object of the specified type.
     * @throws IllegalStateException If the config parameter is null.
     * @throws IllegalArgumentException If the type parameter is null.
     */
    public static AbstractTank createTank(TankType type, int x, int y, Direction dir) {
        if (type == null) {
            throw new IllegalArgumentException("Tank type cannot be null!");
        }

        switch (type) {
            case BASIC_TANK:
                return new BasicTank(x, y, dir);
            case FAST_TANK:
                return new FastTank(x, y, dir);
            case POWER_TANK:
                return new PowerTank(x, y, dir);
            case ARMOR_TANK:
                return new ArmorTank(x, y, dir);
            case PLAYER_TANK:
                return new PlayerTank(x, y, dir);
            default:
                return null;
        }
    }

    public static AbstractTank createTank(TankType type, int x, int y) {
        if (type == null) {
            throw new IllegalArgumentException("Tank type cannot be null!");
        }

        switch (type) {
            case BASIC_TANK:
                return new BasicTank(x, y);
            case FAST_TANK:
                return new FastTank(x, y);
            case POWER_TANK:
                return new PowerTank(x, y);
            case ARMOR_TANK:
                return new ArmorTank(x, y);
            case PLAYER_TANK:
                return new PlayerTank(x, y);
            default:
                return null;
        }
    }

}