package com.aquariux.crypto.service;

import com.aquariux.crypto.model.Price;

import java.util.Map;
import java.util.Set;

public interface IPriceRetrievalService {
    Map<String, Price> getPriceFromBinance(Set<String> symbolSet) throws Exception;
    Map<String, Price> getPriceFromHuobi(Set<String> symbolSet) throws Exception;
}
