package com.example.samistax.components;

import com.example.samistax.astra.data.StockSymbol;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;

import java.util.ArrayList;
import java.util.List;

public class StockSymbolComboBox extends ComboBox<StockSymbol> {

    public StockSymbolComboBox(String label) {

        super();
        // For demo purposes we list only few tickers. For full 8000+ ETF and Stock list you can parse results of
        //  APi endpoint https://www.alphavantage.co/query?function=LISTING_STATUS&apikey=demo
        List<StockSymbol> demoSymbols = new ArrayList<>();
        demoSymbols.add(new StockSymbol("AAPL", "Apple Inc", "NASDAQ"));
        demoSymbols.add(new StockSymbol("IBM", "IBM (International Business Machines) Corp", "NYSE"));
        demoSymbols.add(new StockSymbol("AMZN", "Amazon.com Inc","NASDAQ"));
        setItems(demoSymbols);

        setPlaceholder("Search by company");
        setItemLabelGenerator(StockSymbol::getName);
    }
}
