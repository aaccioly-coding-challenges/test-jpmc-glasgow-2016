package com.jpmorgan.sssm.model;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;


import static com.jpmorgan.sssm.model.Stock.createCommonStock;
import static com.jpmorgan.sssm.model.Stock.createPreferredStock;
import static com.jpmorgan.sssm.model.Trade.buyNow;
import static com.jpmorgan.sssm.model.Trade.createOrder;
import static com.jpmorgan.sssm.model.Trade.sellNow;
import static com.jpmorgan.sssm.model.TradeIndicator.BUY;
import static com.jpmorgan.sssm.model.TradeIndicator.SELL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Anthony Accioly
 */
public class TradeTest {

    private final Stock stock = createCommonStock("STOC", new BigDecimal("10"), new BigDecimal("100"));

    @Test
    public void testCanBuyStock() {
        final Trade buyOrder = buyNow(stock, 10, new BigDecimal("1020"));

        assertThat(buyOrder).as("Buy order is a trade").isExactlyInstanceOf(Trade.class)
                .as("Stock is %s", stock).hasFieldOrPropertyWithValue("stock", stock)
                .as("Stock has a timestamp ").hasFieldOrProperty("timestamp")
                .as("Quantity is 10").hasFieldOrPropertyWithValue("quantity", 10)
                .as("Trade indicator is BUY").hasFieldOrPropertyWithValue("indicator", BUY)
                .as("Price is 1020.00").hasFieldOrPropertyWithValue("price", new BigDecimal("1020.00"));
    }

    @Test
    public void testCanSellStock() {
        final Trade sellOrder = sellNow(stock, 10, new BigDecimal("998"));

        assertThat(sellOrder).as("Sell order is a trade").isExactlyInstanceOf(Trade.class)
                .as("Stock is %s", stock).hasFieldOrPropertyWithValue("stock", stock)
                .as("Stock has a timestamp ").hasFieldOrProperty("timestamp")
                .as("Quantity is 10").hasFieldOrPropertyWithValue("quantity", 10)
                .as("Trade indicator is SELL").hasFieldOrPropertyWithValue("indicator", SELL)
                .as("Price is 998.00").hasFieldOrPropertyWithValue("price", new BigDecimal("998.00"));
    }

    @Test
    public void testCanCreateOrderWithCustomTimestamp() {
        // 5 minutes ago
        final Instant timestamp = Instant.now().minus(Duration.ofMinutes(5));
        final Stock preferredStock = createPreferredStock("PAST", new BigDecimal("2"), new BigDecimal("100"), new BigDecimal("0.02"));

        final Trade order = createOrder(preferredStock, timestamp, 5, BUY, new BigDecimal("500"));

        assertThat(order).as("Buy order is a trade").isExactlyInstanceOf(Trade.class)
                .as("Stock is %s", preferredStock).hasFieldOrPropertyWithValue("stock", preferredStock)
                .as("Stock has custom timestamp").hasFieldOrPropertyWithValue("timestamp", timestamp)
                .as("Quantity is 5").hasFieldOrPropertyWithValue("quantity", 5)
                .as("Trade indicator is BUY").hasFieldOrPropertyWithValue("indicator", BUY)
                .as("Price is 500.00").hasFieldOrPropertyWithValue("price", new BigDecimal("500.00"));
    }

    @DataProvider(name = "ordersWithNullValues")
    private Object[][] ordersWithNullValues() {
        return new Object[][]{
                {"Null stock", null, Instant.now(), BUY, BigDecimal.ONE},
                {"Null timestamp", stock, null, SELL, BigDecimal.ONE},
                {"Null indicator", stock, Instant.now(), null, BigDecimal.ONE},
                {"Null price", stock, Instant.now(), BUY, null}
        };
    }

    @Test(dataProvider = "ordersWithNullValues")
    public void testCanNotCreateOrdersWithNullValues(String nullParam, Stock stock, Instant timestamp, TradeIndicator indicator, BigDecimal price) {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> createOrder(stock, timestamp, 1, indicator, price)).as(nullParam);
    }


    @Test
    public void testCanNotTradeLessThanOneStock() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> buyNow(stock, 0, new BigDecimal("10.0")))
                .withMessage("Quantity has to be equal or greater than 1");
    }

    @Test
    public void testCanNotPayLessThanOneCentInAOrder() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> buyNow(stock, 1, new BigDecimal("0.009")))
                .withMessage("Price has to be equal or greater than 0.01");
    }

}