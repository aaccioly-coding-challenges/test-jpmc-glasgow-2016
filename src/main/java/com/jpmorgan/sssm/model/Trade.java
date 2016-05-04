package com.jpmorgan.sssm.model;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.Instant;

import static com.jpmorgan.sssm.math.FixedPointMath.CURRENCY_SCALE;
import static com.jpmorgan.sssm.math.FixedPointMath.MIN_VALUE;
import static com.jpmorgan.sssm.math.FixedPointMath.ROUNDING_MODE;
import static com.jpmorgan.sssm.math.FixedPointMath.checkArgumentGreaterThanOrEgual;
import static com.jpmorgan.sssm.model.TradeIndicator.BUY;
import static com.jpmorgan.sssm.model.TradeIndicator.SELL;
import static java.time.Instant.now;

/**
 * Represents a trade order for a given stock.
 *
 * @author Anthony Accioly
 */
@Data
public final class Trade {

    public static Trade createOrder(Stock stock, Instant timestamp, int quantity, TradeIndicator indicator, BigDecimal price) {
        return new Trade(stock, timestamp, quantity, indicator, price);
    }

    public static Trade buyNow(Stock stock, int quantity, BigDecimal price) {
        return new Trade(stock, now(), quantity, BUY, price);
    }

    public static Trade sellNow(Stock stock, int quantity, BigDecimal price) {
        return new Trade(stock, now(), quantity, SELL, price);
    }

    @NonNull private final Stock stock;
    @NonNull private final Instant timestamp;
    private final int quantity;
    @NonNull private final TradeIndicator indicator;
    @NonNull private final BigDecimal price;

    private Trade(@NonNull Stock stock, @NonNull Instant timestamp, int quantity, @NonNull TradeIndicator indicator, @NonNull BigDecimal price) {
        checkArgumentGreaterThanOrEgual("Quantity", quantity, 1);
        checkArgumentGreaterThanOrEgual("Price", price, MIN_VALUE);

        this.stock = stock;
        this.timestamp = timestamp;
        this.quantity = quantity;
        this.indicator = indicator;
        this.price = price.setScale(CURRENCY_SCALE, ROUNDING_MODE);
    }
}
