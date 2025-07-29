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

package tank1990.tile;

import java.awt.*;

import tank1990.core.*;
import tank1990.projectiles.Bullet;
import tank1990.tank.AbstractTank;

public abstract class Tile extends StaticGameObject {
    protected TileType type = null;
    protected BlockConfiguration blockConf = BlockConfiguration.BLOCK_CONF_FULL;
    protected SpriteAnimation spriteAnimationFX = null;
    protected boolean isDestroyed = false;

    protected AbstractTank includedTankInst = null;

    private GridLocation gloc = null;

    private boolean isCoordinatesUpdated = false;

    protected Boolean[][] subpieces = null;

    /**
     * Constructor for Tile.
     * Initializes the tile with the specified indices, type, and default block configuration.
     *
     * @param x    The column index of the tile in the map.
     * @param y    The row index of the tile in the map.
     * @param type The type of the tile.
     */
    public Tile(int x, int y, TileType type) {
        setX(x);
        setY(y);
        setDir(Direction.DIRECTION_INVALID);

        this.type = type;
        this.blockConf = BlockConfiguration.BLOCK_CONF_FULL;
        this.subpieces = new Boolean[Globals.TILE_SUBDIVISION][Globals.TILE_SUBDIVISION];
        setSubPieceVisibility(this.blockConf);
    }

    /**
     * Constructor for Tile.
     * Initializes the tile with the specified indices, type, and block configuration.
     *
     * @param x         The column index of the tile in the map.
     * @param y         The row index of the tile in the map.
     * @param type      The type of the tile.
     * @param blockConf The block configuration of the tile.
     */
    public Tile(int x, int y, TileType type, BlockConfiguration blockConf) {
        setX(x);  // By default set as index, then it will be updated in draw method for actual X-Y coordinate
        setY(y);  // By default set as index, then it will be updated in draw method for actual X-Y coordinate
        setDir(Direction.DIRECTION_INVALID);

        this.type = type;
        this.blockConf = blockConf;
        this.gloc = new GridLocation(y, x); // Initialize grid location based on row (y) and column (x) indices
        this.subpieces = new Boolean[Globals.TILE_SUBDIVISION][Globals.TILE_SUBDIVISION];
        setSubPieceVisibility(this.blockConf);
    }

    public TileType getType() { return this.type; }

    public BlockConfiguration getBlockConf() { return this.blockConf; }

    public GridLocation getGridLocation() {
        return this.gloc;
    }

    public void update() { }
    
    public void draw(Graphics g) {
        if (this.spriteAnimationFX == null) {
            System.err.println("Tile textureFX is null for tile: " + this.type);
            return;
        }

        setSize(Utils.normalizeDimension(g, Globals.TILE_WIDTH, Globals.TILE_HEIGHT));

        // Dynamically update the grid location based on the current x and y coordinates
        if (!isCoordinatesUpdated) {
            GridLocation gloc = new GridLocation(this.y, this.x);
            Location loc = Utils.gridLoc2Loc(gloc);

            setX(loc.x());
            setY(loc.y());

            isCoordinatesUpdated = true;
        }

        // Get the clip bounds of the graphics context
        java.awt.Rectangle clipBounds = g.getClipBounds();
        this.spriteAnimationFX.setTargetSize(getSize().width, getSize().height);
        this.spriteAnimationFX.draw(g, x, y, 0.0);

        boolean allSubpiecesDestroyed = true;
        // If the sub-pieces are not visible, draw it as a black rectangle
        for (int r=0; r<Globals.TILE_SUBDIVISION; r++) {
            for (int c=0; c<Globals.TILE_SUBDIVISION; c++) {
                if (!this.subpieces[r][c]) {
                    int subX = x + ((c-2) * getSize().width / Globals.TILE_SUBDIVISION);
                    int subY = y + ((r-2) * getSize().height / Globals.TILE_SUBDIVISION);
                    g.setColor(Color.BLACK);
                    g.fillRect(subX, subY, getSize().width / Globals.TILE_SUBDIVISION, getSize().height / Globals.TILE_SUBDIVISION);
                } else {
                    allSubpiecesDestroyed = false;
                }
            }
        }

        // If all sub-pieces are not visible, set the tile as destroyed
        if (allSubpiecesDestroyed) {
            setAsDestroyed();
        }

        if (Globals.SHOW_BOUNDING_BOX) {
            g.setColor(Color.pink);
            g.drawRect((int) getBoundingBox().getX(), (int) getBoundingBox().getY(), (int) getBoundingBox().getWidth(), (int) getBoundingBox().getHeight());
        }
    }

    public boolean includesTank() {
        return this.includedTankInst!=null;
    }

    /**
     * Destroys the tile and returns true if it was successfully destroyed.
     * If the tile cannot be destroyed, it should return false.
     * This method should be overridden by subclasses to implement specific destruction logic.
     * @param b the bullet that is attempting to destroy the tile
     *
     * @return true if the tile was successfully destroyed, false otherwise
     */
    public abstract boolean destroy(Bullet b);

    public boolean isDestroyed() {
        return this.isDestroyed;
    }

    public void setAsDestroyed() {
        this.isDestroyed = true;
    }

    protected void setSubPieceVisibility(BlockConfiguration blockConfiguration) {
        for (int r=0; r<Globals.TILE_SUBDIVISION; r++) {
            for (int c=0; c<Globals.TILE_SUBDIVISION; c++) {
                this.subpieces[r][c] = false;
            }
        }

        if (blockConfiguration == BlockConfiguration.BLOCK_CONF_FULL) {
            for (int r=0; r<Globals.TILE_SUBDIVISION; r++) {
                for (int c=0; c<Globals.TILE_SUBDIVISION; c++) {
                    this.subpieces[r][c] = true;
                }
            }
        } else if (blockConfiguration == BlockConfiguration.BLOCK_CONF_1) {
            this.subpieces[0][0] = true;
            this.subpieces[0][1] = true;
            this.subpieces[1][0] = true;
            this.subpieces[1][1] = true;
        } else if (blockConfiguration == BlockConfiguration.BLOCK_CONF_2) {
            this.subpieces[0][2] = true;
            this.subpieces[0][3] = true;
            this.subpieces[1][2] = true;
            this.subpieces[1][3] = true;
        } else if (blockConfiguration == BlockConfiguration.BLOCK_CONF_3) {
            this.subpieces[2][2] = true;
            this.subpieces[2][3] = true;
            this.subpieces[3][2] = true;
            this.subpieces[3][3] = true;
        } else if (blockConfiguration == BlockConfiguration.BLOCK_CONF_4) {
            this.subpieces[2][0] = true;
            this.subpieces[2][1] = true;
            this.subpieces[3][0] = true;
            this.subpieces[3][1] = true;
        } else if (blockConfiguration == BlockConfiguration.BLOCK_CONF_5) {
            this.subpieces[0][0] = true;
            this.subpieces[0][1] = true;
            this.subpieces[0][2] = true;
            this.subpieces[0][3] = true;
            this.subpieces[1][0] = true;
            this.subpieces[1][1] = true;
            this.subpieces[1][2] = true;
            this.subpieces[1][3] = true;
        } else if (blockConfiguration == BlockConfiguration.BLOCK_CONF_6) {
            this.subpieces[0][2] = true;
            this.subpieces[0][3] = true;
            this.subpieces[1][2] = true;
            this.subpieces[1][3] = true;
            this.subpieces[2][2] = true;
            this.subpieces[2][3] = true;
            this.subpieces[3][2] = true;
            this.subpieces[3][3] = true;
        } else if (blockConfiguration == BlockConfiguration.BLOCK_CONF_7) {
            this.subpieces[2][0] = true;
            this.subpieces[2][1] = true;
            this.subpieces[2][2] = true;
            this.subpieces[2][3] = true;
            this.subpieces[3][0] = true;
            this.subpieces[3][1] = true;
            this.subpieces[3][2] = true;
            this.subpieces[3][3] = true;
        } else if (blockConfiguration == BlockConfiguration.BLOCK_CONF_8) {
            this.subpieces[0][0] = true;
            this.subpieces[0][1] = true;
            this.subpieces[1][0] = true;
            this.subpieces[1][1] = true;
            this.subpieces[2][0] = true;
            this.subpieces[2][1] = true;
            this.subpieces[3][0] = true;
            this.subpieces[3][1] = true;
        } else { }
    }

    protected boolean hitRow(Direction hitDir) {
        boolean isHit = false;
        if (hitDir == Direction.DIRECTION_DOWNWARDS) {
            for (int r=0; r<Globals.TILE_SUBDIVISION; r++) {
                if (this.subpieces[r][0] || this.subpieces[r][1] |
                    this.subpieces[r][2] || this.subpieces[r][3]) {
                    this.subpieces[r][0] = false;
                    this.subpieces[r][1] = false;
                    this.subpieces[r][2] = false;
                    this.subpieces[r][3] = false;
                    isHit = true;
                    break;
                }
            }
        } else {  // Direction.DIRECTION_UPWARDS
            for (int r=Globals.TILE_SUBDIVISION-1; r>=0; r--) {
                if (this.subpieces[r][0] || this.subpieces[r][1] |
                    this.subpieces[r][2] || this.subpieces[r][3]) {
                    this.subpieces[r][0] = false;
                    this.subpieces[r][1] = false;
                    this.subpieces[r][2] = false;
                    this.subpieces[r][3] = false;
                    isHit = true;
                    break;
                }
            }
        }
        return isHit;
    }

    protected boolean hitColumn(Direction hitDir) {
        boolean isHit = false;
        if (hitDir == Direction.DIRECTION_LEFT) {
            for (int c=Globals.TILE_SUBDIVISION-1; c>=0; c--) {
                if (this.subpieces[0][c] || this.subpieces[1][c] |
                    this.subpieces[2][c] || this.subpieces[3][c]) {
                    this.subpieces[0][c] = false;
                    this.subpieces[1][c] = false;
                    this.subpieces[2][c] = false;
                    this.subpieces[3][c] = false;
                    isHit = true;
                    break;
                }
            }
        } else {  // Direction.DIRECTION_RIGHT
            for (int c=0; c<Globals.TILE_SUBDIVISION; c++) {
                if (this.subpieces[0][c] || this.subpieces[1][c] |
                    this.subpieces[2][c] || this.subpieces[3][c]) {
                    this.subpieces[0][c] = false;
                    this.subpieces[1][c] = false;
                    this.subpieces[2][c] = false;
                    this.subpieces[3][c] = false;
                    isHit = true;
                    break;
                }
            }
        }
        return isHit;
    }

    protected void hit(Direction hitDir) {
        boolean isHit;

        if (hitDir == Direction.DIRECTION_DOWNWARDS ||
            hitDir == Direction.DIRECTION_UPWARDS) {
            isHit = hitRow(hitDir);
        } else {
            isHit = hitColumn(hitDir);
        }

        // If no sub-pieces were hit, set the tile as destroyed
        if (!isHit) setAsDestroyed();
    }

    public RectangleBound getBoundingBox() {
        int minRow = Globals.TILE_SUBDIVISION;
        int maxRow = -1;
        int minCol = Globals.TILE_SUBDIVISION;
        int maxCol = -1;

        // Find the bounds of visible sub-pieces
        boolean hasVisibleSubpieces = false;
        for (int r = 0; r < Globals.TILE_SUBDIVISION; r++) {
            for (int c = 0; c < Globals.TILE_SUBDIVISION; c++) {
                if (this.subpieces[r][c]) {
                    hasVisibleSubpieces = true;
                    minRow = Math.min(minRow, r);
                    maxRow = Math.max(maxRow, r);
                    minCol = Math.min(minCol, c);
                    maxCol = Math.max(maxCol, c);
                }
            }
        }

        // If no visible sub-pieces, return empty bounding box
        if (!hasVisibleSubpieces) {
            return new RectangleBound(getX(), getY(), 0, 0);
        }

        // Calculate sub-piece dimensions
        int subpieceWidth = getSize().width / Globals.TILE_SUBDIVISION;
        int subpieceHeight = getSize().height / Globals.TILE_SUBDIVISION;

        // Calculate the actual bounding box dimensions
        int boundingWidth = (maxCol - minCol + 1) * subpieceWidth;
        int boundingHeight = (maxRow - minRow + 1) * subpieceHeight;

        // Calculate the top-left position of the visible area (not center)
        int topLeftX = (getX() - getSize().width / 2) + (minCol * subpieceWidth);
        int topLeftY = (getY() - getSize().height / 2) + (minRow * subpieceHeight);

        // Return bounding box with top-left position
        return new RectangleBound(topLeftX, topLeftY, boundingWidth, boundingHeight);
    }

    public String toString() {
        return "Tile{" +
                "type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", width=" + getSize().width +
                ", height=" + getSize().height +
                ", LocX=" + Utils.gridLoc2Loc(this.gloc).x() +
                ", LocY=" + Utils.gridLoc2Loc(this.gloc).y() +
                '}';
    }
}
