package com.aquariux.crypto.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "price-aggregation-url")
public class PriceAggregationConfig {
    private String binance;
    private String huobi;
}
