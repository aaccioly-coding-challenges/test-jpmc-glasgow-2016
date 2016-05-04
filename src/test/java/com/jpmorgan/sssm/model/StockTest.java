package com.jpmorgan.sssm.model;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static com.jpmorgan.sssm.model.Stock.createCommonStock;
import static com.jpmorgan.sssm.model.Stock.createPreferredStock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.data.Offset.offset;

/**
 * @author Anthony Accioly
 */
public class StockTest {
    @Test
    public void testCanCreateCommonStock() {
        final Stock commonStock = createCommonStock("COMM", new BigDecimal("10"), new BigDecimal("100"));

        assertThat(commonStock).as("Stock type is common").isExactlyInstanceOf(CommonStock.class)
                .as("Stock Symbol is COMM").hasFieldOrPropertyWithValue("symbol", "COMM")
                .as("Last Dividend is 10.00").hasFieldOrPropertyWithValue("lastDividend", new BigDecimal("10.00"))
                .as("Par value is 100.00").hasFieldOrPropertyWithValue("parValue", new BigDecimal("100.00"));
    }

    @Test
    public void testCanCreatePreferredStock() {
        final Stock preferedStock = Stock.createPreferredStock("PREF", new BigDecimal("8"), new BigDecimal("100"), new BigDecimal("0.02"));

        assertThat(preferedStock).as("Stock type is common").isExactlyInstanceOf(PreferredStock.class)
                .as("Stock Symbol is PREF").hasFieldOrPropertyWithValue("symbol", "PREF")
                .as("Last Dividend was 8.00").hasFieldOrPropertyWithValue("lastDividend", new BigDecimal("8.00"))
                .as("Par value is 100.00").hasFieldOrPropertyWithValue("parValue", new BigDecimal("100.00"))
                .as("Fixed Dividend is 0.02").hasFieldOrPropertyWithValue("fixedDividend", new BigDecimal("0.02"));
    }

    @Test
    public void testCanCreateStockWithZeroDividend() {
        final Stock zeroDividendStock = createCommonStock("ZERO", BigDecimal.ZERO, new BigDecimal("100"));

        assertThat(zeroDividendStock).as("Last Dividend was 0").hasFieldOrPropertyWithValue("lastDividend", new BigDecimal("0.00"));
    }

    @Test
    public void testCantNotCreateStockWithNullDividend() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Stock.createCommonStock("NULL", null, new BigDecimal("100")));
    }

    @Test
    public void testCantNotCreateStockBellowMinimumParValue() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                Stock.createCommonStock("BMPV", BigDecimal.ONE, new BigDecimal("0.005")))
                .withMessage("Par Value has to be equal or greater than 0.01");
    }

    @Test
    public void testCantNotCreatePreferredStockWithFixedDividendBellow1Percent() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                Stock.createPreferredStock("MDIV", BigDecimal.ONE, new BigDecimal("50.55"), new BigDecimal("0.009")))
                .withMessage("Fixed Dividend has to be equal or greater than 0.01");
    }

    @Test
    public void testGetDividendYieldForCommonStock() {
        final Stock commonStock = createCommonStock("ALE", new BigDecimal("23"), new BigDecimal("60"));

        assertThat(commonStock.getDividendYield(new BigDecimal("60")))
                .as("Is around 0.38").isCloseTo(new BigDecimal("0.38"), offset(new BigDecimal("0.01")))
                .as("Has been rounded precisely to the fifth digit").isEqualByComparingTo(new BigDecimal("0.38333"));
    }

    @Test
    public void testGetDividendYieldForPreferredStock() {
        final Stock preferredStock = createPreferredStock("GIN", new BigDecimal("8"), new BigDecimal("100"), new BigDecimal("0.02"));

        assertThat(preferredStock.getDividendYield(new BigDecimal("90")))
                .as("Is around 0.02").isCloseTo(new BigDecimal("0.02"), offset(new BigDecimal("0.01")))
                .as("Has been rounded precisely to the fifth digit").isEqualByComparingTo(new BigDecimal("0.02222"));
    }

    @DataProvider(name = "stocks")
    private Object[][] stocks() {
        return new Object[][]{
                {"Common", createCommonStock("COMM", new BigDecimal("8"), new BigDecimal("100"))},
                {"Preferred", createPreferredStock("PREF", new BigDecimal("8"), new BigDecimal("100"), new BigDecimal("0.02"))}
        };
    }

    @Test(dataProvider = "stocks")
    public void testCanNoGetDividendYieldForPriceBellowMinimum(String stockType, Stock stock) {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> stock.getDividendYield(new BigDecimal("0.00001")))
                .as("%s stock exception has expected message", stockType).withMessage("Price has to be equal or greater than 0.01");

    }

    @Test
    public void testGetPriceToEarningsRatio() throws Exception {
        final Stock cheapStock = createCommonStock("CHEP", new BigDecimal("1000"), BigDecimal.ONE);
        final Stock expensiveStock = createCommonStock("EXPS", new BigDecimal("0.05"), new BigDecimal("10000"));

        final SoftAssertions sofly = new SoftAssertions();
        sofly.assertThat(cheapStock.getPriceToEarningsRatio(new BigDecimal("0.05")))
                .as("PE/Ratio for cheap stock with high dividend is low").isEqualByComparingTo(new BigDecimal("0.00005"));
        sofly.assertThat(expensiveStock.getPriceToEarningsRatio(new BigDecimal("10000")))
                .as("PE/Ratio for expensive stock with lowis high").isEqualByComparingTo(new BigDecimal("200000"));

        sofly.assertAll();
    }

    @Test
    public void testCanNotGetPriceToEarningRatioOnZeroDividend() throws Exception {
        final Stock zeroDividendStock = createCommonStock("ZERO", BigDecimal.ZERO, new BigDecimal("100"));
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> zeroDividendStock.getPriceToEarningsRatio(BigDecimal.ONE))
                .withMessage("No reported dividends for last period, can't compute PE/Ratio");
    }

    @Test(dataProvider = "stocks")
    public void testCanNoGetPriceToEarningRatioOnPriceBellowMinimum(String stockType, Stock stock) {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> stock.getPriceToEarningsRatio(new BigDecimal("0.00001")))
                .as("%s stock exception has expected message", stockType).withMessage("Price has to be equal or greater than 0.01");
    }

}