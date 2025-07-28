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
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import tank1990.core.*;
import tank1990.powerup.AbstractPowerup;
import tank1990.projectiles.Blast;
import tank1990.projectiles.Bullet;
import tank1990.projectiles.BulletType;

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

    private TimeTick redTankTick = null; // Tick for red tank blink animation
    private boolean isColorRed = false;

    // Do not move tanks after each update call, but after a certain time interval.
    private TimeTick movementTick;
    private TimeTick frozenTick; // Tick for blinking effect


    private transient HashMap<Direction, TextureFX> textureFXs = null;
    protected TankTextureStruct tankTextureFxStruct = null;

    protected boolean hasHelmet = false; // Flag to indicate if the tank has a helmet powerup
    protected boolean isFrozen = false;  // Flag to indicate if the tank is frozen and cannot move but can still shoot and rotate

    protected TankTier currentTier = TankTier.TIER_DEFAULT; // Default tank tier

    protected TankState tankState = TankState.UNDEFINED;
    protected Direction lastRandomDirection = Direction.DIRECTION_INVALID;

    protected enum TankState {
        UNDEFINED,
        SPAWNING,
        SEEKING_GOAL,
        MOVING_FORWARD,
        RANDOM_ROTATION,
        DIRECTIONAL_JITTER,
        BACKTRACK,
        PATROLLING
    }

    public AbstractTank(int x, int y) {
        this(x, y, Direction.DIRECTION_DOWNWARDS);
    }

    public AbstractTank(int x, int y, Direction dir) {
        setX(x);
        setY(y);
        setDir(dir);  // Default direction is downwards
        dx = 0;
        dy = 0;
        speedUnit = 0;
        maxSpeedUnit = 0;
        speed = 0;
        maxSpeed = 0;

        // From experimental results, updating tank movement in every 60 milliseconds is a good value.
        movementTick = new TimeTick(Utils.Time2GameTick(60));
        movementTick.setRepeats(-1);  // Repeat indefinitely

        frozenTick = new TimeTick(Utils.Time2GameTick(Globals.FROZEN_COOLDOWN_MS));
        frozenTick.setRepeats(-1);  // Repeat indefinitely

        redTankTick = new TimeTick(Utils.Time2GameTick(Globals.RED_TANK_BLINK_ANIMATION_PERIOD_MS));
        redTankTick.setRepeats(-1);  // Repeat indefinitely

        tankState = TankState.SPAWNING;
    }

    /**
     * Creates texture effects for the tank based on its texture structure.
     * This method initializes the textureFXs map with textures for each direction.
     */
    public void createTextureFXs() {
        if (this.tankTextureFxStruct==null) return;

        if (this.textureFXs==null) {
            this.textureFXs = new HashMap<>();
        } else {
            this.textureFXs.clear();
        }

        this.textureFXs.put(Direction.DIRECTION_UPWARDS, new TextureFX(this.tankTextureFxStruct.upwardsTexturePath));
        this.textureFXs.put(Direction.DIRECTION_RIGHT, new TextureFX(this.tankTextureFxStruct.rightTexturePath));
        this.textureFXs.put(Direction.DIRECTION_DOWNWARDS, new TextureFX(this.tankTextureFxStruct.downwardsTexturePath));
        this.textureFXs.put(Direction.DIRECTION_LEFT, new TextureFX(this.tankTextureFxStruct.leftTexturePath));
    }

    @Override
    public void draw(Graphics g) {
        // Update the tank texture if it is red tank
        if (isRedTank) {
            redTankTick.updateTick();

            if (redTankTick.isTimeOut()) {
                isColorRed = !isColorRed;  // Toggle the color state

                if (isColorRed) {
                    setRedTankTextureFXs();
                } else {
                    setDefaultTankTextureFXs();
                }

                redTankTick.reset();
            }
        }

        // Draw tank animations
        Dimension tankSize = Utils.normalizeDimension(g, Globals.TANK_WIDTH, Globals.TANK_HEIGHT);

        // Set the tank size for collision detection - make it smaller to allow movement
        // Tank should be about 80% of cell size to allow for movement between tiles
        this.textureFXs.get(dir).setTargetSize(tankSize.width, tankSize.height);
        this.textureFXs.get(dir).draw(g, getX(), getY(), 0.0);

        this.speed = Utils.normalize(g, this.speedUnit);
        this.maxSpeed = Utils.normalize(g, this.maxSpeedUnit);

        if (this.hasHelmet) {
            g.setColor(Color.YELLOW);
            g.drawRoundRect((int) getBoundingBox().getX(), (int) getBoundingBox().getY(), (int) getBoundingBox().getWidth(), (int) getBoundingBox().getHeight(), 5, 5);
        }

        if (Globals.SHOW_BOUNDING_BOX) {
            g.setColor(Color.CYAN);
            g.drawRect((int) getBoundingBox().getX(), (int) getBoundingBox().getY(), (int) getBoundingBox().getWidth(), (int) getBoundingBox().getHeight());
        }
    }

    /**
     * Updates the tank's position and direction based on its movement speed and direction.
     * This method also handles the frozen state of the tank, preventing movement if it is frozen.
     *
     * @param level The current game level where the tank is located.
     */
    public void update(GameLevel level) {
        // If tank is frozen, do not update its position or direction.
        if (this.isFrozen) {
            this.frozenTick.updateTick();
            if (this.frozenTick.isTimeOut()) {
                this.isFrozen = false;  // Unfreeze the tank after cooldown
                this.frozenTick.reset();
            } else {
                return;
            }
        }

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
        RectangleBound newTankBound = new RectangleBound(newX - halfWidth, newY - halfHeight, tankWidth, tankHeight);

        // Check map constraints by checking neighbor tiles of the player tank
        boolean isMovable = level.checkMovable(this, newTankBound);
        if (isMovable) {
            // Update tank position and direction
            setX(newX);
            setY(newY);
        } else {}

        setDir(dir);
    }

    /**
     * Getters and Setters for tank properties
     */
    public int getPoints() {return this.points;}

    /**
     * Returns the current armor level of the tank.
     * Armor level determines how many hits the tank can take before being destroyed.
     *
     * @return The current armor level of the tank.
     */
    public int getArmorLevel() {return this.armorLevel;}

    /**
     * Returns the current speed unit of the tank.
     * Speed unit is used to determine the movement speed of the tank.
     *
     * @return The current speed unit of the tank.
     */
    public int getSpeedUnit() {return this.speedUnit;}

    /**
     * Returns the maximum speed unit of the tank.
     * Maximum speed unit is used to limit the tank's movement speed.
     *
     * @return The maximum speed unit of the tank.
     */
    public int getMaxSpeedUnit() {return this.maxSpeedUnit;}

    /**
     * Returns tank has been frozen.
     * Tanks can be frozen by freeze powerups.
     *
     * @return true if the tank is frozen, false otherwise.
     */
    public boolean isFrozen() {return this.isFrozen;}

    /**
     * Sets point of the tank.
     *
     * @param points The points to set for the tank.
     */
    public void setPoints(int points) {this.points = points;}

    /**
     * Sets the armor level of the tank.
     * Armor level determines how many hits the tank can take before being destroyed.
     *
     * @param armorLevel The armor level to set for the tank.
     */
    public void setArmorLevel(int armorLevel) {this.armorLevel = armorLevel;}

    /**
     * Sets the speed unit of the tank.
     * Speed unit is used to determine the movement speed of the tank.
     *
     * @param speedUnit The speed unit to set for the tank.
     */
    public void setSpeedUnit(int speedUnit) {this.speedUnit = speedUnit;}

    /**
     * Sets the maximum speed unit of the tank.
     * This method is used to limit the tank's movement speed.
     *
     * @param maxSpeedUnit The maximum speed unit to set for the tank.
     */
    public void setMaxSpeedUnit(int maxSpeedUnit) {this.maxSpeedUnit = maxSpeedUnit;}

    /**
     * Freezes the tank.
     * Frozen tank cannot move.
     * @param isFrozen true if the tank should be frozen, false otherwise.
     */
    public void setFrozen(boolean isFrozen) {this.isFrozen = isFrozen;}

    /**
     * Decrements delta speed of tank in x direction.
     * They also reset the other movement direction to ensure only one direction is active at a time.
     */
    public void decrementDx() { resetDy(); this.dx = Math.max(this.dx - this.speed, -this.maxSpeed); this.dir = Direction.DIRECTION_LEFT;}

    /**
     * Increments delta speed of tank in x direction.
     * They also reset the other movement direction to ensure only one direction is active at a time.
     */
    public void incrementDx() { resetDy(); this.dx = Math.min(this.dx + this.speed, this.maxSpeed); this.dir = Direction.DIRECTION_RIGHT;}

    /**
     * Decrements delta speed of tank in y direction.
     * They also reset the other movement direction to ensure only one direction is active at a time.
     */
    public void decrementDy() { resetDx(); this.dy = Math.max(this.dy - this.speed, -this.maxSpeed); this.dir = Direction.DIRECTION_UPWARDS; }

    /**
     * Increments delta speed of tank in y direction.
     * They also reset the other movement direction to ensure only one direction is active at a time.
     */
    public void incrementDy() { resetDx(); this.dy = Math.min(this.dy + this.speed, this.maxSpeed); this.dir = Direction.DIRECTION_DOWNWARDS;}

    /**
     * Resets delta speed of the tank in x direction.
     */
    public void resetDx() { this.dx=0; }

    /**
     * Resets delta speed of the tank in y direction.
     */
    public void resetDy() { this.dy=0; }

    /**
     * Rotates the tank in clockwise.
     * This method also resets the movement speed.
     */
    public void rotateClockwise() {
        setDir(getDir().rotateCW());

        // Reset movement direction
        resetDx();
        resetDy();
    }

    /**
     * Shoots the bullet by returning bullet object.
     * @return Bullet object if shoot is successful, null otherwise.
     */
    public Bullet shoot() {
        // Frozen enemy tanks cannot shoot as well.
        if (this instanceof Enemy && this.isFrozen) {
            return null;
        }

        // Do not shoot if bullet has not been destroyed yet
        if (!this.isBulletDestroyed) return null;

        // Set bullet status to prevent multiple bullets
        this.isBulletDestroyed = false;

        // TODO: Initial position of the bullet should be where barrel of the tank locates.
        // Thus, add some offset for visual appearance.
        // This offset is defined in the textureFXs map.
        Dimension offset = this.textureFXs.get(dir).getOffsets();

        Bullet bullet = new Bullet(this, getX() + (int) offset.getWidth(), getY() + (int) offset.getHeight(), getDir(), Globals.BULLET_SPEED_PER_TICK);

        // Configure bullet properties based on tank tier
        if (currentTier == TankTier.TIER_2) {
            bullet.setMoveSpeed(Globals.BULLET_SPEED_PER_TICK * 2); // Double the speed for tier 2
        }
        if (currentTier == TankTier.TIER_3) {
            // TODO No implementation for tier 3, but can be added later
        }
        if (currentTier == TankTier.TIER_4) {
            bullet.setType(BulletType.UPGRADED);
        }

        return  bullet;
    }

    /**
     * Handles damage to the tank.
     * If the tank has a helmet, it will not take damage and the helmet will be consumed.
     * Otherwise, it decrements the armor level by one.
     *
     * @return true if the tank is damaged, false if it was protected by a helmet.
     */
    public boolean getDamage() {
        // If tank has helmet, it will not take damage
        if (this.hasHelmet) {
            this.hasHelmet = false;  // Helmet is consumed after one hit
            return false;
        }

        // After each damage decrement armor level by one.
        armorLevel = armorLevel>0 ? --armorLevel: 0;
        return true;  // Tank is damaged
    }

    /**
     * Destroys the tank and returns a blast object.
     * This method sets the armor level to -1 to indicate destruction and returns a new Blast object.
     *
     * @return A new Blast object representing the explosion of the destroyed tank.
     */
    public Blast destroy() {
        armorLevel = -1;
        return new Blast(getX(), getY());
    }

    /**
     * Checks if the tank is destroyed.
     * A tank is considered destroyed if its armor level is zero or less.
     *
     * @return true if the tank is destroyed, false otherwise.
     */
    public boolean isDestroyed() {
        // If armor level is zero, tank is destroyed
        return this.armorLevel <= 0;
    }

    /**
     * Sets the tank as a red tank.
     * Red tanks are special tanks that can cause to spawn a new powerup in the game upon getting hit.
     */
    public void setAsRed() {
        this.isRedTank = true;
    }

    /**
     * Returns whether the tank is a red tank.
     * Red tanks are special tanks that can cause to spawn a new powerup in the game upon getting hit.
     *
     * @return true if the tank is a red tank, false otherwise.
     */
    public boolean isRedTank() {
        return this.isRedTank;
    }

    /**
     * Sets the texture effects for a default tank.
     * This method should be implemented by subclasses to define specific default tank textures.
     */
    protected abstract void setDefaultTankTextureFXs();

    /**
     * Sets the texture effects for a red tank.
     * This method should be implemented by subclasses to define specific red tank textures.
     */
    protected abstract void setRedTankTextureFXs();

    /**
     * Collects a powerup and applies its effects to the tank.
     * This method handles different types of powerups and updates the tank's state accordingly.
     *
     * @param powerup The powerup to be collected.
     */
    public void collectPowerup(AbstractPowerup powerup) {
        if (powerup == null) return;

        // Collect the powerup and apply its effects
        switch (powerup.getPowerupType()) {
            case POWERUP_GRENADE -> {
                // No specific action for timer powerup in tanks
            }
            case POWERUP_HELMET -> {
                this.hasHelmet = true;
            }
            case POWERUP_SHOVEL -> {
                // No specific action for timer powerup in tanks
            }
            case POWERUP_STAR -> {
                // No specific action for timer powerup in tanks
            }
            case POWERUP_TANK -> {
                // No specific action for timer powerup in tanks
            }
            case POWERUP_TIMER -> {
                // No specific action for timer powerup in tanks
            }
            default -> System.err.println("Unknown powerup type: " + powerup.getPowerupType());
        }
    }

    /**
     * Returns the current bullet status.
     * This indicates whether the bullet fired from the tank is destroyed or not.
     *
     * @param isBulletDestroyed true if the bullet is destroyed, false otherwise.
     */
    public void setBulletStatus(boolean isBulletDestroyed) {
        this.isBulletDestroyed = isBulletDestroyed;
    }

    /**
     * Returns the new location of the tank if it moves forward in the current direction.
     * Notice that this function does not update tank's position only gives hint about the new position.
     * It should be called after setting the direction of the tank.
     */
    public Location moveForwardHint() {
        int newX = getX();
        int newY = getY();

        switch (getDir()) {
            case DIRECTION_UPWARDS -> newY = newY - this.speed;
            case DIRECTION_RIGHT -> newX = newX + this.speed;
            case DIRECTION_DOWNWARDS -> newY = newY + this.speed;
            case DIRECTION_LEFT -> newX = newX - this.speed;
            default -> throw new IllegalStateException("Invalid direction: " + getDir());
        }

        return new Location(newX, newY);
    }

    /**
     * Moves the tank forward in the current direction.
     * This method updates the tank's position based on its current direction and speed.
     * It should be called after setting the direction of the tank.
     */
    public void moveForward() {
        switch (getDir()) {
            case DIRECTION_UPWARDS -> decrementDy();
            case DIRECTION_RIGHT -> incrementDx();
            case DIRECTION_DOWNWARDS -> incrementDy();
            case DIRECTION_LEFT -> decrementDx();
            default -> throw new IllegalStateException("Invalid direction: " + getDir());
        }
    }

    /**
         * Moves the tank in the current direction.
         * This method should be implemented by subclasses to define specific movement behavior.
         *
         * @param level The current game level where the tank is located.
         */
    public void move(GameLevel level) {
        //if (currentTimeMillis - stateTimer < 150) return;

        System.out.printf("%d - (%d) %s\n", System.currentTimeMillis(), System.identityHashCode(this), tankState);

        switch (this.tankState) {
            case SPAWNING -> {
                setDir(Direction.DIRECTION_DOWNWARDS);
                if (level.seeTarget(getBoundingBox(), getDir())) {
                    tankState = TankState.SEEKING_GOAL;
                } else {
                    tankState = TankState.MOVING_FORWARD;
                }
            }
            case SEEKING_GOAL -> {
                Direction goalDir = level.getTargetDirection(getBoundingBox());
                if (goalDir != null || goalDir != Direction.DIRECTION_INVALID) {
                    setDir(goalDir);
                } else {
                    tankState = TankState.MOVING_FORWARD;
                }
            }
            case MOVING_FORWARD -> {
                Location newLocation = moveForwardHint();
                Location oldLocation = new Location(x, y);


                GridLocation oldGloc = Utils.Loc2GridLoc(new Location(x, y));
                GridLocation newGloc = Utils.Loc2GridLoc(newLocation);

                System.out.println("Old location: " + oldLocation  + " New location: " + newLocation);

                if (level.checkMovable(this, new RectangleBound(newLocation.x(), newLocation.y(), getSize()))) {
                    System.out.println("Movable for new location: " + Utils.Loc2GridLoc(newLocation));
                    moveForward();
                } else if (level.seeTarget(getBoundingBox(), getDir())) {
                    System.out.println("Target seen, random rotation");
                    tankState = TankState.RANDOM_ROTATION;
                    moveForward();
                } else {
                    System.out.println("Cannot move forward, random rotation");
                    tankState = TankState.RANDOM_ROTATION;
                    moveForward();
                }

                if (Utils.getRandomProbability(5)) {
                    tankState = TankState.DIRECTIONAL_JITTER;
                }
            }
            case DIRECTIONAL_JITTER -> {
                setDir(Utils.getRandomProbability(50) ? Direction.DIRECTION_UPWARDS : Direction.DIRECTION_DOWNWARDS);
                tankState = TankState.MOVING_FORWARD;
            }
            case RANDOM_ROTATION -> {
                lastRandomDirection = Utils.getRandomProbability(50) ? getDir().rotateCW() : getDir().rotateCCW();
                setDir(lastRandomDirection);
                tankState = TankState.MOVING_FORWARD;
            }
            case BACKTRACK -> {
                setDir(getDir().opposite());
                tankState = TankState.MOVING_FORWARD;
            }
            case PATROLLING -> {
                setDir(getWeightedRandomDirection());
                tankState = TankState.MOVING_FORWARD;
            }
        }
    }

    private Direction getWeightedRandomDirection() {
        Random random = new Random();
        double r = random.nextDouble();
        if (r < 0.4) return Direction.DIRECTION_LEFT;
        else if (r < 0.7) return Direction.DIRECTION_RIGHT;
        else return Direction.DIRECTION_UPWARDS;
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        createTextureFXs();
    }
}
