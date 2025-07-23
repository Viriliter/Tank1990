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

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.Serializable;

public abstract class GameObject implements Serializable {
    protected int x;
    protected int y;
    protected Direction dir;
    protected int depth = 0;
    protected int width;
    protected int height;

    // Marked as transient since it can be reconstructed from other fields
    protected transient RectangleBound boundingBox = null;

    public int getX() { return this.x; }

    public int getY() { return this.y; }

    public Direction getDir() { return this.dir; }

    public int getDepth() { return this.depth; }

    public void setX(int x) { this.x = x; }

    public void setY(int y) { this.y = y; }

    public void setDir(Direction dir) { this.dir = dir; }

    public void setDepth(int depth) { this.depth = depth; }

    public Dimension getSize() {return new Dimension(this.width, this.height);}

    public void setSize(Dimension dimension) {this.width = (int) dimension.getWidth(); this.height = (int) dimension.getHeight();}

    public abstract void draw(Graphics g);

    public RectangleBound getBoundingBox() {
        return new RectangleBound(this.x - (int) (this.width*0.5), this.y - (int) (this.height*0.5), this.width, this.height);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Reset transient fields after deserialization
        this.boundingBox = null;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws java.io.IOException {
        out.defaultWriteObject();
    }
}
