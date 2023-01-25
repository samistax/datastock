package com.example.samistax.astra.data;

import java.util.ArrayList;
import java.util.List;

public class StockSymbol {

    private String symbol;
    private String name;
    private String exchange;

    public StockSymbol(String symbol, String name, String exchange) {
        this.symbol = symbol;
        this.name = name;
        this.exchange = exchange;
    }

    // Symbols for testing
    public static List<StockSymbol> supportedSymbols() {
        // For demo purposes we list only few tickers. For full 8000+ ETF and Stock list you can parse results of
        //  APi endpoint https://www.alphavantage.co/query?function=LISTING_STATUS&apikey=demo
        List<StockSymbol> supportedSymbols = new ArrayList<>();
        supportedSymbols.add(new StockSymbol("AAPL", "Apple Inc", "NASDAQ"));
        supportedSymbols.add(new StockSymbol("IBM", "IBM (International Business Machines) Corp", "NYSE"));
        supportedSymbols.add(new StockSymbol("AMZN", "Amazon.com Inc", "NASDAQ"));
        return supportedSymbols;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

}
