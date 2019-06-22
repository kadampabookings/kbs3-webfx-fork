package mongoose.backend.controls.masterslave.group;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import mongoose.backend.controls.masterslave.MasterSlaveView;
import mongoose.backend.controls.masterslave.MasterTableView;
import mongoose.backend.controls.masterslave.SlaveTableView;
import mongoose.backend.controls.masterslave.UiBuilder;
import mongoose.client.presentationmodel.*;
import webfx.framework.client.ui.controls.ControlFactoryMixin;
import webfx.framework.client.ui.filter.StringFilter;
import webfx.fxkit.util.properties.Properties;
import webfx.platform.shared.util.Strings;

import java.util.function.Function;

public class GroupMasterSlaveView extends MasterSlaveView {

    public GroupMasterSlaveView() {
        this((Node) null, null, null);
    }

    public GroupMasterSlaveView(UiBuilder groupView, UiBuilder masterView, UiBuilder slaveView) {
        this(groupView.buildUi(), masterView.buildUi(), slaveView.buildUi());
    }

    public GroupMasterSlaveView(Node groupView, Node masterView, Node slaveView) {
        super(null, null);
        setGroupView(groupView);
        setMasterView(masterView);
        setSlaveView(slaveView);
        setMasterVisible(true);
    }

    public static <PM extends HasGroupDisplayResultProperty & HasMasterDisplayResultProperty & HasSlaveDisplayResultProperty & HasSelectedMasterProperty>
        GroupMasterSlaveView createAndBind(PM pm, ControlFactoryMixin mixin) {
        return createAndBind(
                GroupView.createAndBind(pm),
                MasterTableView.createAndBind(pm, mixin).buildUi(),
                SlaveTableView.createAndBind(pm).buildUi(),
                pm);
    }

    public static GroupMasterSlaveView createAndBind(GroupView groupView, UiBuilder masterView, UiBuilder slaveView, HasSelectedMasterProperty pm) {
        return createAndBind(groupView, masterView.buildUi(), slaveView.buildUi(), pm);
    }

    public static GroupMasterSlaveView createAndBind(GroupView groupView, Node masterView, Node slaveView, HasSelectedMasterProperty pm) {
        return createAndBind(groupView, masterView, slaveView, pm.selectedMasterProperty(), pm instanceof HasSlaveVisibilityCondition ? selectedMaster -> ((HasSlaveVisibilityCondition) pm).isSlaveVisible(selectedMaster) : null);
    }

        public static <E> GroupMasterSlaveView createAndBind(GroupView groupView, Node masterView, Node slaveView, ObjectProperty<E> masterSelectedEntityProperty, Function<E, Boolean> additionalSlaveVisibilityCondition) {
        GroupMasterSlaveView groupMasterSlaveView = new GroupMasterSlaveView(groupView.buildUi(), masterView, slaveView);
        groupMasterSlaveView.doDataBinding(groupView, masterSelectedEntityProperty, additionalSlaveVisibilityCondition);
        return groupMasterSlaveView;
    }

    public <E> void doDataBinding(GroupView groupView, ObjectProperty<E> masterSelectedEntityProperty, Function<E, Boolean> additionalSlaveVisibilityCondition) {
        groupVisibleProperty() .bind(Properties.compute(groupView.groupStringFilterProperty(), s -> s != null && Strings.isNotEmpty(new StringFilter(s).getGroupBy())));
        masterVisibleProperty().bind(Properties.combine(groupVisibleProperty(),  groupView.selectedGroupProperty(), (groupVisible, selectedGroup) -> !groupVisible || selectedGroup != null));
        slaveVisibleProperty() .bind(Properties.combine(masterVisibleProperty(), masterSelectedEntityProperty, (masterVisible, selectedEntity) -> masterVisible && selectedEntity != null && (additionalSlaveVisibilityCondition == null || additionalSlaveVisibilityCondition.apply(selectedEntity))));
    }

    private final ObjectProperty<Node> groupViewProperty = new SimpleObjectProperty<Node>() { // GWT doesn't accept <>
        @Override
        protected void invalidated() {
            updateView();
        }
    };

    public ObjectProperty<Node> groupViewProperty() {
        return groupViewProperty;
    }

    public void setGroupView(Node groupView) {
        groupViewProperty().set(groupView);
    }

    public Node getGroupView() {
        return groupViewProperty().get();
    }

    @Override
    protected void updateView() {
        updateSplitPane(isGroupVisible() ? getGroupView() : null, isMasterVisible() ? getMasterView() : null, isSlaveVisible() ? getSlaveView() : null);
    }

    private final BooleanProperty groupVisibleProperty = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            updateView();
        }
    };

    public BooleanProperty groupVisibleProperty() {
        return groupVisibleProperty;
    }

    public boolean isGroupVisible() {
        return groupVisibleProperty().get();
    }

    public void setGroupVisible(boolean groupVisible) {
        groupVisibleProperty().setValue(groupVisible);
    }

    private final BooleanProperty masterVisibleProperty = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            updateView();
        }
    };

    public BooleanProperty masterVisibleProperty() {
        return masterVisibleProperty;
    }

    public boolean isMasterVisible() {
        return masterVisibleProperty().get();
    }

    public void setMasterVisible(boolean masterVisible) {
        masterVisibleProperty().setValue(masterVisible);
    }

}
