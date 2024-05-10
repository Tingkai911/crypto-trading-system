package com.aquariux.crypto.service.impl;

import com.aquariux.crypto.client.IPriceAggregationClient;
import com.aquariux.crypto.service.IPriceAggregationService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class PriceAggregationService implements IPriceAggregationService {
    private final IPriceAggregationClient priceAggregationClient;

    // TODO: To extract out information on ETHUSDT and BTCUSDT
    @Override
    public Map<String, Double> getPriceFromBinance() throws Exception{
        JsonNode node = priceAggregationClient.getPriceFromBinance();
        log.info(node.toPrettyString());
        return Map.of();
    }

    // TODO: To extract out information on ETHUSDT and BTCUSDT
    @Override
    public Map<String, Double> getPriceFromHuobi() throws Exception {
        JsonNode node = priceAggregationClient.getPriceFromHuobi();
        log.info(node.toPrettyString());
        return Map.of();
    }
}
