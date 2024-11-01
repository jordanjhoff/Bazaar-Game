package Common.rendering;

import Common.Pebble;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PebbleRenderer implements IRenderer<Pebble> {

    private final int atomicSize;
    private final Color backgroundColor;
    public PebbleRenderer(int atomicSize, Color backgroundColor) {
        this.atomicSize = atomicSize;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public BufferedImage render(Pebble pebbleColor) {
        BufferedImage canvas = new BufferedImage(atomicSize, atomicSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, atomicSize, atomicSize);
        g2d.setColor(pebbleColor.getColor());
        double circleSize = atomicSize * 0.8;
        double offsetSize = atomicSize * 0.1;
        g2d.fillOval((int)offsetSize, (int)offsetSize, (int)circleSize, (int)circleSize);
        g2d.dispose();
        return canvas;
    }
}
