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
import tank1990.projectiles.Blast;
import tank1990.projectiles.Bullet;
import tank1990.tank.AbstractTank;
import tank1990.tank.Enemy;
import tank1990.tile.Tile;

public class GameEngine extends Subject {
    private GameAreaPanel parentPanel = null;

    // Game objects
    private ArrayList<Player> players = null;       /*< List of zombies in the game. */
    private ArrayList<Enemy> enemies = null;        /*< List of enemies in the game. */
    private ArrayList<AbstractPowerup> powerups = null;     /*< List of powerups in the game. */
    private ArrayList<Blast> blastFXs = null;    /*< List of blast effects in the game. */
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
        this.blastFXs = new ArrayList<>();
        this.bullets = new ArrayList<>();

        // Add predefined game levels
        GameLevelManager.getInstance().addLevels();

        // Initialize game timer
        gameTimer = new Timer(Globals.GAME_TICK_MS, e -> this.update());
        gameTimer.setRepeats(true);
    }

    /**
     * Custom paint method for rendering the game area.
     * This method is responsible for drawing all game objects (player, zombies, projectiles, etc.) on the screen.
     *
     * @param g The Graphics object used to render the game area.
     */
    public void paintComponent(Graphics g) {
        // Draw map [Layer - 0]
        GameLevel gameLevel = GameLevelManager.getInstance().getCurrentLevel();
        if (gameLevel!=null) gameLevel.draw(g, 0);
        
        // Draw player(s) [Layer - 1]
        for (Player p: this.players) {
            p.draw(g);
        }

        // Draw enemies [Layer - 2]
        for (Enemy e : this.enemies) {
            AbstractTank t = (AbstractTank) e;
            t.draw(g);
        }

        // Draw bullets [Layer - 3]
        for (Bullet b : this.bullets) {
            if (b == null) continue;
            b.draw(g);
        }

        // Draw higher layers of game map (trees etc.)
        if (gameLevel!=null) gameLevel.draw(g, 1);

        // Draw blast animations [Layer - 5]
        for (Blast blastFX : this.blastFXs) {
            blastFX.draw(g);
        }

        //// Draw powerups [Layer - 6]
        //for (Powerup p : this.powerups) {
        //    if (p == null) continue;
        //    p.draw(g);
        //}

    }

    /**
     * Updates all game objects, and notify the panel to draw them accordingly.
     * This method is called periodically to update all the game objects and check for collisions.
     */
    public void update() {
        // Update map
        GameLevel gameLevel = GameLevelManager.getInstance().getCurrentLevel();
        GameLevelManager.getInstance().update();

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

    /**
     * Sets the parent panel for this game engine.
     *
     * @param parentPanel The parent GameAreaPanel
     */
    public void setParentPanel(GameAreaPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    /**
     * Gets current game level.
     *
     * @return Current game level
     */
    public GameLevel getCurrentLevel() {
        return GameLevelManager.getInstance().getCurrentLevel();
    }

    /**
     * Gets the index of the current game level.
     *
     * @return Index of the current game level
     */
    public int getCurrentLevelIndex() {
        return GameLevelManager.getInstance().getCurrentIndex();
    }

    /**
     * Gets the first player in multiplayer mode.
     * If the game mode is single player, this method returns the only player.
     *
     * @return The first player in multiplayer mode or the only player in single player mode.
     */
    public Player getPlayer1() { return players.getFirst(); }

    /**
     * Gets the second player in multiplayer mode.
     * If the game mode is single player, this method returns null.
     *
     * @return The second player if in multiplayer mode, otherwise null.
     */
    public Player getPlayer2() {
        if (this.gameMode==GameMode.MODE_MULTI_PLAYER) return players.getLast();
        return null;
    }

    /**
     * Checks if the game is stopped.
     * @return true if the game is stopped, false otherwise.
     */
    public boolean isStopped() {
        return this.isStopped;
    }

    /**
     * Checks if the game is paused.
     * @return true if the game is paused, false otherwise.
     */
    public boolean isPaused() {
        return this.isPaused;
    }

    /**
     * Starts the game engine.
     * This method initializes the game state and starts the game timer.
     */
    public void start() {
        this.isStopped = false;
        this.isPaused = false;
        this.gameTimer.start();

        notify(EventType.STARTED, null);  // Notify observers to repaint event
    }

    /**
     * Resumes the game engine if it was paused.
     * This method resumes the game timer and updates the game state.
     */
    public void pause() {
        this.isStopped = false;
        this.isPaused = true;
        this.gameTimer.stop();

        notify(EventType.PAUSED, null);  // Notify observers to repaint event
    }

    /**
     * Stops the game engine.
     * This method stops the game timer and sets the game state to stopped.
     */
    public void stop() {
        this.isStopped = true;
        this.isPaused = false;
        if (this.gameTimer!=null) this.gameTimer.stop();
    }

    /**
     * Loads the next game level.
     * This method stops the current game level and loads the next one from the GameLevelManager.
     */
    public void loadGameLevel() {
        this.stop();

        GameLevel currentGameLevel = GameLevelManager.getInstance().nextLevel();
        if (currentGameLevel != null) {
            this.currentGameLevel = currentGameLevel;
            this.currentGameLevel.setCurrentState(LevelState.LOADED);
        }
    }

    /**
     * Starts the current game level.
     * This method starts the game timer and sets the game level state to PLAYING.
     */
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

    /**
     * Adds a bullet to the game.
     * @param bullet The bullet to be added
     */
    private void addBullet(Bullet bullet) {
        if (bullet==null) return;

        this.bullets.add(bullet);
    } 

    /**
     * Updates the players' tanks and checks for player deaths.
     * If a player's tank is destroyed, it spawns a new tank.
     * If a player has no remaining lives, they are removed from the game.
     */
    private void updatePlayers(GameLevel gameLevel) {
        // Remove players' tank
        Iterator<Player> it = this.players.iterator();
        while (it.hasNext()) {
            Player p = it.next();

            if (p.isTankDestroyed()) {
                p.spawnTank();
            }

            if (p.getRemainingLife() < 0) {
                // Player is dead, remove from the game
                it.remove();
            }

            p.update(gameLevel);
        }

        // If there are no players left, game is over
        if (this.players.isEmpty()) {
            // No players left, stop the game
            this.stop();
            notify(EventType.GAMEOVER, null);  // Notify observers that the game is over
            return;
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

        AbstractTank newEnemyTank = GameLevelManager.getInstance().update();
        if (newEnemyTank != null) {
            this.enemies.add((Enemy) newEnemyTank);
            //notify(EventType.ENEMY_TANK_SPAWNED, newEnemyTank);
        }

        //gameLevel.updateAsync();
    }

    /**
     * Updates the enemies' tanks and checks for enemy deaths.
     * If an enemy's tank is destroyed, it is removed from the game.
     */
    private void updateEnemies(GameLevel gameLevel) {
        // Remove destroyed enemies
        Iterator<Enemy> it = this.enemies.iterator();
        while (it.hasNext()) {
            AbstractTank t = (AbstractTank) it.next();
            if (t.isDestroyed()) it.remove();
        }

        // Update remaining enemies
        for (Enemy e: this.enemies) {
            AbstractTank t = (AbstractTank) e;
            t.update(gameLevel);
            Bullet bullet = t.shoot();
            if (bullet != null) {
                addBullet(bullet);
            }
        }
    }

    /**
     * Updates the projectiles in the game.
     * This method updates the position of each bullet and checks if they are out of bounds.
     * If a bullet is out of bounds, it is destroyed and removed from the game.
     */
    private void updateProjectiles(GameLevel gameLevel) {
        Iterator<Bullet> it = this.bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            
            // Update bullet position
            b.update(gameLevel);
            
            if (b.isOutOfBounds(gameLevel.getGameAreaSize().width, gameLevel.getGameAreaSize().height)) {
                Blast blast = b.destroy(); // Notify the tank that bullet is destroyed
                blastFXs.add(blast);
                it.remove(); // Remove bullet
                continue;
            }

        }
    }

    /**
     * Updates the powerups in the game.
     * This method updates the position of each powerup and checks if they are collected by players.
     */
    private void updatePowerups(GameLevel gameLevel) {
        for (AbstractPowerup p : this.powerups) {
             p.update();
        }
    }

    /**
     * Checks for collisions between bullets and other game objects (tiles, tanks, etc.).
     * This method iterates through all bullets and checks for collisions with tiles, player tanks, enemy tanks, and other bullets.
     * If a collision occurs, the bullet is destroyed and the appropriate effects are applied.
     */
    private void checkCollisions(GameLevel gameLevel) {
        // Collect bullets to be removed to avoid ConcurrentModificationException
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();

        // Check bullet collisions with tiles, tanks, and other bullets
        Iterator<Bullet> bulletIt = this.bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet bullet = bulletIt.next();
            RectangleBound bulletBounds = bullet.getBoundingBox();
            boolean bulletDestroyed = false;

            // Skip if bullet is already marked for removal
            if (bulletsToRemove.contains(bullet)) {
                continue;
            }

            // 1. Check collision with tiles
            if (!bulletDestroyed && checkBulletTileCollision(bullet, gameLevel)) {
                Blast blast = bullet.destroy();
                blastFXs.add(blast);
                bulletsToRemove.add(bullet);
                bulletDestroyed = true;
                continue;
            }

            // 2. Check collision with player tanks
            if (!bulletDestroyed) {
                for (Player player : this.players) {
                    if (!player.isTankDestroyed()) {
                        RectangleBound tankBounds = player.getBoundingBox();
                        if (tankBounds==null) continue;

                        // Check if bullet intersects with player tank
                        if (bulletBounds.intersects(tankBounds)) {
                            // Check if bullet belongs to enemy
                            if (bullet.isEnemyBullet()) {
                                // Player tank hit by enemy bullet
                                // TODO uncomment this line to enable player damage
                                //player.getDamage();

                                Blast blast = bullet.destroy();
                                blastFXs.add(blast);
                                bulletsToRemove.add(bullet);
                                bulletDestroyed = true;
                                break;
                            }
                        }
                    }
                }
            }

            // 3. Check collision with enemy tanks
            if (!bulletDestroyed) {
                Iterator<Enemy> enemyIt = this.enemies.iterator();
                while (enemyIt.hasNext() && !bulletDestroyed) {
                    Enemy enemy = enemyIt.next();
                    AbstractTank enemyTank = (AbstractTank) enemy;
                    if (!enemyTank.isDestroyed()) {
                        RectangleBound tankBounds = enemyTank.getBoundingBox();
                        if (bulletBounds.intersects(tankBounds)) {
                            // Check if bullet belongs to player (don't let enemies shoot themselves)
                            if (!bullet.isEnemyBullet()) {
                                // Enemy tank hit by player bullet
                                enemyTank.getDamage();

                                // Remove enemy if destroyed
                                if (enemyTank.isDestroyed()) {
                                    enemyIt.remove();
                                }

                                Blast blast = bullet.destroy();
                                blastFXs.add(blast);
                                bulletsToRemove.add(bullet);
                                bulletDestroyed = true;
                                break;
                            }
                        }
                    }
                }
            }

            // 4. Check collision with other bullets
            if (!bulletDestroyed) {
                for (Bullet otherBullet : this.bullets) {
                    // Don't check bullet against itself
                    if (bullet == otherBullet) {
                        continue;
                    }

                    // Skip if other bullet is already marked for removal
                    if (bulletsToRemove.contains(otherBullet)) {
                        continue;
                    }

                    RectangleBound otherBulletBounds = otherBullet.getBoundingBox();
                    if (bulletBounds.intersects(otherBulletBounds)) {
                        // Check if bullets belong to different teams
                        if (bullet.isEnemyBullet() != otherBullet.isEnemyBullet()) {
                            // Bullets from different teams (enemy vs player tanks) collided - destroy both
                            Blast blast1 = bullet.destroy();
                            Blast blast2 = otherBullet.destroy();
                            blastFXs.add(blast1);
                            blastFXs.add(blast2);

                            bulletsToRemove.add(bullet);
                            bulletsToRemove.add(otherBullet);
                            bulletDestroyed = true;
                            break;
                        }
                    }
                }
            }
        }

        // Remove all bullets that were marked for removal
        this.bullets.removeAll(bulletsToRemove);
    }

    /**
     * Updates the game information such as score, lives, etc.
     * This method can be expanded later to include more detailed game statistics.
     */
    private void updateGameInfo() {
        // TODO implement later
    }

    /**
     * Checks if a bullet collides with any destroyable tiles.
     * @param bullet The bullet to check
     * @param gameLevel The current game level
     * @return true if collision occurred and tile was destroyed
     */
    private boolean checkBulletTileCollision(Bullet bullet, GameLevel gameLevel) {
        if (gameLevel == null || gameLevel.getMap() == null) {
            return false;
        }

        RectangleBound bulletBounds = bullet.getBoundingBox();
        Tile[][] map = gameLevel.getMap();

        // Convert bullet position to grid coordinates
        GridLocation bulletGridLoc = Utils.Loc2GridLoc(new Location(bullet.getX(), bullet.getY()));
        int r = bulletGridLoc.rowIndex();
        int c = bulletGridLoc.colIndex();

        // Check the current tile and surrounding tiles for collision
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int checkR = r + dr;
                int checkC = c + dc;

                // Check bounds
                if (checkR < 0 || checkR >= Globals.ROW_TILE_COUNT ||
                    checkC < 0 || checkC >= Globals.COL_TILE_COUNT) {
                    continue;
                }

                Tile tile = map[checkR][checkC];
                if (tile == null) {
                    continue;
                }

                // Get tile position and create bounding box
                Location tilePos = Utils.gridLoc2Loc(new GridLocation(checkR, checkC));
                RectangleBound tileBounds = new RectangleBound(
                    tilePos.x(), tilePos.y(),
                    60, 60
                );

                // Check for intersection
                if (bulletBounds.intersects(tileBounds)) {
                    // Handle different tile types
                    switch(tile.getType()) {
                        case TILE_BRICKS: map[checkR][checkC] = null; return true;
                        case TILE_STEEL: return true;  // Steel blocks bullets but isn't destroyed
                        case TILE_EAGLE:
                            map[checkR][checkC] = null;
                            stop();  // Stop the game engine
                            GameScoreStruct gameScore = new GameScoreStruct();
                            gameScore.setTotalScore(GameLevelManager.getInstance().getPlayerScore());
                            gameScore.setReachedLevel(GameLevelManager.getInstance().getCurrentIndex());

                            // TODO: Add dummy score for now
                            gameScore.setHiScore(20000);
                            gameScore.setTotalScore(4900);
                            gameScore.setReachedLevel(2);
                            gameScore.setBasicTankCount(6);
                            gameScore.setBasicTankScore(600);
                            gameScore.setFastTankCount(5);
                            gameScore.setFastTankScore(1000);
                            gameScore.setPowerTankCount(3);
                            gameScore.setPowerTankScore(900);
                            gameScore.setArmorTankCount(6);
                            gameScore.setArmorTankScore(2400);

                            notify(EventType.GAMEOVER, gameScore);   // Eagle is destroyed - this triggers game over
                            return true;
                        default: break;
                    }
                    // Trees, ice, and water don't stop bullets
                }
            }
        }

        return false;
    }

    /**
     * Updates the blast effects in the game.
     * This method iterates through all blast effects and updates their animations.
     * If a blast effect is done, it is removed from the list.
     */
    private void updateBlasts() {
        Iterator<Blast> it = blastFXs.iterator();
        while (it.hasNext()) {
            Blast blast = it.next();

            if (!blast.update()) {
                // If the blast animation is done, remove it
                it.remove();
            }
        }
    }
}
