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


    private transient Map<Direction, TextureFX> textureFXs = null;
    protected TankTextureStruct tankTextureFxStruct = null;

    protected boolean hasHelmet = false; // Flag to indicate if the tank has a helmet powerup
    protected boolean isFrozen = false;  // Flag to indicate if the tank is frozen and cannot move but can still shoot and rotate

    protected TankTier currentTier = TankTier.TIER_DEFAULT; // Default tank tier

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

        frozenTick = new TimeTick(Utils.Time2GameTick(Globals.FROZEN_COOLDOWN_MS));
        frozenTick.setRepeats(-1);  // Repeat indefinitely

        redTankTick = new TimeTick(Utils.Time2GameTick(Globals.RED_TANK_BLINK_ANIMATION_PERIOD_MS));
        redTankTick.setRepeats(-1);  // Repeat indefinitely
    }

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
        boolean isMovable = level.checkMovable(newTankBound);
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

    public int getArmorLevel() {return this.armorLevel;}

    public int getSpeedUnit() {return this.speedUnit;}

    public int getMaxSpeedUnit() {return this.maxSpeedUnit;}

    public boolean isFrozen() {return this.isFrozen;}

    public void setPoints(int points) {this.points = points;}

    public void setArmorLevel(int armorLevel) {this.armorLevel = armorLevel;}

    public void setSpeedUnit(int speedUnit) {this.speedUnit = speedUnit;}

    public void setMaxSpeedUnit(int maxSpeedUnit) {this.maxSpeedUnit = maxSpeedUnit;}

    public void setFrozen(boolean isFrozen) {this.isFrozen = isFrozen;}

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

    public Blast destroy() {
        armorLevel = -1;
        return new Blast(getX(), getY());
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

    protected abstract void setDefaultTankTextureFXs();

    protected abstract void setRedTankTextureFXs();

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

    public void setBulletStatus(boolean isBulletDestroyed) {
        this.isBulletDestroyed = isBulletDestroyed;
    }

    /**
     * Moves the tank towards the target location based on the eagle and player distances.
     * If both are reachable, it moves towards the closer one.
     * If one is unreachable, it moves towards the reachable one.
     * If both are unreachable, it does not move.
     * If direct movement is blocked, tries lateral movement to find alternative path.
     *
     * @param level The current game level containing eagle and player locations.
     */
    public void moveToTarget(GameLevel level) {
        GridLocation gLoc = Utils.Loc2GridLoc(new Location(x, y));

        GridLocation eagleLoc = level.getEagleLocation();
        //GridLocation playerLoc = level.getPlayerLocation();

        int eagleDistance = level.getEagleDistance(gLoc);
        //int playerDistance = level.getPlayerDistance(gLoc);
        //System.out.println("eagleDistance:" + eagleDistance /*+ " playerDistance:" + playerDistance*/);

        //GridLocation targetLoc;
        //if (eagleDistance == -1 && playerDistance == -1) {
        //    System.err.println("Eagle and Player distance are null for grid location: " + gLoc);
        //    return;
        //} else if (eagleDistance == -1) {
        //    targetLoc = playerLoc; // If eagle is not reachable, target player
        //} else if (playerDistance == -1) {
        //    targetLoc = eagleLoc; // If player is not reachable, target eagle
        //} else {
        //    targetLoc = (eagleDistance <= playerDistance) ? eagleLoc : playerLoc;
        //}

        GridLocation targetLoc = eagleLoc;

        // Determine the direction to move towards the target
        Direction targetDir = null;

        // Calculate differences to determine priority direction
        int colDiff = targetLoc.colIndex() - gLoc.colIndex();
        int rowDiff = targetLoc.rowIndex() - gLoc.rowIndex();

        // Choose the direction based on the largest difference (Manhattan distance approach)
        if (Math.abs(colDiff) >= Math.abs(rowDiff)) {
            // Horizontal movement has priority
            if (colDiff < 0) {
                targetDir = Direction.DIRECTION_LEFT;
            } else if (colDiff > 0) {
                targetDir = Direction.DIRECTION_RIGHT;
            }
        } else {
            // Vertical movement has priority
            if (rowDiff < 0) {
                targetDir = Direction.DIRECTION_UPWARDS;
            } else if (rowDiff > 0) {
                targetDir = Direction.DIRECTION_DOWNWARDS;
            }
        }

        // If we're exactly at the target location, don't move
        if (targetDir == null) {
            return;
        }

        // Calculate the intended new position based on the target direction
        int newX = x;
        int newY = y;

        switch (targetDir) {
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
                break;
        }

        // Check if the intended new position is movable
        RectangleBound newTankBound = new RectangleBound(newX - getSize().width/2, newY - getSize().height/2, getSize().width, getSize().height);
        boolean isMovable = level.checkMovable(newTankBound);

        if (isMovable) {
            // Set the direction and move in the target direction
            setDir(targetDir);
            switch (targetDir) {
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
                default:
                    break;
            }
        } else {
            // If direct movement is blocked, try lateral movement
            Direction[] lateralDirections = getLateralDirections(targetDir);

            boolean movedLaterally = false;
            for (Direction lateralDir : lateralDirections) {
                int lateralX = x;
                int lateralY = y;

                switch (lateralDir) {
                    case DIRECTION_UPWARDS:
                        lateralY -= speed;
                        break;
                    case DIRECTION_DOWNWARDS:
                        lateralY += speed;
                        break;
                    case DIRECTION_LEFT:
                        lateralX -= speed;
                        break;
                    case DIRECTION_RIGHT:
                        lateralX += speed;
                        break;
                    default:
                        continue;
                }

                RectangleBound newTankBound_2 = new RectangleBound(newX - getSize().width/2, newY - getSize().height/2, getSize().width, getSize().height);
                if (level.checkMovable(newTankBound_2)) {
                    // Move laterally
                    setDir(lateralDir);
                    switch (lateralDir) {
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
                        default:
                            break;
                    }
                    movedLaterally = true;
                    break; // Exit loop after successful lateral movement
                }
            }

            // If no lateral movement was possible, rotate as last resort
            if (!movedLaterally) {
                rotateRight();
            }
        }
    }

    /**
     * Returns the lateral directions (perpendicular to the target direction) for obstacle avoidance.
     * This helps the tank move sideways when the direct path is blocked.
     *
     * @param targetDir The direction towards the target
     * @return Array of lateral directions to try
     */
    private Direction[] getLateralDirections(Direction targetDir) {
        switch (targetDir) {
            case DIRECTION_UPWARDS:
            case DIRECTION_DOWNWARDS:
                // For vertical movement, try horizontal directions
                return new Direction[]{Direction.DIRECTION_LEFT, Direction.DIRECTION_RIGHT};
            case DIRECTION_LEFT:
            case DIRECTION_RIGHT:
                // For horizontal movement, try vertical directions
                return new Direction[]{Direction.DIRECTION_UPWARDS, Direction.DIRECTION_DOWNWARDS};
            default:
                return new Direction[]{Direction.DIRECTION_LEFT, Direction.DIRECTION_RIGHT};
        }
    }

    /**
     * Moves the tank to a random direction.
     * The tank will randomly choose one of the four directions (up, right, down, left)
     * and move in that direction by updating its dx or dy accordingly.
     */
    public void moveToRandom() {
        Random random = new Random();
        int randomDirection = random.nextInt(0, 4);

        switch (randomDirection) {
            case 0 -> setDir(Direction.DIRECTION_UPWARDS);
            case 1 -> setDir(Direction.DIRECTION_RIGHT);
            case 2 -> setDir(Direction.DIRECTION_DOWNWARDS);
            case 3 -> setDir(Direction.DIRECTION_LEFT);
        }

        switch (getDir()) {
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
            default:
                break;
        }
    }

    /**
     * Moves the tank in the current direction.
     * This method should be implemented by subclasses to define specific movement behavior.
     *
     * @param level The current game level where the tank is located.
     */
    public abstract void move(GameLevel level);
}
