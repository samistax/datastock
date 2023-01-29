package com.example.samistax.astra.service;

import com.example.samistax.astra.data.StockPrice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockPriceService {
    private final StockPriceRepository repository;

    public StockPriceService(StockPriceRepository repository) {
        this.repository = repository;
    }

    public List<StockPrice> findAllByTicker(String ticker) {
        return repository.findAllBySymbol(ticker);
    }

    public StockPrice update(StockPrice entity) {
        return repository.insert(entity);
    }

    public List<StockPrice> update(Iterable<StockPrice> entities) {
        return repository.insert(entities);
    }

}
