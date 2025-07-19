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

import java.awt.Color;

/**
 * Globals
 * Stores game specific configuration parameters and file paths of the resources that can be used throughout the application life.
 */
public interface Globals {
    String CONFIGURATION_FILE = "config.ini";

    String GAME_TITLE = "TANK 1990 - NES";

    // Game Window Dimensions
    int WINDOW_WIDTH = 1040;
    int WINDOW_HEIGHT = 780;

    // Tile Parameters
    int TILE_WIDTH = 16;
    int TILE_HEIGHT = 16;

    // Tank Parameters
    int TANK_WIDTH = 16;
    int TANK_HEIGHT = 16;
    
    // Bullet Parameters
    int BULLET_WIDTH = 3;
    int BULLET_HEIGHT = 5;
    int BLAST_WIDTH = 8;
    int BLAST_HEIGHT = 8;

    // Powerup Parameters
    int POWERUP_WIDTH = 16;
    int POWERUP_HEIGHT = 16;
    int DEFAULT_POWERUP_DURATION = 10; // Default powerup duration in seconds 

    // Map Dimensions
    int COL_TILE_COUNT = 13;// * TILE_SUBDIVISION;
    int ROW_TILE_COUNT = 13;// * TILE_SUBDIVISION;
    int TILE_SUBDIVISION = 4;

    // Fonts
    String FONT_PRESS_START_2P = "fonts/PressStart2PRegular.ttf";

    /**
     * PLAYER KEYBOARD CONTROLS
     */
    int KEY_PLAYER_1_MOVE_UP = KeyEvent.VK_UP;
    int KEY_PLAYER_1_MOVE_RIGHT = KeyEvent.VK_RIGHT;
    int KEY_PLAYER_1_MOVE_DOWN = KeyEvent.VK_DOWN;
    int KEY_PLAYER_1_MOVE_LEFT = KeyEvent.VK_LEFT;
    int KEY_PLAYER_1_MOVE_SHOOT = KeyEvent.VK_Z;
    int KEY_PLAYER_2_MOVE_UP = KeyEvent.VK_W;
    int KEY_PLAYER_2_MOVE_RIGHT = KeyEvent.VK_D;
    int KEY_PLAYER_2_MOVE_DOWN = KeyEvent.VK_S;
    int KEY_PLAYER_2_MOVE_LEFT = KeyEvent.VK_A;
    int KEY_PLAYER_2_MOVE_SHOOT = KeyEvent.KEY_LOCATION_LEFT;
    //KeyEvent.VK_CONTROL: 17: 00010001
    //KeyEvent.KEY_LOCATION_RIGHT: 3: 00000011
    //KeyEvent.KEY_LOCATION_LEFT: 2: 00000010


    /**
     * PLAYER PARAMETERS
     */
    int INITAL_PLAYER_HEALTH = 3;

    GridLocation INITIAL_PLAYER_1_LOC = new GridLocation(12, 4);
    Direction INITIAL_PLAYER_1_DIR = Direction.DIRECTION_UPWARDS;
    GridLocation INITIAL_PLAYER_2_LOC = new GridLocation(12, 8);
    Direction INITIAL_PLAYER_2_DIR = Direction.DIRECTION_UPWARDS;

    int PLAYER_TANK_MOVEMENT_SPEED = 4;
    int PLAYER_TANK_MOVEMENT_MAX_SPEED = 4;

    /**
     * ENEMY PARAMETERS
     */
    int BASIC_TANK_MOVEMENT_SPEED = 4;
    int BASIC_TANK_MOVEMENT_MAX_SPEED = 4;

    int FAST_TANK_MOVEMENT_SPEED = 8;
    int FAST_TANK_MOVEMENT_MAX_SPEED = 8;

    int POWER_TANK_MOVEMENT_SPEED = 4;
    int POWER_TANK_MOVEMENT_MAX_SPEED = 4;

    int ARMOR_TANK_MOVEMENT_SPEED = 2;
    int ARMOR_TANK_MOVEMENT_MAX_SPEED = 2;

    /**
     * COLOR PALLETTE
     */
    Color COLOR_GRAY = new Color(114, 116, 114); // Gray

    /**
     * GAME ENGINE PARAMETERS
     */
    int GAME_TICK_MS = 100;

    int BULLET_SPEED_PER_TICK = 20;         // It defines movementSpeed speed of bulletSpeed in pixels for each GameTick

    int ENEMY_SPAWN_PROTECTION_TIME = 3;    // Protection time in seconds
    int PLAYER_SPAWN_PROTECTION_TIME = 3;   // Protection time in seconds

    int SKID_DISTANCE = 10;                 // Skid distance of a tank on ice tile in pixels

    /**
     * GAME LEVEL PARAMETERS
     */
    int GAME_LEVEL_COUNTS = 5; // Total number of game levels
    int ENEMY_SPAWN_DELAY = 100;

    /**
     * POWERUPS
     */

    TextureFXStruct TEXTURE_POWERUP_GRENADE = new TextureFXStruct("textures/powerups/powerup-grenade.png",0,0,0);
    TextureFXStruct TEXTURE_POWERUP_HELMET = new TextureFXStruct("textures/powerups/powerup-helmet.png",0,0,0);
    TextureFXStruct TEXTURE_POWERUP_SHOVEL = new TextureFXStruct("textures/powerups/powerup-shovel.png",0,0,0);
    TextureFXStruct TEXTURE_POWERUP_STAR = new TextureFXStruct("textures/powerups/powerup-star.png",0,0,0);
    TextureFXStruct TEXTURE_POWERUP_TANK = new TextureFXStruct("textures/powerups/powerup-tank.png",0,0,0);
    TextureFXStruct TEXTURE_POWERUP_TIMER = new TextureFXStruct("textures/powerups/powerup-timer.png",0,0,0);

    /**
     * TILES
     */

    TextureFXStruct TEXTURE_TILE_BRICKS = new TextureFXStruct("textures/tiles/tile-bricks.png",0,0,0);
    TextureFXStruct TEXTURE_TILE_STEEL = new TextureFXStruct("textures/tiles/tile-steel.png",0,0,0);
    TextureFXStruct TEXTURE_TILE_TREES = new TextureFXStruct("textures/tiles/tile-trees.png",0,0,0);
    TextureFXStruct TEXTURE_TILE_SEA = new TextureFXStruct("textures/tiles/tile-sea.png",0,0,0);
    TextureFXStruct TEXTURE_TILE_ICE = new TextureFXStruct("textures/tiles/tile-ice.png",0,0,0);
    TextureFXStruct TEXTURE_TILE_EAGLE = new TextureFXStruct("textures/tiles/tile-eagle.png",0,0,0);

    /**
     * TANKS
     */
    String TEXTURE_PLAYER1_TANK_PATH = "textures/tank/tank-player-1-right.png";
    
    TankTextureStruct TEXTURE_PLAYER1_TANK_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-player-1-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-player-1-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-player-1-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-player-1-left.png",0,0,0)
    );
    TankTextureStruct TEXTURE_PLAYER2_TANK_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-player-2-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-player-2-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-player-2-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-player-2-left.png",0,0,0)
    );

    TankTextureStruct TEXTURE_BASIC_TANK_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-basic-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-basic-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-basic-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-basic-left.png",0,0,0)
    );
    TankTextureStruct TEXTURE_FAST_TANK_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-fast-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-fast-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-fast-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-fast-left.png",0,0,0)
    );
    TankTextureStruct TEXTURE_POWER_TANK_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-power-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-power-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-power-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-power-left.png",0,0,0)
    );
    TankTextureStruct TEXTURE_ARMOR_TANK_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-armor-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-left.png",0,0,0)
    );

    /**
     * MISCS TEXTURES
     */
    int FRAME_DELAY = 5;
    SpriteAnimationStruct BLAST_ANIMATION = new SpriteAnimationStruct("textures/miscs/blast.png", 3, FRAME_DELAY, 1, 3, 0, 0);

    /**
     * MAPS
     */
    String MAP_PATH = "maps/";


    /**
     * SOUND EFFECTS
     */

    String SOUND_BATTLE_CITY_GAME_START = "battle-city-game-start.wav";
}
