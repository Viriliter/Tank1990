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

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;

import java.awt.Rectangle;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * @class GlobalConstants
 * @brief Stores game specific configuration parameters and file paths of the resources that can be used throughout the application life.
 */
public interface GlobalConstants {
    public static final String CONFIGURATION_FILE = "config.ini";
    public static final int GAME_TICK_MS = 10;

    public static final String GAME_TITLE = "TANK 1990 - NES";

    public static final int TILE_SUBDIVISION = 4;

    public static final int COL_TILE_COUNT = 13;// * TILE_SUBDIVISION;
    public static final int ROW_TILE_COUNT = 13;// * TILE_SUBDIVISION;

    public static final double SCALE = 3.75;

    public static final int GAME_FONT = 4;

    /**
     * PLAYER KEYBOARD CONTROLS
     */

    public static final int KEY_PLAYER_1_MOVE_UP = KeyEvent.VK_UP;
    public static final int KEY_PLAYER_1_MOVE_RIGHT = KeyEvent.VK_RIGHT;
    public static final int KEY_PLAYER_1_MOVE_DOWN = KeyEvent.VK_DOWN;
    public static final int KEY_PLAYER_1_MOVE_LEFT = KeyEvent.VK_LEFT;
    public static final int KEY_PLAYER_1_MOVE_SHOOT = KeyEvent.KEY_LOCATION_RIGHT;
    public static final int KEY_PLAYER_2_MOVE_UP = KeyEvent.VK_W;
    public static final int KEY_PLAYER_2_MOVE_RIGHT = KeyEvent.VK_D;
    public static final int KEY_PLAYER_2_MOVE_DOWN = KeyEvent.VK_S;
    public static final int KEY_PLAYER_2_MOVE_LEFT = KeyEvent.VK_A;
    public static final int KEY_PLAYER_2_MOVE_SHOOT = KeyEvent.KEY_LOCATION_LEFT;
    //KeyEvent.VK_CONTROL: 17: 00010001
    //KeyEvent.KEY_LOCATION_RIGHT: 3: 00000011
    //KeyEvent.KEY_LOCATION_LEFT: 2: 00000010


    /**
     * PLAYER PARAMETERS
     */

    public static final int INITIAL_PLAYER_1_X = 0;
    public static final int INITIAL_PLAYER_1_Y = 0;
    public static final int INITIAL_PLAYER_2_X = 0;
    public static final int INITIAL_PLAYER_2_Y = 0;

    /**
     * GAME ENGINE PARAMETERS
     */

    public static final int BULLET_SPEED_PER_TICK = 10;  // It defines movementSpeed speed of bulletSpeed in pixels for each GameTick

    public static final int ENEMY_SPAWN_TIME = 3;
    public static final int PLAYER_SPAWN_TIME = 3;
    public static final int SPAWN_PROTECTION_TIME = 3;

    public static final int SKID_DISTANCE = 10;


    /**
     * POWERUPS
     */

    public static final String ICON_POWERUP_GRENADE_PATH = "textures/powerups/powerup-grenade.png";
    public static final String ICON_POWERUP_HELMET_PATH = "textures/powerups/powerup-helmet.png";
    public static final String ICON_POWERUP_SHOVEL_PATH = "textures/powerups/powerup-shovel.png";
    public static final String ICON_POWERUP_STAR_PATH = "textures/powerups/powerup-star.png";
    public static final String ICON_POWERUP_TANK_PATH = "textures/powerups/powerup-tank.png";
    public static final String ICON_POWERUP_TIMER_PATH = "textures/powerups/powerup-timer.png";

    /**
     * TILES
     */

    public static final String TEXTURE_TILE_BRICKS_PATH = "textures/tiles/tile-bricks.png";
    public static final String TEXTURE_TILE_STEEL_PATH = "textures/tiles/tile-steel.png";
    public static final String TEXTURE_TILE_TREES_PATH = "textures/tiles/tile-trees.png";
    public static final String TEXTURE_TILE_WATER_PATH = "textures/tiles/tile-water.png";
    public static final String TEXTURE_TILE_ICE_PATH = "textures/tiles/tile-ice.png";
    public static final String TEXTURE_TILE_EAGLE_PATH = "textures/tiles/tile-eagle.png";

    /**
     * TANKS
     */
    public static final String TEXTURE_PLAYER1_TANK_PATH = "textures/tank/tank-player-1.png";
    public static final String TEXTURE_PLAYER2_TANK_PATH = "textures/tank/tank-player-2.png";

    public static final String TEXTURE_BASIC_TANK = "textures/tank/basic-tank.png";
    public static final String TEXTURE_FAST_TANK = "textures/tank/fast-tank.png";
    public static final String TEXTURE_POWER_TANK = "textures/tank/power-tank.png";
    public static final String TEXTURE_ARMOR_TANK = "textures/tank/armor-tankpng";


    /**
     * MISCS TEXTURES
     */


    /**
     * SOUND EFFECTS
     */

    /**
     * UTILITY FUNCTIONS
     */

    static int Time2GameTick(int durationMs) {
        return durationMs / GAME_TICK_MS;
    }

    static int GameTick2Time(int tick) {
        return tick * GAME_TICK_MS;
    }

    static ImageIcon loadPNGIcon(String path, int targetWidth, int targetHeight) throws NullPointerException {
        InputStream inputStream = GlobalConstants.class.getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            System.err.println("PNG file not found: " + path);
            return null;
        }

        try {
            ImageIcon icon = new ImageIcon(inputStream.readAllBytes());
            Image image = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);  // Resize image
            return new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static Image loadTexture(String fileName) {
        try {
            InputStream inputStream = GlobalConstants.class.getClassLoader().getResourceAsStream(fileName);
            if (inputStream == null) {
                System.err.println("Texture file not found: " + fileName);
                return null;
            }
            return ImageIO.read(inputStream);  // Load as BufferedImage
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    static Image loadTexture(String fileName, int width, int height) {
        try {
            InputStream inputStream = GlobalConstants.class.getClassLoader().getResourceAsStream(fileName);
            if (inputStream == null) {
                System.err.println("Texture file not found: " + fileName);
                return null;
            }
            Image original = ImageIO.read(inputStream);  // Load as BufferedImage
            return original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double radToDeg(double radians) {
        return Math.toDegrees(radians);
    }

    // Convert degrees to radians
    public static double degToRad(double degrees) {
        return Math.toRadians(degrees);
    }

    public static boolean isObjectsCollided(Rectangle rect1, Rectangle rect2) {
        return rect1.getBounds().intersects(rect2.getBounds()) ||
                rect2.getBounds().contains(rect1.getBounds()) ||
                rect1.getBounds().contains(rect2.getBounds());
    }

    public static boolean isObjectsCollided(RectangleBound rectBound1, RectangleBound rectBound2) {
        return RectangleBound.isCollided(rectBound1, rectBound2);
    }

    public static boolean isObjectsCollided(Rectangle rect1, RectangleBound rectBound2) {
        return RectangleBound.isCollided(rect1, rectBound2);
    }

}
