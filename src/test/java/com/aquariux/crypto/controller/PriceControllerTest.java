package com.aquariux.crypto.controller;

import com.aquariux.crypto.exception.PriceNotFoundException;
import com.aquariux.crypto.model.Price;
import com.aquariux.crypto.service.IPriceRetrievalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(PriceController.class)
@ContextConfiguration(classes = {PriceController.class})
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@Import(ErrorControllerAdvice.class)
public class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPriceRetrievalService priceRetrievalService;

    @Test
    public void getAllPrices_ReturnsPriceList() throws Exception {
        Price price = new Price("BTCUSDT", 50000.0, 51000.0, true);
        List<Price> prices = Arrays.asList(price);

        when(priceRetrievalService.getAllPrices()).thenReturn(prices);

        mockMvc.perform(get("/price/v1.0/prices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("List of trading prices"))
                .andExpect(jsonPath("$.data[0].symbol").value("BTCUSDT"));
    }

    @Test
    public void getAllPrices_ReturnsEmptyList() throws Exception {
        when(priceRetrievalService.getAllPrices()).thenThrow(new PriceNotFoundException("No prices found"));

        mockMvc.perform(get("/price/v1.0/prices"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("No prices found"));
    }

    @Test
    public void getPrice_ReturnsPriceForSymbol() throws Exception {
        String symbol = "BTCUSDT";
        Price price = new Price(symbol, 50000.0, 51000.0, true);

        when(priceRetrievalService.getPriceBySymbol(any(String.class))).thenReturn(price);

        mockMvc.perform(get("/price/v1.0/{symbol}", symbol))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Price for symbol: BTCUSDT"))
                .andExpect(jsonPath("$.data.symbol").value(symbol));
    }

    @Test
    public void getPrice_SymbolNotFound() throws Exception {
        String symbol = "BTCUSDT";
        when(priceRetrievalService.getPriceBySymbol(any(String.class))).thenThrow(new PriceNotFoundException("No price found for symbol: " + symbol));

        mockMvc.perform(get("/price/v1.0/{symbol}", symbol))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("No price found for symbol: " + symbol));
    }
}

