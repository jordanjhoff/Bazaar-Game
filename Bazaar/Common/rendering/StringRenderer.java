package Common.rendering;

import java.awt.*;
import java.awt.image.BufferedImage;


public class StringRenderer implements IRenderer<String>{


    private final int atomicSize;
    private final Color stringColor;
    public StringRenderer(int atomicSize, Color stringColor) {
        this.atomicSize = atomicSize;
        this.stringColor = stringColor;
    }

    @Override
    public BufferedImage render(String text) {
        BufferedImage image = new BufferedImage(atomicSize, atomicSize, BufferedImage.TYPE_INT_ARGB);
        return new ImageManipulator(new Color(0, 0, 0, 0)).placeInfront(image, justTheString(text));
    }

    private BufferedImage justTheString(String text) {
        int size = (int)(atomicSize*0.8);
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(stringColor);
        int fontSize = size;
        Font font;
        FontMetrics metrics;
        do {
            font = new Font("Arial", Font.PLAIN, fontSize);
            g2d.setFont(font);
            metrics = g2d.getFontMetrics();
            fontSize--;
        } while (metrics.stringWidth(text) > size || metrics.getHeight() > size);
        int x = (size - metrics.stringWidth(text)) / 2;
        int y = ((size - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(text, x, y);
        g2d.dispose();
        return image;
    }
}
