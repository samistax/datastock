package com.example.samistax.views;

import com.example.samistax.astra.data.StockPrice;
import com.example.samistax.astra.service.AstraStreaming;
import com.example.samistax.astra.service.StockPriceFetcher;
import com.example.samistax.astra.service.StockPriceService;
import com.example.samistax.components.StockSymbolComboBox;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.pulsar.client.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@PageTitle("Dashboard")
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AstraStreaming astraStreaming;
    private Chart ohlcChart;

    // Variables used for async chart updating
    private DataSeries dataSeries;
    private Thread chartUpdateThread;

    public DashboardView(StockPriceService stockPriceService, AstraStreaming astraStreaming, StockPriceFetcher stockPriceFetcher) {
        this.astraStreaming = astraStreaming;


        var stockSelector = new StockSymbolComboBox("Company");
        stockSelector.setWidth(50, Unit.PERCENTAGE);
        stockSelector.addValueChangeListener(e -> {
            String ticker = e.getValue().getSymbol();
            Chart newOhlcChart = createOHLCChart(e.getValue().getSymbol());
            replace(ohlcChart, newOhlcChart);
            ohlcChart = newOhlcChart;

            // Clear old data series, reusing chart for multiple tickers
            dataSeries.clear();

            // Generate stock data price items (and push them to Astra Streaming)
            stockPriceFetcher.fetchStockDataSeries(ticker);

            // Subscribe to symbol ticker data stream from Astra Streaming
            startSymbolDataConsumer(ticker);

        });
        add(stockSelector);

        ohlcChart = createOHLCChart("");
        add(ohlcChart);
    }

    public Chart createOHLCChart(String ticker) {

        var chart = new Chart(ChartType.OHLC);
        var tooltip = new Tooltip();
        tooltip.setPointFormat("<b>{point.x}</b>: {series.name}: {point.y}");
        var seriesTooltip = new SeriesTooltip();
        seriesTooltip.setPointFormat("<b><u>{series.name}:</u></b> <br>High: {point.high}, <br>Low: {point.low}, <br>Open: {point.open}, <br>Close: {point.close}");

        var configuration = chart.getConfiguration();
        configuration.getTitle().setText(ticker + " Stock Price");
        configuration.setTooltip(tooltip);

        dataSeries = new DataSeries(ticker + " Stock Price");

        var plotOptionsOhlc = new PlotOptionsOhlc();
        var grouping = new DataGrouping();
        grouping.addUnit(new TimeUnitMultiples(TimeUnit.MINUTE, 1, 5, 15));
        grouping.addUnit(new TimeUnitMultiples(TimeUnit.HOUR, 1, 24));
        grouping.addUnit(new TimeUnitMultiples(TimeUnit.DAY, 1, 7, 30));

        plotOptionsOhlc.setDataGrouping(grouping);
        plotOptionsOhlc.setAllowPointSelect(true);
        plotOptionsOhlc.setAnimation(true);
        plotOptionsOhlc.setTooltip(seriesTooltip);

        dataSeries.setPlotOptions(plotOptionsOhlc);
        configuration.setSeries(dataSeries);

        RangeSelector rangeSelector = new RangeSelector();
        configuration.setRangeSelector(rangeSelector);
        chart.setTimeline(true);
        return chart;
    }

    private OhlcItem OhlcItemFromStrockPrice(StockPrice data) {
        var item = new OhlcItem();
        if (data != null) {
            item.setX(data.getTime().toInstant(ZoneOffset.UTC)); // From DateTime object
            item.setLow(data.getLow());
            item.setHigh(data.getHigh());
            item.setClose(data.getClose());
            item.setOpen(data.getOpen());
        }
        return item;

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        // Terminate chartUpdateThread when the view is detached
        if (chartUpdateThread != null) {
            chartUpdateThread.interrupt();
        }
    }

    private void startSymbolDataConsumer(String ticker) {
        // Read streamed stock prices from beg of the queue using Pulsar Consumer
        var consumer = astraStreaming.getConsumer();
        if (consumer != null) {

            // Vaadin Chart updater Thread
            chartUpdateThread = new Thread(() -> {
                logger.info("chartUpdateThread: Starting Thread " + Thread.currentThread().getId());
                CompletableFuture<Message<StockPrice>> future;
                while ((future = consumer.receiveAsync()) != null) {
                    try {
                        Message<StockPrice> msg = future.get();
                        StockPrice data = msg.getValue();
                        // Update Vaadin Charts after each received stock price update
                        getUI().ifPresent(ui -> ui.access(() -> dataSeries.add(OhlcItemFromStrockPrice(data), true, false)));

                        // Acknowledge message is received.
                        consumer.acknowledgeAsync(msg);

                        // Slow down reading messages from stream for smoother chart rendering
                        Thread.sleep(100);

                    } catch (InterruptedException e) {
                        logger.info("chartUpdateThread: Thread Interrupted " + Thread.currentThread().getId());
                        break;
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
                logger.info("chartUpdateThread: Thread Terminated " + Thread.currentThread().getId());
            });
            chartUpdateThread.start();
        }
    }
}
