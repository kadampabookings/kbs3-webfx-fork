package naga.core.ui.rx;

import javafx.beans.property.Property;
import naga.core.json.Json;
import naga.core.json.JsonArray;
import naga.core.json.JsonObject;
import naga.core.ui.displayresultset.ExpressionColumn;
import naga.core.ui.displayresultset.DisplayResultSet;
import naga.core.ui.displayresultset.EntityListToDisplayResultSetGenerator;
import naga.core.ui.displayselection.DisplaySelection;
import naga.core.orm.domainmodel.DataSourceModel;
import naga.core.orm.entity.Entity;
import naga.core.orm.entity.EntityList;
import naga.core.orm.entity.EntityStore;
import naga.core.orm.expression.Expression;
import naga.core.orm.expression.term.ExpressionArray;
import naga.core.orm.expressionsqlcompiler.sql.SqlCompiled;
import naga.core.orm.mapping.QueryResultSetToEntityListGenerator;
import naga.core.orm.stringfilter.StringFilter;
import naga.core.orm.stringfilter.StringFilterBuilder;
import naga.core.queryservice.QueryArgument;
import naga.core.spi.platform.Platform;
import naga.core.spi.toolkit.Toolkit;
import naga.core.util.function.Converter;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public class RxFilter {

    private final List<Observable<StringFilter>> stringFilterObservables = new ArrayList<>();
    private Object domainClassId;
    private ExpressionColumn[] expressionColumns;
    private DataSourceModel dataSourceModel;
    private EntityStore store;
    private Object listId;
    private Property<DisplaySelection> displaySelectionProperty;
    private boolean selectFirstRowOnFirstDisplay;

    public RxFilter() {
    }

    public RxFilter(Object jsonOrClass) {
        combine(new StringFilterBuilder(jsonOrClass));
    }

    public RxFilter setDataSourceModel(DataSourceModel dataSourceModel) {
        this.dataSourceModel = dataSourceModel;
        return this;
    }

    public RxFilter setStore(EntityStore store) {
        this.store = store;
        return this;
    }

    public RxFilter setListId(Object listId) {
        this.listId = listId;
        return this;
    }

    public Property<DisplaySelection> getDisplaySelectionProperty() {
        return displaySelectionProperty;
    }

    public RxFilter setDisplaySelectionProperty(Property<DisplaySelection> displaySelectionProperty) {
        this.displaySelectionProperty = displaySelectionProperty;
        return this;
    }

    public RxFilter selectFirstRowOnFirstDisplay() {
        this.selectFirstRowOnFirstDisplay = true;
        return this;
    }

    public Entity getSelectedEntity() {
        return getSelectedEntity(displaySelectionProperty.getValue());
    }

    public Entity getSelectedEntity(DisplaySelection selection) {
        Entity selectedEntity = null;
        int selectedRow = selection == null ? -1 : selection.getSelectedRow();
        if (selectedRow >= 0)
            selectedEntity = getCurrentEntityList().get(selectedRow);
        return selectedEntity;
    }

    public EntityList getCurrentEntityList() {
        return store.getEntityList(listId);
    }

    public RxFilter setExpressionColumns(String jsonArrayDisplayColumns) {
        return setExpressionColumns(Json.parseArray(jsonArrayDisplayColumns));
    }

    public RxFilter setExpressionColumns(JsonArray array) {
        int n = array.size();
        this.expressionColumns = new ExpressionColumn[n];
        for (int i = 0; i < n; i++)
            this.expressionColumns[i] = new ExpressionColumn(array.getObject(i));
        return this;
    }

    public RxFilter setExpressionColumns(ExpressionColumn... expressionColumns) {
        this.expressionColumns = expressionColumns;
        return this;
    }

    public RxFilter combine(String json) {
        return combine(new StringFilter(json));
    }

    public RxFilter combine(JsonObject json) {
        return combine(new StringFilter(json));
    }

    public RxFilter combine(StringFilterBuilder stringFilterBuilder) {
        return combine(stringFilterBuilder.build());
    }

    public RxFilter combine(StringFilter stringFilter) {
        if (domainClassId == null)
            domainClassId = stringFilter.getDomainClassId();
        return combine(Observable.just(stringFilter));
    }

    public RxFilter combine(Observable<StringFilter> stringFilterObservable) {
        stringFilterObservables.add(stringFilterObservable);
        return this;
    }

    public <T> RxFilter combine(Property<T> property, Converter<T, String> toJsonFilterConverter) {
        return combine(RxUi.observe(property)
                .map(t -> {
                    String json = toJsonFilterConverter.convert(t);
                    return json == null ? null : new StringFilter(json);
                }));
    }

    public RxFilter combine(Property<Boolean> ifProperty, StringFilterBuilder stringFilterBuilder) {
        return combine(ifProperty, stringFilterBuilder.build());
    }

    public RxFilter combine(Property<Boolean> ifProperty, String json) {
        return combine(ifProperty, new StringFilter(json));
    }

    public RxFilter combine(Property<Boolean> ifProperty, StringFilter stringFilter) {
        return combine(RxUi.observeIf(Observable.just(stringFilter), ifProperty));
    }

    private void checkFields() {
        if (store == null)
            store = new EntityStore();
        if (listId == null)
            listId = "default";
        List<Expression> displayPersistentTerms = new ArrayList<>();
        for (ExpressionColumn expressionColumn : expressionColumns) {
            expressionColumn.parseIfNecessary(dataSourceModel.getDomainModel(), domainClassId);
            expressionColumn.getExpression().collectPersistentTerms(displayPersistentTerms);
        }
        if (!displayPersistentTerms.isEmpty())
            combine(new StringFilterBuilder().setFields(new ExpressionArray<>(displayPersistentTerms).toString()));
    }

    public RxFilter displayResultSetInto(Property<DisplayResultSet> displayResultSetProperty) {
        checkFields();
        // Emitting an initial empty display result (no rows but columns) to initialize the component (probably a table) with the columns before calling the server
        if (displayResultSetProperty.getValue() == null && expressionColumns != null)
            Toolkit.get().scheduler().runInUiThread(() -> displayResultSetProperty.setValue(emptyDisplayResultSet()));
        Observable<DisplayResultSet> displayResultObservable = Observable
                .combineLatest(stringFilterObservables, StringFilterBuilder::mergeStringFilters)
                .distinctUntilChanged()
                .switchMap(stringFilter -> {
                    if ("false".equals(stringFilter.getWhere()))
                        return Observable.just(emptyDisplayResultSet());
                    SqlCompiled sqlCompiled = dataSourceModel.getDomainModel().compileSelect(stringFilter.toStringSelect());
                    Platform.log(sqlCompiled.getSql());
                    return RxFuture.from(Platform.query().read(new QueryArgument(sqlCompiled.getSql(), dataSourceModel.getId())))
                            .map(sqlReadResult -> QueryResultSetToEntityListGenerator.createEntityList(sqlReadResult, sqlCompiled.getQueryMapping(), store, listId))
                            .map(entities -> {
                                if (selectFirstRowOnFirstDisplay && entities.size() > 0) {
                                    selectFirstRowOnFirstDisplay = false;
                                    displaySelectionProperty.setValue(new DisplaySelection(new int[]{0})); // Temporary implementation
                                }
                                return EntityListToDisplayResultSetGenerator.createDisplayResultSet(entities, expressionColumns);
                            });
                });
        RxUi.displayObservable(displayResultObservable, displayResultSetProperty);
        return this;
    }

    DisplayResultSet emptyDisplayResultSet() {
        return EntityListToDisplayResultSetGenerator.createDisplayResultSet(new EntityList(listId, store), expressionColumns);
    }

}

