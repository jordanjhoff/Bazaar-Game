package Runnables;

import Common.*;
import Common.rendering.*;
import Referee.GameObjectGenerator;
import Referee.GameState;

import javax.imageio.ImageIO;
import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;

public class RenderingTest {

    public static void main(String[] args) {

        GameObjectGenerator generator = new GameObjectGenerator(1);

        EquationTable equationTable = generator.generateRandomEquationTable();
        CardDeck cards = generator.generateRandomCards(20);
        PebbleCollection bank = generator.generateFullBank(20);
        PebbleCollection brokePlayerWallet = new PebbleCollection(new ArrayList<>());
        PebbleCollection normalPlayerWallet = generator.generateRandomPebbleCollection(3);
        PlayerInformation richPlayer = new PlayerInformation("jordy",bank, 10);
        PlayerInformation brokePlayer = new PlayerInformation("benlerner", brokePlayerWallet, 19);
        PlayerInformation normalPlayer = new PlayerInformation("jack",normalPlayerWallet, 5);
        GameState gameState = new GameState(bank, cards, List.of(richPlayer, brokePlayer, normalPlayer));
        TurnState ts = gameState.getTurnState();

        GameStateRenderer gameStateRenderer = new GameStateRenderer(equationTable, 50, Color.gray);
        TurnStateRenderer turnStateRenderer = new TurnStateRenderer(equationTable, 50, Color.gray);

        File f1 = new File("gamestate.png");
        File f2 = new File("turnstate.png");
        try {
            ImageIO.write(gameStateRenderer.render(gameState), "png", f1);
            ImageIO.write(turnStateRenderer.render(ts), "png", f2);
        } catch (IOException e) {
            System.out.println("Exception thrown: " + e.getMessage());
        }
    }



}
