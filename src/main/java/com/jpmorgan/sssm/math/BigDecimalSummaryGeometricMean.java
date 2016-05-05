package com.jpmorgan.sssm.math;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static com.jpmorgan.sssm.math.FixedPointMath.MATH_CONTEXT;
import static com.jpmorgan.sssm.math.FixedPointMath.checkArgumentGreaterThanOrEgual;

/**
 * A state object for collecting the average mean of a set of {@link java.math.BigDecimal}
 *
 * @author Anthony Accioly
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.MODULE)
public final class BigDecimalSummaryGeometricMean implements Consumer<BigDecimal> {

    private BigDecimal product = BigDecimal.ONE;
    private int count = 0;

    /**
     * Records a new {@code {@link BigDecimal}} value into the summary information.
     *
     * @param value the input value, has to be greater than zero.
     */
    @Override
    public void accept(@NonNull BigDecimal value) {
        checkArgumentGreaterThanOrEgual("value", value, BigDecimal.ZERO);
        count++;
        product = product.multiply(value, MATH_CONTEXT);
    }

    /**
     * Combines the state of another {@code BigDecimalSummaryGeometricMean} into this one.
     *
     * @param other another {@code IntSummaryStatistics}
     */
    public void combine(@NonNull BigDecimalSummaryGeometricMean other) {
        count += other.count;
        product = product.multiply(other.getProduct());
    }

    /**
     * Returns the geometric mean of values recorded, or zero if no values have been
     * recorded.
     *
     * @return the geometric mean of values, or zero if none
     */
    public BigDecimal geometricMean() {
        return count != 0 ? FixedPointMath.nthRoot(count, product) : BigDecimal.ZERO;
    }

}
