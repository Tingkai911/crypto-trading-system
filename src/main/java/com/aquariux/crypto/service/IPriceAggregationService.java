package com.aquariux.crypto.service;

import java.util.Map;

public interface IPriceAggregationService {
    Map<String, Double> getPriceFromBinance() throws Exception;
    Map<String, Double> getPriceFromHuobi() throws Exception;
}
