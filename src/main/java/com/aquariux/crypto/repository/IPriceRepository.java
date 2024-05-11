package com.aquariux.crypto.repository;

import com.aquariux.crypto.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPriceRepository extends JpaRepository<Price, Long> {
    Optional<Price> findBySymbol(String symbol);
}
