package com.jpmorgan.sssm;

import static org.assertj.core.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * Test class for Stocks.
 *
 * @author Anthony Accioly
 */
@Slf4j
public class StocksTest {

    @Test
    void testDependenciesAreWorking() {
        log.info("Logging something");
        final Condition<String> working = new Condition<>(w -> true, "is working!");
        assertThat("This test").is(working);
    }
}
