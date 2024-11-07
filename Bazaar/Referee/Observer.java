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

    public void setup(EquationTable equations, GameState startingState) {
        this.renderer = new GameStateRenderer(equations, 40, Color.gray.brighter());
        updateGameState(startingState);
        this.mainPanel.setup(this.renderer);
        openFrame();
    }

    public void notifyOfGameStateUpdate(GameState gs) {
        if (!shutDown) {
            updateGameState(gs);
        }
    }



    public void openFrame() {
        JFrame frame = new JFrame("Bazaar Observer GameState Viewer");
        frame.add(mainPanel);
        frame.setSize(100,100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        mainPanel.requestFocusInWindow();
        mainPanel.setVisible(true);
        mainPanel.repaint();
    }

    public GameState currentGameState() {
        return gameStateHistory.get(gameStatePointer);
    }

    public void shutDown() {
        this.shutDown = true;
    }

    /**
     * Requires renderer to be instantiated. todo make safe
     */
    protected void updateGameState(GameState gs) {
        String fileName = gameStateHistory.size() + ".png";
        gameStateHistory.add(gs);
        saveGameStateImage(gs, fileName);
        mainPanel.repaint();
    }

    /**
     * Requires renderer to be instantiated. todo make safe
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


    public void advancePointer() {
        if (gameStatePointer + 1 < gameStateHistory.size()) {
            gameStatePointer++;
        }
    }

    public void retreatPointer() {
        if (gameStatePointer - 1 >= 0) {
            gameStatePointer--;
        }
    }

    public void saveGameStateJson(String fileName) throws IOException {
        enforceSetup();
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(JSONSerializer.gameStateToJson(currentGameState()).toString());
        writer.close();
    }

    protected void enforceSetup() {
        if (Objects.isNull(renderer)) {
            throw new IllegalStateException("Observer renderer is not setup");
        }
    }


}
