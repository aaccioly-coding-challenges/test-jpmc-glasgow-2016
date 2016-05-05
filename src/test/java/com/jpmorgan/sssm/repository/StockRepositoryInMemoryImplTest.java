package com.jpmorgan.sssm.repository;

import com.jpmorgan.sssm.model.Stock;
import com.jpmorgan.sssm.model.Trade;
import com.jpmorgan.sssm.model.TradeIndicator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;

import static com.jpmorgan.sssm.model.Stock.createCommonStock;
import static com.jpmorgan.sssm.model.Trade.buyNow;
import static com.jpmorgan.sssm.model.Trade.createOrder;
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

    @Test
    public void testCanRetrieveTradesForStockOverThePastFiveMinutes() {
        final StockRepository repository = StockRepositoryInMemoryImpl.getInstance();
        final Stock stock = createCommonStock("STCK", new BigDecimal("2"), new BigDecimal("80"));
        // 1 hour and a half ago
        final Instant oldTradeTimeStamp = Instant.now().minus(Duration.ofHours(1).plusMinutes(30));
        final Trade oldTrade = createOrder(stock, oldTradeTimeStamp, 15, TradeIndicator.BUY, new BigDecimal("1500.00"));
        // 2 minutes ago
        final Instant recentTradeTimeStamp = Instant.now().minus(Duration.ofMinutes(2));
        final Trade recentTrade = createOrder(stock, recentTradeTimeStamp, 20, TradeIndicator.BUY, new BigDecimal("2000.00"));
        // Right now
        final Trade veryRecentTrade = buyNow(stock, 5, new BigDecimal("500.00"));

        repository.record(oldTrade);
        repository.record(recentTrade);
        repository.record(veryRecentTrade);

        final Instant fiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));
        final Collection<Trade> tradesOnTimeHorizon = repository.findTradesByStockSinceInstant(stock, fiveMinutesAgo);

        assertThat(tradesOnTimeHorizon)
                .as("Has expected size").hasSize(2)
                .as("Contains only recent trades").contains(recentTrade, veryRecentTrade)
                .as("Doesn't contain old trades ").doesNotContain(oldTrade)
                .as("Only contains trades in the past 5 minutes").extracting(Trade::getTimestamp).allMatch(t -> t.isAfter(fiveMinutesAgo));

    }

    @Test
    public void testReturnsEmptySetIfStockHasNoTrades() {
        final StockRepository repository = StockRepositoryInMemoryImpl.getInstance();
        final Stock stock = createCommonStock("PHANT", new BigDecimal("4"), new BigDecimal("150"));

        assertThat(repository.findTradesByStock(stock)).isEmpty();
        assertThat(repository.findTradesByStockSinceInstant(stock, Instant.now())).isEmpty();
    }

    @Test
    public void canNotModifyTheStockMarketUsingReturnedViews() {
        final StockRepository repository = StockRepositoryInMemoryImpl.getInstance();
        final Stock sneakyStock = createCommonStock("SNEKY", new BigDecimal("0.6"), new BigDecimal("66"));
        final Trade sneakyTrade = buyNow(sneakyStock, 1000, new BigDecimal("0.01"));

        final Instant fiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

        final Set<Stock> tradedStocksView = repository.findAllStocks();
        final Collection<Trade> tradesForStockView = repository.findTradesByStock(sneakyStock);
        final Collection<Trade> recentTradesForStockView = repository.findTradesByStockSinceInstant(sneakyStock, fiveMinutesAgo);

        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> tradedStocksView.add(sneakyStock));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> tradesForStockView.add(sneakyTrade));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> recentTradesForStockView.add(sneakyTrade));
    }

}