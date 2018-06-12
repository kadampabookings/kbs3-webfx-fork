package naga.fxdata.cell.renderer;

import emul.javafx.scene.image.ImageView;
import naga.util.Strings;
import naga.fx.util.ImageStore;

/**
 * @author Bruno Salmon
 */
public class ImageRenderer implements ValueRenderer {

    public static ImageRenderer SINGLETON = new ImageRenderer();

    private ImageRenderer() {}

    @Override
    public ImageView renderValue(Object value) {
        return value == null ? new ImageView() : ImageStore.createImageView(Strings.toString(value));
    }
}
