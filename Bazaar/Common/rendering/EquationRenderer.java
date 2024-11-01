package Common.rendering;

import Common.Equation;
import Common.ExchangeRule;

import java.awt.*;
import java.awt.image.BufferedImage;

public class EquationRenderer implements IRenderer<Equation> {

    private final int atomicSize;
    private final Color backgroundColor;
    public EquationRenderer(int atomicSize, Color backgroundColor) {
        this.atomicSize = atomicSize;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public BufferedImage render(Equation equation) {
        ImageManipulator imageManipulator = new ImageManipulator(backgroundColor);
        PebbleCollectionEquationRenderer pebblesRenderer = new PebbleCollectionEquationRenderer(atomicSize, backgroundColor);
        StringRenderer stringRenderer = new StringRenderer(atomicSize, Color.BLACK);

        ExchangeRule rule = equation.getRules().stream().findFirst().orElseThrow();
        BufferedImage image = pebblesRenderer.render(rule.getInputPebbles());
        image = imageManipulator.placeBeside(image, stringRenderer.render("="));
        image = imageManipulator.placeBeside(image, pebblesRenderer.render(rule.getOutputPebbles()));
        image = imageManipulator.placeInfront(new BufferedImage(atomicSize*9, atomicSize, BufferedImage.TYPE_INT_ARGB), image);
        return image;
    }
}
