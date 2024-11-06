package Common.rendering;

import Common.*;
import Referee.GameState;

import java.awt.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GameStateRenderer implements IRenderer<GameState> {

    private final int atomicSize;
    private final Color backgroundColor;
    private final EquationTable equationTable;
    public GameStateRenderer(EquationTable equationTable, int atomicSize, Color backgroundColor) {
        this.atomicSize = atomicSize;
        this.backgroundColor = backgroundColor;
        this.equationTable = equationTable;
    }

    @Override
    public BufferedImage render(GameState gameState) {
        ImageManipulator imageManipulator = new ImageManipulator(backgroundColor);
        EquationTableRenderer equationTableRenderer = new EquationTableRenderer(atomicSize, backgroundColor);
        CardDeckRenderer cardDeckRenderer = new CardDeckRenderer(atomicSize, backgroundColor);

        BufferedImage image = renderPlayerInfos(gameState.players());
        BufferedImage middleRow = equationTableRenderer.render(equationTable);
        middleRow = imageManipulator.placeBeside(middleRow, cardDeckRenderer.render(gameState.cards()));
        image = imageManipulator.placeBelow(image, middleRow);
        image = imageManipulator.placeBelow(image, renderBank(gameState.bank(), imageManipulator));
        return image;
    }

    private BufferedImage renderBank(PebbleCollection bank, ImageManipulator imageManipulator) {
        StringRenderer stringRenderer = new StringRenderer(atomicSize, Color.BLACK);
        PebbleCollectionCountRenderer pebbleCollectionCountRenderer = new PebbleCollectionCountRenderer(atomicSize, backgroundColor);
        BufferedImage image = stringRenderer.render("Bank: ");
        return imageManipulator.placeBeside(image, pebbleCollectionCountRenderer.render(bank));
    }

    private BufferedImage renderPlayerInfos(List<PlayerInformation> players) {
        ImageManipulator imageManipulator = new ImageManipulator(backgroundColor);
        PlayerInfoRenderer playerInfoRenderer = new PlayerInfoRenderer(atomicSize, backgroundColor);
        if (players.isEmpty()) {
            BufferedImage image = new BufferedImage(atomicSize*6, atomicSize*4, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.setColor(backgroundColor);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.dispose();
            return image;
        }
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        return players.stream()
                .map(playerInfoRenderer::render)
                .reduce(image, imageManipulator::placeBeside);
    }


}
