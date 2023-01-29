package com.example.samistax.astra.service;

import com.example.samistax.astra.data.StockPrice;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface StockPriceRepository extends CassandraRepository<StockPrice, String> {
    List<StockPrice> findAllBySymbol(final String symbol);

}
