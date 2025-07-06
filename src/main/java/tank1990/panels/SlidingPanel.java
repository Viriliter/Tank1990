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

package tank1990.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class SlidingPanel extends AbstractPanel{
    protected int yOffset;
    protected Timer timer;

    public SlidingPanel(JFrame frame) {
        super(frame);

        this.yOffset = this.frame.getHeight();  // Start from to bottom
        setPreferredSize(new Dimension(this.frame.getWidth(), this.frame.getHeight()));
        setBackground(Color.BLACK);
    }

    protected void startAnimation() {
        timer = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                yOffset -= 5;
                if (yOffset <= 0) {
                    yOffset = 0;
                    timer.stop();
                    animationStopped();
                }
                repaint();
            }
        });
        timer.start();
    }

    protected abstract void animationStarted();

    protected abstract void animationStopped();
}
