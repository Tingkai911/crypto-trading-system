package com.aquariux.crypto.repository;

import com.aquariux.crypto.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ITransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUsername(String username);
}
