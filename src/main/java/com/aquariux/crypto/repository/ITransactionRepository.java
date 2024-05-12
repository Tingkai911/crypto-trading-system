package com.aquariux.crypto.repository;

import com.aquariux.crypto.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ITransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUsername(String username);
    Page<Transaction> findByUsername(String username, Pageable pageable);
}
