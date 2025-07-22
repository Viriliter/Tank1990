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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractPanel extends JPanel {
    protected JFrame frame;

    protected JPanel parentPanel;

    public AbstractPanel(JFrame frame) {
        this.frame = frame;

        initPanel();
    }

    public void setParentPanel(JPanel parentPanel) { this.parentPanel = parentPanel; }

    public JPanel getParentPanel() { return this.parentPanel; }

    protected abstract void initPanel();

    protected void resetPanel() {
        this.frame.getContentPane().removeAll();
        this.removeAll();
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.requestFocusInWindow();

        // Add mouse listeners for dragging
        addMouseListener(new MouseAdapter() {});
        addMouseMotionListener(new MouseMotionAdapter() {});

        initPanel();

        postInitPanel();

        this.frame.add(this);

        // Revalidate and repaint the panel
        revalidate();
        repaint();
    }

    protected void postInitPanel() {}
}
