package tank1990;

import javax.swing.*;
import java.awt.*;

import tank1990.core.GlobalConstants;
import tank1990.panels.MenuPanel;

public class Game {
    public static void main(String[] args) {
        // Disable DPI scaling to ensure exact pixel dimensions
        //System.setProperty("sun.java2d.dpiaware", "false");
        //System.setProperty("sun.java2d.uiScale", "1.0");
        
        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame(GlobalConstants.GAME_TITLE);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            // Set full-screen mode if either window width or height is set to 0
            if (GlobalConstants.WINDOW_WIDTH == 0 ||  GlobalConstants.WINDOW_HEIGHT == 0 ) {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Dimension screenSize = toolkit.getScreenSize();
                frame.setSize(screenSize.width, screenSize.height);
            } else {
                // Set the content pane size to get exact content area dimensions (excluding title bar)
                frame.getContentPane().setPreferredSize(new Dimension(GlobalConstants.WINDOW_WIDTH, GlobalConstants.WINDOW_HEIGHT));
                frame.pack(); // This will size the frame to fit the content area exactly
            }

            MenuPanel menuPanel = new MenuPanel(frame);
            frame.add(menuPanel);
            frame.setVisible(true);
        });
    }
}