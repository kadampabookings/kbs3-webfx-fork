package webfx.fxkit.javafxgraphics.mapper.spi.impl.peer.markers;


import javafx.beans.property.Property;

/**
 * @author Bruno Salmon
 */
public interface HasTitleProperty {

    Property<String> titleProperty();
    default void setTitle(String text) { titleProperty().setValue(text); }
    default String getTitle() { return titleProperty().getValue(); }

}