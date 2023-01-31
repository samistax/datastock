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
public class StockDetailsView extends VerticalLayout {

    private StockSymbolComboBox stockSymbolSelector;

    public StockDetailsView(StockPriceService stockPriceService) {
        var stockPriceGrid = getStockPriceGrid();
        var stockSymbolSelector = getSymbolSelector(stockPriceService, stockPriceGrid);

        add(
                new Paragraph("Apache Pulsar Sink persisted stock prices in Cassandra DB"),
                stockSymbolSelector,
                stockPriceGrid
        );
    }


    private static Grid<StockPrice> getStockPriceGrid() {
        var decimalFormat = new DecimalFormat("#.00");
        var stockPriceGrid = new Grid<StockPrice>();
        stockPriceGrid.addColumn(StockPrice::getTime).setAutoWidth(true).setHeader("Time").setSortable(true);
        stockPriceGrid.addColumn(stockPrice -> decimalFormat.format(stockPrice.getOpen())).setHeader("Open").setSortable(true);
        stockPriceGrid.addColumn(stockPrice -> decimalFormat.format(stockPrice.getHigh())).setHeader("High").setSortable(true);
        stockPriceGrid.addColumn(stockPrice -> decimalFormat.format(stockPrice.getLow())).setHeader("Low").setSortable(true);
        stockPriceGrid.addColumn(stockPrice -> decimalFormat.format(stockPrice.getClose())).setHeader("Close").setSortable(true);
        stockPriceGrid.addColumn(StockPrice::getVolume).setHeader("Volume").setSortable(true);
        return stockPriceGrid;
    }

    private static StockSymbolComboBox getSymbolSelector(StockPriceService stockPriceService, Grid<StockPrice> stockPriceGrid) {
        var stockSymbolSelector = new StockSymbolComboBox("Stock Ticker");
        stockSymbolSelector.setWidth("50%");
        stockSymbolSelector.addValueChangeListener(e -> {
            var stockPrices = stockPriceService.findAllByTicker(e.getValue().symbol());
            stockPriceGrid.setItems(stockPrices);
        });
        return stockSymbolSelector;
    }

}
