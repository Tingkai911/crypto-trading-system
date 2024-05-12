package com.aquariux.crypto.client;

import com.aquariux.crypto.exception.PriceRetrievalClientException;
import com.fasterxml.jackson.databind.JsonNode;

public interface IPriceRetrievalClient {
    JsonNode getPriceFromBinance() throws PriceRetrievalClientException;
    JsonNode getPriceFromHuobi() throws PriceRetrievalClientException;
}
