package com.aquariux.crypto.model;

import com.aquariux.crypto.validator.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String symbol;
    @TransactionType
    private String type;
    @NotNull
    private Double amount;
}
