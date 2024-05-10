package com.aquariux.crypto.client;

import com.fasterxml.jackson.databind.JsonNode;

public interface IPriceAggregationClient {
    JsonNode getPriceFromBinance() throws Exception;
    JsonNode getPriceFromHuobi() throws Exception;
}
