package Common.rendering;

import Common.PebbleCollection;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PebbleCollectionEquationRenderer implements IRenderer<PebbleCollection> {

    private final int atomicSize;
    private final Color backgroundColor;
    public PebbleCollectionEquationRenderer(int atomicSize, Color backgroundColor) {
        this.atomicSize = atomicSize;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public BufferedImage render(PebbleCollection pebbleCollection) {
        ImageManipulator imageManipulator = new ImageManipulator(backgroundColor);
        PebbleRenderer pebbleRenderer = new PebbleRenderer(atomicSize, backgroundColor);

        BufferedImage image = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
        image = pebbleCollection.getPebblesAsList().stream()
                .map(pebbleRenderer::render)
                .reduce(image, imageManipulator::placeBeside);
        return image;
    }
}
