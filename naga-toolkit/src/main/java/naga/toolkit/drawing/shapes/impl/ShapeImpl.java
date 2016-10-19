package naga.toolkit.drawing.shapes.impl;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import naga.toolkit.drawing.paint.Color;
import naga.toolkit.drawing.paint.Paint;
import naga.toolkit.drawing.shapes.Shape;

/**
 * @author Bruno Salmon
 */
public class ShapeImpl implements Shape {

    private final Property<Paint> fillProperty = new SimpleObjectProperty<>(Color.TRANSPARENT);
    @Override
    public Property<Paint> fillProperty() {
        return fillProperty;
    }
}
