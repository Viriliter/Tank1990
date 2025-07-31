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

public class PowerupFactory {
    /**
     * Creates a new powerup based on the given type.
     *
     * This method generates an enemy using the provided configuration
     * and assigns it the given x and y coordinates.
     *
     * @param type The type of powerup to create.
     * @param x The x-coordinate of the powerup's spawn location.
     * @param y The y-coordinate of the powerup's spawn location.
     * @return A newly created AbstractPowerup object of the specified type.
     * @throws IllegalStateException If the config parameter is null.
     * @throws IllegalArgumentException If the type parameter is null.
     */
    public static AbstractPowerup createPowerup(PowerupType type, int x, int y) {
        if (type == null) {
            throw new IllegalArgumentException("Powerup type cannot be null!");
        }

        switch (type) {
            case POWERUP_GRENADE:
                return new PowerupGrenade(x, y);
            case POWERUP_HELMET:
                return new PowerupHelmet(x, y);
            case POWERUP_SHOVEL:
                return new PowerupShovel(x, y);
            case POWERUP_STAR:
                return new PowerupStar(x, y);
            case POWERUP_TANK:
                return new PowerupTank(x, y);
            case POWERUP_TIMER:
                return new PowerupTimer(x, y);
            case POWERUP_WEAPON:
                return new PowerupWeapon(x, y);
            default:
                return null;
        }
    }
}
