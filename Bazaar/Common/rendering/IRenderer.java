package Common.rendering;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface IRenderer<T> {

    BufferedImage render(T t);

}
