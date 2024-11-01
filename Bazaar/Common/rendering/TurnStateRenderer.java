package Common.rendering;

import Common.EquationTable;
import Common.PebbleCollection;
import Common.TurnState;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TurnStateRenderer implements IRenderer<TurnState>{

    private final int atomicSize;
    private final Color backgroundColor;
    private final EquationTable equationTable;
    public TurnStateRenderer(EquationTable equationTable, int atomicSize, Color backgroundColor) {
        this.atomicSize = atomicSize;
        this.backgroundColor = backgroundColor;
        this.equationTable = equationTable;
    }

    @Override
    public BufferedImage render(TurnState turnState) {
        ImageManipulator imageManipulator = new ImageManipulator(backgroundColor);
        EquationTableRenderer equationTableRenderer = new EquationTableRenderer(atomicSize, backgroundColor);
        VisibleCardsRenderer visibleCardsRenderer = new VisibleCardsRenderer(atomicSize, backgroundColor);
        PlayerInfoRenderer playerInfoRenderer = new PlayerInfoRenderer(atomicSize, backgroundColor);
        BufferedImage image = renderScores(turnState, imageManipulator);
        image = imageManipulator.placeBelow(image, equationTableRenderer.render(equationTable));
        image = imageManipulator.placeBelow(image, renderBank(turnState.bank(), imageManipulator));
        image = imageManipulator.placeBelow(image, visibleCardsRenderer.render(turnState.visibleCards()));
        image = imageManipulator.placeBelow(image, playerInfoRenderer.render(turnState.activePlayer()));
        return image;
    }

    private BufferedImage renderBank(PebbleCollection bank, ImageManipulator imageManipulator) {
        StringRenderer stringRenderer = new StringRenderer(atomicSize, Color.BLACK);
        PebbleCollectionCountRenderer pebbleCollectionCountRenderer = new PebbleCollectionCountRenderer(atomicSize, backgroundColor);
        BufferedImage image = stringRenderer.render("Bank: ");
        return imageManipulator.placeBeside(image, pebbleCollectionCountRenderer.render(bank));
    }

    private BufferedImage renderScores(TurnState turnState, ImageManipulator imageManipulator) {
        StringRenderer stringRenderer = new StringRenderer(atomicSize*2, Color.BLACK);
        BufferedImage image = stringRenderer.render("Scores: ");
        image = turnState.scores().stream()
                .map(score -> stringRenderer.render(String.valueOf(score)))
                .reduce(image, imageManipulator::placeBeside);
        return image;
    }
}
