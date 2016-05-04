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
 * Represents preferred stock that always pays dividends according to a fixed percentage of the par value.
 *
 * @author Anthony Accioly
 */
@Value
@EqualsAndHashCode(callSuper = true)
final class PreferredStock extends Stock {

    @NonNull private final BigDecimal fixedDividend;

    PreferredStock(String symbol, BigDecimal lastDividend, BigDecimal parValue, BigDecimal fixedDividend) {
        super(symbol, lastDividend, parValue);
        checkArgumentGreaterThanOrEgual("Fixed Dividend", fixedDividend, MIN_VALUE);
        this.fixedDividend = fixedDividend.setScale(PERCENTAGE_SCALE, ROUNDING_MODE).stripTrailingZeros();
    }

    @Override
    public BigDecimal dividendYield(BigDecimal price) {
        checkArgumentGreaterThanOrEgual("Price", price, MIN_VALUE);

        return fixedDividend.multiply(getParValue(), MATH_CONTEXT)
                .divide(price, MATH_CONTEXT)
                .setScale(PERCENTAGE_SCALE, ROUNDING_MODE);
    }
}
