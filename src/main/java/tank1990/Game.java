package tank1990;

import javax.swing.*;
import java.awt.*;

import tank1990.core.GlobalConstants;
import tank1990.core.ConfigHandler;
import tank1990.core.ConfigHandler.WindowProperties;
import tank1990.panels.MenuPanel;

public class Game {
    public static void main(String[] args) {
        ConfigHandler.getInstance().parse(GlobalConstants.CONFIGURATION_FILE);

        WindowProperties windowProperties = ConfigHandler.getInstance().getWindowProperties();

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame(GlobalConstants.GAME_TITLE);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setSize(windowProperties.windowWidth(), windowProperties.windowHeight());
            frame.setResizable(false);

            // Set full-screen mode if either window width or height is set to 0
            if (windowProperties.windowWidth() == 0 ||  windowProperties.windowHeight() == 0 ) {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Dimension screenSize = toolkit.getScreenSize();
                frame.setSize(screenSize.width, screenSize.height);
                frame.setResizable(false);
            } else {
                frame.setSize(windowProperties.windowWidth(), windowProperties.windowHeight());
                frame.setResizable(false);
            }

            MenuPanel menuPanel = new MenuPanel(frame);
            frame.add(menuPanel);
            frame.setVisible(true);
        });
    }
}