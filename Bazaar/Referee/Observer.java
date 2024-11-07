package Referee;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.*;

import Common.EquationTable;
import Common.converters.JSONSerializer;
import Common.rendering.GameStateRenderer;
import Referee.gui.BazaarObserverPanel;

/**
 * A class that observers an ObservableReferee, and renders GameState in a GUI
 */
public class Observer implements EventListener {


    protected List<GameState> gameStateHistory;
    protected int gameStatePointer;
    protected BazaarObserverPanel mainPanel;
    protected GameStateRenderer renderer;
    protected boolean shutDown;

    public Observer() {
        gameStateHistory = new ArrayList<>();
        mainPanel = new BazaarObserverPanel(this);
        this.shutDown = false;
    }

    /**
     * Sets up the observer using the maximally useful equationTable and starting state
     * @param equations
     * @param startingState
     */
    public void setup(EquationTable equations, GameState startingState) {
        this.renderer = new GameStateRenderer(equations, 40, Color.gray.brighter());
        updateGameState(startingState);
        this.mainPanel.setup(this.renderer);
        openFrame();
    }

    /**
     * A method to accept new GameStates
     * @param gs a new GameState
     */
    public void notifyOfGameStateUpdate(GameState gs) {
        if (!shutDown) {
            updateGameState(gs);
        }
    }


    /**
     * Opens a BazaarObserverPanel in a new JFrame
     */
    protected void openFrame() {
        JFrame frame = new JFrame("Bazaar Observer GameState Viewer");
        frame.add(mainPanel);
        frame.setSize(100,100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        mainPanel.requestFocusInWindow();
        mainPanel.setVisible(true);
        mainPanel.repaint();
    }

    /**
     * Gets the current gamestate that is being viewed
     * @return the current gamestate
     */
    public GameState currentGameState() {
        return gameStateHistory.get(gameStatePointer);
    }

    /**
     * Shuts the observer down so that it no longer accepts updated GameStates
     */
    public void shutDown() {
        this.shutDown = true;
    }

    /**
     * Adds a gamestate to the observer's history
     * @param gs
     */
    protected void updateGameState(GameState gs) {
        String fileName = gameStateHistory.size() + ".png";
        gameStateHistory.add(gs);
        saveGameStateImage(gs, fileName);
        mainPanel.repaint();
    }

    /**
     * Saves the given GameState as a .png file
     * @param gameState
     * @param fileName
     */
    public void saveGameStateImage(GameState gameState, String fileName){
        enforceSetup();
        File directory = new File("Tmp");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, fileName);
        try {
            ImageIO.write(renderer.render(gameState), "png", file);
        }
        catch (IOException e){
            //do nothing for now!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! TODO
        }

    }


    /**
     * Cycles the current visible GameState forwards.
     */
    public void moveCurrentGameStateForward() {
        if (gameStatePointer + 1 < gameStateHistory.size()) {
            gameStatePointer++;
        }
    }

    /**
     * Cycles the current visible GameState backwards.
     */
    public void moveCurrentGameStateBackwards() {
        if (gameStatePointer - 1 >= 0) {
            gameStatePointer--;
        }
    }

    /**
     * Saves the current gamestate as a json file
     * @param fileName the filename
     * @throws IOException
     */
    public void saveGameStateJson(String fileName) throws IOException {
        enforceSetup();
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(JSONSerializer.gameStateToJson(currentGameState()).toString());
        writer.close();
    }

    /**
     * Ensuures the referee is setup before calling any rendering methods
     */
    protected void enforceSetup() {
        if (Objects.isNull(renderer)) {
            throw new IllegalStateException("Observer renderer is not setup");
        }
    }


}
