package com.jpmorgan.sssm.math;

import lombok.experimental.UtilityClass;

import java.math.MathContext;
import java.math.RoundingMode;

import static java.math.RoundingMode.HALF_EVEN;

/**
 * @author Anthony Accioly
 */
@UtilityClass
public class FixedPointConstants {

    private static final int INTERNAL_OPERATIONS_PRECISION = 30;

    public static final RoundingMode ROUNDING_MODE = HALF_EVEN;
    public static final MathContext MATH_CONTEXT = new MathContext(INTERNAL_OPERATIONS_PRECISION, ROUNDING_MODE);
    public static final int CURRENCY_SCALE = 2;
    public static final int PERCENTAGE_SCALE = 5;

}
