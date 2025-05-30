package org.petya8bachey.service;

import org.petya8bachey.model.Stock;
import org.petya8bachey.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Stock> findAllStocks() {
        return stockRepository.findAll();
    }

    public Stock findStockById(String id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Stock not found with ID: " + id));
    }
}
