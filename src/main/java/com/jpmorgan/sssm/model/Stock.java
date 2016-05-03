package com.jpmorgan.sssm.model;

import lombok.Data;
import lombok.NonNull;
import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.jpmorgan.sssm.math.FixedPointConstants.*;
import static java.math.BigDecimal.ZERO;

/**
 * @author Anthony Accioly
 */
@Data
public abstract class Stock {

    protected static final BigDecimal MIN_VALUE = new BigDecimal("0.01");
    protected static final String MIN_VALUE_MESSAGE = "%s has to be equal or greater than %s";

    public static Stock createCommonStock(String symbol, BigDecimal lastDividend, BigDecimal parValue) {
        return new CommonStock(symbol, lastDividend, parValue);
    }

    public static Stock createPreferredStock(String symbol, BigDecimal lastDividend, BigDecimal parValue, BigDecimal fixedDividend) {
        return new PreferredStock(symbol, lastDividend, parValue, fixedDividend);
    }

    @NonNull final String symbol;
    @NonNull final BigDecimal lastDividend;
    @NonNull final BigDecimal parValue;

    protected Stock(String symbol, BigDecimal lastDividend, BigDecimal parValue) {
        checkArgument(lastDividend.compareTo(ZERO) >= 0, MIN_VALUE_MESSAGE, "Last dividend", ZERO);
        checkArgument(parValue.compareTo(MIN_VALUE) >= 0, MIN_VALUE_MESSAGE, "Par Value", MIN_VALUE);

        this.symbol = symbol;
        this.lastDividend = lastDividend.setScale(CURRENCY_SCALE, ROUNDING_MODE);
        this.parValue = parValue.setScale(CURRENCY_SCALE, ROUNDING_MODE);
    }


    public abstract BigDecimal getDividendYield(BigDecimal price);

    public BigDecimal getPriceToEarningsRatio(@NonNull BigDecimal price) {
        checkArgument(price.compareTo(MIN_VALUE) >= 0, MIN_VALUE_MESSAGE, "Price", MIN_VALUE);
        checkState(lastDividend.compareTo(ZERO) > 0, "No reported dividends for last period, can't compute PE/Ratio");

        return price.divide(lastDividend, MATH_CONTEXT)
                .setScale(PERCENTAGE_SCALE, ROUNDING_MODE)
                .stripTrailingZeros();
    }

}
