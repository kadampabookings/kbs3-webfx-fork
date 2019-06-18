package mongoose.backend.activities.bookings;

import javafx.beans.property.*;
import mongoose.client.activity.eventdependent.EventDependentGenericTablePresentationModel;
import mongoose.client.entities.util.filters.HasColumnsStringFilterProperty;
import mongoose.client.entities.util.filters.HasConditionStringFilterProperty;
import mongoose.client.entities.util.filters.HasGroupStringFilterProperty;
import mongoose.shared.entities.Document;
import webfx.fxkit.extra.displaydata.DisplayResult;
import webfx.fxkit.extra.displaydata.DisplaySelection;

/**
 * @author Bruno Salmon
 */
final class BookingsPresentationModel extends EventDependentGenericTablePresentationModel implements
        HasConditionStringFilterProperty,
        HasGroupStringFilterProperty,
        HasColumnsStringFilterProperty {

    private final Property<DisplaySelection> groupDisplaySelectionProperty = new SimpleObjectProperty<>();
    Property<DisplaySelection> groupDisplaySelectionProperty() { return groupDisplaySelectionProperty; }

    private final Property<DisplayResult> groupDisplayResultProperty = new SimpleObjectProperty<>();
    Property<DisplayResult> groupDisplayResultProperty() { return groupDisplayResultProperty; }

    private final StringProperty conditionStringFilterProperty = new SimpleStringProperty();
    public final StringProperty conditionStringFilterProperty() { return conditionStringFilterProperty; }

    private final StringProperty groupStringFilterProperty = new SimpleStringProperty();
    public final StringProperty groupStringFilterProperty() { return groupStringFilterProperty; }

    private final StringProperty columnsStringFilterProperty = new SimpleStringProperty();
    public final StringProperty columnsStringFilterProperty() { return columnsStringFilterProperty; }

    private final ObjectProperty<Document> selectedDocumentProperty = new SimpleObjectProperty<>();
    ObjectProperty<Document> selectedDocumentProperty() {
        return selectedDocumentProperty;
    }
    void setSelectedDocument(Document document) {
        selectedDocumentProperty.set(document);
    }

    private final ObjectProperty<Document> selectedGroupProperty = new SimpleObjectProperty<>();
    ObjectProperty<Document> selectedGroupProperty() {
        return selectedGroupProperty;
    }
    void setSelectedGroup(Document selectedGroup) {
        selectedGroupProperty.set(selectedGroup);
    }
    Document getSelectedGroup() {
        return selectedGroupProperty.get();
    }

    private final StringProperty selectedGroupConditionStringFilterProperty = new SimpleStringProperty();
    StringProperty selectedGroupConditionStringFilterProperty() { return selectedGroupConditionStringFilterProperty; }

}
