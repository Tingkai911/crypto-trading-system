package com.aquariux.crypto.service.impl;

import com.aquariux.crypto.client.IPriceRetrievalClient;
import com.aquariux.crypto.exception.PriceNotFoundException;
import com.aquariux.crypto.exception.PriceRetrievalException;
import com.aquariux.crypto.model.Price;
import com.aquariux.crypto.repository.IPriceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PriceRetrievalServiceTest {

    @Mock
    private IPriceRetrievalClient priceAggregationClient;

    @Mock
    private IPriceRepository priceRepository;

    @InjectMocks
    private PriceRetrievalService priceRetrievalService;

    private final ObjectMapper mapper = new ObjectMapper();
    private JsonNode binanceJsonNode;
    private JsonNode huobiJsonNode;

    @BeforeEach
    void setUp() throws Exception {
        String jsonBinance = "[{\"symbol\": \"BTCUSDT\", \"bidPrice\": \"50000\", \"askPrice\": \"50100\"}]";
        binanceJsonNode = mapper.readTree(jsonBinance);
        String jsonHuobi = "{\"data\": [{\"symbol\": \"btcusdt\", \"bid\": \"60000\", \"ask\": \"60100\"}]}";
        huobiJsonNode = mapper.readTree(jsonHuobi);
    }

    @Test
    void testGetPriceFromBinance_Success() throws Exception {
        when(priceAggregationClient.getPriceFromBinance()).thenReturn(binanceJsonNode);
        Set<String> symbols = new HashSet<>(Arrays.asList("BTCUSDT"));
        Map<String, Price> prices = priceRetrievalService.getPriceFromBinance(symbols);

        assertNotNull(prices);
        assertEquals(1, prices.size());
        assertEquals(50000, prices.get("BTCUSDT").getBid());
        assertEquals(50100, prices.get("BTCUSDT").getAsk());
    }

    @Test
    void testGetPriceFromBinance_CorruptedData() throws Exception {
        when(priceAggregationClient.getPriceFromBinance()).thenReturn(null);  // No array
        Set<String> symbols = new HashSet<>(Arrays.asList("BTCUSDT"));

        assertThrows(PriceRetrievalException.class, () -> priceRetrievalService.getPriceFromBinance(symbols));
    }

    @Test
    void testGetPriceFromHuobi_Success() throws Exception {
        when(priceAggregationClient.getPriceFromHuobi()).thenReturn(huobiJsonNode);
        Set<String> symbols = new HashSet<>(Arrays.asList("BTCUSDT"));

        Map<String, Price> prices = priceRetrievalService.getPriceFromHuobi(symbols);
        assertNotNull(prices);
        assertEquals(1, prices.size());
        assertEquals(60000, prices.get("BTCUSDT").getBid());
        assertEquals(60100, prices.get("BTCUSDT").getAsk());
    }

    @Test
    void testGetPriceFromHuobi_CorruptedData() throws Exception {
        String corruptedJsonHuobi = "{}";  // No "data" field
        JsonNode corruptedHuobiJsonNode = mapper.readTree(corruptedJsonHuobi);

        when(priceAggregationClient.getPriceFromHuobi()).thenReturn(corruptedHuobiJsonNode);
        Set<String> symbols = new HashSet<>(Arrays.asList("BTCUSDT"));

        assertThrows(PriceRetrievalException.class, () -> priceRetrievalService.getPriceFromHuobi(symbols));
    }

    @Test
    void testGetAllPrices_Success() throws Exception {
        List<Price> expectedPrices = Arrays.asList(new Price("BTCUSDT", 50000D, 50100D, true));
        when(priceRepository.findAll()).thenReturn(expectedPrices);

        List<Price> prices = priceRetrievalService.getAllPrices();
        assertFalse(prices.isEmpty());
        assertEquals(1, prices.size());
        assertEquals("BTCUSDT", prices.get(0).getSymbol());
    }

    @Test
    void testGetAllPrices_NoPricesFound() {
        when(priceRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(PriceNotFoundException.class, () -> priceRetrievalService.getAllPrices());
    }

    @Test
    void testGetPriceBySymbol_Found() throws Exception {
        Optional<Price> expectedPrice = Optional.of(new Price("BTCUSDT", 50000D, 50100D, true));
        when(priceRepository.findBySymbol("BTCUSDT")).thenReturn(expectedPrice);

        Price price = priceRetrievalService.getPriceBySymbol("BTCUSDT");
        assertNotNull(price);
        assertEquals("BTCUSDT", price.getSymbol());
    }

    @Test
    void testGetPriceBySymbol_NotFound() {
        when(priceRepository.findBySymbol("BTCUSDT")).thenReturn(Optional.empty());

        assertThrows(PriceNotFoundException.class, () -> priceRetrievalService.getPriceBySymbol("BTCUSDT"));
    }
}

