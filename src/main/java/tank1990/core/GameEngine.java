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

import javax.swing.*;
import java.awt.*;

import java.util.ArrayList;
import java.util.Iterator;

import tank1990.panels.GameAreaPanel;
import tank1990.player.Player;
import tank1990.player.PlayerType;
import tank1990.powerup.AbstractPowerup;
import tank1990.projectiles.Bullet;
import tank1990.tank.AbstractTank;
import tank1990.tank.Enemy;

public class GameEngine extends Subject {
    private GameAreaPanel parentPanel = null;

    // Game objects
    private ArrayList<Player> players = null;       /*< List of zombies in the game. */
    private ArrayList<Enemy> enemies = null;        /*< List of enemies in the game. */
    private ArrayList<AbstractPowerup> powerups = null;     /*< List of powerups in the game. */
    private ArrayList<TextureFX> blastFX = null;    /*< List of blast effects in the game. */
    private ArrayList<Bullet> bullets = null;       /*< List of bullets in the game. */

    // Game parameters
    private boolean isStopped = false;
    private boolean isPaused = false;               /*< Flag indicating whether the game is paused. */

    private Timer gameTimer;                        /*< Timer for handling periodic updates. */

    private GameMode gameMode = GameMode.MODE_SINGLE_PLAYER;
    private GameLevel currentGameLevel = null;

    public GameEngine(GameMode gameMode) {
        this.gameMode = gameMode;

        this.players = new ArrayList<>();

        // Add first player by default
        players.add(new Player(PlayerType.PLAYER_1));

        // Add second player if game mode is multiplayer
        if (this.gameMode==GameMode.MODE_MULTI_PLAYER) {
            players.add(new Player(PlayerType.PLAYER_2));
        }

        // Initialize other game objects
        this.enemies = new ArrayList<>();
        this.powerups = new ArrayList<>();
        this.blastFX = new ArrayList<>();
        this.bullets = new ArrayList<>();

        // Add predefined game levels
        GameLevelManager.getInstance().addLevels();

        // Initialize game timer
        gameTimer = new Timer(GlobalConstants.GAME_TICK_MS, e -> this.update());
        gameTimer.setRepeats(true);
    }

    /**
     * Custom paint method for rendering the game area.
     *
     * This method is responsible for drawing all game objects (player, zombies, projectiles, etc.) on the screen.
     *
     * @param g The Graphics object used to render the game area.
     */
    public void paintComponent(Graphics g) {
        // Draw map
        GameLevel gameLevel = GameLevelManager.getInstance().getCurrentLevel();
        if (gameLevel!=null) gameLevel.draw(g);
        
        // Draw player(s)
        for (Player p: this.players) {
            p.draw(g);
        }
//
        //// Draw enemies
        //for (Enemy e : this.enemies) {
        //    AbstractTank t = (AbstractTank) e;
        //    t.draw(g);
        //}
//
        // Draw bullets
        for (Bullet b : this.bullets) {
            if (b == null) continue;
            b.draw(g);
        }
//
        //// Draw powerups
        //for (Powerup p : this.powerups) {
        //    if (p == null) continue;
        //    p.draw(g);
        //}

        //for (TextureFX tFX : this.TextureFX) {
        //    tFX.draw(g);
        //}
    }

    /**
     * Updates all game objects, and draw them into the screen accordingly.
     * 
     * This method is called periodically to update all the game objects and check for collisions.
     */
    public void update() {
        // Update map
        GameLevel gameLevel = GameLevelManager.getInstance().getCurrentLevel();
        if (gameLevel!=null) gameLevel.update();
        
        // Update players
        updatePlayers(gameLevel);

        // Update players
        updateGameLevel();

        // Update enemies movement
        updateEnemies(gameLevel);

        // Update projectiles movement
        updateProjectiles(gameLevel);

        // Update projectiles movement
        updatePowerups(gameLevel);

        // Check collisions
        checkCollisions(gameLevel);

        // Update Game Info
        updateGameInfo();

        // Update Visual FXs
        updateBlasts();

        notify(EventType.REPAINT, null);  // Notify observers to repaint event
    }

    public void setParentPanel(GameAreaPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    public GameLevel getCurrentLevel() {
        return GameLevelManager.getInstance().getCurrentLevel();
    }

    public int getCurrentLevelIndex() {
        return GameLevelManager.getInstance().getCurrentIndex();
    }

    public Player getPlayer1() { return players.getFirst(); }

    public Player getPlayer2() {
        if (this.gameMode==GameMode.MODE_MULTI_PLAYER) return players.getLast();
        return null;
    }

    public boolean isStopped() {
        return this.isStopped;
    }

    public void start() {
        this.isStopped = false;
        this.isPaused = false;
        this.gameTimer.start();
    }

    public void pause() {
        this.isStopped = false;
        this.isPaused = true;
        this.gameTimer.stop();
    }

    public void stop() {
        this.isStopped = true;
        this.isPaused = false;
        if (this.gameTimer!=null) this.gameTimer.stop();
    }

    public void loadGameLevel() {
        this.stop();

        GameLevel currentGameLevel = GameLevelManager.getInstance().nextLevel();
        if (currentGameLevel != null) {
            this.currentGameLevel = currentGameLevel;
            this.currentGameLevel.setCurrentState(LevelState.LOADED);
        }
    }

    public void startGameLevel() {
        start();
        this.currentGameLevel.setCurrentState(LevelState.PLAYING);
    }

    /**
     * Adds a projectile to the projectile list when the player shoots.
     */
    public void triggerPlayerShooting(Player player) {
        Bullet bullet = player.shoot();
        if (bullet != null) addBullet(bullet);
    }

    private void addBullet(Bullet bullet) {
        if (bullet==null) return;

        this.bullets.add(bullet);
    } 

    private void updatePlayers(GameLevel gameLevel) {
        for (Player p: this.players) {
            p.update(gameLevel);
        }
    }

    /**
     * Updates the game level status, including checking level progression.
     */
    private void updateGameLevel() {
        GameLevel gameLevel = GameLevelManager.getInstance().getCurrentLevel();
        if (gameLevel.getRemainingEnemyTanks() == 0) {
            //finishLevel();            
        }

        //gameLevel.updateAsync();
    }

    private void updateEnemies(GameLevel gameLevel) {
        for (Enemy e: this.enemies) {
            AbstractTank t = (AbstractTank) e;
            t.update();
        }
    }

    private void updateProjectiles(GameLevel gameLevel) {
        Iterator<Bullet> it = this.bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            
            // Update bullet position
            b.update(gameLevel);
            
            System.out.println("Updating bullet: " + b);
            if (b.isOutOfBounds(gameLevel.getGameAreaSize().width, gameLevel.getGameAreaSize().height)) {
                b.destroy(); // Properly destroy bullet and notify tank
                it.remove(); // Remove bullet if it is out of bounds
                continue;
            }
            if (b.checkCollision(gameLevel)) {
                b.destroy(); // Destroy bullet if it collides with something
                it.remove();
                continue;
            }
        }
    }

    private void updatePowerups(GameLevel gameLevel) {
        // TODO implement later
    }

    private void checkCollisions(GameLevel gameLevel) {
        // TODO implement later
    }

    private void updateGameInfo() {
        // TODO implement later
    }

    private void updateBlasts() {
        // TODO implement later
    }

}
