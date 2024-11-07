package Common.rendering;

import Common.Card;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class CardRenderer implements IRenderer<Card> {

    private final int atomicSize;
    private final Color backgroundColor;
    public CardRenderer(int atomicSize, Color backgroundColor) {
        this.atomicSize = atomicSize;
        this.backgroundColor = backgroundColor;
    }
    @Override
    public BufferedImage render(Card card) {
        PebbleCollectionCardRenderer renderer = new PebbleCollectionCardRenderer(atomicSize, backgroundColor);
        ImageManipulator manipulator = new ImageManipulator(backgroundColor);
        BufferedImage pebbles = renderer.render(card.pebbles());
        BufferedImage border = renderCardBorder(5, 1, Color.darkGray);
        BufferedImage cardImage = manipulator.placeBelow(border, pebbles);
        cardImage = manipulator.placeBelow(cardImage, border);
        cardImage = manipulator.placeInfront(cardImage,renderFace(card.hasFace()));
        BufferedImage cardBorder = renderCardBorder(6, 8, Color.black);
        return manipulator.placeInfront(cardBorder, cardImage);
    }

    private BufferedImage renderCardBorder(int width, int height, Color color) {
        BufferedImage border = new BufferedImage(atomicSize*width, atomicSize*height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = border.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, atomicSize*width, atomicSize*height);
        return border;
    }

    private BufferedImage renderFace(boolean hasFace) {
        if (!hasFace) {
            return new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        }
        else {
            try {
                InputStream input  = getClass().getClassLoader().getResourceAsStream("happyFace.jpg");
                BufferedImage face = ImageIO.read(input);
                Image scaledImage = face.getScaledInstance(atomicSize, atomicSize, Image.SCALE_SMOOTH);
                BufferedImage bufferedScaledImage = new BufferedImage(atomicSize, atomicSize, face.getType());
                Graphics2D g2d = bufferedScaledImage.createGraphics();
                g2d.drawImage(scaledImage, 0, 0, atomicSize, atomicSize, null);
                g2d.dispose();
                return bufferedScaledImage;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
