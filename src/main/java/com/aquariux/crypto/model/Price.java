package com.aquariux.crypto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Price {
    @Id
    @Column(unique = true, nullable = false)
    private String symbol;
    private double bid;
    private double ask;
    private boolean allowTrading;
}
