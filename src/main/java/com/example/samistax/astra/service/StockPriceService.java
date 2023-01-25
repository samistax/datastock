package com.example.samistax.astra.service;

import com.example.samistax.astra.data.StockPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockPriceService {

    @Autowired
    private final StockPriceRepository repository;

    @Autowired
    public StockPriceService(StockPriceRepository repository) {
        this.repository = repository;
    }
    public List<StockPrice> findAllByTicker(String ticker) {return repository.findAllBySymbol(ticker);}
    public Slice<StockPrice> findAllByTicker(String ticker, Pageable pageable) {return repository.findAllBySymbol(ticker,pageable);}
    public StockPrice update(StockPrice entity) {return repository.insert(entity);}
    public List<StockPrice> update(Iterable<StockPrice> entities) {return repository.insert(entities);}
    public void delete(String symbol) {
        repository.deleteById(symbol);
    }
    public void delete(Iterable<StockPrice> entities) {repository.deleteAll(entities);}
    public long countBySymbol(String ticker) {
        return repository.countBySymbol(ticker);
    }
    public int count() {
        return (int) repository.count();
    }
}
