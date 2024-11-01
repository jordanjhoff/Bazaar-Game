package Common.rendering;

import Common.Pebble;
import Common.PebbleCollection;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferDouble;
import java.util.List;

public class PebbleCollectionCardRenderer implements IRenderer<PebbleCollection> {

    private final int atomicSize;
    private final Color backgroundColor;
    public PebbleCollectionCardRenderer(int atomicSize, Color backgroundColor) {
        this.atomicSize = atomicSize;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public BufferedImage render(PebbleCollection pebbleCollection) {
        ImageManipulator imageManipulator = new ImageManipulator(backgroundColor);
        PebbleRenderer pebbleRenderer = new PebbleRenderer(atomicSize, backgroundColor);

        List<Pebble> pebbles = pebbleCollection.getPebblesAsList();
        BufferedImage image = new BufferedImage(atomicSize*3,atomicSize*3, BufferedImage.TYPE_INT_ARGB);
        image = imageManipulator.placeBelow(pebbleRenderer.render(pebbles.getFirst()), image);
        image = imageManipulator.placeBeside(pebbleRenderer.render(pebbles.get(4)), image);
        image = imageManipulator.placeBeside(image, pebbleRenderer.render(pebbles.get(1)));
        image = imageManipulator.placeBelow(image, bottomRow(pebbles, imageManipulator, pebbleRenderer));
        return image;
    }

    private BufferedImage bottomRow(List<Pebble> pebbles, ImageManipulator imageManipulator, PebbleRenderer pebbleRenderer) {
        BufferedImage image = pebbleRenderer.render(pebbles.get(3));
        image = imageManipulator.placeBeside(image, new BufferedImage(atomicSize, atomicSize, BufferedImage.TYPE_INT_ARGB));
        image = imageManipulator.placeBeside(image, pebbleRenderer.render(pebbles.get(2)));
        return image;
    }
}
