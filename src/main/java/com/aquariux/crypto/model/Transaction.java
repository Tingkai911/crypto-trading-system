package com.aquariux.crypto.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Transaction {
    public enum Type {
        BID("Bid"),
        ASK("Ask");

        private String value;

        Type(String v) {
            this.value = v;
        }

        public String getValue() {
            return value;
        }
    }

    public Transaction(String username, Type type, String symbol,
                       double amount, double ask, double bid,
                       Instant timestamp) {
        this.username = username;
        this.type = type;
        this.symbol = symbol;
        this.amount = amount;
        this.ask = ask;
        this.bid = bid;
        this.timestamp = timestamp;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID transactionId;
    @NotBlank
    private String username;
    @NotBlank
    private String symbol;
    @NotNull
    private Type type;
    @NotNull
    private Double amount;
    @NotNull
    private Double ask;
    @NotNull
    private Double bid;
    @NotNull
    private Instant timestamp;
}
