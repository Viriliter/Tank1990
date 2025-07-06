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

import tank1990.player.Player;
import tank1990.player.PlayerType;
import tank1990.powerup.Powerup;
import tank1990.projectiles.Bullet;
import tank1990.tank.Enemy;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GameEngine {
    // Game objects

    private ArrayList<Player> players = null;       /*< List of zombies in the game. */
    private ArrayList<Enemy> enemies = null;        /*< List of enemies in the game. */
    private ArrayList<Powerup> powerups = null;     /*< List of powerups in the game. */
    private ArrayList<TextureFX> blastFX = null;    /*< List of blast effects in the game. */
    private ArrayList<Bullet> bullets = null;       /*< List of bullets in the game. */

    // Game parameters
    private boolean isStopped = false;
    private boolean isPaused = false;            /*< Flag indicating whether the game is paused. */

    private Timer gameTimer;                         /*< Timer for handling periodic updates. */

    private GameMode gameMode = GameMode.MODE_SINGLE_PLAYER;

    public GameEngine(GameMode gameMode) {
        this.gameMode = gameMode;

        // Add first player by default
        players.add(new Player(ConfigHandler.getInstance().getPlayerProperties(), PlayerType.PLAYER_1));

        // Add second player if game mode is multiplayer
        if (this.gameMode==GameMode.MODE_MULTI_PLAYER) {
            players.add(new Player(ConfigHandler.getInstance().getPlayerProperties(), PlayerType.PLAYER_2));
        }

    }

    /**
     * Custom paint method for rendering the game area.
     *
     * This method is responsible for drawing all game objects (player, zombies, projectiles, etc.) on the screen.
     *
     * @param g The Graphics object used to render the game area.
     */
    public void paintComponent(Graphics g) {

    }

    public void update() {
        // Update players movement

        // Update projectiles movement

        // Update enemies movement

        // Check collisions

        // Update Game Info

        // Update Visual FXs

    }

    public Player getPlayer1() { return players.getFirst(); }

    public Player getPlayer2() {
        if (this.gameMode==GameMode.MODE_MULTI_PLAYER) return players.getLast();
        return null;
    }

    public boolean isStopped() {
        return this.isStopped;
    }

    public void stop() {
        this.isStopped = true;
    }

}
