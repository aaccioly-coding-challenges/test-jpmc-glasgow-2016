package com.jpmorgan.sssm.service;

import com.jpmorgan.sssm.model.Stock;

import java.math.BigDecimal;

/**
 * @author Anthony Accioly
 */
public interface StockMarketService {

    BigDecimal volumeWeightedStockPrice(Stock stock);
    BigDecimal allShareIndex();
}
