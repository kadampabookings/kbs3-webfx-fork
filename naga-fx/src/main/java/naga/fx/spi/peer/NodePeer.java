package naga.fx.spi.peer;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import naga.fx.scene.SceneRequester;

/**
 * @author Bruno Salmon
 */
public interface NodePeer<N extends Node> {

    void bind(N node, SceneRequester sceneRequester);

    void unbind();

    boolean updateProperty(ObservableValue changedProperty);

    boolean updateList(ObservableList list, ListChangeListener.Change change);

    void requestFocus();

    default boolean isTreeVisible() {
        return true;
    }
}
