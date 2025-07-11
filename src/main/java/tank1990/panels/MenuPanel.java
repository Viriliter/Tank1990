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

import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import tank1990.core.ConfigHandler;
import tank1990.core.GameMode;
import tank1990.core.GlobalConstants;

public class MenuPanel extends SlidingPanel implements KeyListener {

    private int selectedIndex = 0;
    private static final String[] menuItems = {
            "1 PLAYER", "2 PLAYERS", "CONSTRUCTION"
    };
    private static final List<JLabel> selectorItems = new ArrayList<>();

    public MenuPanel(JFrame frame) {
        super(frame);
    }

    @Override
    protected void initPanel() {
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);

        int width = ConfigHandler.getInstance().getWindowProperties().windowWidth();
        int height = frame.getHeight();

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBounds(0, 0, width, height);
        backgroundPanel.setBackground(Color.BLACK);
        backgroundPanel.setOpaque(true);

        // Base Panel
        JPanel basePanel = new JPanel(new GridBagLayout());
        basePanel.setBounds(0, height, width, height);  // Create at the bottom so the sliding animation takes its place
        basePanel.setOpaque(true);
        basePanel.setBackground(Color.BLACK);
        revalidate();
        repaint();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        gbc.fill = GridBagConstraints.BOTH;

        // Score Panel - 15% height
        gbc.gridy = 0;
        gbc.weighty = 0.15;
        JPanel panelScore = new JPanel();
        panelScore.setOpaque(true);
        panelScore.setBackground(Color.BLACK);
        basePanel.add(panelScore, gbc);

        JLabel labelScore = new JLabel("I- 00 HI- 20000");
        labelScore.setFont(GlobalConstants.loadFont(GlobalConstants.FONT_PRESS_START_2P, Font.BOLD, 24));
        labelScore.setHorizontalAlignment(SwingConstants.CENTER);
        labelScore.setForeground(Color.WHITE);
        panelScore.setLayout(new BorderLayout());
        panelScore.add(labelScore, BorderLayout.CENTER);

        // Game Title - 35% height
        gbc.gridy = 1;
        gbc.weighty = 0.35;
        JPanel panelGameTitle = new JPanel();
        panelGameTitle.setOpaque(true);
        panelGameTitle.setBackground(Color.BLACK);
        basePanel.add(panelGameTitle, gbc);

        JLabel labelTitle = new JLabel("<html><div style='text-align:center;'>TANK H<br>1990</div></html>");
        labelTitle.setFont(GlobalConstants.loadFont(GlobalConstants.FONT_PRESS_START_2P, Font.BOLD, 48));
        labelTitle.setHorizontalAlignment(SwingConstants.CENTER);
        labelTitle.setForeground(Color.WHITE);
        panelGameTitle.setLayout(new BorderLayout());
        panelGameTitle.add(labelTitle, BorderLayout.CENTER);

        // Selection - 25% height
        gbc.gridy = 2;
        gbc.weighty = 0.25;
        JPanel panelSelection = new JPanel(new GridBagLayout());
        panelSelection.setOpaque(true);
        panelSelection.setBackground(Color.BLACK);
        basePanel.add(panelSelection, gbc);

        selectorItems.clear();

        for (int i = 0; i < menuItems.length; i++) {
            GridBagConstraints gbcIcon = new GridBagConstraints();
            gbcIcon.gridx = 0;
            gbcIcon.gridy = i;
            gbcIcon.insets = new Insets(5, 5, 5, 5); // optional spacing
            gbcIcon.anchor = GridBagConstraints.WEST;

            JLabel labelSelectorItem = new JLabel(GlobalConstants.loadPNGIcon(GlobalConstants.TEXTURE_PLAYER1_TANK_PATH, 20, 20));
            panelSelection.add(labelSelectorItem, gbcIcon);
            labelSelectorItem.setVisible(false);
            selectorItems.add(labelSelectorItem);

            GridBagConstraints gbcText = new GridBagConstraints();
            gbcText.gridx = 1;
            gbcText.gridy = i;
            gbcText.insets = new Insets(5, 5, 5, 5);
            gbcText.anchor = GridBagConstraints.WEST;

            JLabel labelSelectionItem = new JLabel(menuItems[i]);
            labelSelectionItem.setFont(GlobalConstants.loadFont(GlobalConstants.FONT_PRESS_START_2P, Font.PLAIN, 18));
            labelSelectionItem.setForeground(Color.WHITE);
            panelSelection.add(labelSelectionItem, gbcText);
        }

        // Copyright - 25% height
        gbc.gridy = 3;
        gbc.weighty = 0.25;
        JPanel panelCopyRight = new JPanel();
        panelCopyRight.setOpaque(true);
        panelCopyRight.setBackground(Color.BLACK);
        basePanel.add(panelCopyRight, gbc);

        JLabel labelCopyRight = new JLabel("© YS 1990");

        labelCopyRight.setFont(GlobalConstants.loadFont(GlobalConstants.FONT_PRESS_START_2P, Font.BOLD, 24));
        labelCopyRight.setHorizontalAlignment(SwingConstants.CENTER);
        labelCopyRight.setForeground(Color.WHITE);
        panelCopyRight.setLayout(new BorderLayout());
        panelCopyRight.add(labelCopyRight, BorderLayout.CENTER);

        frame.add(basePanel);
        frame.add(backgroundPanel);

        // Apply sliding animation for base panel
        startAnimation(basePanel, 5);
    }

    /**
     * Called when the panel is added to the container.
     * This method is overridden to set this panel for focus which is required for keyboard events.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    /**
     * Paints the components of the menu panel, including the background image.
     *
     * @param g The Graphics object used to paint the component.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        this.frame.repaint();
    }

    private void updateSelectorVisibility() {
        if (selectorItems == null || selectorItems.isEmpty()) return;

        for (int i = 0; i < selectorItems.size(); i++) {
            selectorItems.get(i).setVisible(i == selectedIndex);
        }

        SwingUtilities.invokeLater(() -> {
            revalidate(); // refresh layout
            repaint();    // redraw component
        });
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            selectedIndex = (selectedIndex - 1 + menuItems.length) % menuItems.length;
            updateSelectorVisibility();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            selectedIndex = (selectedIndex + 1) % menuItems.length;
            updateSelectorVisibility();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            System.out.println("Selected: " + menuItems[selectedIndex]);
            if (selectedIndex==0) {
                GamePanel gamePanel = new GamePanel(frame, GameMode.MODE_SINGLE_PLAYER);
                frame.getContentPane().removeAll();
                frame.add(gamePanel);
                gamePanel.requestFocusInWindow();
                SwingUtilities.invokeLater(() -> {
                    gamePanel.show();            
                });

            } else if (selectedIndex==1) {
                GamePanel gamePanel = new GamePanel(frame, GameMode.MODE_MULTI_PLAYER);
                frame.getContentPane().removeAll();
                frame.add(gamePanel);
                gamePanel.requestFocusInWindow();
                SwingUtilities.invokeLater(() -> {
                    gamePanel.show();            
                });
            } else if (selectedIndex==2) {
                // TODO implement later
            } else {}
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    @Override protected void animationStarted() { }

    @Override protected void animationFinished() {
        if (!selectorItems.isEmpty()) selectorItems.getFirst().setVisible(true);
    }

}
