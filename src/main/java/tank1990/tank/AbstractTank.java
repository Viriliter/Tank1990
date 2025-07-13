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

package tank1990.tank;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import tank1990.core.Direction;
import tank1990.core.DynamicGameObject;
import tank1990.core.GlobalConstants;
import tank1990.core.GridSize;
import tank1990.core.TankTextureStruct;
import tank1990.core.TextureFX;
import tank1990.powerup.Powerup;
import tank1990.projectiles.Bullet;

public abstract class AbstractTank extends DynamicGameObject {
    private boolean isBulletDestroyed = false;

    private int points = 200;
    private int armorLevel = 1;
    private int movement = 3;
    private int bullet = 2;
    private transient Map<Direction, TextureFX> textureFXs = null;
    protected TankTextureStruct tankTextureFxStruct = null;

    public AbstractTank(int row, int col, Direction dir) {
        setRow(row);
        setCol(col);
        setDir(dir);
        setSize(new GridSize(GlobalConstants.TANK_WIDTH, GlobalConstants.TANK_HEIGHT));
    }

    public void createTextureFXs() {
        if (this.tankTextureFxStruct==null) return;

        this.textureFXs = new HashMap<>();

        this.textureFXs.put(Direction.DIRECTION_UPWARDS, new TextureFX(this.tankTextureFxStruct.upwardsTexturePath));
        this.textureFXs.get(Direction.DIRECTION_UPWARDS).setTargetSize(getSize().width(), getSize().height());
        this.textureFXs.put(Direction.DIRECTION_RIGHT, new TextureFX(this.tankTextureFxStruct.rightTexturePath));
        this.textureFXs.get(Direction.DIRECTION_RIGHT).setTargetSize(getSize().width(), getSize().height());
        this.textureFXs.put(Direction.DIRECTION_DOWNWARDS, new TextureFX(this.tankTextureFxStruct.downwardsTexturePath));
        this.textureFXs.get(Direction.DIRECTION_DOWNWARDS).setTargetSize(getSize().width(), getSize().height());
        this.textureFXs.put(Direction.DIRECTION_LEFT, new TextureFX(this.tankTextureFxStruct.leftTexturePath));
        this.textureFXs.get(Direction.DIRECTION_LEFT).setTargetSize(getSize().width(), getSize().height());
    }

    @Override
    public void draw(Graphics g) {
        // Draw tank animations
        this.textureFXs.get(dir).drawGrid(g, row, col);
    }

    public abstract void update();

    @Override
    public void move() {

    }
    
    @Override
    public void setDir(Direction dir) {
        this.dir = dir;
    }

    public int getPoints() {return this.points;}
    public int getArmorLevel() {return this.armorLevel;}
    public int getMovement() {return this.movement;}
    public int getBullet() {return this.bullet;}

    public void setPoints(int points) {this.points = points;}
    public void setArmorLevel(int armorLevel) {this.armorLevel = armorLevel;}
    public void setMovement(int movement) {this.movement = movement;}
    public void setBullet(int bullet) {this.bullet = bullet;}

    public Bullet shoot() {
        if (!this.isBulletDestroyed) return null;

        return new Bullet(this, getCol(), getRow(), getDir(), GlobalConstants.BULLET_SPEED_PER_TICK);
    }

    public void getDamage() {
        // After each damage decrement armor level by one.
        armorLevel = armorLevel>0 ? --armorLevel: 0;
    }

    public void getPowerup(Powerup powerup) {

    }

    public void setBulletStatus(boolean isBulletDestroyed) {
        this.isBulletDestroyed = isBulletDestroyed;
    }

    public boolean checkCollision() {
        return false;
    }

}
