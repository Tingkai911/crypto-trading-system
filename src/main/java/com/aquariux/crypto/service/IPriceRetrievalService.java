package com.aquariux.crypto.service;

import com.aquariux.crypto.exception.PriceNotFoundException;
import com.aquariux.crypto.exception.PriceRetrievalException;
import com.aquariux.crypto.model.Price;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IPriceRetrievalService {
    Map<String, Price> getPriceFromBinance(Set<String> symbolSet) throws PriceRetrievalException;
    Map<String, Price> getPriceFromHuobi(Set<String> symbolSet) throws PriceRetrievalException;
    List<Price> getAllPrices() throws PriceNotFoundException;
    Price getPriceBySymbol(String symbol) throws PriceNotFoundException;
}
