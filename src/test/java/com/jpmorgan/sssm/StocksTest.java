package com.jpmorgan.sssm;

import static org.assertj.core.api.Assertions.*;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
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

    @Test
    void guavaWorks() {
        final Multiset<Integer> multiset = ImmutableMultiset.<Integer>builder()
                .addCopies(10, 4)
                .addCopies(20, 3)
                .build();
        final Integer totalValue = multiset.entrySet().stream()
                .reduce(0, (i, e) -> i + e.getElement() * e.getCount(), Integer::sum);
        assertThat(multiset)
                .hasSize(7);
        assertThat(totalValue).isEqualTo(100);

    }
}
