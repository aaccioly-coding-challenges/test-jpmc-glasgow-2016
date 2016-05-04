package com.jpmorgan.sssm.model;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkState;
import static com.jpmorgan.sssm.math.FixedPointMath.CURRENCY_SCALE;
import static com.jpmorgan.sssm.math.FixedPointMath.MATH_CONTEXT;
import static com.jpmorgan.sssm.math.FixedPointMath.MIN_VALUE;
import static com.jpmorgan.sssm.math.FixedPointMath.PERCENTAGE_SCALE;
import static com.jpmorgan.sssm.math.FixedPointMath.ROUNDING_MODE;
import static com.jpmorgan.sssm.math.FixedPointMath.checkArgumentGreaterThanOrEgual;
import static java.math.BigDecimal.ZERO;

/**
 * A generic immutable representation of a capital stock.
 *
 * @author Anthony Accioly
 */
@Data
public abstract class Stock {

    public static Stock createCommonStock(String symbol, BigDecimal lastDividend, BigDecimal parValue) {
        return new CommonStock(symbol, lastDividend, parValue);
    }

    public static Stock createPreferredStock(String symbol, BigDecimal lastDividend, BigDecimal parValue, BigDecimal fixedDividend) {
        return new PreferredStock(symbol, lastDividend, parValue, fixedDividend);
    }

    @NonNull private final String symbol;
    @NonNull private final BigDecimal lastDividend;
    @NonNull private final BigDecimal parValue;

    protected Stock(String symbol, BigDecimal lastDividend, BigDecimal parValue) {
        checkArgumentGreaterThanOrEgual("Last dividend", lastDividend, ZERO);
        checkArgumentGreaterThanOrEgual("Par Value", parValue, MIN_VALUE);

        this.symbol = symbol;
        this.lastDividend = lastDividend.setScale(CURRENCY_SCALE, ROUNDING_MODE);
        this.parValue = parValue.setScale(CURRENCY_SCALE, ROUNDING_MODE);
    }


    /**
     * Given any {@code price} as input, calculates the dividend yield.
     * <p>
     * Dividend yield is a financial ratio that indicates how much a company pays out in dividends each year relative to its share price.
     *
     * @param price the price of the stock. Must be a positive number greater than 0.01.
     *
     * @return the dividend yield given as a percentage with up to 5 decimal digits.
     *
     * @see <a href="http://www.investopedia.com/terms/d/dividendyield.asp">Price-Earnings Ratio (P/E Ratio) Definition | Investopedia</a>
     */
    public abstract BigDecimal getDividendYield(@NonNull  BigDecimal price);

    /**
     * Given any {@code price} as input, calculates the P/E Ratio.
     * <p>
     * In essence, the price-earnings ratio indicates the dollar amount an investor can expect to invest in a company in order to receive one dollar
     * of that company's earnings.
     * <p>
     * Super simple stock market bases P/E ratio on past performance even when dealing with preferred stock. If no dividends where paid P/E Ratio
     * can't de calculated. P/E Ratio can be inconsistent for preferred stocks if last dividend is different than fixed dividend * par value.
     *
     * @param price the price of the stock. Must be a positive number greater than 0.01.
     *
     * @return the P/E ratio given as a value with up to 5 decimal digits.
     *
     * @throws IllegalStateException if no dividends were paid over the last period.
     *
     * @see <a href="http://www.investopedia.com/terms/p/price-earningsratio.asp">Price-Earnings Ratio (P/E Ratio) Definition | Investopedia</a>
     */
    public BigDecimal getPriceToEarningsRatio(@NonNull BigDecimal price) {
        checkArgumentGreaterThanOrEgual("Price", price, MIN_VALUE);
        checkState(lastDividend.compareTo(ZERO) > 0, "No reported dividends for last period, can't compute PE/Ratio");

        return price.divide(lastDividend, MATH_CONTEXT)
                .setScale(PERCENTAGE_SCALE, ROUNDING_MODE)
                .stripTrailingZeros();
    }

}
