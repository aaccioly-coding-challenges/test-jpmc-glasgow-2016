package com.jpmorgan.sssm.service;

import com.jpmorgan.sssm.model.Stock;
import com.jpmorgan.sssm.model.Trade;
import com.jpmorgan.sssm.repository.StockRepository;
import com.jpmorgan.sssm.repository.StockRepositoryInMemoryImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static com.jpmorgan.sssm.model.Trade.buyNow;
import static com.jpmorgan.sssm.model.Trade.createOrder;
import static com.jpmorgan.sssm.model.Trade.sellNow;
import static com.jpmorgan.sssm.model.TradeIndicator.BUY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Anthony Accioly
 */
public class StockMarketServiceImplTest {
    private StockMarketService stockMarketService;
    private StockRepository stockRepository;

    @BeforeMethod
    public void setUp() {
        this.stockRepository = StockRepositoryInMemoryImpl.getInstance();
        ((StockRepositoryInMemoryImpl)stockRepository).clearHistory();
        this.stockMarketService = new StockMarketServiceImpl(stockRepository);
    }

    @Test
    public void testCanCalculateVolumeWeightedStockPriceForStock() {
        final Stock stock = Stock.createCommonStock("STCK", new BigDecimal("5.00"), new BigDecimal("200.00"));
        stockRepository.record(sellNow(stock, 100, new BigDecimal("220.00")));
        stockRepository.record(sellNow(stock, 300, new BigDecimal("240.00")));

        final BigDecimal vWAP = stockMarketService.volumeWeightedStockPrice(stock);

        assertThat(vWAP).isEqualTo(new BigDecimal("235.00"));
    }

    @Test
    public void testVolumeWeightedStockPriceForStockIgnoresTradesOutsideTheTimeHorizon() {
        final Stock stock = Stock.createCommonStock("POP", new BigDecimal("8.00"), new BigDecimal("100.00"));
        stockRepository.record(buyNow(stock, 100, new BigDecimal("190.00")));
        stockRepository.record(buyNow(stock, 100, new BigDecimal("210.00")));

        final Instant oneHourAgo = Instant.now().minus(Duration.ofHours(1));
        final Trade tradeOutsideTheTimeHorizon = createOrder(stock, oneHourAgo, 1000, BUY, new BigDecimal("300.00"));
        stockRepository.record(tradeOutsideTheTimeHorizon);

        final BigDecimal vWAP = stockMarketService.volumeWeightedStockPrice(stock);

        assertThat(vWAP).isEqualTo(new BigDecimal("200.00"));
    }

    @Test
    public void testVolumeWeightedStockPriceIsZeroWhenThereAreNoTrades() throws Exception {
        final Stock stock = Stock.createCommonStock("ZERO", new BigDecimal("8.00"), new BigDecimal("100.00"));

        final BigDecimal vWAP = stockMarketService.volumeWeightedStockPrice(stock);

        assertThat(vWAP).isEqualByComparingTo(BigDecimal.ZERO)
                .isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    public void testCanCalculateAllShareIndex() {
        final Stock firstStock = Stock.createCommonStock("STK1", new BigDecimal("5.00"), new BigDecimal("200.00"));
        stockRepository.record(sellNow(firstStock, 100, new BigDecimal("220.00")));
        stockRepository.record(sellNow(firstStock, 300, new BigDecimal("240.00")));

        final Stock secondStock = Stock.createCommonStock("STK2", new BigDecimal("8.00"), new BigDecimal("100.00"));
        stockRepository.record(buyNow(secondStock, 100, new BigDecimal("190.00")));
        stockRepository.record(buyNow(secondStock, 100, new BigDecimal("210.00")));

        final BigDecimal allShareIndex = stockMarketService.allShareIndex();

        assertThat(allShareIndex).isEqualTo(new BigDecimal("216.79"));
    }

    @Test
    public void testShareIndexIsZeroWhenThereAreNoTrades() {
        final Stock stock = Stock.createCommonStock("ZERO", new BigDecimal("8.00"), new BigDecimal("100.00"));

        final BigDecimal allShareIndex = stockMarketService.allShareIndex();

        assertThat(allShareIndex).isEqualByComparingTo(BigDecimal.ZERO)
                .isEqualTo(new BigDecimal("0.00"));
    }

}