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

import java.awt.Graphics;
import java.io.Serializable;

public abstract class GameObject implements Serializable {
    protected int row;
    protected int col;
    protected Direction dir;
    protected int depth=0;
    protected int width;
    protected int height;

    public int getRow() { return this.row; }

    public int getCol() { return this.col; }

    public Direction getDir() { return this.dir; }

    public int getDepth() { return this.depth; }

    public void setRow(int row) { this.row = row; }

    public void setCol(int col) { this.col = col; }

    public void setDir(Direction dir) { this.dir = dir; }

    public void setDepth(int depth) { this.depth = depth; }

    public GridSize getSize() {return new GridSize(this.width, this.height);}

    public void setSize(GridSize dimension) {this.width = dimension.width(); this.height = dimension.height();}

    public abstract void draw(Graphics g);

}
