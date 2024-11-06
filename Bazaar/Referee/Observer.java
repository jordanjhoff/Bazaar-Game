package Referee;

import Common.EquationTable;
import Common.converters.JSONSerializer;
import Common.rendering.GameStateRenderer;
import Referee.gui.BazaarObserverPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Observer implements EventListener {


    protected List<GameState> gameStateHistory;
    protected ListIterator<GameState> gameStatePointer;
    protected GameState currentGameState;
    protected BazaarObserverPanel mainPanel;
    protected GameStateRenderer renderer;
    protected boolean shutDown;

    public Observer() {
        gameStateHistory = Collections.synchronizedList(new LinkedList<GameState>());
        mainPanel = new BazaarObserverPanel(this);
        gameStatePointer = gameStateHistory.listIterator();
        this.shutDown = false;
    }

    public void setup(EquationTable equations, GameState startingState) {
        currentGameState = startingState;
        this.renderer = new GameStateRenderer(equations, 40, Color.gray.brighter());
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
        return currentGameState;
    }

    public void shutDown() {
        this.shutDown = true;
    }

    protected void updateGameState(GameState gs) {
        String fileName = gameStateHistory.size() + ".png";
        gameStateHistory.add(gs);
        saveGameStateImage(gs, fileName);
        mainPanel.repaint();
    }

    public void saveGameStateImage(GameState gameState, String fileName){
        enforceSetup();
        File file = new File(fileName);
        try {
            ImageIO.write(renderer.render(gameState), "png", file);
        }
        catch (IOException e){
            //do nothing for now!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }

    }


    public void advancePointer() {
        if (gameStatePointer.hasNext()) {
            System.out.println(":)");
            currentGameState = gameStatePointer.next();
        }
    }

    public void retreatPointer() {
        if (gameStatePointer.hasPrevious()) {
            currentGameState = gameStatePointer.previous();
        }
    }



    public void saveGameStateJson(String fileName) throws IOException {
        enforceSetup();
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(JSONSerializer.gameStateToJson(currentGameState).toString());
        writer.close();
    }




    protected void enforceSetup() {
        if (Objects.isNull(renderer)) {
            throw new IllegalStateException("Observer renderer is not setup");
        }
    }


}
