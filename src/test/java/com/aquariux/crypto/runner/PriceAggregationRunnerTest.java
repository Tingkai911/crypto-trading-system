package com.aquariux.crypto.runner;

import com.aquariux.crypto.model.Price;
import com.aquariux.crypto.repository.IPriceRepository;
import com.aquariux.crypto.service.IPriceRetrievalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PriceAggregationRunnerTest {

    @Mock
    private IPriceRetrievalService service;

    @Mock
    private IPriceRepository priceRepository;

    @InjectMocks
    private PriceAggregationRunner priceAggregationRunner;

    @Test
    void testRunTask_SuccessfulAggregation() throws Exception {
        Map<String, Price> binancePrices = new HashMap<>();
        binancePrices.put("ETHUSDT", new Price("ETHUSDT", 2000.0, 2050.0, true));
        binancePrices.put("BTCUSDT", new Price("BTCUSDT", 40000.0, 40500.0, true));

        Map<String, Price> huobiPrices = new HashMap<>();
        huobiPrices.put("ETHUSDT", new Price("ETHUSDT", 1995.0, 2055.0, true));
        huobiPrices.put("BTCUSDT", new Price("BTCUSDT", 39900.0, 40500.0, true));

        when(service.getPriceFromBinance(any())).thenReturn(binancePrices);
        when(service.getPriceFromHuobi(any())).thenReturn(huobiPrices);

        priceAggregationRunner.runTask();

        verify(service).getPriceFromBinance(Set.of("ETHUSDT", "BTCUSDT"));
        verify(service).getPriceFromHuobi(Set.of("ETHUSDT", "BTCUSDT"));
        verify(priceRepository).saveAll(anyList());

        ArgumentCaptor<List<Price>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(priceRepository).saveAll(argumentCaptor.capture());

        List<Price> capturedPrices = argumentCaptor.getValue();
        assertNotNull(capturedPrices);
        assertEquals(2, capturedPrices.size());

        // Verify ETH
        Price ethPrice = capturedPrices.stream().filter(p -> p.getSymbol().equals("ETHUSDT")).findFirst().orElse(null);
        assertNotNull(ethPrice);
        assertEquals(2000.0, ethPrice.getBid());
        assertEquals(2050.0, ethPrice.getAsk());

        // Verify BTC
        Price btcPrice = capturedPrices.stream().filter(p -> p.getSymbol().equals("BTCUSDT")).findFirst().orElse(null);
        assertNotNull(btcPrice);
        assertEquals(40000.0, btcPrice.getBid());
        assertEquals(40500.0, btcPrice.getAsk());
    }

    @Test
    void testRunTask_BinanceDataMissing() throws Exception {
        // Simulate missing Binance data
        Map<String, Price> binancePrices = new HashMap<>();

        Map<String, Price> huobiPrices = new HashMap<>();
        huobiPrices.put("ETHUSDT", new Price("ETHUSDT", 1995.0, 2055.0, true));
        huobiPrices.put("BTCUSDT", new Price("BTCUSDT", 39900.0, 40500.0, true));

        when(service.getPriceFromBinance(any())).thenReturn(binancePrices);
        when(service.getPriceFromHuobi(any())).thenReturn(huobiPrices);

        priceAggregationRunner.runTask();

        verify(service).getPriceFromBinance(Set.of("ETHUSDT", "BTCUSDT"));
        verify(service).getPriceFromHuobi(Set.of("ETHUSDT", "BTCUSDT"));
        verify(priceRepository).saveAll(anyList());

        ArgumentCaptor<List<Price>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(priceRepository).saveAll(argumentCaptor.capture());

        List<Price> capturedPrices = argumentCaptor.getValue();
        assertNotNull(capturedPrices);
        assertEquals(2, capturedPrices.size());

        // Verify aggregated ETH price (from Huobi only)
        Price ethPrice = capturedPrices.stream().filter(p -> p.getSymbol().equals("ETHUSDT")).findFirst().orElse(null);
        assertNotNull(ethPrice);
        assertEquals(1995.0, ethPrice.getBid());
        assertEquals(2055.0, ethPrice.getAsk());

        // Verify aggregated BTC price (from Huobi only)
        Price btcPrice = capturedPrices.stream().filter(p -> p.getSymbol().equals("BTCUSDT")).findFirst().orElse(null);
        assertNotNull(btcPrice);
        assertEquals(39900.0, btcPrice.getBid());
        assertEquals(40500.0, btcPrice.getAsk());
    }

    @Test
    void testRunTask_HuobiDataMissing() throws Exception {
        // Simulate missing Huobi data
        Map<String, Price> binancePrices = new HashMap<>();
        binancePrices.put("ETHUSDT", new Price("ETHUSDT", 2000.0, 2050.0, true));
        binancePrices.put("BTCUSDT", new Price("BTCUSDT", 40000.0, 40500.0, true));

        Map<String, Price> huobiPrices = new HashMap<>();

        when(service.getPriceFromBinance(any())).thenReturn(binancePrices);
        when(service.getPriceFromHuobi(any())).thenReturn(huobiPrices);

        priceAggregationRunner.runTask();

        verify(service).getPriceFromBinance(Set.of("ETHUSDT", "BTCUSDT"));
        verify(service).getPriceFromHuobi(Set.of("ETHUSDT", "BTCUSDT"));
        verify(priceRepository).saveAll(anyList());

        ArgumentCaptor<List<Price>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(priceRepository).saveAll(argumentCaptor.capture());

        List<Price> capturedPrices = argumentCaptor.getValue();
        assertNotNull(capturedPrices);
        assertEquals(2, capturedPrices.size());

        // Verify aggregated ETH price (from Binance only)
        Price ethPrice = capturedPrices.stream().filter(p -> p.getSymbol().equals("ETHUSDT")).findFirst().orElse(null);
        assertNotNull(ethPrice);
        assertEquals(2000.0, ethPrice.getBid());
        assertEquals(2050.0, ethPrice.getAsk());

        // Verify aggregated BTC price (from Binance only)
        Price btcPrice = capturedPrices.stream().filter(p -> p.getSymbol().equals("BTCUSDT")).findFirst().orElse(null);
        assertNotNull(btcPrice);
        assertEquals(40000.0, btcPrice.getBid());
        assertEquals(40500.0, btcPrice.getAsk());
    }

    @Test
    void testRunTask_BothDataMissing() throws Exception {
        // Simulate missing data from both sources
        Map<String, Price> binancePrices = new HashMap<>();
        Map<String, Price> huobiPrices = new HashMap<>();
        when(service.getPriceFromBinance(any())).thenReturn(binancePrices);
        when(service.getPriceFromHuobi(any())).thenReturn(huobiPrices);

        priceAggregationRunner.runTask();

        verify(service).getPriceFromBinance(Set.of("ETHUSDT", "BTCUSDT"));
        verify(service).getPriceFromHuobi(Set.of("ETHUSDT", "BTCUSDT"));
        verify(priceRepository).saveAll(anyList());

        ArgumentCaptor<List<Price>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(priceRepository).saveAll(argumentCaptor.capture());

        List<Price> capturedPrices = argumentCaptor.getValue();
        assertNotNull(capturedPrices);
        assertEquals(2, capturedPrices.size());

        // Verify aggregated ETH price (both unavailable)
        Price ethPrice = capturedPrices.stream().filter(p -> p.getSymbol().equals("ETHUSDT")).findFirst().orElse(null);
        assertNotNull(ethPrice);
        assertEquals(0.0, ethPrice.getBid());
        assertEquals(0.0, ethPrice.getAsk());
        assertFalse(ethPrice.getAllowTrading());

        // Verify aggregated BTC price (both unavailable)
        Price btcPrice = capturedPrices.stream().filter(p -> p.getSymbol().equals("BTCUSDT")).findFirst().orElse(null);
        assertNotNull(btcPrice);
        assertEquals(0.0, btcPrice.getBid());
        assertEquals(0.0, btcPrice.getAsk());
        assertFalse(btcPrice.getAllowTrading());
    }
}
