package com.aquariux.crypto.runner;

import com.aquariux.crypto.service.IPriceAggregationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class PriceAggregationRunner {
    private final IPriceAggregationService service;

    @Scheduled(fixedRate = 10000, initialDelay = 0)
    public void runTask() {
        try {
            service.getPriceFromBinance();
            service.getPriceFromHuobi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
