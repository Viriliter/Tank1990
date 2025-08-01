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
import java.util.*;

import tank1990.core.*;
import tank1990.powerup.AbstractPowerup;
import tank1990.projectiles.Blast;
import tank1990.projectiles.Bullet;
import tank1990.projectiles.BulletType;
import tank1990.tile.Tile;
import tank1990.tile.TileType;

/**
 * @class AbstractTank
 * @brief Represents a tank in the game.
 * @details This class is an abstract base class for all tank types, providing common properties and methods.
 * It handles movement, shooting, and interactions with powerups.
 */
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

    protected TimeTick spawnTick;  // Tick for spawn animation
    protected TimeTick spawnBlinkTick;  // Tick for spawn blink animation
    private boolean isSpawnBlinkedOut = false;
    protected TimeTick movementTick;  // Tick for tank movement updates to avoid too frequent updates
    private TimeTick shootTick;  // Tick for shooting bullets
    private TimeTick frozenTick;  // Tick for blinking effect
    private TimeTick helmetTick;  // Tick for helmet powerup effect

    private transient HashMap<Direction, TextureFX> textureFXs = null;
    protected TankTextureStruct tankTextureFxStruct = null;

    protected boolean spawnProtectionEnabled = true; // Flag to indicate if spawn protection is enabled

    protected boolean hasHelmet = false; // Flag to indicate if the tank has a helmet powerup
    protected boolean isFrozen = false;  // Flag to indicate if the tank is frozen and cannot move but can still shoot and rotate

    protected TankTier currentTier = TankTier.TIER_DEFAULT; // Default tank tier

    protected TankState tankState = TankState.UNDEFINED;
    protected Direction lastRandomDirection = Direction.DIRECTION_INVALID;

    private int lastEagleDistance = Integer.MAX_VALUE;

    private transient Thread movementThread;
    private volatile boolean moving = false;

    protected BulletType bulletType = BulletType.NORMAL; // Default bullet type

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

        spawnTick = new TimeTick(Utils.Time2GameTick(Globals.SPAWN_PROTECTION_COOLDOWN_MS), ()-> {
            spawnProtectionEnabled = false;  // Disable spawn protection after the cooldown
        });
        spawnTick.setRepeats(0);  // Repeat only once for spawning animation

        spawnBlinkTick = new TimeTick(Utils.Time2GameTick(Globals.SPAWN_PROTECTION_BLINK_PERIOD_MS));
        spawnBlinkTick.setRepeats(-1);  // Repeat only once for spawning animation

        // From experimental results, updating tank movement in every 100 milliseconds is a good value for at least enemy tanks.
        movementTick = new TimeTick(Utils.Time2GameTick(100));
        movementTick.setRepeats(-1);  // Repeat indefinitely

        frozenTick = new TimeTick(Utils.Time2GameTick(Globals.FROZEN_COOLDOWN_MS));
        frozenTick.setRepeats(-1);  // Repeat indefinitely

        redTankTick = new TimeTick(Utils.Time2GameTick(Globals.RED_TANK_BLINK_ANIMATION_PERIOD_MS));
        redTankTick.setRepeats(-1);  // Repeat indefinitely

        helmetTick = new TimeTick(Utils.Time2GameTick(Globals.HELMET_COOLDOWN_MS));
        helmetTick.setRepeats(-1);  // Repeat indefinitely

        shootTick = new TimeTick(Utils.Time2GameTick(Globals.DEFAULT_SHOOT_PERIOD_MS));
        shootTick.setRepeats(-1);  // Repeat indefinitely

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
        if (this.spawnProtectionEnabled) {
            if (spawnBlinkTick.isTimeOut()) {
                spawnBlinkTick.reset();
                isSpawnBlinkedOut = !isSpawnBlinkedOut;
            }
            if (isSpawnBlinkedOut) return;  // Do not draw the tank if it is blinked out
        }

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
        if (this.spawnProtectionEnabled) {
            spawnTick.updateTick();
            spawnBlinkTick.updateTick();
        }

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

        if (this.hasHelmet) {
            this.helmetTick.updateTick();
            if (this.helmetTick.isTimeOut()) {
                this.hasHelmet = false;  // Consume the helmet after cooldown
                this.helmetTick.reset();
            }
        }

        movementTick.updateTick();

        // Do not update tank position if movement tick is not timed out.
        if (!movementTick.isTimeOut()) return;
        movementTick.reset();

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

        // Calculate movement based on the current direction and speed
        move(level);
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
     * Sets the shooting period for the tank.
     * @param shootPeriod The period in milliseconds between each shot.
     */
    public void setShootPeriod(int shootPeriod) {
        this.shootTick.setDefaultTick(Utils.Time2GameTick(shootPeriod));
    }

    /**
     * Freezes the tank.
     * Frozen tank cannot move.
     * @param isFrozen true if the tank should be frozen, false otherwise.
     */
    public void setFrozen(boolean isFrozen) {this.isFrozen = isFrozen;}

    /**
     * Returns the delta speed of the tank in x direction.
     * @return dx
     */
    public int getDx() { return this.dx; }

    /**
     * Returns the delta speed of the tank in y direction.
     * @return dy
     */
    public int getDy() { return this.dy; }

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
        // If tank is enemy regulate shooting rate with shoot tick.
        if (this instanceof Enemy) {
            this.shootTick.updateTick();

            if (this.shootTick.isTimeOut()) {
                this.shootTick.reset();  // Reset the shoot tick for next shot
            }
            else {
                return null;  // Do not shoot if shoot tick is not timed out
            }
        }

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
        bullet.setType(this.bulletType);  // Set the bullet type

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

    public boolean isSpawnProtectionEnabled() {
        return this.spawnProtectionEnabled;
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
        resetTier();  // Reset the tank tier to default upon destruction
        return true;  // Tank is damaged
    }

    /**
     * Destroys the tank and returns a blast object.
     * This method sets the armor level to -1 to indicate destruction and returns a new Blast object.
     *
     * @return A new Blast object representing the explosion of the destroyed tank.
     */
    public Blast destroy() {
        this.armorLevel = -1;
        resetTier();  // Reset the tank tier to default upon destruction
        this.bulletType = BulletType.NORMAL;  // Reset bullet type to default
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
     * Increments the tank tier.
     * This method is used to upgrade the tank's tier, for example, when it collects a powerup.
     * It will not exceed the maximum tier defined in TankTier enum.
     */
    private void incrementTier() {
        switch (this.currentTier) {
            case TIER_DEFAULT -> this.currentTier = TankTier.TIER_2;
            case TIER_2 -> this.currentTier = TankTier.TIER_3;
            case TIER_3 -> this.currentTier = TankTier.TIER_4;
            case TIER_4 -> this.currentTier = TankTier.TIER_5;
            default -> this.currentTier = TankTier.TIER_5;  // Do not exceed maximum tier
        }
    }

    /**
     * Resets the tank tier to the default tier.
     * This method is used to reset the tank's tier, for example, when the tank is destroyed.
     */
    private void resetTier() {
        this.currentTier = TankTier.TIER_DEFAULT;
    }

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
                if (!(this instanceof Enemy)) incrementTier();  // Only players can upgrade their tank tier
            }
            case POWERUP_TANK -> {
                // No specific action for timer powerup in tanks
            }
            case POWERUP_TIMER -> {
                // No specific action for timer powerup in tanks
            }
            case POWERUP_WEAPON -> {
                bulletType = BulletType.UPGRADED;
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
    public RectangleBound moveForwardHint() {
        int newX = getX();
        int newY = getY();


        switch (getDir()) {
            case DIRECTION_UPWARDS -> newY = newY - this.speed;
            case DIRECTION_RIGHT -> newX = newX + this.speed;
            case DIRECTION_DOWNWARDS -> newY = newY + this.speed;
            case DIRECTION_LEFT -> newX = newX - this.speed;
            default -> throw new IllegalStateException("Invalid direction: " + getDir());
        }
        return new RectangleBound(newX - getSize().width/2, newY - getSize().height/2, getSize());
    }

    /**
     * Moves the tank forward in the current direction.
     * This method updates the tank's position based on its current direction and speed.
     * It should be called after setting the direction of the tank.
     */
    public void moveForward() {
        switch (getDir()) {
            case DIRECTION_UPWARDS -> setY(getY() - this.speed);
            case DIRECTION_RIGHT -> setX(getX() + this.speed);
            case DIRECTION_DOWNWARDS -> setY(getY() + this.speed);
            case DIRECTION_LEFT -> setX(getX() - this.speed);
            default -> throw new IllegalStateException("Invalid direction: " + getDir());
        }
    }

    /**
     * Returns a weighted random direction for the tank to move.
     * This method is used to add some randomness to the tank's movement behavior.
     * The probabilities are set such that left and right movements are more likely than upwards.
     *
     * @return A Direction object representing the chosen direction.
     */
    private Direction getWeightedRandomDirection() {
        Random random = new Random();
        double r = random.nextDouble();
        if (r < 0.4) return Direction.DIRECTION_LEFT;
        else if (r < 0.7) return Direction.DIRECTION_RIGHT;
        else return Direction.DIRECTION_UPWARDS;
    }

    /**
     * Represents a cell in the grid with its row, column, distance from the start,
     * and the first move coordinates that led to this cell.
     * This class is used for pathfinding algorithms like Dijkstra's.
     */
    private static class Cell implements Comparable<Cell> {
        int r, c, dist;
        int firstMoveRow, firstMoveCol;

        Cell(int r, int c, int dist, int firstMoveRow, int firstMoveCol) {
            this.r = r;
            this.c = c;
            this.dist = dist;
            this.firstMoveRow = firstMoveRow;
            this.firstMoveCol = firstMoveCol;
        }

        public int compareTo(Cell other) {
            return Integer.compare(this.dist, other.dist);
        }
    }

    /**
     * Finds the best move for the tank to reach the eagle location applying Dijkstra path finding algorithm.
     * This method uses a priority queue to find the shortest path to the eagle location.
     * It returns the first move that leads towards the eagle.
     *
     * @param level The current game level where the tank is located.
     * @param start The starting location of the tank.
     * @return The best move as a GridLocation, or null if no valid move is found.
     */
    private GridLocation findBestMove(GameLevel level, GridLocation start) {
        int[][] costMap = new int[Globals.ROW_TILE_COUNT][Globals.COL_TILE_COUNT];
        Tile[][] map = level.getMap();

        int eagleRow = level.getEagleLocation().rowIndex();
        int eagleCol = level.getEagleLocation().colIndex();

        for (int[] row : costMap) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        costMap[start.rowIndex()][start.colIndex()] = 0;

        PriorityQueue<Cell> pq = new PriorityQueue<>();
        pq.offer(new Cell(start.rowIndex(), start.colIndex(), 0, start.rowIndex(), start.colIndex()));

        while (!pq.isEmpty()) {
            Cell current = pq.poll();

            if (current.c == eagleCol && current.r == eagleRow) {
                if (current.r == start.rowIndex() && current.c == start.colIndex()) {
                    return null;
                }
                return new GridLocation(current.firstMoveRow, current.firstMoveCol);
            }

            for (Direction dir: Direction.values()) {
                if (dir == Direction.DIRECTION_INVALID) continue;

                int dRow =0;
                int dCol =0;
                switch (dir) {
                    case DIRECTION_UPWARDS -> dRow=-1;
                    case DIRECTION_RIGHT -> dCol=1;
                    case DIRECTION_LEFT -> dCol=-1;
                    case DIRECTION_DOWNWARDS -> dRow=1;
                    default -> throw new IllegalStateException("Unexpected value: " + dir);
                }

                int nRow = current.r + dRow;
                int nCol = current.c + dCol;

                if (nRow < 0 || nRow >= Globals.ROW_TILE_COUNT ||
                    nCol < 0 || nCol >= Globals.COL_TILE_COUNT) continue;

                Tile t = map[nRow][nCol];

                if (t != null && !TileType.isPassable(t.getType())) continue;

                int tileCost = (t == null) ? 1 : TileType.getCost(t.getType());

                // Skip if tile is occupied by another tank or object
                if (level.isTileOccupied(new GridLocation(nRow, nCol), start)) continue;

                // Calculate new cumulative cost to reach this neighbor
                int newCost = current.dist + tileCost;

                if (newCost < costMap[nRow][nCol]) {
                    costMap[nRow][nCol] = newCost;

                    int firstMoveRow = (current.r == start.rowIndex() && current.c == start.colIndex()) ? nRow : current.firstMoveRow;
                    int firstMoveCol = (current.r == start.rowIndex() && current.c == start.colIndex()) ? nCol : current.firstMoveCol;

                    // Add this neighbor to the priority queue for further exploration
                    pq.offer(new Cell(nRow, nCol, newCost, firstMoveRow, firstMoveCol));
                }
            }
        }

        // No path found to eagle location
        return null;
    }

    /**
     * Moves the tank in a random direction.
     * This method is used when the tank cannot find a valid move in the current direction.
     * It recursively tries to move in a random direction for a specified depth.
     *
     * @param level The current game level where the tank is located.
     * @param depth The depth of recursion for random movement.
     */
    private void randomMove(GameLevel level, int depth) {
        if (depth < 0) return;

        // If the next tile is not movable, try to find a random direction
        Direction randomDir = getWeightedRandomDirection();
        setDir(randomDir);
        RectangleBound nextTileHint = moveForwardHint();

        if (level.checkMovable(this, nextTileHint)) {
            moveForward();
            randomMove(level, --depth);  // Continue moving in the random direction
        }
    }

    /**
     * Moves the tank in the current direction.
     * This method should be implemented by subclasses to define specific movement behavior.
     *
     * @param level The current game level where the tank is located.
     */
    public synchronized void move(GameLevel level) {
        GridLocation startLoc = Utils.loc2GridLoc(new Location(getX(), getY()));

        Location currentLoc = new Location(getX(), getY());
        GridLocation currentGridLoc = Utils.loc2GridLoc(currentLoc);
        Location gridCenterLoc = Utils.gridLoc2Loc(currentGridLoc);

        GridLocation nextTileLoc = null;

        // Try to calculate best move only if the tank is at the center of a grid cell.
        if (currentLoc.x()==gridCenterLoc.x() && currentLoc.y()==gridCenterLoc.y()) {
            nextTileLoc = findBestMove(level, startLoc);
        } else {
            nextTileLoc = currentGridLoc;  // If not at the center, stay in the grid center
        }

        if (nextTileLoc != null) {
            // Update the tank's position based on the next tile
            int dx = nextTileLoc.colIndex() - startLoc.colIndex();
            int dy = nextTileLoc.rowIndex() - startLoc.rowIndex();

            if (dx > 0) setDir(Direction.DIRECTION_RIGHT);
            else if (dx < 0) setDir(Direction.DIRECTION_LEFT);
            else if (dy > 0) setDir(Direction.DIRECTION_DOWNWARDS);
            else if (dy < 0) setDir(Direction.DIRECTION_UPWARDS);

            RectangleBound nextTileHint = moveForwardHint();
            if (level.checkMovable(this, nextTileHint)) {
                moveForward();
            } else {
                // If the next tile is not movable, try to find a random direction
                randomMove(level, 0);  // No recursion depth limit for random movement
            }
        } else {
            // If the next tile is not movable, try to find a random direction
            randomMove(level, 0);  // No recursion depth limit for random movement
        }
    }

    /**
     * Deserializes the tank object from a stream.
     * @param in The ObjectInputStream to read the tank object from.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        createTextureFXs();
    }

    public synchronized void setX(int x) { this.x = x; }
    public synchronized int getX() { return this.x; }
    public synchronized void setY(int y) { this.y = y; }
    public synchronized int getY() { return this.y; }
    public synchronized void setDir(Direction dir) { this.dir = dir; }
    public synchronized Direction getDir() { return this.dir; }

}
