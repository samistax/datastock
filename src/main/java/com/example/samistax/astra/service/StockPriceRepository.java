package com.example.samistax.astra.service;

import java.util.List;

import com.example.samistax.astra.data.StockPrice;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface StockPriceRepository extends CassandraRepository<StockPrice,String> {
        List<StockPrice> findAllBySymbol(final String symbol);
        Slice<StockPrice> findAllBySymbol(final String symbol, Pageable pageable);
        long countBySymbol(String symbol);
}
