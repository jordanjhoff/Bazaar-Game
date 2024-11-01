package Common.rendering;

import Common.PlayerInformation;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PlayerInfoRenderer implements IRenderer<PlayerInformation> {

    private final int atomicSize;
    private final Color backgroundColor;
    public PlayerInfoRenderer(int atomicSize, Color backgroundColor) {
        this.atomicSize = atomicSize;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public BufferedImage render(PlayerInformation playerInformation) {
        ImageManipulator imageManipulator = new ImageManipulator(backgroundColor);
        PebbleCollectionCountRenderer pebbleCollectionCountRenderer = new PebbleCollectionCountRenderer(atomicSize, backgroundColor);
        StringRenderer stringRenderer = new StringRenderer(atomicSize*2, Color.BLACK);
        String name;
        if (playerInformation.name().isPresent()) {
            name = playerInformation.name().get();
        }
        else {
            name = "";
        }
        BufferedImage image = stringRenderer.render(name);
        image = imageManipulator.placeBeside(image, stringRenderer.render("Score: " + playerInformation.score()));
        image = imageManipulator.placeBelow(image, pebbleCollectionCountRenderer.render(playerInformation.wallet()));
        image = imageManipulator.placeInfront(renderBorder(), image);
        return image;
    }

    private BufferedImage renderBorder() {
        BufferedImage border = new BufferedImage(atomicSize*6, atomicSize*4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = border.createGraphics();
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, border.getWidth(), border.getHeight());
        g2d.dispose();
        return border;
    }
}
