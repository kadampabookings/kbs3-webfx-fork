package mongoose.activities.shared.book.event.fees;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import mongoose.activities.shared.book.event.shared.BookingProcessPresentationViewActivity;
import mongoose.activities.shared.logic.ui.highlevelcomponents.HighLevelComponents;
import mongoose.activities.shared.logic.ui.highlevelcomponents.SectionPanelStyleOptions;
import naga.framework.ui.i18n.I18n;
import naga.fxdata.cell.collator.GridCollator;

/**
 * @author Bruno Salmon
 */
public class FeesPresentationViewActivity extends BookingProcessPresentationViewActivity<FeesPresentationModel> {

    private HBox buttonsBox;
    private GridCollator feesGroupsCollator;

    @Override
    protected void createViewNodes(FeesPresentationModel pm) {
        super.createViewNodes(pm);
        I18n i18n = getI18n();
        Button termsButton = i18n.translateText(setGraphic(new Button(), "{url: 'images/certificate.svg', width: 16, height: 16}"), "TermsAndConditions");
        Button programButton = i18n.translateText(setGraphic(new Button(), "{url: 'images/calendar.svg', width: 16, height: 16}"), "Program");
        feesGroupsCollator = new GridCollator(this::toFeesGroupPanel, nodes -> {
            VBox vBox = new VBox(nodes);
            vBox.setPadding(new Insets(10));
            return vBox;
        });
        buttonsBox = new HBox(previousButton, termsButton, programButton);

        termsButton.onActionProperty().bind(pm.onTermsActionProperty());
        programButton.onActionProperty().bind(pm.onProgramActionProperty());
        feesGroupsCollator.displayResultSetProperty().bind(pm.dateInfoDisplayResultSetProperty());
    }

    @Override
    protected Node assemblyViewNodes() {
        return new BorderPane(feesGroupsCollator, null, null, buttonsBox, null);
    }

    private Node toFeesGroupPanel(Node... nodes) {
        BorderPane borderPane = buildFeesSectionPanel(nodes[0]);
        borderPane.setCenter(nodes[1]);
        borderPane.setBottom(nodes[2]);
        return borderPane;
    }

    private BorderPane buildFeesSectionPanel(Node node) {
        SectionPanelStyleOptions options = new SectionPanelStyleOptions(false);
        return HighLevelComponents.createSectionPanel(options, node);
    }
}
