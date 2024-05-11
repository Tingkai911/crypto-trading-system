package com.aquariux.crypto.runner;

import com.aquariux.crypto.model.Price;
import com.aquariux.crypto.repository.IPriceRepository;
import com.aquariux.crypto.service.IPriceRetrievalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@AllArgsConstructor
public class PriceAggregationRunner {
    private final IPriceRetrievalService service;
    private final IPriceRepository priceRepository;

    private final Set<String> symbolSet = Set.of("ETHUSDT", "BTCUSDT");

    @Scheduled(fixedRate = 10000, initialDelay = 0)
    public void runTask() {
        try {
            log.info("Price aggregation started");
            Map<String, Price> binancePrices = service.getPriceFromBinance(symbolSet);
            Map<String, Price> huobiPrices = service.getPriceFromHuobi(symbolSet);

            log.info(binancePrices.toString());
            log.info(huobiPrices.toString());

            List<Price> aggregatedPrices = new ArrayList<>();
            for (String symbol : symbolSet) {
                Price binancePrice = binancePrices.getOrDefault(symbol, null);
                Price huobiPrice = huobiPrices.getOrDefault(symbol, null);
                if (binancePrice != null && huobiPrice != null) {
                    // Want the highest Bid/Sell price and the lowest Ask/Buy price
                    aggregatedPrices.add(new Price(symbol, Math.max(binancePrice.getBid(), huobiPrice.getBid()),
                            Math.min(binancePrice.getAsk(), huobiPrice.getAsk()), true));
                }
                else if (huobiPrice != null) {
                    aggregatedPrices.add(huobiPrice);
                }
                else if (binancePrice != null) {
                    aggregatedPrices.add(binancePrice);
                }
                else {
                    // Stop trading for that pair (To mark as unavailable in DB)
                    aggregatedPrices.add(new Price(symbol, 0, 0, false));
                }
            }

            log.info(aggregatedPrices.toString());
            priceRepository.saveAll(aggregatedPrices);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
