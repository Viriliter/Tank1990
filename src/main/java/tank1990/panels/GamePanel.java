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

import tank1990.core.ConfigHandler;
import tank1990.core.GameEngine;
import tank1990.core.GameMode;
import tank1990.core.GlobalConstants;

public class GamePanel extends AbstractPanel implements ActionListener, KeyListener {

    GameEngine gameEngine = null;

    public GamePanel(JFrame frame, GameMode gameMode) {
        super(frame);

        gameEngine = new GameEngine(gameMode);
    }

    @Override
    protected void initPanel() {
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.BLACK);
        addKeyListener(this);

        ConfigHandler.WindowProperties wProperties = ConfigHandler.getInstance().getWindowProperties();

        // Root Panel
        JPanel root = new JPanel(new BorderLayout());

        // Panels Container
        JPanel panelContainer = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;

        // Game Area panel with overlay panels
        gbc.weightx = 0.75;
        JLayeredPane gameAreaPanel = new JLayeredPane();
        gameAreaPanel.setPreferredSize(new Dimension(600, 600));

        // Overlay panel for game map
        JPanel mapPanel = new JPanel();
        mapPanel.setBackground(Color.DARK_GRAY);
        mapPanel.setBounds(0, 0, 600, 600);
        gameAreaPanel.add(mapPanel, JLayeredPane.DEFAULT_LAYER);

        // Overlay panel for actors (enemy and player(s))
        JPanel actorsPanel = new JPanel();
        actorsPanel.setBackground(new Color(255, 0, 0, 100)); // Semi-transparent red
        actorsPanel.setBounds(100, 100, 400, 400);
        gameAreaPanel.add(actorsPanel, JLayeredPane.PALETTE_LAYER);

        //// Overlay panel for visual FX
        //JPanel fxPanel = new JPanel();
        //fxPanel.setBackground(new Color(255, 0, 0, 100)); // Semi-transparent red
        //fxPanel.setBounds(100, 100, 400, 400);
        //gameAreaPanel.add(fxPanel, JLayeredPane.PALETTE_LAYER);
        //
        //// Overlay panel for Items
        //JPanel itemsPanel = new JPanel();
        //itemsPanel.setBackground(new Color(255, 0, 0, 100)); // Semi-transparent red
        //itemsPanel.setBounds(100, 100, 400, 400);
        //gameAreaPanel.add(itemsPanel, JLayeredPane.PALETTE_LAYER);
        //
        //// Overlay panel for HUD
        //JPanel hudPanel = new JPanel();
        //hudPanel.setBackground(new Color(255, 0, 0, 100)); // Semi-transparent red
        //hudPanel.setBounds(100, 100, 400, 400);
        //gameAreaPanel.add(hudPanel, JLayeredPane.PALETTE_LAYER);

        gbc.gridx = 0;
        panelContainer.add(gameAreaPanel, gbc);

        // Game status panel
        gbc.weightx = 0.25;
        gbc.gridx = 1;
        JPanel gameStatPanel = new JPanel();
        gameStatPanel.setBackground(Color.LIGHT_GRAY);
        panelContainer.add(gameStatPanel, gbc);

        // Add to root
        root.add(panelContainer, BorderLayout.CENTER);
        frame.add(root);
        frame.setVisible(true);
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

        this.gameEngine.paintComponent(g);

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
    @SuppressWarnings("unchecked")
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
        //switch (e.getKeyCode()) {
        //    case KeyEvent.VK_A -> player.decrementDx();
        //    case KeyEvent.VK_D -> player.incrementDx();
        //    case KeyEvent.VK_W -> player.decrementDy();
        //    case KeyEvent.VK_S -> player.incrementDy();
        //    case KeyEvent.VK_R -> player.getCurrentWeapon().reload();
        //    case KeyEvent.VK_Q -> player.switchWeapon();
        //    case KeyEvent.VK_ESCAPE -> openInGameMenu();
        //}
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

        switch (key) {
            case (GlobalConstants.KEY_PLAYER_1_MOVE_UP):
                gameEngine.getPlayer1().decrementDy();
                break;
            case (GlobalConstants.KEY_PLAYER_1_MOVE_RIGHT):
                gameEngine.getPlayer1().incrementDx();
                break;
            case (GlobalConstants.KEY_PLAYER_1_MOVE_DOWN):
                gameEngine.getPlayer1().incrementDy();
                break;
            case (GlobalConstants.KEY_PLAYER_1_MOVE_LEFT):
                gameEngine.getPlayer1().decrementDx();
                break;
            case (GlobalConstants.KEY_PLAYER_2_MOVE_UP):
                gameEngine.getPlayer2().decrementDy();
                break;
            case (GlobalConstants.KEY_PLAYER_2_MOVE_RIGHT):
                gameEngine.getPlayer2().incrementDx();
                break;
            case (GlobalConstants.KEY_PLAYER_2_MOVE_DOWN):
                gameEngine.getPlayer2().incrementDy();
                break;
            case (GlobalConstants.KEY_PLAYER_2_MOVE_LEFT):
                gameEngine.getPlayer2().decrementDx();
                break;
            case (KeyEvent.VK_CONTROL):
                if (location == GlobalConstants.KEY_PLAYER_1_MOVE_SHOOT)
                    gameEngine.getPlayer1().shoot();
                else if (location == GlobalConstants.KEY_PLAYER_2_MOVE_SHOOT)
                    gameEngine.getPlayer2().shoot();
                else {}
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
    public void actionPerformed(ActionEvent e) {

    }

}
