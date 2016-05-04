package com.jpmorgan.sssm.service;

import com.jpmorgan.sssm.math.FixedPointMath;
import com.jpmorgan.sssm.model.Stock;
import com.jpmorgan.sssm.model.Trade;
import com.jpmorgan.sssm.repository.StockRepository;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.jpmorgan.sssm.math.FixedPointMath.CURRENCY_SCALE;
import static com.jpmorgan.sssm.math.FixedPointMath.MATH_CONTEXT;
import static com.jpmorgan.sssm.math.FixedPointMath.ROUNDING_MODE;

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
        // 5 minutes ago
        final Instant cutTime = Instant.now().minus(VWAP_TIME_PERIOD);

        final Collection<Trade> trades = stockRepository.findTradesByStockSinceInstant(stock, cutTime);

        if (trades.isEmpty()) {
            return new BigDecimal("0.00");
        }

        final BigDecimal weigthedTotalPrice = trades.stream()
                .map(share -> share.getPrice().multiply(BigDecimal.valueOf(share.getQuantity()), MATH_CONTEXT))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final int totalSharesBought = trades.stream().collect(Collectors.summingInt(Trade::getQuantity));

        return weigthedTotalPrice.divide(BigDecimal.valueOf(totalSharesBought), ROUNDING_MODE)
                .setScale(CURRENCY_SCALE, FixedPointMath.ROUNDING_MODE);

    }

    @Override
    public BigDecimal allShareIndex() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
