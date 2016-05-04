package com.jpmorgan.sssm.repository;

import com.jpmorgan.sssm.model.Stock;
import com.jpmorgan.sssm.model.Trade;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static com.jpmorgan.sssm.model.Stock.createCommonStock;
import static com.jpmorgan.sssm.model.Trade.buyNow;
import static com.jpmorgan.sssm.model.Trade.sellNow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Anthony Accioly
 */
public class StockRepositoryInMemoryImplTest {

    @BeforeMethod
    public void setUp() {
        final StockRepository repository = StockRepositoryInMemoryImpl.getInstance();
        ((StockRepositoryInMemoryImpl)repository).clearHistory();
    }

    @Test
    public void testCanGetInstance() {
        assertThat(StockRepositoryInMemoryImpl.getInstance())
                .as("Is in memory instance of repository").isExactlyInstanceOf(StockRepositoryInMemoryImpl.class);
    }

    @Test
    public void testAlwaysReturnsTheSameInstance() {
        final StockRepository firstInstance = StockRepositoryInMemoryImpl.getInstance();
        final StockRepository secondInstance = StockRepositoryInMemoryImpl.getInstance();

        assertThat(firstInstance).as("StockRepositoryInMemoryImpl is singleton").isSameAs(secondInstance);
    }

    @Test
    public void testCanRecordTradeForNewStock() {
        final StockRepository repository = StockRepositoryInMemoryImpl.getInstance();
        final Stock stock = createCommonStock("STCK", new BigDecimal("2"), new BigDecimal("80"));
        final Trade trade = buyNow(stock, 10, new BigDecimal("1000.50"));

        repository.record(trade);

        assertThat(repository.findAllStocks()).hasSize(1).contains(stock);
        assertThat(repository.findTradesByStock(stock)).hasSize(1).contains(trade);
    }

    @Test
    public void testCanRecordTradeForMultipleStocks() {
        final StockRepository repository = StockRepositoryInMemoryImpl.getInstance();
        final Stock firstStock = createCommonStock("STK1", new BigDecimal("2"), new BigDecimal("80"));
        final Stock secondStock = createCommonStock("STK2", new BigDecimal("6"), new BigDecimal("120"));
        final Trade firstTrade = buyNow(firstStock, 10, new BigDecimal("50.10"));
        final Trade secondTrade = sellNow(secondStock, 10, new BigDecimal("800.53"));
        final Trade thirdTrade = sellNow(secondStock, 20, new BigDecimal("2000.00"));

        repository.record(firstTrade);
        repository.record(secondTrade);
        repository.record(thirdTrade);

        assertThat(repository.findAllStocks()).hasSize(2).contains(firstStock, secondStock);
        assertThat(repository.findTradesByStock(firstStock)).hasSize(1).contains(firstTrade);
        assertThat(repository.findTradesByStock(secondStock)).hasSize(2).contains(secondTrade, thirdTrade);
    }

    @Test
    public void testCanNotRecordNullTrade() {
        final StockRepository repository = StockRepositoryInMemoryImpl.getInstance();

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> repository.record(null));
    }

}