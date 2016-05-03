package com.jpmorgan.sssm.model;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.jpmorgan.sssm.math.FixedPointConstants.*;

/**
 * @author Anthony Accioly
 */
@Value
@EqualsAndHashCode(callSuper = true)
final class PreferredStock extends Stock {

    @NonNull BigDecimal fixedDividend;

    PreferredStock(String symbol, BigDecimal lastDividend, BigDecimal parValue, BigDecimal fixedDividend) {
        super(symbol, lastDividend, parValue);
        checkArgument(fixedDividend.compareTo(MIN_VALUE) >= 0, MIN_VALUE_MESSAGE, "Fixed Dividend", MIN_VALUE);
        this.fixedDividend = fixedDividend.setScale(PERCENTAGE_SCALE, ROUNDING_MODE).stripTrailingZeros();
    }

    @Override
    public BigDecimal getDividendYield(BigDecimal price) {
        checkArgument(price.compareTo(MIN_VALUE) >= 0, MIN_VALUE_MESSAGE, "Price", MIN_VALUE);

        return fixedDividend.multiply(parValue, MATH_CONTEXT)
                .divide(price, MATH_CONTEXT)
                .setScale(PERCENTAGE_SCALE, ROUNDING_MODE);
    }
}
