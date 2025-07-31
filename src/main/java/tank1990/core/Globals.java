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

    int MAJOR_VERSION = 1;
    int MINOR_VERSION = 0;

    String CONFIGURATION_FILE = "config.ini";

    String DEFAULT_SAVE_LOCATION = "saves/";

    String GAME_TITLE = "TANK 1990 - NES";
    String COPYRIGHT_TEXT = "Viriliter 2025";

    // Debugging Parameters
    Boolean SHOW_BOUNDING_BOX = false;  // Show bounding box of game objects

    // Game Window Dimensions
    int WINDOW_WIDTH = 1040;
    int WINDOW_HEIGHT = 780;

    int GAMEOVER_OVERLAY_DURATION = 3000;
    int POPUP_OVERLAY_DURATION_MS = 3000;

    // Tile Parameters
    int TILE_WIDTH = 16;
    int TILE_HEIGHT = 16;

    // Tank Parameters
    int TANK_WIDTH = 16;
    int TANK_HEIGHT = 16;

    int DEFAULT_SHOOT_PERIOD_MS = 500;
    
    // Bullet Parameters
    int BULLET_WIDTH = 2;
    int BULLET_HEIGHT = 3;
    int BULLET_SPEED_PER_TICK = 3;         // It defines movementSpeed speed of bulletSpeed in pixels for each GameTick
    int BLAST_WIDTH = 8;
    int BLAST_HEIGHT = 8;

    // Powerup Parameters
    int POWERUP_WIDTH = 16;
    int POWERUP_HEIGHT = 16;
    int DEFAULT_POWERUP_LIFETIME_MS = 15000; // Default powerup duration in milliseconds
    int POWERUP_BLINK_INTERVAL_MS = 500;

    int SHOVEL_COOLDOWN_MS = 10000;  // Cooldown for shovel powerup in milliseconds ((Shovel is a powerup activates when player tank collects shovel powerup))
    int ANTI_SHOVEL_COOLDOWN_MS = 10000;  // Cooldown for anti-shovel powerup in milliseconds (Anti-shovel is a powerup activates when enemy tank collects shovel powerup)
    int FROZEN_COOLDOWN_MS = 5000;  // Cooldown for frozen powerup in milliseconds (Frozen is a powerup activates when either player or enemy tank collects frozen powerup)
    int HELMET_COOLDOWN_MS = 10000;  // Cooldown for helmet powerup in milliseconds (Helmet is a powerup activates when a tank collects helmet powerup)

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

    int ARMOR_TANK_MOVEMENT_SPEED = 4;
    int ARMOR_TANK_MOVEMENT_MAX_SPEED = 4;

    int RED_TANK_BLINK_ANIMATION_PERIOD_MS = 500;

    GridLocation ENEMY_SPAWN_LOCATION_1 = new GridLocation(0, 0);
    GridLocation ENEMY_SPAWN_LOCATION_2 = new GridLocation(0, 6);
    GridLocation ENEMY_SPAWN_LOCATION_3 = new GridLocation(0, 12);

    int ENEMY_TANK_SPAWN_DELAY_MS = 1000;

    /**
     * COLOR PALLETTE
     */
    Color COLOR_GRAY = new Color(114, 116, 114); // Gray
    Color COLOR_RED  = new Color(177, 68, 31); // Red
    Color COLOR_ORANGE  = new Color(222, 156, 71); // orange

    /**
     * GAME ENGINE PARAMETERS
     */
    int GAME_TICK_MS = 12;

    int SPAWN_PROTECTION_COOLDOWN_MS = 1500;    // Protection time in milliseconds
    int SPAWN_PROTECTION_BLINK_PERIOD_MS = 100;   // Blink animation during Protection period in seconds

    int SKID_DISTANCE = 10;                 // Skid distance of a tank on ice tile in pixels

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
    String TEXTURE_TILE_BRICKS_PATH = "textures/tiles/tile-bricks.png";

    // SpriteFXStructs for tiles
    SpriteAnimationStruct TEXTURE_TILE_BRICKS_SPRITE = new SpriteAnimationStruct("textures/tiles/tile-bricks.png", 1, 1, 1, 1, 0, 0);
    SpriteAnimationStruct TEXTURE_TILE_STEEL_SPRITE = new SpriteAnimationStruct("textures/tiles/tile-steel.png", 1, 1, 1, 1, 0, 0);
    SpriteAnimationStruct TEXTURE_TILE_TREES_SPRITE = new SpriteAnimationStruct("textures/tiles/tile-trees.png", 1, 1, 1, 1, 0, 0);
    SpriteAnimationStruct TEXTURE_TILE_SEA_SPRITE = new SpriteAnimationStruct("textures/tiles/tile-sea.png", 4, 60, 1, 4, 0, 0);
    SpriteAnimationStruct TEXTURE_TILE_ICE_SPRITE = new SpriteAnimationStruct("textures/tiles/tile-ice.png", 1, 1, 1, 1, 0, 0);
    SpriteAnimationStruct TEXTURE_TILE_EAGLE_SPRITE = new SpriteAnimationStruct("textures/tiles/tile-eagle.png", 1, 1, 1, 1, 0, 0);
    SpriteAnimationStruct TEXTURE_TILE_WITHDRAW_SPRITE = new SpriteAnimationStruct("textures/tiles/tile-withdraw.png", 1, 1, 1, 1, 0, 0);

    /**
     * TANKS
     */
    String ICON_PLAYER1_TANK_PATH = "textures/tank/tank-player-1-right.png";
    String ICON_BASIC_TANK_PATH = "textures/tank/tank-basic-upwards.png";
    String ICON_FAST_TANK_PATH = "textures/tank/tank-fast-upwards.png";
    String ICON_POWER_TANK_PATH = "textures/tank/tank-power-upwards.png";
    String ICON_ARMOR_TANK_PATH = "textures/tank/tank-armor-l1-upwards.png";

    String ICON_ENEMY_TANK_PATH = "textures/miscs/enemy-icon.png";
    String ICON_PLAYER_LIFE_PATH = "textures/miscs/player-life.png";
    String ICON_STAGE_PATH = "textures/miscs/stage-icon.png";

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
    TankTextureStruct TEXTURE_BASIC_TANK_RED_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-basic-red-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-basic-red-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-basic-red-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-basic-red-left.png",0,0,0)
    );
    TankTextureStruct TEXTURE_FAST_TANK_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-fast-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-fast-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-fast-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-fast-left.png",0,0,0)
    );
    TankTextureStruct TEXTURE_FAST_TANK_RED_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-fast-red-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-fast-red-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-fast-red-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-fast-red-left.png",0,0,0)
    );
    TankTextureStruct TEXTURE_POWER_TANK_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-power-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-power-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-power-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-power-left.png",0,0,0)
    );
    TankTextureStruct TEXTURE_POWER_TANK_RED_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-power-red-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-power-red-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-power-red-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-power-red-left.png",0,0,0)
    );
    TankTextureStruct TEXTURE_ARMOR_TANK_L4_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-armor-l4-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-l4-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-l4-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-l4-left.png",0,0,0)
    );
    TankTextureStruct TEXTURE_ARMOR_TANK_L3_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-armor-l3-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-l3-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-l3-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-l3-left.png",0,0,0)
    );
    TankTextureStruct TEXTURE_ARMOR_TANK_L2_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-armor-l2-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-l2-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-l2-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-l2-left.png",0,0,0)
    );
    TankTextureStruct TEXTURE_ARMOR_TANK_L1_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-armor-l1-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-l1-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-l1-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-l1-left.png",0,0,0)
    );
    TankTextureStruct TEXTURE_ARMOR_TANK_RED_STRUCT = new TankTextureStruct(
            new TextureFXStruct("textures/tank/tank-armor-red-upwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-red-right.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-red-downwards.png",0,0,0),
            new TextureFXStruct("textures/tank/tank-armor-red-left.png",0,0,0)
    );

    /**
     * MISCS TEXTURES
     */
    int FRAME_DELAY = 1;
    SpriteAnimationStruct BLAST_ANIMATION = new SpriteAnimationStruct("textures/miscs/blast.png", 5, FRAME_DELAY*2, 1, 5, 0, 0);

    /**
     * MAPS
     */
    String MAP_PATH = "maps/";


    /**
     * SOUND EFFECTS
     */

    String SOUND_BATTLE_CITY_GAME_START = "battle-city-game-start.wav";
}
