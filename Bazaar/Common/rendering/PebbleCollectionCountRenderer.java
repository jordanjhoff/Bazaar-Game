package Common.rendering;

import Common.Pebble;
import Common.PebbleCollection;

import java.awt.*;
import java.util.List;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class PebbleCollectionCountRenderer implements IRenderer<PebbleCollection> {

    private final int atomicSize;
    private final Color backgroundColor;
    private final ImageManipulator imageManipulator;
    public PebbleCollectionCountRenderer(int atomicSize, Color backgroundColor) {
        this.atomicSize = atomicSize;
        this.backgroundColor = backgroundColor;
        this.imageManipulator = new ImageManipulator(backgroundColor);
    }

    @Override
    public BufferedImage render(PebbleCollection pebbleCollection) {
        List<Pebble> colors = new ArrayList<>(java.util.List.of(Pebble.values()));
        BufferedImage image = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        for (Pebble color : colors) {
            int num = pebbleCollection.numOfColor(color);
            image = imageManipulator.placeBeside(image, renderPebbleWithCount(color, num));
        }
        return image;
    }

    private BufferedImage renderPebbleWithCount(Pebble color, int count) {
        PebbleRenderer pebblesRenderer = new PebbleRenderer(atomicSize, backgroundColor);
        StringRenderer stringRenderer = new StringRenderer(atomicSize, Color.BLACK);
        BufferedImage pebbleWithCount = pebblesRenderer.render(color);
        BufferedImage countImage = stringRenderer.render("" + count);
        return imageManipulator.placeInfront(pebbleWithCount, countImage);
    }
}
