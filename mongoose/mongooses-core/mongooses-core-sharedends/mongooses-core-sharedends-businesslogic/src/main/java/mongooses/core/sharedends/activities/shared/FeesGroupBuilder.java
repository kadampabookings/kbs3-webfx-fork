package mongooses.core.sharedends.activities.shared;

import mongooses.core.sharedends.businesslogic.preselection.OptionsPreselection;
import mongooses.core.sharedends.businesslogic.preselection.OptionsPreselectionBuilder;
import mongooses.core.shared.domainmodel.time.DateTimeRange;
import mongooses.core.shared.entities.DateInfo;
import mongooses.core.shared.entities.Event;
import mongooses.core.shared.entities.Label;
import mongooses.core.shared.entities.Option;
import mongooses.core.sharedends.aggregates.EventAggregate;
import webfx.platforms.core.util.collection.Collections;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class FeesGroupBuilder {

    private final EventAggregate eventAggregate;
    private DateInfo dateInfo;
    private Object id;
    private Label label;
    private String i18nKey;
    private Label feesBottomLabel;
    private Label feesPopupLabel;
    private boolean forceSoldout;

    private Iterable<Option> defaultOptions;
    private Iterable<Option> accommodationOptions;
    private boolean addNoAccommodationOption;

    public FeesGroupBuilder(EventAggregate eventAggregate) {
        this.eventAggregate = eventAggregate;
    }

    public FeesGroupBuilder setDateInfo(DateInfo dateInfo) {
        this.dateInfo = dateInfo;
        if (dateInfo != null) {
            id = dateInfo.getId();
            label = dateInfo.getLabel();
            feesBottomLabel = dateInfo.getFeesBottomLabel();
            feesPopupLabel = dateInfo.getFeesPopupLabel();
            forceSoldout = dateInfo.isForceSoldout();
        }
        return this;
    }

    public FeesGroupBuilder setLabel(Label label) {
        this.label = label;
        return this;
    }

    public FeesGroupBuilder setI18nKey(String i18nKey) {
        this.i18nKey = i18nKey;
        return this;
    }

    private Event getEvent() {
        return eventAggregate.getEvent();
    }

    public FeesGroupBuilder setDefaultOptions(Iterable<Option> defaultOptions) {
        this.defaultOptions = defaultOptions;
        return this;
    }

    public FeesGroupBuilder setAccommodationOptions(Iterable<Option> accommodationOptions) {
        this.accommodationOptions = accommodationOptions;
        return this;
    }

    public FeesGroupBuilder setAddNoAccommodationOption(boolean addNoAccommodationOption) {
        this.addNoAccommodationOption = addNoAccommodationOption;
        return this;
    }

    public FeesGroup build() {
        DateTimeRange dateTimeRange = dateInfo == null ? null : dateInfo.getParsedDateTimeRange();
        if (dateTimeRange == null)
            dateTimeRange = getEvent().getParsedDateTimeRange();
        List<OptionsPreselection> optionsPreselections = new ArrayList<>();
        if (accommodationOptions != null)
            for (Option accommodationOption : accommodationOptions)
                addOptionsPreselection(accommodationOption, dateTimeRange, optionsPreselections);
        // Adding Course or No accommodation option
        if (optionsPreselections.isEmpty() || // Ex: a day course or a section with no accommodation (like Food for Thought)
                addNoAccommodationOption)     // If there are accommodation options, checking we can offer no accommodation (not the case for Refresh and Revive Overnighter)
            addOptionsPreselection(null, dateTimeRange, optionsPreselections);

        return new FeesGroup(getEvent(), id, label, i18nKey, feesBottomLabel, feesPopupLabel, forceSoldout, Collections.toArray(optionsPreselections, OptionsPreselection[]::new));
    }

    private void addOptionsPreselection(Option accommodationOption, DateTimeRange dateTimeRange, List<OptionsPreselection> optionsPreselections) {
        Collections.addIfNotNull(new OptionsPreselectionBuilder(eventAggregate, dateTimeRange)
                .addDefaultOptions(defaultOptions)
                .addAccommodationOption(accommodationOption)
                .build(), optionsPreselections);
    }
}