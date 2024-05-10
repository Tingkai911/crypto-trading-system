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
public class UserWallet {
    @Id
    @Column(unique = true, nullable = false)
    private String username;
    private double usdt;
    private double eth;
    private double btc;
}
