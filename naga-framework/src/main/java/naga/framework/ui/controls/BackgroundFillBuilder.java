package naga.framework.ui.controls;

import javafx.geometry.Insets;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;

/**
 * @author Bruno Salmon
 */
public class BackgroundFillBuilder {

    private PaintBuilder fillBuilder;
    private Paint fill;

    private double radius;
    private CornerRadii radii;

    private double inset;
    private Insets insets;

    public BackgroundFillBuilder setFillBuilder(PaintBuilder fillBuilder) {
        this.fillBuilder = fillBuilder;
        return this;
    }

    public BackgroundFillBuilder setFill(Paint fill) {
        this.fill = fill;
        return this;
    }

    public BackgroundFillBuilder setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    public BackgroundFillBuilder setRadii(CornerRadii radii) {
        this.radii = radii;
        return this;
    }

    public BackgroundFillBuilder setInset(double inset) {
        this.inset = inset;
        return this;
    }

    public BackgroundFillBuilder setInsets(Insets insets) {
        this.insets = insets;
        return this;
    }

    public BackgroundFill build() {
        if (fill == null && fillBuilder != null)
            fill = fillBuilder.build();
        if (radii == null && radius > 0)
            radii = new CornerRadii(radius);
        if (insets == null && inset > 0)
            insets = new Insets(inset);
        return new BackgroundFill(fill, radii, insets);
    }

}
