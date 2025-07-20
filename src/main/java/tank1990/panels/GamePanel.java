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
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import tank1990.core.*;
import tank1990.player.Player;

public class GamePanel extends AbstractPanel implements ActionListener, KeyListener, Observer {

    private static Dimension gameAreaDimension = null;

    GameEngine gameEngine = null;
    
    JLayeredPane rootPanel = null;      // Store reference to layered root panel
    JPanel getReadyPanel = null;        // Store reference to get ready panel
    JPanel gamePanel = null;            // Store reference to game content panel
    GameAreaPanel gameplayArea = null;  // Store reference to gameplay area
    JPanel pausePanel = null;           // Store reference to pause overlay panel
    JPanel gameOverPanel = null;        // Store reference to game over overlay panel
    JPanel gameScorePanel = null;        // Store reference to game over overlay panel

    public GamePanel(JFrame frame, GameMode gameMode) {
        super(frame);

        initLayeredPanels();

        this.gameEngine = new GameEngine(gameMode);
        this.gameEngine.subscribe((Observer) this);

        gameplayArea.setGameEngine(this.gameEngine);
    }

    @Override
    public void eventFilter(EventType event, Object data) {
        switch (event) {
            case EventType.REPAINT:
                repaint();
                break;
            case EventType.UPDATE_MAP:
                // Handle map update
                break;
            case EventType.PAUSED:
                showPauseOverlay();
                break;
            case EventType.UPDATE:
            case EventType.STARTED:
                hidePauseOverlay();
                break;
            case EventType.GAMEOVER:
                showGameOverOverlay();
                // Add timer to show game score overlay after GAMEOVER_OVERLAY_DURATION seconds
                Timer gameScoreTimer = new Timer(Globals.GAMEOVER_OVERLAY_DURATION, e -> {
                    showGameScoreOverlay();
                });
                gameScoreTimer.setRepeats(false); // Only execute once
                gameScoreTimer.start();

                break;
            default:
                break;
        }
    }

    @Override
    protected void initPanel() {
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.RED);
        addKeyListener(this);
    }

    private void initLayeredPanels() {
        // Set this panel's layout to BorderLayout to contain the root panel
        this.setLayout(new BorderLayout());

        // Root Panel as JLayeredPane
        this.rootPanel = new JLayeredPane();
        this.rootPanel.setPreferredSize(new Dimension(Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT));

        // Game Content Panel (contains gameplay area and info panel)
        this.gamePanel = new JPanel(new BorderLayout());
        this.gamePanel.setBounds(0, 0, Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT);

        // Gameplay Area (3/4 of the width)
        this.gameplayArea = new GameAreaPanel();
        this.gameplayArea.setBackground(Color.BLACK);
        this.gameplayArea.setOpaque(true);

        // Calculate dimensions for square gameplay area
        int gameplaySize = Math.min(Globals.WINDOW_WIDTH * 3 / 4, Globals.WINDOW_HEIGHT);
        this.gameplayArea.setPreferredSize(new Dimension(gameplaySize, gameplaySize));
        gameAreaDimension = this.gameplayArea.getPreferredSize();

        // Game Info Panel (1/4 of the width)
        JPanel gameInfoPanel = new GameInfoPanel();
        gameInfoPanel.setBackground(Globals.COLOR_GRAY);
        gameInfoPanel.setPreferredSize(new Dimension(Globals.WINDOW_WIDTH / 4, gameplaySize));
        gameInfoPanel.setBorder(BorderFactory.createTitledBorder("Game Info"));

        // Add placeholder content to game info panel
        gameInfoPanel.setLayout(new BoxLayout(gameInfoPanel, BoxLayout.Y_AXIS));
        JLabel infoLabel = new JLabel("Game Information");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameInfoPanel.add(infoLabel);

        // Add components to game content panel
        this.gamePanel.add(this.gameplayArea, BorderLayout.CENTER);
        this.gamePanel.add(gameInfoPanel, BorderLayout.EAST);

        // Add game content panel to layered pane (background layer)
        this.rootPanel.add(this.gamePanel, JLayeredPane.DEFAULT_LAYER);

        // Add the root panel to this GamePanel component
        this.add(this.rootPanel, BorderLayout.CENTER);
    }

    /**
     * Called when the panel is added to the container.
     *
     * This method is overridden to set this panel for focus which is required for keyboard events.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    public static Dimension getGameAreaDimension() {
        return gameAreaDimension;
    }

    /**
     * Custom paint method for rendering the game area.
     *
     * This method is responsible for drawing all game objects (player, zombies, projectiles, etc.) on the screen.
     *
     * @param g The Graphics object used to render the game area.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    /**
     * Serializes the current game objects to an output stream.
     *
     * @param os The output stream to write the serialized game objects to.
     * @throws IOException If an I/O error occurs during serialization.
     */
    private void serializeGameObjects(ObjectOutputStream os) throws IOException {
        // Serialize game engine
        os.writeObject(this.gameEngine);
    }

    /**
     * Creates game objects from a serialized input stream.
     *
     * @param os The input stream to read the serialized game objects from.
     * @throws IOException If an I/O error occurs during deserialization.
     * @throws ClassNotFoundException If the class definition of a game object is not found.
     */
    private void createGameObjects(ObjectInputStream os) throws IOException, ClassNotFoundException{
        try {
            this.gameEngine = (GameEngine) os.readObject();
        } catch (EOFException e) {
            // Reached to end of file
        }
    }

    /**
     * Loads a saved game from a file.
     *
     * @param inputStream The input stream from which the saved game data is loaded.
     * @throws IOException If an I/O error occurs during loading.
     * @throws ClassNotFoundException If the class definitions for the saved game objects are not found.
     */
    public void loadGame(FileInputStream inputStream) throws IOException, ClassNotFoundException{
        if (inputStream==null) return;

        ObjectInputStream os = new ObjectInputStream(inputStream);

        //resetGame();

        createGameObjects(os);

        //this.backgroundSoundFX = new SoundFX(Globals.BACKGROUND_SOUND_FX_PATH);
        //this.backgroundSoundFX.play(true);

        os.close();
    }

    /**
     * Opens a dialog screen to save the current game state to a file.
     */
    public void saveGame() {
        SwingUtilities.invokeLater(() -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save File"); // Set dialog title

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
            String formattedDatetime = LocalDateTime.now().format(formatter);
            String defaultFilename = formattedDatetime + ".dat";

            fileChooser.setSelectedFile(new File(defaultFilename));

            int userSelection = fileChooser.showSaveDialog(null); // Open "Save File" dialog

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile(); // Get selected file

                // Save content to the file
                try {
                    FileOutputStream savedFile = new FileOutputStream(fileToSave);
                    try {
                        ObjectOutputStream os = new ObjectOutputStream(savedFile);

                        serializeGameObjects(os);

                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {

                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Stops game timer, and clears resources.
     */
    public void exit() {
        if (this.gameEngine.isStopped()) this.gameEngine.stop();

        //if (this.backgroundSoundFX!=null) {
        //    this.backgroundSoundFX.stop();
        //    this.backgroundSoundFX = null;
        //}
    }

    /**
     * Ends the game and transitions to the game over state.
     */
    public void endGame() {
        exit();

        //showGameOverDialog();
    }

    /**
     * Handles key press events.
     * This method is called when a key is pressed.
     *
     * @param e The KeyEvent that contains the details of the key press.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        int location = e.getKeyLocation();
        
        Player player = null; 
        switch (key) {
            case (Globals.KEY_PLAYER_1_MOVE_UP):
                player = gameEngine.getPlayer1();
                if(player!=null) player.decrementDy();
                break;
            case (Globals.KEY_PLAYER_1_MOVE_RIGHT):
                player = gameEngine.getPlayer1();
                if(player!=null) player.incrementDx();
                break;
            case (Globals.KEY_PLAYER_1_MOVE_DOWN):
                player = gameEngine.getPlayer1();
                if(player!=null) player.incrementDy();
                break;
            case (Globals.KEY_PLAYER_1_MOVE_LEFT):
                player = gameEngine.getPlayer1();
                if(player!=null) player.decrementDx();
                break;
            case (Globals.KEY_PLAYER_2_MOVE_UP):
                player = gameEngine.getPlayer2();
                if(player!=null) player.decrementDy();
                break;
            case (Globals.KEY_PLAYER_2_MOVE_RIGHT):
                player = gameEngine.getPlayer2();
                if(player!=null) player.incrementDx();
                break;
            case (Globals.KEY_PLAYER_2_MOVE_DOWN):
                player = gameEngine.getPlayer2();
                if(player!=null) player.incrementDy();
                break;
            case (Globals.KEY_PLAYER_2_MOVE_LEFT):
                player = gameEngine.getPlayer2();
                if(player!=null) player.decrementDx();
                break;
            case (Globals.KEY_PLAYER_1_MOVE_SHOOT):
                System.out.println("Fire for Player 1");
                player = gameEngine.getPlayer1();
                if (player!=null) gameEngine.triggerPlayerShooting(player);
                break;
            case KeyEvent.VK_ESCAPE:
                // Toggle pause state when ESC is pressed
                if (gameEngine.isPaused()) {
                    gameEngine.start();
                } else {
                    gameEngine.pause();
                }
                break;
            /*
             * TODO: implement Player 2 shooting later
                case (KeyEvent.VK_CONTROL):
                if (location == GlobalConstants.KEY_PLAYER_1_MOVE_SHOOT)
                    gameEngine.startFireTimer(gameEngine.getPlayer1());
                else if (location == GlobalConstants.KEY_PLAYER_2_MOVE_SHOOT)
                    gameEngine.startFireTimer(gameEngine.getPlayer2());
                else {}
                break;
            */
            default: break;
        }
    }

    /**
     * Handles key release events.
     * This method is called when a key is released.
     *
     * @param e The KeyEvent that contains the details of the key release.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        int location = e.getKeyLocation();

        Player player = null;
        switch (key) {
            case (Globals.KEY_PLAYER_1_MOVE_UP):
                player = gameEngine.getPlayer1();
                if (player!=null) player.resetDy();
                break;
            case (Globals.KEY_PLAYER_1_MOVE_RIGHT):
                player = gameEngine.getPlayer1();
                if (player!=null) player.resetDx();
                break;
            case (Globals.KEY_PLAYER_1_MOVE_DOWN):
                player = gameEngine.getPlayer1();
                if (player!=null) player.resetDy();
                break;
            case (Globals.KEY_PLAYER_1_MOVE_LEFT):
                player = gameEngine.getPlayer1();
                if (player!=null) player.resetDx();
                break;
            case (Globals.KEY_PLAYER_2_MOVE_UP):
                player = gameEngine.getPlayer2();
                if (player!=null) player.resetDy();
                break;
            case (Globals.KEY_PLAYER_2_MOVE_RIGHT):
                player = gameEngine.getPlayer2();
                if (player!=null) player.resetDx();
                break;
            case (Globals.KEY_PLAYER_2_MOVE_DOWN):
                player = gameEngine.getPlayer2();
                if (player!=null) player.resetDy();
                break;
            case (Globals.KEY_PLAYER_2_MOVE_LEFT):
                player = gameEngine.getPlayer2();
                if (player!=null) player.resetDx();
                break;
            case (Globals.KEY_PLAYER_1_MOVE_SHOOT):
                System.out.println("Stopping fire for Player 1");
                break;
            /*
             * TODO: implement Player 2 shooting later
            case (KeyEvent.VK_CONTROL):
                if (location == GlobalConstants.KEY_PLAYER_1_MOVE_SHOOT)
                    gameEngine.stopFire(gameEngine.getPlayer1());
                else if (location == GlobalConstants.KEY_PLAYER_2_MOVE_SHOOT)
                    gameEngine.stopFire(gameEngine.getPlayer2());
                else {}
                break;
            */
            case (KeyEvent.VK_ENTER):
                switch (gameEngine.getCurrentLevel().getCurrentState()) {
                    case LevelState.GET_READY:
                        SwingUtilities.invokeLater(() -> {
                            showGamePanel();
                        });
                        break;
                    default: break;
                }
                break;
            default:
                break;
        }
    }

    /**
     * Handles key typing events.
     *
     * Not implemented
     *
     * @param e The KeyEvent that contains the details of the key typed.
     */
    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void actionPerformed(ActionEvent e) { }

    public void show() {
        this.gameEngine.loadGameLevel();
        showGetReadyPanel();
        this.gameEngine.getCurrentLevel().setCurrentState(LevelState.GET_READY);
    }

    private void showGetReadyPanel() {
        int levelIndex = this.gameEngine.getCurrentLevelIndex();

        // Hide the main game panel
        if (this.gamePanel != null) {
            this.gamePanel.setVisible(false);
        }

        // Create and configure the get ready panel
        this.getReadyPanel = new JPanel();
        this.getReadyPanel.setLayout(new BorderLayout());

        JLabel getReadyLabel = new JLabel(String.format("STAGE\t%2d", levelIndex), SwingConstants.CENTER);
        getReadyLabel.setFont(Utils.loadFont(Globals.FONT_PRESS_START_2P, Font.BOLD, 24));
        getReadyLabel.setForeground(Color.BLACK);

        this.getReadyPanel.add(getReadyLabel, BorderLayout.CENTER);
        this.getReadyPanel.setBackground(Globals.COLOR_GRAY);
        this.getReadyPanel.setBounds(0, 0, Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT);
        this.getReadyPanel.setVisible(true);
        
        // Add get ready panel to layered pane (foreground layer)
        this.rootPanel.add(this.getReadyPanel, JLayeredPane.POPUP_LAYER);
        
        this.rootPanel.revalidate();
        this.rootPanel.repaint();
    }

    private void showGamePanel() {
        // Hide the get ready panel
        if (this.getReadyPanel != null) {
            this.getReadyPanel.setVisible(false);
            this.rootPanel.remove(getReadyPanel);
            this.getReadyPanel = null;
        }
        
        // Show the main game panel
        if (this.gamePanel != null) {
            this.gamePanel.setVisible(true);
        }
        
        // Start the game level
        this.gameEngine.startGameLevel();

        requestFocus(); // Ensure game panel has focus for key events
        this.rootPanel.revalidate();
        this.rootPanel.repaint();
    }

    /**
     * Shows the pause overlay on top of the game area.
     * Displays "PAUSE" text centered over the gameplay area.
     */
    private void showPauseOverlay() {
        // Don't create multiple pause panels
        if (this.pausePanel != null) {
            return;
        }

        // Create pause overlay panel that covers only the gameplay area
        this.pausePanel = new JPanel();
        this.pausePanel.setLayout(new BorderLayout());

        // Create the PAUSE label
        JLabel pauseLabel = new JLabel("PAUSE", SwingConstants.CENTER);
        pauseLabel.setFont(Utils.loadFont(Globals.FONT_PRESS_START_2P, Font.BOLD, 48));
        pauseLabel.setForeground(Color.YELLOW);

        // Add label to panel
        this.pausePanel.add(pauseLabel, BorderLayout.CENTER);

        // Set semi-transparent background
        this.pausePanel.setBackground(new Color(0, 0, 0, 150)); // Semi-transparent black
        this.pausePanel.setOpaque(true);

        // Position the pause panel to cover only the gameplay area
        // Calculate the gameplay area position within the game panel
        int gameplaySize = Math.min(Globals.WINDOW_WIDTH * 3 / 4, Globals.WINDOW_HEIGHT);
        this.pausePanel.setBounds(0, 0, gameplaySize, gameplaySize);
        this.pausePanel.setVisible(true);

        // Add pause panel to the highest layer
        this.rootPanel.add(this.pausePanel, JLayeredPane.MODAL_LAYER);

        this.rootPanel.revalidate();
        this.rootPanel.repaint();
    }

    /**
     * Hides the pause overlay from the game area.
     */
    private void hidePauseOverlay() {
        if (this.pausePanel != null) {
            this.pausePanel.setVisible(false);
            this.rootPanel.remove(this.pausePanel);
            this.pausePanel = null;

            this.rootPanel.revalidate();
            this.rootPanel.repaint();
        }
    }

    private void showGameOverOverlay() {
        // Don't create multiple pause panels
        if (this.gameOverPanel != null) {
            return;
        }

        // Create pause overlay panel that covers only the gameplay area
        this.gameOverPanel = new JPanel();
        this.gameOverPanel.setLayout(new BorderLayout());

        // Create the PAUSE label
        JLabel pauseLabel = new JLabel("GAME OVER", SwingConstants.CENTER);
        pauseLabel.setFont(Utils.loadFont(Globals.FONT_PRESS_START_2P, Font.BOLD, 48));
        pauseLabel.setForeground(Color.RED);

        // Add label to panel
        this.gameOverPanel.add(pauseLabel, BorderLayout.CENTER);

        // Set semi-transparent background
        this.gameOverPanel.setBackground(new Color(0, 0, 0, 150)); // Semi-transparent black
        this.gameOverPanel.setOpaque(true);

        // Position the pause panel to cover only the gameplay area
        // Calculate the gameplay area position within the game panel
        int gameplaySize = Math.min(Globals.WINDOW_WIDTH * 3 / 4, Globals.WINDOW_HEIGHT);
        this.gameOverPanel.setBounds(0, 0, gameplaySize, gameplaySize);
        this.gameOverPanel.setVisible(true);

        // Add pause panel to the highest layer
        this.rootPanel.add(this.gameOverPanel, JLayeredPane.MODAL_LAYER);

        this.rootPanel.revalidate();
        this.rootPanel.repaint();


    }

    private void showGameScoreOverlay() {
        // Don't create multiple pause panels
        if (this.gameScorePanel != null) {
            return;
        }

        // Create pause overlay panel that covers only the gameplay area
        // TODO: Reimplement this panel to show game score
        this.gameScorePanel = new JPanel();
        this.gameScorePanel.setLayout(new BorderLayout());

        this.gameScorePanel.setBackground(Globals.COLOR_GRAY); // Semi-transparent black
        this.gameScorePanel.setOpaque(true);
        this.gameScorePanel.setBounds(0, 0, Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT);
        this.gameScorePanel.setVisible(true);

        // Add pause panel to the highest layer
        this.rootPanel.add(this.gameScorePanel, JLayeredPane.POPUP_LAYER);

        this.rootPanel.revalidate();
        this.rootPanel.repaint();
    }

}
