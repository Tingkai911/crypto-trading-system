package com.aquariux.crypto.service.impl;

import com.aquariux.crypto.client.IPriceRetrievalClient;
import com.aquariux.crypto.exception.PriceNotFoundException;
import com.aquariux.crypto.exception.PriceRetrievalException;
import com.aquariux.crypto.model.Price;
import com.aquariux.crypto.repository.IPriceRepository;
import com.aquariux.crypto.service.IPriceRetrievalService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class PriceRetrievalService implements IPriceRetrievalService {
    private final IPriceRetrievalClient priceAggregationClient;
    private final IPriceRepository priceRepository;

    @Override
    public Map<String, Price> getPriceFromBinance(Set<String> symbolSet) throws PriceRetrievalException {
        JsonNode root;
        Map<String, Price> prices = new HashMap<>();

        try {
            root = priceAggregationClient.getPriceFromBinance();
        } catch (Exception e) {
            log.error("Error retrieving data from Binance API", e);
            throw new PriceRetrievalException("Error retrieving data from Binance API", e);
        }

        if (root == null || !root.isArray()) {
            log.error("Expected an array but got null or a different type from Binance");
            throw new PriceRetrievalException("Received malformed data structure from Binance");
        }

        try {
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
        } catch (Exception e) {
            log.error("Error processing price data from Binance", e);
            throw new PriceRetrievalException("Error processing price data from Binance", e);
        }

        return prices;
    }

    @Override
    public Map<String, Price> getPriceFromHuobi(Set<String> symbolSet) throws PriceRetrievalException {
        JsonNode root;
        Map<String, Price> prices = new HashMap<>();

        try {
            root = priceAggregationClient.getPriceFromHuobi();
        } catch (Exception e) {
            log.error("Error retrieving data from Huobi API", e);
            throw new PriceRetrievalException("Error retrieving data from Huobi API", e);
        }

        if (root == null || !root.has("data") || !root.get("data").isArray()) {
            log.error("Expected an array under 'data', but got something else from Huobi");
            throw new PriceRetrievalException("Received malformed data structure from Huobi");
        }

        JsonNode data = root.get("data");
        try {
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
        } catch (Exception e) {
            log.error("Error processing price data from Huobi", e);
            throw new PriceRetrievalException("Error processing price data from Huobi", e);
        }

        return prices;
    }

    @Override
    public List<Price> getAllPrices() throws PriceNotFoundException {
        List<Price> prices;
        try {
            prices = priceRepository.findAll();
        } catch (Exception e) {
            log.error("Database error occurred while retrieving all prices", e);
            throw new PriceNotFoundException("Failed to retrieve prices due to a data error", e);
        }

        if (prices.isEmpty()) {
            log.warn("No prices found in the repository");
            throw new PriceNotFoundException("No prices found");
        }

        return prices;
    }

    @Override
    public Price getPriceBySymbol(String symbol) throws PriceNotFoundException {
        Optional<Price> price;
        try {
            price = priceRepository.findBySymbol(symbol);
        } catch (Exception e) {
            log.error("Database error occurred while retrieving price for symbol: {}", symbol, e);
            throw new PriceNotFoundException("Failed to retrieve price due to a data error for symbol: " + symbol, e);
        }

        if (price.isEmpty()) {
            log.warn("No price found for symbol: {}", symbol);
            throw new PriceNotFoundException("No price found for symbol: " + symbol);
        }

        return price.get();
    }
}
