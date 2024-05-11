package com.aquariux.crypto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private Double bid;
    @NotNull
    private Double ask;
    @NotNull
    private Boolean allowTrading;
}
