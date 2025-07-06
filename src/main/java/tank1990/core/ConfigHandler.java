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

package tank1990.core;

import org.ini4j.Ini;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * @class ConfigHandler
 * @brief Handles the loading, parsing, and retrieval of configuration values from a file.
 */
public class ConfigHandler implements Serializable{

    public record WindowProperties (
            int windowWidth,
            int windowHeight,
            int x,
            int y
    ) {}

    public record PlayerProperties (
            int initialHealth,
            int speed
    ) {}

    public record TankProperties (
            int points,
            int health,
            int movementSpeed,
            int bulletSpeed
    ) {}

    public record LevelProperties (
    ) {}

    private static Ini ini;

    private static ConfigHandler instance = null;

    private ConfigHandler() {}

    public static ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
        }
        return instance;
    }

    /**
     * Loads and parses the configuration file.
     * @param filePath The path to the configuration file.
     */
    public void parse(String filePath) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (input == null) {
                throw new IOException(filePath + " is not found in resources");
            }
            ConfigHandler.ini = new Ini(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor that loads and parses the configuration file.
     * @param filePath The path to the configuration file.
     */

    /**
     * Sets value for the key of the configuration parameter in provided section.
     * @param section The section where the key locates.
     * @param key The key of the configuration parameter.
     * @param value The value to set.
     */
    public <T> void setProperty(String section, String key, T value) {
        ConfigHandler.ini.put(section, key, value);
    }

    /**
     * Returns window properties of the game.
     * @return The weapon properties.
     */
    public WindowProperties getWindowProperties() {
        int windowWidth = Integer.parseInt(ConfigHandler.ini.get("Window").get("Width"));
        int windowHeight = Integer.parseInt(ConfigHandler.ini.get("Window").get("Height"));
        int x = Integer.parseInt(ConfigHandler.ini.get("Window").get("X"));
        int y = Integer.parseInt(ConfigHandler.ini.get("Window").get("Y"));
        return new WindowProperties(windowWidth, windowHeight, x ,y);
    }

    /**
     * Returns player properties.
     * @return The player properties.
     */
    public PlayerProperties getPlayerProperties() {
        int initialHealth = Integer.parseInt(ConfigHandler.ini.get("Player").get("InitialHealth"));
        int speed = Integer.parseInt(ConfigHandler.ini.get("Player").get("Speed"));
        return new PlayerProperties(initialHealth, speed);
    }

    /**
     * Returns tank properties of the basic type.
     * @return The enemy properties.
     */
    public TankProperties getBasicTankProperties() {
        int points = Integer.parseInt(ConfigHandler.ini.get("BasicTank").get("Points"));
        int health = Integer.parseInt(ConfigHandler.ini.get("BasicTank").get("Health"));
        int movement = Integer.parseInt(ConfigHandler.ini.get("BasicTank").get("Movement"));
        int bullet = Integer.parseInt(ConfigHandler.ini.get("BasicTank").get("Bullet"));
        return new TankProperties(points, health, movement, bullet);
    }
    /**
     * Returns tank properties of the fast type.
     * @return The enemy properties.
     */
    public TankProperties getFastTankProperties() {
        int points = Integer.parseInt(ConfigHandler.ini.get("FastTank").get("Points"));
        int health = Integer.parseInt(ConfigHandler.ini.get("FastTank").get("Health"));
        int movement = Integer.parseInt(ConfigHandler.ini.get("FastTank").get("Movement"));
        int bullet = Integer.parseInt(ConfigHandler.ini.get("FastTank").get("Bullet"));
        return new TankProperties(points, health, movement, bullet);
    }

    /**
     * Returns tank properties of the power type.
     * @return The enemy properties.
     */
    public TankProperties getPowerTankProperties() {
        int points = Integer.parseInt(ConfigHandler.ini.get("PowerTank").get("Points"));
        int health = Integer.parseInt(ConfigHandler.ini.get("PowerTank").get("Health"));
        int movement = Integer.parseInt(ConfigHandler.ini.get("PowerTank").get("Movement"));
        int bullet = Integer.parseInt(ConfigHandler.ini.get("PowerTank").get("Bullet"));
        return new TankProperties(points, health, movement, bullet);
    }

    /**
     * Returns tank properties of the armor type.
     * @return The enemy properties.
     */
    public TankProperties getArmorTankProperties() {
        int points = Integer.parseInt(ConfigHandler.ini.get("FastTank").get("Points"));
        int health = Integer.parseInt(ConfigHandler.ini.get("FastTank").get("Health"));
        int movement = Integer.parseInt(ConfigHandler.ini.get("FastTank").get("Movement"));
        int bullet = Integer.parseInt(ConfigHandler.ini.get("FastTank").get("Bullet"));
        return new TankProperties(points, health, movement, bullet);
    }


    /**
     * Returns level properties of the level.
     * @return The level properties.
     */
    public LevelProperties getLevel1Properties() {
        return new LevelProperties();
    }

}
