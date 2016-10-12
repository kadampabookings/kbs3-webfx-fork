package mongoose.activities.frontend.event.fees;

import mongoose.activities.frontend.event.booking.BookingProcessActivity;
import mongoose.activities.shared.logic.preselection.OptionsPreselection;
import mongoose.entities.DateInfo;
import mongoose.entities.Option;
import mongoose.entities.Person;
import mongoose.services.PersonService;
import naga.commons.util.Booleans;
import naga.commons.util.async.Future;
import naga.commons.util.collection.Collections;
import naga.commons.util.tuples.Pair;
import naga.framework.orm.entity.EntityList;
import naga.framework.ui.i18n.I18n;
import naga.framework.ui.rx.RxScheduler;
import naga.framework.ui.rx.RxUi;
import naga.platform.json.Json;
import naga.platform.json.spi.JsonObject;
import naga.platform.json.spi.WritableJsonObject;
import naga.platform.spi.Platform;
import naga.toolkit.display.DisplayColumn;
import naga.toolkit.display.DisplayResultSet;
import naga.toolkit.display.DisplayResultSetBuilder;
import naga.toolkit.spi.Toolkit;
import naga.toolkit.spi.events.ActionEvent;
import naga.toolkit.spi.nodes.GuiNode;
import naga.toolkit.spi.nodes.controls.RadioButton;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public class FeesActivity extends BookingProcessActivity<FeesViewModel, FeesPresentationModel> {

    public FeesActivity() {
        super(FeesPresentationModel::new, "options");
        registerViewBuilder(getClass(), new FeesViewModelBuilder());
    }

    @Override
    protected void bindViewModelWithPresentationModel(FeesViewModel vm, FeesPresentationModel pm) {
        super.bindViewModelWithPresentationModel(vm, pm);
        I18n i18n = getI18n();
        i18n.translateText(setImage(vm.getProgramButton(), "{url: 'images/calendar.svg', width: 16, height: 16}"), "Program")
                .actionEventObservable().subscribe(this::onProgramButtonPressed);
        i18n.translateText(setImage(vm.getTermsButton(), "{url: 'images/certificate.svg', width: 16, height: 16}"), "TermsAndConditions")
                .actionEventObservable().subscribe(this::onTermsButtonPressed);
        vm.getDateInfoCollator().displayResultSetProperty().bind(pm.dateInfoDisplayResultSetProperty());
    }

    private void onProgramButtonPressed(ActionEvent actionEvent) {
        goToNextBookingProcessPage("program");
    }

    private void onTermsButtonPressed(ActionEvent actionEvent) {
        goToNextBookingProcessPage("terms");
    }

    protected void bindPresentationModelWithLogic(FeesPresentationModel pm) {
        // Load and display fees groups now
        loadAndDisplayFeesGroups(pm);
        // But also on event change
        pm.eventIdProperty().addListener((observable, oldValue, newValue) -> loadAndDisplayFeesGroups(pm));
    }

    private void loadAndDisplayFeesGroups(FeesPresentationModel pm) {
        onFeesGroup().setHandler(async -> {
            if (async.failed())
                Platform.log(async.cause());
            else {
                FeesGroup[] feesGroups = async.result();
                I18n i18n = getI18n();
                Observable.combineLatest(
                        RxUi.observe(i18n.dictionaryProperty()),
                        RxUi.observe(activeProperty()),
                        (dictionary, active) -> active)
                        .filter(active -> active)
                        .observeOn(RxScheduler.UI_SCHEDULER)
                        .subscribe(active -> displayFeesGroups(feesGroups, pm));
                onEventAvailabilities().setHandler(ar -> {
                    if (ar.succeeded())
                        displayFeesGroups(feesGroups, pm);
                });
            }
        });
    }

    private Future<FeesGroup[]> onFeesGroup() {
        return onEventOptions().map(this::createFeesGroups);
    }

    private FeesGroup[] createFeesGroups() {
        List<FeesGroup> feesGroups = new ArrayList<>();
        EntityList<DateInfo> dateInfos = getEventDateInfos();
        List<Option> defaultOptions = selectDefaultOptions();
        List<Option> accommodationOptions = selectOptions(o -> o.isConcrete() && o.isAccommodation());
        if (dateInfos.isEmpty())
            populateFeesGroups(null, defaultOptions, accommodationOptions, feesGroups);
        else
            for (DateInfo dateInfo : dateInfos)
                populateFeesGroups(dateInfo, defaultOptions, accommodationOptions, feesGroups);
        return Collections.toArray(feesGroups, FeesGroup[]::new);
    }

    private void populateFeesGroups(DateInfo dateInfo, List<Option> defaultOptions, List<Option> accommodationOptions, List<FeesGroup> feesGroups) {
        feesGroups.add(createFeesGroup(dateInfo, defaultOptions, accommodationOptions));
    }

    private FeesGroup createFeesGroup(DateInfo dateInfo, List<Option> defaultOptions, List<Option> accommodationOptions) {
        return new FeesGroupBuilder()
                .setDateInfo(dateInfo)
                .setEvent(getEvent())
                .setDefaultOptions(defaultOptions)
                .setAccommodationOptions(accommodationOptions)
                .build();
    }

    private void displayFeesGroups(FeesGroup[] feesGroups, FeesPresentationModel pm) {
        DisplayResultSetBuilder rsb = DisplayResultSetBuilder.create(feesGroups.length, new DisplayColumn[]{
                DisplayColumn.create(value -> renderFeesGroupHeader((Pair<JsonObject, String>) value, feesGroups, pm)),
                DisplayColumn.create(value -> renderFeesGroupBody((DisplayResultSet) value))});
        I18n i18n = getI18n();
        int rowIndex = 0;
        WritableJsonObject jsonImage = Json.parseObject("{url: 'images/price-tag.svg', width: 16, height: 16}");
        for (FeesGroup feesGroup : feesGroups) {
            rsb.setValue(rowIndex,   0, new Pair<>(jsonImage, feesGroup.getDisplayName(i18n)));
            rsb.setValue(rowIndex++, 1, feesGroup.generateDisplayResultSet(i18n, this, this::onBookButtonPressed));
        }
        DisplayResultSet rs = rsb.build();
        pm.dateInfoDisplayResultSetProperty().setValue(rs);
    }

    private GuiNode renderFeesGroupHeader(Pair<JsonObject, String> pair, FeesGroup[] feesGroups, FeesPresentationModel pm) {
        Toolkit toolkit = Toolkit.get();
        I18n i18n = getI18n();
        boolean hasUnemployedRate = hasUnemployedRate();
        boolean hasFacilityFeeRate = hasFacilityFeeRate();
        boolean hasDiscountRates = hasUnemployedRate || hasFacilityFeeRate;
        RadioButton noDiscountRadio  = hasDiscountRates ?   i18n.instantTranslateText(toolkit.createRadioButton(), "NoDiscount") : null;
        RadioButton unemployedRadio  = hasUnemployedRate ?  i18n.instantTranslateText(toolkit.createRadioButton(), "UnemployedDiscount") : null;
        RadioButton facilityFeeRadio = hasFacilityFeeRate ? i18n.instantTranslateText(toolkit.createRadioButton(), "FacilityFeeDiscount") : null;
        PersonService personService = PersonService.get(getDataSourceModel());
        Person person = personService.getPreselectionProfilePerson();
        if (unemployedRadio != null) {
            unemployedRadio.setSelected(Booleans.isTrue(person.isUnemployed()));
            unemployedRadio.selectedProperty().addListener((observable, oldValue, unemployed) -> {
                person.setUnemployed(unemployed);
                if (unemployed)
                    person.setFacilityFee(false);
                displayFeesGroups(feesGroups, pm);
            });
        }
        if (facilityFeeRadio != null) {
            facilityFeeRadio.setSelected(Booleans.isTrue(person.isFacilityFee()));
            facilityFeeRadio.selectedProperty().addListener((observable, oldValue, facilityFee) -> {
                person.setFacilityFee(facilityFee);
                if (facilityFee)
                    person.setUnemployed(false);
                displayFeesGroups(feesGroups, pm);
            });
        }
        if (noDiscountRadio != null) {
            noDiscountRadio.setSelected(Booleans.isNotTrue(person.isUnemployed()) && Booleans.isNotTrue(person.isFacilityFee()));
            noDiscountRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    person.setUnemployed(false);
                    person.setFacilityFee(false);
                }
                displayFeesGroups(feesGroups, pm);
            });
        }
        return toolkit.createHBox(toolkit.createImage(pair.get1()), toolkit.createTextView(pair.get2()), noDiscountRadio, unemployedRadio, facilityFeeRadio);
    }

    private GuiNode renderFeesGroupBody(DisplayResultSet rs) {
        return Toolkit.get().createTable(rs);
    }

    private void onBookButtonPressed(OptionsPreselection optionsPreselection) {
        Platform.log("Booking " + optionsPreselection);
        onNextButtonPressed(null);
    }
}
