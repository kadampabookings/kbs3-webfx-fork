package webfx.fxkits.extra.spi.peer.base;

import webfx.fxkits.extra.chart.LineChart;

/**
 * @author Bruno Salmon
 */
public interface LineChartPeerMixin
        <C, N extends LineChart, NB extends LineChartPeerBase<C, N, NB, NM>, NM extends LineChartPeerMixin<C, N, NB, NM>>

        extends ChartPeerMixin<C, N, NB, NM> {
}