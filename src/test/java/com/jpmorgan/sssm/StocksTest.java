package com.jpmorgan.sssm;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * Test class for Stocks.
 *
 * @author Anthony Accioly
 */
public class StocksTest {

    private static final Logger logger = LoggerFactory.getLogger(StocksTest.class);

    @Test
    void testDependenciesAreWorking() {
        logger.info("Logging something");
        final Condition<String> working = new Condition<>(w -> true, "is working!");
        assertThat("This test").is(working);
    }
}
