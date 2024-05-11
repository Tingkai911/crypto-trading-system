package com.aquariux.crypto.client.impl;

import com.aquariux.crypto.configuration.PriceAggregationConfig;
import com.aquariux.crypto.exception.PriceRetrievalException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PriceRetrievalClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PriceAggregationConfig config;

    @InjectMocks
    private PriceRetrievalClient priceRetrievalClient;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(config.getBinance()).thenReturn("https://test-binance-url.com");
        when(config.getHuobi()).thenReturn("https://test-huobi-url.com");
    }

    @Test
    void testGetPriceFromBinance_Success() throws Exception {
        String binanceResponse = "[{\"symbol\": \"BTCUSDT\", \"bidPrice\": \"50000\", \"askPrice\": \"50100\"}]";
        JsonNode responseJson = mapper.readTree(binanceResponse);

        ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(responseJson, HttpStatus.OK);
        when(restTemplate.getForEntity(config.getBinance(), JsonNode.class)).thenReturn(responseEntity);

        JsonNode result = priceRetrievalClient.getPriceFromBinance();
        assertNotNull(result);
        assertTrue(result.isArray());
        assertEquals("BTCUSDT", result.get(0).get("symbol").asText());
    }

    @Test
    void testGetPriceFromBinance_Error() {
        ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        when(restTemplate.getForEntity(config.getBinance(), JsonNode.class)).thenReturn(responseEntity);

        Exception exception = assertThrows(PriceRetrievalException.class, () -> priceRetrievalClient.getPriceFromBinance());
        assertTrue(exception.getMessage().contains("Unable to retrieve data from binance"));
    }

    @Test
    void testGetPriceFromHuobi_Success() throws Exception {
        String huobiResponse = "{\"data\": [{\"symbol\": \"btcusdt\", \"bid\": \"60000\", \"ask\": \"60100\"}]}";
        JsonNode responseJson = mapper.readTree(huobiResponse);

        ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(responseJson, HttpStatus.OK);
        when(restTemplate.getForEntity(config.getHuobi(), JsonNode.class)).thenReturn(responseEntity);

        JsonNode result = priceRetrievalClient.getPriceFromHuobi();
        assertNotNull(result);
        assertTrue(result.has("data"));
        assertEquals("btcusdt", result.get("data").get(0).get("symbol").asText());
    }

    @Test
    void testGetPriceFromHuobi_Error() {
        ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        when(restTemplate.getForEntity(config.getHuobi(), JsonNode.class)).thenReturn(responseEntity);

        Exception exception = assertThrows(PriceRetrievalException.class, () -> priceRetrievalClient.getPriceFromHuobi());
        assertTrue(exception.getMessage().contains("Unable to retrieve data from huobi"));
    }

}

