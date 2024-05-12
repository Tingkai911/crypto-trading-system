package com.aquariux.crypto.client.impl;

import com.aquariux.crypto.client.IPriceRetrievalClient;
import com.aquariux.crypto.configuration.PriceAggregationConfig;
import com.aquariux.crypto.exception.PriceRetrievalClientException;
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

    public JsonNode getPriceFromBinance() throws PriceRetrievalClientException {
        try {
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(config.getBinance(), JsonNode.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                log.error("Unable to retrieve data from binance API: {}", response.getStatusCode());
                throw new PriceRetrievalClientException("Unable to retrieve data from binance API: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new PriceRetrievalClientException(e.getMessage(), e);
        }
    }

    public JsonNode getPriceFromHuobi() throws PriceRetrievalClientException {
        try {
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(config.getHuobi(), JsonNode.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                log.error("Unable to retrieve data from huobi API: {}", response.getStatusCode());
                throw new PriceRetrievalClientException("Unable to retrieve data from huobi API: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new PriceRetrievalClientException(e.getMessage(), e);
        }
    }
}
