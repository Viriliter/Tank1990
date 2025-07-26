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

import tank1990.Game;
import tank1990.core.*;
import tank1990.player.Player;

public class GamePanel extends AbstractPanel implements ActionListener, KeyListener, Observer {

    private static Dimension gameAreaDimension = null;

    GameEngine gameEngine = null;
    
    JLayeredPane rootPanel = null;      // Store reference to layered root panel
    JPanel getReadyPanel = null;        // Store reference to get ready panel
    JPanel gamePanel = null;            // Store reference to game content panel
    GameAreaPanel gameplayArea = null;  // Store reference to gameplay area
    GameInfoPanel gameInfoPanel = null; // Store reference to game info panel
    JPanel pausePanel = null;           // Store reference to pause overlay panel
    JPanel gameOverPanel = null;        // Store reference to game over overlay panel
    ScorePanel gameScorePanel = null;        // Store reference to game over overlay panel

    private boolean isGameOver = false;

    public GamePanel(JFrame frame, JPanel parentPanel, GameMode gameMode) {
        super(frame);

        setParentPanel(parentPanel);

        postInitPanel();

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
            case EventType.UPDATE_GAME_INFO:
                this.gameInfoPanel.update((GameScoreStruct) data);
                break;
            case EventType.STARTED:
                hidePauseOverlay();
                break;
            case EventType.NEXT_LEVEL: {
                System.out.println("Game Panel: Next Level Event Triggered");
                this.isGameOver = false;
                this.gameEngine.reset();

                // Add timer to show game score overlay after GAMEOVER_OVERLAY_DURATION milliseconds
                Timer gameScoreTimer = new Timer(Globals.GAMEOVER_OVERLAY_DURATION, e -> {
                    showGameScoreOverlay((GameScoreStruct) data);
                });
                gameScoreTimer.setRepeats(false); // Only execute once
                gameScoreTimer.start();

                break;
            }
            case EventType.GAMEOVER: {
                System.out.println("Game Panel: Game Over Event Triggered");
                this.isGameOver = true;
                // Update Player score if current game score is higher
                int playerCurrentScore = ((GameScoreStruct) data).getTotalScore();
                Game.iPlayerScore = Math.max(Game.iPlayerScore, playerCurrentScore);

                // Update high score if current score is higher than the saved high score
                if (playerCurrentScore > ConfigHandler.getInstance().getBattleCityProperties().hiScore()) {
                    ConfigHandler.getInstance().setProperty("BattleCity", "HiScore", Integer.toString(playerCurrentScore));
                }

                showGameOverOverlay();
                // Add timer to show game score overlay after GAMEOVER_OVERLAY_DURATION milliseconds
                Timer gameScoreTimer = new Timer(Globals.GAMEOVER_OVERLAY_DURATION, e -> {
                    showGameScoreOverlay((GameScoreStruct) data);
                });
                gameScoreTimer.setRepeats(false); // Only execute once
                gameScoreTimer.start();
                break;
            }
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

    @Override
    protected void postInitPanel() {
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
        this.gameInfoPanel = new GameInfoPanel(this.frame, this);
        this.gameInfoPanel.setPreferredSize(new Dimension(Globals.WINDOW_WIDTH / 4, gameplaySize));

        // Add components to game content panel
        this.gamePanel.add(this.gameplayArea, BorderLayout.CENTER);
        this.gamePanel.add(this.gameInfoPanel, BorderLayout.EAST);

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

        this.gameEngine.getCurrentLevel().setCurrentState(LevelState.GET_READY);
    }

    private void showGamePanel() {
        //  Clean up the get ready panel
        if (this.getReadyPanel != null) {
            this.getReadyPanel.setVisible(false);
            this.rootPanel.remove(getReadyPanel);
            this.getReadyPanel = null;
        }

        //  Clean up the score panel if it exists
        if (this.gameScorePanel != null) {
            this.gameScorePanel.setVisible(false);
            this.rootPanel.remove(this.gameScorePanel);
            this.gameScorePanel = null;
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

        // Create the GAME OVER label
        JLabel gameOverLabel = new JLabel("GAME OVER", SwingConstants.CENTER);
        gameOverLabel.setFont(Utils.loadFont(Globals.FONT_PRESS_START_2P, Font.BOLD, 48));
        gameOverLabel.setForeground(Color.RED);

        // Add label to panel
        this.gameOverPanel.add(gameOverLabel, BorderLayout.CENTER);

        // Set semi-transparent background
        this.gameOverPanel.setBackground(new Color(0, 0, 0, 150)); // Semi-transparent black
        this.gameOverPanel.setOpaque(true);

        // Position the game over panel to cover only the gameplay area
        // Calculate the gameplay area position within the game panel
        int gameplaySize = Math.min(Globals.WINDOW_WIDTH * 3 / 4, Globals.WINDOW_HEIGHT);
        this.gameOverPanel.setBounds(0, 0, gameplaySize, gameplaySize);
        this.gameOverPanel.setVisible(true);

        // Add pause panel to the highest layer
        this.rootPanel.add(this.gameOverPanel, JLayeredPane.MODAL_LAYER);

        this.rootPanel.revalidate();
        this.rootPanel.repaint();


    }

    private void showGameScoreOverlay(GameScoreStruct gameScore) {
        System.out.println("Game Panel: Showing game score overlay...");
        // Don't create multiple pause panels
        if (this.gameScorePanel != null) {
            return;
        }

        // Create pause overlay panel that covers only the gameplay area
        this.gameScorePanel = new ScorePanel(this.frame, this, gameScore);
        this.gameScorePanel.setLayout(new BorderLayout());

        this.rootPanel.add(this.gameScorePanel, JLayeredPane.POPUP_LAYER);

        this.gameScorePanel.showPanel();

        this.rootPanel.revalidate();
        this.rootPanel.repaint();
    }

    public void onScorePanelAnimationFinished() {
        // Clean up the score panel first
        if (this.gameScorePanel != null) {
            this.gameScorePanel.setVisible(false);
            this.rootPanel.remove(this.gameScorePanel);
            this.gameScorePanel = null;
        }

        if (this.isGameOver) {
            // Reset the game engine
            this.gameEngine = null;
            GameLevelManager.getInstance().reset();

            // Reset current panel
            resetPanel();

            // Reset menu panel
            SwingUtilities.invokeLater(() -> {
                if (getParentPanel() != null) {
                    MenuPanel menuPanel = (MenuPanel) getParentPanel();
                    menuPanel.resetPanel();
                    this.frame.revalidate();
                    this.frame.repaint();
                }
            });
        } else {
            System.out.println("Game Panel: Starting next level...");

            // Ensure proper cleanup and revalidation before showing ready panel
            this.rootPanel.revalidate();
            this.rootPanel.repaint();

            // Use SwingUtilities.invokeLater to ensure UI updates are processed
            SwingUtilities.invokeLater(() -> {
                this.gameEngine.loadGameLevel();
                showGetReadyPanel();
                requestFocus(); // Ensure the panel has focus for key events
            });
        }
    }
}
