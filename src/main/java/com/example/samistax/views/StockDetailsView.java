package com.example.samistax.views;

import com.example.samistax.astra.data.StockPrice;
import com.example.samistax.astra.service.StockPriceService;
import com.example.samistax.components.StockSymbolComboBox;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Stock Details")
@Route(value = "stock-detail", layout = MainLayout.class)
@Uses(Icon.class)
public class StockDetailsView extends VerticalLayout {

    private StockSymbolComboBox stockSymbolSelector;

    public StockDetailsView(StockPriceService stockPriceService) {

        var stockPriceGrid = new Grid<>(StockPrice.class);
        stockPriceGrid.getColumnByKey("time").setAutoWidth(true);

        var stockSymbolSelector = new StockSymbolComboBox("Stock Ticker");
        stockSymbolSelector.setWidth(50, Unit.PERCENTAGE);
        stockSymbolSelector.addValueChangeListener(e -> {
            var ticker = e.getValue().getSymbol();

            var startTime = System.currentTimeMillis();
            var stockPrices = stockPriceService.findAllByTicker(ticker);
            var duration = System.currentTimeMillis() - startTime;

            stockPriceGrid.setItems(stockPrices);
            stockSymbolSelector.setLabel(stockPrices.size() + " results fetched in " + duration + "ms.");
        });
        add("Apache Pulsar Sink persisted stock prices in Cassandra DB");
        add(stockSymbolSelector);
        add(stockPriceGrid);
    }
}
