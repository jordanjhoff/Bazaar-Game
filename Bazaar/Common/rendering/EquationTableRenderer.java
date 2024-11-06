package Common.rendering;

import Common.EquationTable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class EquationTableRenderer implements IRenderer<EquationTable> {


    private final int atomicSize;
    private final Color backgroundColor;
    public EquationTableRenderer(int atomicSize, Color backgroundColor) {
        this.atomicSize = atomicSize;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public BufferedImage render(EquationTable equationTable) {
        BufferedImage background = new BufferedImage(10*atomicSize, 10*atomicSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = background.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, background.getWidth(), background.getHeight());
        ImageManipulator imageManipulator = new ImageManipulator(backgroundColor);
        g.dispose();
        return imageManipulator.placeInfront(background, withoutBackground(equationTable));
    }

    private BufferedImage withoutBackground(EquationTable equationTable) {
        ImageManipulator imageManipulator = new ImageManipulator(backgroundColor);
        EquationRenderer equationRenderer = new EquationRenderer(atomicSize, backgroundColor);

        BufferedImage image = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
        image = equationTable.equationSet().stream()
                .map(equationRenderer::render)
                .reduce(image, imageManipulator::placeBelow);
        return image;
    }
}
