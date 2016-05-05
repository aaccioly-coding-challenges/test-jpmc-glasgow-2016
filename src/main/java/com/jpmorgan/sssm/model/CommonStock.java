package com.jpmorgan.sssm.model;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

import static com.jpmorgan.sssm.math.FixedPointMath.MATH_CONTEXT;
import static com.jpmorgan.sssm.math.FixedPointMath.MIN_VALUE;
import static com.jpmorgan.sssm.math.FixedPointMath.PERCENTAGE_SCALE;
import static com.jpmorgan.sssm.math.FixedPointMath.ROUNDING_MODE;
import static com.jpmorgan.sssm.math.FixedPointMath.checkArgumentGreaterThanOrEgual;

/**
 * Represents common stock that may or may not pay dividends.
 *
 * @author Anthony Accioly
 */
@Value
@EqualsAndHashCode(callSuper = true)
final class CommonStock extends Stock {

    CommonStock(String symbol, BigDecimal lastDividend, BigDecimal parValue) {
        super(symbol, lastDividend, parValue);
    }

    @Override
    public final BigDecimal dividendYield(@NonNull BigDecimal price) {
        checkArgumentGreaterThanOrEgual("Price", price, MIN_VALUE);

        return getLastDividend().divide(price, MATH_CONTEXT)
                .setScale(PERCENTAGE_SCALE, ROUNDING_MODE)
                .stripTrailingZeros();
    }
}
