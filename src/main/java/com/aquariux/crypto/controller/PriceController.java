package com.aquariux.crypto.controller;

import com.aquariux.crypto.model.Price;
import com.aquariux.crypto.model.Response;
import com.aquariux.crypto.service.IPriceRetrievalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/price")
public class PriceController {
    private final IPriceRetrievalService priceRetrievalService;

    @GetMapping("/v1.0/prices")
    public Response<List<Price>> getAllPrices() throws Exception {
        log.error("getAllPrices");
        Response<List<Price>> response = new Response<>();
        List<Price> prices = priceRetrievalService.getAllPrices();
        response.setCode(HttpStatus.OK.value());
        response.setMessage("List of trading prices");
        response.setData(prices);
        log.info(response.toString());
        return response;
    }

    @GetMapping("/v1.0/{symbol}")
    public Response<Price> getPrice(@PathVariable String symbol) throws Exception {
        log.info("getPrice {}", symbol);
        Response<Price> response = new Response<>();
        symbol = symbol.toUpperCase();
        Price price = priceRetrievalService.getPriceBySymbol(symbol);
        response.setCode(HttpStatus.OK.value());
        response.setMessage("Price for symbol: " + symbol);
        response.setData(price);
        log.info(response.toString());
        return response;
    }
}
