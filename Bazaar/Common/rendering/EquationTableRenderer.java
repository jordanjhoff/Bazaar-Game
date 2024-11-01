package Common.rendering;

import Common.EquationTable;

import java.awt.*;
import java.awt.image.BufferedImage;

public class EquationTableRenderer implements IRenderer<EquationTable> {


    private final int atomicSize;
    private final Color backgroundColor;
    public EquationTableRenderer(int atomicSize, Color backgroundColor) {
        this.atomicSize = atomicSize;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public BufferedImage render(EquationTable equationTable) {
        ImageManipulator imageManipulator = new ImageManipulator(backgroundColor);
        EquationRenderer equationRenderer = new EquationRenderer(atomicSize, backgroundColor);

        BufferedImage image = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
        image = equationTable.equationSet().stream()
                .map(equationRenderer::render)
                .reduce(image, imageManipulator::placeBelow);
        return image;
    }
}
