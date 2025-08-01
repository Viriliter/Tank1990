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

import java.io.Serializable;

/**
 * @class GameScoreStruct
 * @brief Represents the game score structure containing various scoring metrics.
 * @details This class is used to store and manage the scores and statistics of a game session.
 */
public class GameScoreStruct implements Serializable {
    private int hiScore = 0;
    private int totalScore = 0;
    private int playerRemainingLives = 0;
    private int remainingTankCount = 0;
    private int reachedLevel = 0;
    private int basicTankCount = 0;
    private int fastTankCount = 0;
    private int powerTankCount = 0;
    private int armorTankCount = 0;
    private int basicTankScore = 0;
    private int fastTankScore = 0;
    private int powerTankScore = 0;
    private int armorTankScore = 0;

    public GameScoreStruct() { }

    public int getHiScore() {return this.hiScore; }
    public int getTotalScore() {return this.totalScore; }
    public int getPlayerRemainingLives() {return this.playerRemainingLives; }
    public int getRemainingTankCount() {return this.remainingTankCount; }
    public int getReachedLevel() {return this.reachedLevel; }
    public int getBasicTankCount() {return this.basicTankCount; }
    public int getFastTankCount() {return this.fastTankCount; }
    public int getPowerTankCount() {return this.powerTankCount; }
    public int getArmorTankCount() {return this.armorTankCount; }
    public int getBasicTankScore() {return this.basicTankScore; }
    public int getFastTankScore() {return this.fastTankScore; }
    public int getPowerTankScore() {return this.powerTankScore; }
    public int getArmorTankScore() {return this.armorTankScore; }

    public void setHiScore(int hiScore) { this.hiScore = hiScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public void setPlayerRemainingLives(int playerRemainingLives) {this.playerRemainingLives = playerRemainingLives; }
    public void setRemainingTankCount(int remainingTankCount) {this.remainingTankCount = remainingTankCount; }
    public void setReachedLevel(int reachedLevel) { this.reachedLevel = reachedLevel; }
    public void setBasicTankCount(int basicTankCount) { this.basicTankCount = basicTankCount; }
    public void setFastTankCount(int fastTankCount) { this.fastTankCount = fastTankCount; }
    public void setPowerTankCount(int powerTankCount) { this.powerTankCount = powerTankCount; }
    public void setArmorTankCount(int armorTankCount) { this.armorTankCount = armorTankCount; }
    public void setBasicTankScore(int basicTankScore) { this.basicTankScore = basicTankScore; }
    public void setFastTankScore(int fastTankScore) { this.fastTankScore = fastTankScore; }
    public void setPowerTankScore(int powerTankScore) { this.powerTankScore = powerTankScore; }
    public void setArmorTankScore(int armorTankScore) { this.armorTankScore = armorTankScore; }

}
