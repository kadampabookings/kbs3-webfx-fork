package naga.core.spi.toolkit.gwt.charts;

import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.googlecode.gwt.charts.client.ChartWidget;
import naga.core.spi.toolkit.charts.AreaChart;

/**
 * @author Bruno Salmon
 */
public class GwtAreaChart extends GwtChart implements AreaChart<SimpleLayoutPanel> {

    @Override
    protected ChartWidget createChartWidget() {
        return new com.googlecode.gwt.charts.client.corechart.AreaChart();
    }
}
