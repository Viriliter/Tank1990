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

package tank1990.projectiles;

import java.awt.Dimension;
import java.awt.Graphics;

import tank1990.core.DynamicGameObject;
import tank1990.core.Globals;
import tank1990.core.SpriteAnimation;
import tank1990.core.Utils;

public class Blast extends DynamicGameObject {
    protected SpriteAnimation spriteAnimation;

    public Blast(int x, int y) {
        setX(x);
        setY(y);
        setSize(new Dimension(Globals.BLAST_WIDTH, Globals.BLAST_HEIGHT));
        this.spriteAnimation = new SpriteAnimation(Globals.BLAST_ANIMATION);
        this.spriteAnimation.setRepeat(1);  // Do not repeat the blast animation
    }

    public boolean update() {
        return this.spriteAnimation.update();
    }

    @Override
    public void draw(Graphics g) {
        Dimension nDim = Utils.normalizeDimension(g, Globals.BLAST_WIDTH, Globals.BLAST_HEIGHT);
        
        this.spriteAnimation.setTargetSize(nDim.width, nDim.height);
        this.spriteAnimation.draw(g, x - nDim.width/2, y - nDim.height/2, 0);
    }

    @Override
    public boolean checkCollision() { return false; }

}
