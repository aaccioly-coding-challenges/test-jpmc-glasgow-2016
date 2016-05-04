package com.jpmorgan.sssm.service;

import com.jpmorgan.sssm.model.Stock;
import com.jpmorgan.sssm.repository.StockRepository;
import lombok.NonNull;

import java.math.BigDecimal;

/**
 * @author Anthony Accioly
 */
public final class StockMarketServiceImpl implements StockMarketService {

    private final StockRepository stockRepository;

    public StockMarketServiceImpl(@NonNull StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public BigDecimal volumeWeightedStockPrice(@NonNull Stock stock) {
        return null;
    }

    @Override
    public BigDecimal allShareIndex() {
        return null;
    }
}
