package com.example.samistax.astra.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.AlphaVantageException;
import com.crazzyghost.alphavantage.parameters.Interval;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;
import com.example.samistax.astra.data.StockPrice;
import org.apache.pulsar.client.api.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StockPriceFetcher {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss sss");

    private final StockPriceService stockPriceService;
    private final AstraStreaming astraStreaming;

    @Autowired
    public StockPriceFetcher(StockPriceService stockPriceService, AstraStreaming astraStreaming) {
        this.stockPriceService = stockPriceService;
        this.astraStreaming = astraStreaming;
    }

    public void fetchStockDataSeries(final String symbol) {

        Date now = new Date();
        String nowString = df.format(now);

        AlphaVantage.api()
                .timeSeries()
                .intraday()
                .forSymbol(symbol)
                .interval(Interval.ONE_MIN)
                .outputSize(OutputSize.COMPACT)
                .onSuccess(e->handleSuccess(symbol, (TimeSeriesResponse)e))
                .onFailure(e->handleFailure(e))
                .fetch();
    }
    public void handleSuccess(String symbol, TimeSeriesResponse response) {

        List<StockUnit> stockUnits = response.getStockUnits();
        // Reverse StockUnits to list oldest items first
        Collections.reverse(stockUnits);
        // Create POJOs to be sent to Astra Streaming, tagged with symbol name
        List<StockPrice> ticks = new ArrayList<>();
        for (StockUnit unit : stockUnits) {
            ticks.add(new StockPrice(symbol, unit));
        }
        // For demo purposes produce stream messages and send to Astra Streaming (Apache Pulsar)
        if ( astraStreaming != null ) {
            // Get handle to Pulsar producer for streaming stock info to Astra
            Producer<StockPrice> producer = astraStreaming.getProducer();
            if ( producer != null) {
                // Send messages using asynchronously
                var startTime = System.currentTimeMillis();
                for (StockPrice stockPrice : ticks) {
                    producer.sendAsync(stockPrice);
                }
                var duration = System.currentTimeMillis() - startTime;
                logger.info(ticks.size() + " pulsar messages sent in " + duration + "ms.");
            }
        }
    }
    public void handleFailure(AlphaVantageException error) {
        /* uh-oh! */
        logger.info("handleFailure: AlphaVantageException " + error.toString());
    }
}