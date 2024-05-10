package com.aquariux.crypto.service.impl;

import com.aquariux.crypto.client.IPriceRetrievalClient;
import com.aquariux.crypto.exception.PriceRetrievalException;
import com.aquariux.crypto.model.Price;
import com.aquariux.crypto.repository.PriceRepository;
import com.aquariux.crypto.service.IPriceRetrievalService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class PriceRetrievalService implements IPriceRetrievalService {
    private final IPriceRetrievalClient priceAggregationClient;
    private final PriceRepository priceRepository;

    @Override
    public Map<String, Price> getPriceFromBinance(Set<String> symbolSet) throws Exception {
        JsonNode root = priceAggregationClient.getPriceFromBinance();
        Map<String, Price> prices = new HashMap<>();

        if (root != null && root.isArray()) {
            for (JsonNode objectNode : root) {
                if (objectNode.has("symbol")) {
                    String symbolValue = objectNode.get("symbol").asText().toUpperCase();
                    if (symbolSet.contains(symbolValue)) {
                        double bidPrice = Double.parseDouble(objectNode.get("bidPrice").asText());
                        double askPrice = Double.parseDouble(objectNode.get("askPrice").asText());
                        prices.put(symbolValue, new Price(symbolValue, bidPrice, askPrice, true));
                    }
                }
            }
        } else {
            throw new PriceRetrievalException("Data from binance is corrupted");
        }

        return prices;
    }

    @Override
    public Map<String, Price> getPriceFromHuobi(Set<String> symbolSet) throws Exception {
        JsonNode root = priceAggregationClient.getPriceFromHuobi();
        Map<String, Price> prices = new HashMap<>();

        if (root.has("data") && root.get("data").isArray()) {
            JsonNode data = root.get("data");
            for (JsonNode objectNode : data) {
                if (objectNode.has("symbol")) {
                    String symbolValue = objectNode.get("symbol").asText().toUpperCase();
                    if (symbolSet.contains(symbolValue)) {
                        double bidPrice = Double.parseDouble(objectNode.get("bid").asText());
                        double askPrice = Double.parseDouble(objectNode.get("ask").asText());
                        prices.put(symbolValue, new Price(symbolValue, bidPrice, askPrice, true));
                    }
                }
            }
        } else {
            throw new PriceRetrievalException("Data from huobi is corrupted");
        }

        return prices;
    }
}
