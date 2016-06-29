package naga.core.spi.toolkit.javafx;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import naga.core.spi.toolkit.Toolkit;
import naga.core.spi.toolkit.charts.AreaChart;
import naga.core.spi.toolkit.charts.BarChart;
import naga.core.spi.toolkit.charts.LineChart;
import naga.core.spi.toolkit.charts.PieChart;
import naga.core.spi.toolkit.controls.*;
import naga.core.spi.toolkit.javafx.charts.FxAreaChart;
import naga.core.spi.toolkit.javafx.charts.FxBarChart;
import naga.core.spi.toolkit.javafx.charts.FxLineChart;
import naga.core.spi.toolkit.javafx.charts.FxPieChart;
import naga.core.spi.toolkit.javafx.controls.*;
import naga.core.spi.toolkit.javafx.layouts.FxHBox;
import naga.core.spi.toolkit.javafx.layouts.FxVBox;
import naga.core.spi.toolkit.javafx.layouts.FxVPage;
import naga.core.spi.toolkit.javafx.layouts.FxWindow;
import naga.core.spi.toolkit.layouts.HBox;
import naga.core.spi.toolkit.layouts.VBox;
import naga.core.spi.toolkit.layouts.VPage;
import naga.core.spi.toolkit.layouts.Window;
import naga.core.spi.toolkit.property.ConvertedProperty;
import naga.core.ui.displayresultset.DisplayResultSet;
import naga.core.util.function.Factory;

/**
 * @author Bruno Salmon
 */
public class JavaFxToolkit extends Toolkit {

    public JavaFxToolkit() {
        this(() -> FxApplication.applicationWindow = new FxWindow(FxApplication.primaryStage));
    }

    protected JavaFxToolkit(Factory<Window> windowFactory) {
        super(FxScheduler.SINGLETON, windowFactory);
        new Thread(() -> Application.launch(FxApplication.class)).start();
        registerNodeFactoryAndWrapper(Table.class, FxTable::new, TableView.class, FxTable::new);
        registerNodeFactoryAndWrapper(CheckBox.class, FxCheckBox::new, javafx.scene.control.CheckBox.class, FxCheckBox::new);
        registerNodeFactoryAndWrapper(ToggleSwitch.class, FxToggleSwitch::new, naga.core.spi.toolkit.javafx.controlsfx.ToggleSwitch.class, FxToggleSwitch::new);
        registerNodeFactoryAndWrapper(TextField.class, FxTextField::new, javafx.scene.control.TextField.class, FxTextField::new);
        registerNodeFactoryAndWrapper(Button.class, FxButton::new, javafx.scene.control.Button.class, FxButton::new);
        registerNodeFactoryAndWrapper(Slider.class, FxSlider::new, javafx.scene.control.Slider.class, FxSlider::new);
        registerNodeFactory(SearchBox.class, FxSearchBox::new);
        registerNodeFactory(VPage.class, FxVPage::new);
        registerNodeFactoryAndWrapper(VBox.class, FxVBox::new, javafx.scene.layout.VBox.class, FxVBox::new);
        registerNodeFactoryAndWrapper(HBox.class, FxHBox::new, javafx.scene.layout.HBox.class, FxHBox::new);
        registerNodeFactoryAndWrapper(LineChart.class, FxLineChart::new, javafx.scene.chart.LineChart.class, FxLineChart::new);
        registerNodeFactoryAndWrapper(AreaChart.class, FxAreaChart::new, javafx.scene.chart.AreaChart.class, FxAreaChart::new);
        registerNodeFactoryAndWrapper(BarChart.class, FxBarChart::new, javafx.scene.chart.BarChart.class, FxBarChart::new);
        registerNodeFactoryAndWrapper(PieChart.class, FxPieChart::new, javafx.scene.chart.PieChart.class, FxPieChart::new);
    }

    public static DisplayResultSet transformDisplayResultSetValuesToProperties(DisplayResultSet displayResultSet) {
        Object[] values = displayResultSet.getValues();
        ObjectProperty[] fxProperties = new ObjectProperty[values.length];
        for (int i = 0; i < values.length; i++)
            fxProperties[i] = new SimpleObjectProperty<>(values[i]);
        return new DisplayResultSet(displayResultSet.getRowCount(), fxProperties, displayResultSet.getColumns());
    }

    public static class FxApplication extends Application {
        public static Stage primaryStage;
        public static FxWindow applicationWindow;
        @Override
        public void start(Stage primaryStage) throws Exception {
            FxApplication.primaryStage = primaryStage;
            primaryStage.setOnCloseRequest(windowEvent -> System.exit(0));
            if (applicationWindow != null)
                applicationWindow.setStage(primaryStage);
        }
    }

    public static ConvertedProperty<Integer, Number> numberToIntegerProperty(Property<Number> numberProperty) {
        return new ConvertedProperty<>(numberProperty, Integer::doubleValue, Number::intValue);
    }

}
