package naga.toolkit.fx.spi.viewer.base;

import javafx.beans.value.ObservableValue;
import naga.toolkit.fx.scene.shape.Circle;
import naga.toolkit.fx.spi.DrawingRequester;
import naga.toolkit.fx.spi.viewer.CircleViewer;

/**
 * @author Bruno Salmon
 */
public class CircleViewerBase
        extends ShapeViewerBase<Circle, CircleViewerBase, CircleViewerMixin>
        implements CircleViewer {

    @Override
    public void bind(Circle c, DrawingRequester drawingRequester) {
        super.bind(c, drawingRequester);
        requestUpdateOnPropertiesChange(drawingRequester
                , c.centerXProperty()
                , c.centerYProperty()
                , c.radiusProperty()
        );
    }

    @Override
    public boolean updateProperty(ObservableValue changedProperty) {
        Circle c = node;
        return super.updateProperty(changedProperty)
                || updateProperty(c.centerXProperty(), changedProperty, mixin::updateCenterX)
                || updateProperty(c.centerYProperty(), changedProperty, mixin::updateCenterY)
                || updateProperty(c.radiusProperty(), changedProperty, mixin::updateRadius)
                ;
    }
}
