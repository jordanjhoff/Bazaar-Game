package Common.rendering;

import Common.Card;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class VisibleCardsRenderer implements IRenderer<List<Card>> {

    private final int atomicSize;
    private final Color backgroundColor;
    public VisibleCardsRenderer(int atomicSize, Color backgroundColor) {
        this.atomicSize = atomicSize;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public BufferedImage render(List<Card> visibleCards) {
        ImageManipulator imageManipulator = new ImageManipulator(backgroundColor);
        CardRenderer cardRenderer = new CardRenderer(atomicSize, backgroundColor);

        BufferedImage image = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
        image = visibleCards.stream()
                .map(cardRenderer::render)
                .reduce(image, imageManipulator::placeBeside);
        image = imageManipulator.placeInfront(new BufferedImage(atomicSize*24, atomicSize*8, BufferedImage.TYPE_INT_ARGB), image);
        return image;
    }
}
