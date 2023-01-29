package com.example.samistax.views;

import com.example.samistax.astra.data.StockPrice;
import com.example.samistax.astra.service.StockPriceService;
import com.example.samistax.components.StockSymbolComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.text.DecimalFormat;

@PageTitle("Stock Details")
@Route(value = "stock-detail", layout = MainLayout.class)
@Uses(Icon.class)
public class StockDetailsView extends VerticalLayout {

    private StockSymbolComboBox stockSymbolSelector;

    public StockDetailsView(StockPriceService stockPriceService) {
        var decimalFormat = new DecimalFormat("#.00");
        var stockPriceGrid = new Grid<StockPrice>();
        var stockSymbolSelector = new StockSymbolComboBox("Stock Ticker");

        stockPriceGrid.addColumn(StockPrice::getTime).setAutoWidth(true).setHeader("Time").setSortable(true);
        stockPriceGrid.addColumn(stockPrice -> decimalFormat.format(stockPrice.getOpen())).setHeader("Open").setSortable(true);
        stockPriceGrid.addColumn(stockPrice -> decimalFormat.format(stockPrice.getHigh())).setHeader("High").setSortable(true);
        stockPriceGrid.addColumn(stockPrice -> decimalFormat.format(stockPrice.getLow())).setHeader("Low").setSortable(true);
        stockPriceGrid.addColumn(stockPrice -> decimalFormat.format(stockPrice.getClose())).setHeader("Close").setSortable(true);
        stockPriceGrid.addColumn(StockPrice::getVolume).setHeader("Volume").setSortable(true);

        stockSymbolSelector.setWidth("50%");
        stockSymbolSelector.addValueChangeListener(e -> {
            var ticker = e.getValue().symbol();

            var startTime = System.currentTimeMillis();
            var stockPrices = stockPriceService.findAllByTicker(ticker);
            var duration = System.currentTimeMillis() - startTime;

            stockPriceGrid.setItems(stockPrices);
            stockSymbolSelector.setHelperText(stockPrices.size() + " results fetched in " + duration + "ms.");
        });

        add(
                new Paragraph("Apache Pulsar Sink persisted stock prices in Cassandra DB"),
                stockSymbolSelector,
                stockPriceGrid
        );
    }

}
