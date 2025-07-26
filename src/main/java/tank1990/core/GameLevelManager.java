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

import java.util.ArrayList;
import java.util.List;

import tank1990.tank.*;

public class GameLevelManager {
    private List<GameLevel> gameLevels; // List of game levels
    private int currentLevelIndex; // Index of the current game level
    private int totalPlayerScore; // Total score of the player

    private TimeTick spawnTick;

    private static GameLevelManager instance;

    private GameScoreStruct gameScore = null;

    private GameLevelManager() {
        this.gameLevels = new ArrayList<>();
        this.currentLevelIndex = 0;
        this.totalPlayerScore = 0;
    }

    public static GameLevelManager getInstance() {
        if (instance == null) {
            instance = new GameLevelManager();
        }
        return instance;
    }

    /**
     * Initializes the game level manager by adding predefined levels.
     */
    public void addLevels() {
        // Add predefined game levels to the manager
        for (int i = 1; i <= Globals.GAME_LEVEL_COUNTS; i++) {
            // FIXME: Use a proper path for the level files
            // For now, we will use a placeholder path
            GameLevel level = new GameLevel(Globals.MAP_PATH + String.format("stage-%02d.bin", 1));
            this.gameLevels.add(level);
        }
    }

    public int getCurrentIndex() { return this.currentLevelIndex; }

    public GameLevel getCurrentLevel() {
        return this.gameLevels.get(this.currentLevelIndex);
    }

    public int getPlayerScore() {
        return (this.gameScore==null)? 0: this.gameScore.getTotalScore();
    }

    public GameScoreStruct getGameScore() {
        return this.gameScore;
    }

    public void reset() {
        System.out.println("Resetting game level manager...");
        this.currentLevelIndex = 0;
        this.totalPlayerScore = 0;
        this.gameScore = new GameScoreStruct();
        this.spawnTick = null;

        // Reset all game levels
        this.gameLevels.clear();

        // Load the first level
        loadLevel(this.currentLevelIndex);
    }

    public GameLevel nextLevel() {
        currentLevelIndex = Math.max(1, ++this.currentLevelIndex % (this.gameLevels.size()+1));
        return loadLevel(currentLevelIndex);
    }

    /**
     * Loads a game level by its index.
     *
     * @param levelIndex The index of the level to load.
     * @return The loaded GameLevel instance.
     * @throws IndexOutOfBoundsException if the levelIndex is invalid.
     */
    private GameLevel loadLevel(int levelIndex) {
        System.out.println("Loading level: " + levelIndex);

        if (levelIndex < 0 || levelIndex >= this.gameLevels.size()) {
            throw new IndexOutOfBoundsException("Invalid level index: " + levelIndex);
        }
        // Create a new game score structure for the new level
        this.gameScore = new GameScoreStruct();
        this.gameScore.setReachedLevel(levelIndex);
        this.gameScore.setHiScore(ConfigHandler.getInstance().getBattleCityProperties().hiScore());
        this.gameScore.setRemainingTankCount(levelIndex);

        if (this.spawnTick!=null) this.spawnTick.reset();

        return this.gameLevels.get(levelIndex);
    }

    /**
     * Updates the game level and spawns enemy tanks if conditions are met.
     *
     * @return An instance of AbstractTank if an enemy tank is spawned, null otherwise.
     */
    public AbstractTank update() {
        GameLevel gameLevel = getCurrentLevel();
        gameLevel.update();

        if (this.spawnTick!=null) this.spawnTick.updateTick();

        if (gameLevel.getActiveEnemyTankCount()<4 && gameLevel.getEnemyTankCounts()>0) {
            if (this.spawnTick==null) {
                this.spawnTick = new TimeTick(Utils.Time2GameTick(Globals.ENEMY_TANK_SPAWN_DELAY_MS));
                this.spawnTick.setRepeats(1);
                return null;
            }

            return this.spawnTick.isTimeOut() ? gameLevel.spawnEnemyTank(): null;
        }

        return null;
    }

    private void addPlayerScore(int point) {
        if (this.gameScore != null) {
            this.gameScore.setTotalScore(this.gameScore.getTotalScore() + point);
        }
    }

    /**
     * Adds the score of a tank to the game score.
     * This method updates the score based on the type of tank and increments the count of that tank type.
     *
     * @param tank The tank whose score is to be added.
     */
    public void addTankScore(AbstractTank tank) {
        if (this.gameScore==null) return;

        if (tank instanceof BasicTank) {
            this.gameScore.setBasicTankScore(this.gameScore.getBasicTankScore()+tank.getPoints());
            this.gameScore.setBasicTankCount(this.gameScore.getBasicTankCount()+1);
        }
        else if (tank instanceof FastTank) {
            this.gameScore.setFastTankScore(this.gameScore.getFastTankScore()+tank.getPoints());
            this.gameScore.setFastTankCount(this.gameScore.getFastTankCount()+1);
        }
        else if (tank instanceof PowerTank) {
            this.gameScore.setPowerTankScore(this.gameScore.getPowerTankScore()+tank.getPoints());
            this.gameScore.setPowerTankCount(this.gameScore.getPowerTankCount()+1);
        }
        else if (tank instanceof ArmorTank) {
            this.gameScore.setArmorTankScore(this.gameScore.getArmorTankScore()+tank.getPoints());
            this.gameScore.setArmorTankCount(this.gameScore.getArmorTankCount()+1);
        } else { }

        addPlayerScore(tank.getPoints());
        this.totalPlayerScore = this.gameScore.getTotalScore();

    }

    public void setPlayerLives(int lives) {
        if (this.gameScore != null) {
            this.gameScore.setPlayerRemainingLives(lives);
        }
    }
}
