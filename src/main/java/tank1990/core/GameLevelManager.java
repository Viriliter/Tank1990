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

import tank1990.tank.AbstractTank;

import java.util.ArrayList;
import java.util.List;

public class GameLevelManager {
    private List<GameLevel> gameLevels; // List of game levels
    private int currentLevelIndex; // Index of the current game level
    private int playerScore; // Player's score

    private TimeTick spawnTick;

    private static GameLevelManager instance;

    private GameLevelManager() {
        this.gameLevels = new ArrayList<>();
        this.currentLevelIndex = 0;
        this.playerScore = 0;
    }

    public static GameLevelManager getInstance() {
        if (instance == null) {
            instance = new GameLevelManager();
        }
        return instance;
    }

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
        return this.playerScore;
    }

    public GameLevel nextLevel() {
        return loadLevel(++this.currentLevelIndex % this.gameLevels.size());
    }

    private GameLevel loadLevel(int levelIndex) {
        if (levelIndex < 0 || levelIndex >= this.gameLevels.size()) {
            throw new IndexOutOfBoundsException("Invalid level index: " + levelIndex);
        }
        return this.gameLevels.get(levelIndex);
    }

    public AbstractTank update() {
        GameLevel gameLevel = getCurrentLevel();
        if (this.spawnTick!=null) this.spawnTick.updateTick();

        if (gameLevel.getActiveEnemyTankCount()<4 && gameLevel.getEnemyTankCounts()>0) {
            if (this.spawnTick==null) {
                this.spawnTick = new TimeTick(Globals.ENEMY_SPAWN_DELAY);
                this.spawnTick.setRepeats(1);
                return null;
            }

            return this.spawnTick.isTimeOut() ? gameLevel.spawnEnemyTank(): null;
        }

        return null;
    }
}
