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

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import tank1990.core.*;
import tank1990.powerup.AbstractPowerup;
import tank1990.projectiles.Bullet;

public abstract class AbstractTank extends DynamicGameObject {
    private boolean isBulletDestroyed = true;  // Flag to indicate if the bullet fired from the tank is destroyed or not

    private int points = 200;
    private int armorLevel = 1;

    private int dx, dy;   // Player's position and movement direction
    private int speedUnit;
    private int maxSpeedUnit; // Maximum speed of the player
    private int speed;
    private int maxSpeed; // Maximum speed of the player

    private boolean isRedTank = false; // Flag to indicate if the tank is a red tank

    // Do not move tanks after each update call, but after a certain time interval.
    private TimeTick movementTick;

    // Movement strategy state for consistent movement
    private Direction currentMovementStrategy = Direction.DIRECTION_DOWNWARDS;
    private int movementConsistencyCounter = 0;
    private static final int MIN_MOVEMENT_CONSISTENCY = 5; // Minimum moves in same direction

    private transient Map<Direction, TextureFX> textureFXs = null;
    protected TankTextureStruct tankTextureFxStruct = null;

    RectangleBound mBoundingBoxInfo = null;

    public AbstractTank(int x, int y, Direction dir) {
        setX(x);
        setY(y);
        setDir(dir);
        dx = 0;
        dy = 0;
        speedUnit = 0;
        maxSpeedUnit = 0;
        speed = 0;
        maxSpeed = 0;

        // From experimental results, updating tank movement in every 60 milliseconds is a good value.
        movementTick = new TimeTick(Utils.Time2GameTick(60));
        movementTick.setRepeats(-1);  // Repeat indefinitely
    }

    public void createTextureFXs() {
        if (this.tankTextureFxStruct==null) return;

        this.textureFXs = new HashMap<>();

        this.textureFXs.put(Direction.DIRECTION_UPWARDS, new TextureFX(this.tankTextureFxStruct.upwardsTexturePath));
        this.textureFXs.put(Direction.DIRECTION_RIGHT, new TextureFX(this.tankTextureFxStruct.rightTexturePath));
        this.textureFXs.put(Direction.DIRECTION_DOWNWARDS, new TextureFX(this.tankTextureFxStruct.downwardsTexturePath));
        this.textureFXs.put(Direction.DIRECTION_LEFT, new TextureFX(this.tankTextureFxStruct.leftTexturePath));
    }

    @Override
    public void draw(Graphics g) {
        // Draw tank animations
        java.awt.Rectangle clipBounds = g.getClipBounds();

        Dimension tankSize = Utils.normalizeDimension(g, Globals.TANK_WIDTH, Globals.TANK_HEIGHT);

        // Set the tank size for collision detection - make it smaller to allow movement
        // Tank should be about 80% of cell size to allow for movement between tiles
        this.textureFXs.get(dir).setTargetSize(tankSize.width, tankSize.height);
        this.textureFXs.get(dir).draw(g, getX(), getY(), 0.0);

        this.speed = Utils.normalize(g, this.speedUnit);
        this.maxSpeed = Utils.normalize(g, this.maxSpeedUnit);

        if (Globals.SHOW_BOUNDING_BOX) {
            g.setColor(Color.CYAN);
            g.drawRect((int) getBoundingBox().getX(), (int) getBoundingBox().getY(), (int) getBoundingBox().getWidth(), (int) getBoundingBox().getHeight());
        }
    }

    public void update(GameLevel level) {
        movementTick.updateTick();

        // Do not update tank position if movement tick is not timed out.
        if (!movementTick.isTimeOut()) return;
        movementTick.reset();

        this.move(level);

        // Get tank dimensions for boundary calculations
        int tankWidth = (int) getSize().getWidth();
        int tankHeight = (int) getSize().getHeight();

        Dimension gameAreaSize = level.getGameAreaSize();

        // If tank size is not set, calculate it based on grid
        if (tankWidth == 0 || tankHeight == 0) {
            int cellWidth = (int) gameAreaSize.getWidth() / Globals.COL_TILE_COUNT;
            int cellHeight = (int) gameAreaSize.getHeight() / Globals.ROW_TILE_COUNT;
            tankWidth = cellWidth;
            tankHeight = cellHeight;
        }

        // TODO: Dynamically updating size is not ideal, but necessary for now.
        setSize(new Dimension(tankWidth, tankHeight));  // Dynamically update tank size.

        // Since tank position is center point, calculate proper boundaries
        int halfWidth = tankWidth / 2;
        int halfHeight = tankHeight / 2;

        // Calculate new position with boundary checking (accounting for center position)
        int newX = Math.max(halfWidth, Math.min(getX() + this.dx, (int) gameAreaSize.getWidth() - halfWidth));
        int newY = Math.max(halfHeight, Math.min(getY() + this.dy, (int) gameAreaSize.getHeight() - halfHeight));
        //System.out.println("newX:" + newX + " newY:" + newY);
        RectangleBound newTankBound = new RectangleBound(newX-halfWidth, newY-halfHeight, tankWidth, tankHeight);

        // Check map constraints by checking neighbor tiles of the player tank
        boolean isMovable = level.checkMovable(newTankBound);
        if (isMovable) {
            // Update tank position and direction
            setX(newX);
            setY(newY);

            //GridLocation gLoc = Utils.Loc2GridLoc(new Location(x, y));
            //GridLocation newGLoc = Utils.Loc2GridLoc(new Location(newX, newY));

        } else {}

        setDir(dir);
    }

    /**
     * Getters and Setters for tank properties
     */
    public int getPoints() {return this.points;}

    public int getArmorLevel() {return this.armorLevel;}

    public int getSpeedUnit() {return this.speedUnit;}

    public int getMaxSpeedUnit() {return this.maxSpeedUnit;}

    public void setPoints(int points) {this.points = points;}

    public void setArmorLevel(int armorLevel) {this.armorLevel = armorLevel;}

    public void setSpeedUnit(int speedUnit) {this.speedUnit = speedUnit;}

    public void setMaxSpeedUnit(int maxSpeedUnit) {this.maxSpeedUnit = maxSpeedUnit;}

    /* * Movement methods for the tank
     * These methods update the dx and dy values based on the speed and direction of the tank.
     * They also reset the other movement direction to ensure only one direction is active at a time.
     */
    public void decrementDx() { resetDy(); this.dx = Math.max(this.dx - this.speed, -this.maxSpeed); this.dir = Direction.DIRECTION_LEFT;}

    public void incrementDx() { resetDy(); this.dx = Math.min(this.dx + this.speed, this.maxSpeed); this.dir = Direction.DIRECTION_RIGHT;}

    public void decrementDy() { resetDx(); this.dy = Math.max(this.dy - this.speed, -this.maxSpeed); this.dir = Direction.DIRECTION_UPWARDS; }

    public void incrementDy() { resetDx(); this.dy = Math.min(this.dy + this.speed, this.maxSpeed); this.dir = Direction.DIRECTION_DOWNWARDS;}

    public void resetDx() { this.dx=0; }

    public void resetDy() { this.dy=0; }

    public void rotateRight() {
        switch (this.dir) {
            case DIRECTION_UPWARDS -> this.dir = Direction.DIRECTION_RIGHT;
            case DIRECTION_RIGHT -> this.dir = Direction.DIRECTION_DOWNWARDS;
            case DIRECTION_DOWNWARDS -> this.dir = Direction.DIRECTION_LEFT;
            case DIRECTION_LEFT -> this.dir = Direction.DIRECTION_UPWARDS;
            default -> throw new IllegalStateException("Invalid direction: " + this.dir);
        }
        // Reset movement direction
        resetDx();
        resetDy();
    }

    public Bullet shoot() {
        // Do not shoot if bullet has not been destroyed yet
        if (!this.isBulletDestroyed) return null;

        // Set bullet status to prevent multiple bullets
        this.isBulletDestroyed = false;

        // TODO: Initial position of the bullet should be where barrel of the tank locates.
        // Thus, add some offset for visual appearance.
        // This offset is defined in the textureFXs map.
        Dimension offset = this.textureFXs.get(dir).getOffsets();

        return new Bullet(this, getX() + (int) offset.getWidth(), getY() + (int) offset.getHeight(), getDir(), Globals.BULLET_SPEED_PER_TICK);
    }

    public void getDamage() {
        // After each damage decrement armor level by one.
        armorLevel = armorLevel>0 ? --armorLevel: 0;
    }

    public boolean isDestroyed() {
        // If armor level is zero, tank is destroyed
        return this.armorLevel <= 0;
    }

    public void setAsRed() {
        this.isRedTank = true;
    }

    public boolean isRedTank() {
        return this.isRedTank;
    }

    public void getPowerup(AbstractPowerup powerup) {

    }

    public void setBulletStatus(boolean isBulletDestroyed) {
        this.isBulletDestroyed = isBulletDestroyed;
    }

    public boolean checkCollision() {
        return false;
    }

    /**
     * Moves the tank towards the target location with consistent movement strategy:
     * - Continues current movement direction for minimum consistency count
     * - Primary strategy: Move downwards when possible
     * - Secondary: Move horizontally towards target when downward blocked
     * - Maintains direction until blocked to avoid jittery movement
     *
     * @param level The current game level containing eagle and player locations.
     */
    public void moveToTarget(GameLevel level) {
        GridLocation gLoc = Utils.Loc2GridLoc(new Location(x, y));
        GridLocation eagleLoc = level.getEagleLocation();

        int eagleDistance = level.getEagleDistance(gLoc);
        System.out.println("eagleDistance:" + eagleDistance);

        GridLocation targetLoc = eagleLoc;

        // Calculate target direction for horizontal movement
        int colDiff = targetLoc.colIndex() - gLoc.colIndex();
        Direction horizontalTargetDir = null;
        if (colDiff < 0) {
            horizontalTargetDir = Direction.DIRECTION_LEFT;
        } else if (colDiff > 0) {
            horizontalTargetDir = Direction.DIRECTION_RIGHT;
        }

        // Try to continue current movement strategy if we haven't reached minimum consistency
        if (movementConsistencyCounter < MIN_MOVEMENT_CONSISTENCY) {
            if (tryMoveInDirection(currentMovementStrategy, level)) {
                movementConsistencyCounter++;
                return;
            }
        }

        // Reset counter and try new strategy
        movementConsistencyCounter = 0;

        // Strategy 1: Try downward movement first
        if (tryMoveInDirection(Direction.DIRECTION_DOWNWARDS, level)) {
            currentMovementStrategy = Direction.DIRECTION_DOWNWARDS;
            movementConsistencyCounter = 1;
            return;
        }

        // Strategy 2: Try horizontal movement towards target
        if (horizontalTargetDir != null && tryMoveInDirection(horizontalTargetDir, level)) {
            currentMovementStrategy = horizontalTargetDir;
            movementConsistencyCounter = 1;
            return;
        }

        // Strategy 3: Try alternative horizontal directions
        Direction[] alternativeDirections = {Direction.DIRECTION_LEFT, Direction.DIRECTION_RIGHT};
        for (Direction altDir : alternativeDirections) {
            if (altDir == horizontalTargetDir) continue; // Skip already tried direction

            if (tryMoveInDirection(altDir, level)) {
                currentMovementStrategy = altDir;
                movementConsistencyCounter = 1;
                return;
            }
        }

        // Strategy 4: Try upward movement as last resort
        if (tryMoveInDirection(Direction.DIRECTION_UPWARDS, level)) {
            currentMovementStrategy = Direction.DIRECTION_UPWARDS;
            movementConsistencyCounter = 1;
            return;
        }

        // If all movements are blocked, rotate to change facing direction
        rotateRight();
        movementConsistencyCounter = 0;
    }

    /**
     * Helper method to try moving in a specific direction
     * @param direction The direction to try moving
     * @param level The game level for collision checking
     * @return true if movement was successful, false if blocked
     */
    private boolean tryMoveInDirection(Direction direction, GameLevel level) {
        int newX = x;
        int newY = y;

        switch (direction) {
            case DIRECTION_UPWARDS:
                newY -= speed;
                break;
            case DIRECTION_DOWNWARDS:
                newY += speed;
                break;
            case DIRECTION_LEFT:
                newX -= speed;
                break;
            case DIRECTION_RIGHT:
                newX += speed;
                break;
            default:
                return false;
        }

        RectangleBound newBound = new RectangleBound(
            newX - getSize().width/2,
            newY - getSize().height/2,
            getSize().width,
            getSize().height
        );

        if (level.checkMovable(newBound)) {
            setDir(direction);
            switch (direction) {
                case DIRECTION_UPWARDS:
                    decrementDy();
                    break;
                case DIRECTION_DOWNWARDS:
                    incrementDy();
                    break;
                case DIRECTION_LEFT:
                    decrementDx();
                    break;
                case DIRECTION_RIGHT:
                    incrementDx();
                    break;
            }
            return true;
        }
        return false;
    }

    /**
     * Moves the tank in the current direction.
     * This method should be implemented by subclasses to define specific movement behavior.
     *
     * @param level The current game level where the tank is located.
     */
    public abstract void move(GameLevel level);
}
