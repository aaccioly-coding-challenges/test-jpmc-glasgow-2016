package com.jpmorgan.sssm.model;

import com.sun.istack.internal.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.jpmorgan.sssm.math.FixedPointConstants.*;

/**
 * @author Anthony Accioly
 */
@Value
@EqualsAndHashCode(callSuper = true)
final class CommonStock extends Stock {

    CommonStock(String symbol, BigDecimal lastDividend, BigDecimal parValue) {
        super(symbol, lastDividend, parValue);
    }

    @Override
    public final BigDecimal getDividendYield(@NotNull BigDecimal price) {
        checkArgument(price.compareTo(MIN_VALUE) >= 0, MIN_VALUE_MESSAGE, "Price", MIN_VALUE);

        return lastDividend.divide(price, MATH_CONTEXT)
                .setScale(PERCENTAGE_SCALE, ROUNDING_MODE);
    }
}
