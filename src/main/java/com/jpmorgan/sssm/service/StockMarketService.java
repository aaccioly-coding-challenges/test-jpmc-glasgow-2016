package com.jpmorgan.sssm.service;

import com.jpmorgan.sssm.model.Stock;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * @author Anthony Accioly
 */
public interface StockMarketService {
    /**
     * Time horizon for volume-weighted average price
     */
    Duration VWAP_TIME_PERIOD = Duration.ofMinutes(5);

    /**
     * Calculates volume weighted stock price based on trades in past 5 minutes.
     *
     * @param stock the stock traded in the stock market
     *
     * @return VWAP for the {@link Stock} over the past 5 minutes. 0.00 if no trades were recorded for the stock during that time.
     */
    BigDecimal volumeWeightedStockPrice(@NonNull Stock stock);
    BigDecimal allShareIndex();
}
