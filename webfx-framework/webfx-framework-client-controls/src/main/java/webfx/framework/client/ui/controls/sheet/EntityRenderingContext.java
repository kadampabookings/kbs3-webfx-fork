package webfx.framework.client.ui.controls.sheet;

import javafx.scene.layout.Pane;
import webfx.framework.shared.orm.domainmodel.DomainClass;
import webfx.framework.shared.orm.entity.EntityStore;
import webfx.framework.client.ui.filter.ExpressionColumn;
import webfx.framework.client.ui.controls.button.ButtonFactoryMixin;
import webfx.fxkit.extra.cell.renderer.ValueRenderingContext;
import webfx.platform.shared.util.function.Callable;

/**
 * @author Bruno Salmon
 */
final class EntityRenderingContext extends ValueRenderingContext {

    private final ExpressionColumn foreignFieldColumn;
    private final DomainClass entityClass;
    private final Callable<EntityStore> entityStoreGetter;
    private final Callable<Pane> parentGetter;
    private final ButtonFactoryMixin buttonFactory;

    EntityRenderingContext(boolean readOnly, Object labelKey, Object placeholderKey, ExpressionColumn foreignFieldColumn, Callable<EntityStore> entityStoreGetter, Callable<Pane> parentGetter, ButtonFactoryMixin buttonFactory) {
        this(readOnly, labelKey, placeholderKey, foreignFieldColumn, foreignFieldColumn.getForeignClass(), entityStoreGetter, parentGetter, buttonFactory);
    }

    EntityRenderingContext(boolean readOnly, Object labelKey, Object placeholderKey, DomainClass entityClass, Callable<EntityStore> entityStoreGetter, Callable<Pane> parentGetter, ButtonFactoryMixin buttonFactory) {
        this(readOnly, labelKey, placeholderKey, null, entityClass, entityStoreGetter, parentGetter, buttonFactory);
    }

    private EntityRenderingContext(boolean readOnly, Object labelKey, Object placeholderKey, ExpressionColumn foreignFieldColumn, DomainClass entityClass, Callable<EntityStore> entityStoreGetter, Callable<Pane> parentGetter, ButtonFactoryMixin buttonFactory) {
        super(readOnly, labelKey, placeholderKey, null);
        this.foreignFieldColumn = foreignFieldColumn;
        this.entityClass = entityClass;
        this.entityStoreGetter = entityStoreGetter;
        this.parentGetter = parentGetter;
        this.buttonFactory = buttonFactory;
    }

    ExpressionColumn getForeignFieldColumn() {
        return foreignFieldColumn;
    }

    DomainClass getEntityClass() {
        return entityClass;
    }

    Callable<EntityStore> getEntityStoreGetter() {
        return entityStoreGetter;
    }

    Callable<Pane> getParentGetter() {
        return parentGetter;
    }

    EntityStore getEntityStore() {
        return entityStoreGetter.call();
    }

    ButtonFactoryMixin getButtonFactory() {
        return buttonFactory;
    }

}
