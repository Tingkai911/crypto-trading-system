package com.aquariux.crypto.client.impl;

import com.aquariux.crypto.client.IPriceRetrievalClient;
import com.aquariux.crypto.configuration.PriceAggregationConfig;
import com.aquariux.crypto.exception.PriceRetrievalException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@AllArgsConstructor
public class PriceRetrievalClient implements IPriceRetrievalClient {
    private final RestTemplate restTemplate;

    private final PriceAggregationConfig config;

    public JsonNode getPriceFromBinance() throws Exception {
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(config.getBinance(), JsonNode.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new PriceRetrievalException("Unable to retrieve data from binance: " + response.getStatusCode());
        }
    }

    public JsonNode getPriceFromHuobi() throws Exception {
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(config.getHuobi(), JsonNode.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new PriceRetrievalException("Unable to retrieve data from huobi: " + response.getStatusCode());
        }
    }
}
