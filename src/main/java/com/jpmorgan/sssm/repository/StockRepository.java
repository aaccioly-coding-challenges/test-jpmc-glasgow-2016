package com.jpmorgan.sssm.repository;

import com.jpmorgan.sssm.model.Stock;
import com.jpmorgan.sssm.model.Trade;
import lombok.NonNull;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;

/**
 * Interface for generic operations on a repository for stocks and associated trades.
 *
 * @author Anthony Accioly
 */
public interface StockRepository {

    /**
     * Records a trade in the repository.
     * <p> After recording a trade it becomes permanent part of the trade history of the Global Beverage Corporation Exchange stock market
     *
     * @param trade the trade to be recorded.
     */
    void record(@NonNull Trade trade);

    /**
     * Returns all stocks traded in Global Beverage Corporation Exchange stock market.
     *
     * @return A Set with one instance of each stock traded in the stock market. The resulting set may be empty.
     */
    Set<Stock> findAllStocks();

    /**
     * Returns all trades for a specific {@code Stock}.
     *
     * @param stock the stock to search
     *
     * @return A collection with every trade recorded for the given stock. The resulting collection may be empty.
     */
    Collection<Trade> findTradesByStock(@NonNull Stock stock);

    /**
     * Returns all recent trades (that is, after a given @{code instant}) for a specific {@code Stock}.
     *
     * @param stock the stock to search
     * @param instant time used to filter trades (non-inclusive)
     *
     * @return A collection with every trade recorded for the given stock. The resulting collection may be empty.
     */
    Collection<Trade> findTradesByStockSinceInstant(@NonNull Stock stock, @NonNull Instant instant);
}
