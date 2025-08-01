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

/** * @class SlidingPanel
 * @brief Abstract class for panels that can slide in and out of view.
 * @details This class provides methods to handle sliding animations for panels in a JFrame.
 */
public abstract class SlidingPanel extends AbstractPanel{
    protected boolean isAnimationFinished = false; // Flag to check if animation is finished
    private JPanel panel; // The panel to animate

    public SlidingPanel(JFrame frame) {
        super(frame);
    }

    protected void startAnimation(JPanel panel, int delay) {
        this.panel = panel;
        int finalY = 0; // Target position (top)
        int step = 5; // Pixels to move per tick

        Timer timer = new Timer(delay, null);
        timer.addActionListener(e -> {
            Point current = this.panel.getLocation();
            int newY = current.y - step;

            if (newY <= finalY) {
                this.panel.setLocation(0, finalY);
                timer.stop();
                isAnimationFinished = true;
                animationFinished();
            } else {
                this.panel.setLocation(0, newY);
            }

            this.panel.repaint();
        });

        timer.start();
        animationStarted();
    }

    /**
     * Finishes the animation forcefully by setting the panel's location to the final position.
     */
    protected void finishAnimation() {
        if (this.panel==null) {
            System.err.println("Panel is null, the animation might not be started yet.");
            return;
        }

        int finalY = 0; // Target position (top)
        this.panel.setLocation(0, finalY);
        isAnimationFinished = true;
        animationFinished();
        this.panel.repaint();
    }

    protected abstract void animationStarted();

    protected abstract void animationFinished();

    protected void resetPanel() {
        isAnimationFinished = false;

        super.resetPanel();


    }

}
