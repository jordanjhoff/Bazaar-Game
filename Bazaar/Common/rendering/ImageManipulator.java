package Common.rendering;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageManipulator {

    private final Color backgroundColor;
    public ImageManipulator(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    public BufferedImage placeBeside(BufferedImage left, BufferedImage right) {
        int height = Math.max(left.getHeight(), right.getHeight());
        int width = left.getWidth() + right.getWidth();
        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int canvasCenterY = height / 2;
        int leftY = canvasCenterY - (left.getHeight() / 2);
        int rightY = canvasCenterY - (right.getHeight() / 2);

        g.drawImage(left, 0, leftY, null);
        g.drawImage(right, left.getWidth(), rightY, null);
        g.dispose();
        return canvas;
    }

    public BufferedImage placeBelow(BufferedImage top, BufferedImage bottom) {
        int height = top.getHeight() + bottom.getHeight();
        int width = Math.max(top.getWidth(), bottom.getWidth());
        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int canvasCenterX = width / 2;
        int topX = canvasCenterX - (top.getWidth() / 2);
        int bottomX = canvasCenterX - (bottom.getWidth() / 2);

        g.drawImage(top, topX, 0, null);
        g.drawImage(bottom, bottomX, top.getHeight(), null);
        g.dispose();
        return canvas;

    }

    public BufferedImage placeInfront(BufferedImage behind, BufferedImage front) {
        int width = Math.max(behind.getWidth(), front.getWidth());
        int height = Math.max(behind.getHeight(), front.getHeight());
        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();

        int canvasCenterX = width / 2;
        int canvasCenterY = height / 2;

        int behindX = canvasCenterX - (behind.getWidth() / 2);
        int behindY = canvasCenterY - (behind.getHeight() / 2);

        g.drawImage(behind, behindX, behindY, null);


        int frontX = canvasCenterX - (front.getWidth() / 2);
        int frontY = canvasCenterY - (front.getHeight() / 2);
        g.drawImage(front, frontX, frontY, null);

        g.dispose();

        return canvas;

    }
    
    
}
