package Common.rendering;

import Common.Card;
import Common.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class CardDeckRenderer implements IRenderer<CardDeck> {

    private final int atomicSize;
    private final Color backgroundColor;
    public CardDeckRenderer(int atomicSize, Color backgroundColor) {
        this.atomicSize = atomicSize;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public BufferedImage render(CardDeck cardDeck) {
        ImageManipulator imageManipulator = new ImageManipulator(backgroundColor);
        VisibleCardsRenderer visibleCardsRenderer = new VisibleCardsRenderer(atomicSize, backgroundColor);
        List<Card> visibleCards = cardDeck.visibleCards();
        BufferedImage image = visibleCardsRenderer.render(visibleCards);
        image = imageManipulator.placeBeside(image, remainingPile(cardDeck.nonVisibleCards().size(), imageManipulator));
        return image;
    }

    private BufferedImage remainingPile(int count, ImageManipulator imageManipulator) {
        StringRenderer stringRenderer = new StringRenderer(atomicSize*5, Color.BLACK);
        BufferedImage image = new BufferedImage(atomicSize*5, atomicSize*7, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0,0,atomicSize*5,atomicSize*7);
        g.dispose();
        image = imageManipulator.placeInfront(image, stringRenderer.render(String.valueOf(count)));
        return imageManipulator.placeInfront(renderCardBorder(6, 8, Color.BLACK), image);
    }

    private BufferedImage renderCardBorder(int width, int height, Color color) {
        BufferedImage border = new BufferedImage(atomicSize*width, atomicSize*height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = border.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, atomicSize*width, atomicSize*height);
        return border;
    }
}
