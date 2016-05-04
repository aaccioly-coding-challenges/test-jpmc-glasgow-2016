package com.jpmorgan.sssm.repository;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jpmorgan.sssm.model.Stock;
import com.jpmorgan.sssm.model.Trade;
import lombok.NonNull;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * In-memory implementation of a repository for stocks and trades.
 * <p>
 * All returned collections are unmodifiable by design.
 *
 * @author Anthony Accioly
 */
public final class StockRepositoryInMemoryImpl implements StockRepository {

    private static final StockRepository INSTANCE = new StockRepositoryInMemoryImpl(ArrayListMultimap.create());

    public static StockRepository getInstance() {
        return INSTANCE;
    }

    private final Multimap<Stock, Trade> tradingHistory;

    private StockRepositoryInMemoryImpl(Multimap<Stock, Trade> stocks) {
        this.tradingHistory = stocks;
    }

    @Override
    public void record(@NonNull Trade trade) {
        tradingHistory.put(trade.getStock(), trade);
    }

    @Override
    public Set<Stock> findAllStocks() {
        return Collections.unmodifiableSet(tradingHistory.keySet());
    }

    @Override
    public Collection<Trade> findTradesByStock(@NonNull Stock stock) {
        return Collections.unmodifiableCollection(tradingHistory.get(stock));
    }

    @Override
    public Collection<Trade> findTradesByStockSinceInstant(@NonNull Stock stock, @NonNull Instant instant) {
        return tradingHistory.get(stock).stream()
                .filter(trade -> trade.getTimestamp().isAfter(instant))
                .collect(Collectors.toList());
    }

    @VisibleForTesting
    void clearHistory() {
        tradingHistory.clear();
    }

}
