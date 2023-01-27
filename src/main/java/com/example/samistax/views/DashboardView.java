package com.example.samistax.views;

import com.example.samistax.astra.data.StockPrice;
import com.example.samistax.astra.service.StockPriceFetcher;
import com.example.samistax.components.StockSymbolComboBox;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.pulsar.annotation.PulsarListener;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@PageTitle("Dashboard")
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {
    private Chart ohlcChart = createOhlcChart("");
    private DataSeries dataSeries;


    public DashboardView(StockPriceFetcher stockPriceFetcher) {
        var stockSelector = new StockSymbolComboBox("Company");

        stockSelector.setWidth("50%");
        stockSelector.addValueChangeListener(e -> {
            var ticker = e.getValue().symbol();
            var newOhlcChart = createOhlcChart(ticker);

            replace(ohlcChart, newOhlcChart);
            ohlcChart = newOhlcChart;

            // Clear old data series, reusing chart for multiple tickers
            dataSeries.clear();

            // Generate stock data price items (and push them to Astra Streaming)
            stockPriceFetcher.fetchStockDataSeries(ticker);
        });

        add(
                new Paragraph("Apache Pulsar Producer"),
                stockSelector,
                ohlcChart
        );
    }

    public Chart createOhlcChart(String ticker) {
        var chart = new Chart(ChartType.OHLC);

        var tooltip = new Tooltip();
        tooltip.setPointFormat("<b>{point.x}</b>: {series.name}: {point.y}");

        var seriesTooltip = new SeriesTooltip();
        seriesTooltip.setPointFormat("<b><u>{series.name}:</u></b> <br>High: {point.high}, <br>Low: {point.low}, <br>Open: {point.open}, <br>Close: {point.close}");

        var configuration = chart.getConfiguration();
        var tickerLabel = "";
        if (ticker != null) {
            tickerLabel = "(" + ticker + " stock data)";
        }
        configuration.getTitle().setText("Apache Pulsar Consumer " + tickerLabel);
        configuration.setTooltip(tooltip);

        dataSeries = new DataSeries(ticker + " Stock Price");

        var grouping = new DataGrouping();
        grouping.addUnit(new TimeUnitMultiples(TimeUnit.MINUTE, 1, 5, 15));
        grouping.addUnit(new TimeUnitMultiples(TimeUnit.HOUR, 1, 24));
        grouping.addUnit(new TimeUnitMultiples(TimeUnit.DAY, 1, 7, 30));

        var plotOptions = new PlotOptionsOhlc();
        plotOptions.setDataGrouping(grouping);
        plotOptions.setAllowPointSelect(true);
        plotOptions.setAnimation(true);
        plotOptions.setTooltip(seriesTooltip);

        dataSeries.setPlotOptions(plotOptions);
        configuration.setSeries(dataSeries);

        RangeSelector rangeSelector = new RangeSelector();
        configuration.setRangeSelector(rangeSelector);
        chart.setTimeline(true);
        return chart;
    }

    private OhlcItem ohlcItemFromStockPrice(StockPrice data) {
        var item = new OhlcItem();
        if (data != null) {
            DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);
            LocalDateTime localDateTime = LocalDateTime.parse(data.getTime(), dtFormatter);
            item.setX(localDateTime.toInstant(ZoneOffset.UTC));
            item.setLow(data.getLow());
            item.setHigh(data.getHigh());
            item.setClose(data.getClose());
            item.setOpen(data.getOpen());
        }
        return item;

    }


    @PulsarListener
    public void stockPriceReceived(StockPrice stockPrice) {
        getUI().ifPresent(ui -> {
            ui.access(() -> dataSeries.add(ohlcItemFromStockPrice(stockPrice), true, false));
        });
    }
}
