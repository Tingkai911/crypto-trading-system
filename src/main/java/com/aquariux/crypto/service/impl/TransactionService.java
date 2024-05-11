package com.aquariux.crypto.service.impl;

import com.aquariux.crypto.model.Transaction;
import com.aquariux.crypto.repository.IPriceRepository;
import com.aquariux.crypto.repository.ITransactionRepository;
import com.aquariux.crypto.repository.IUserWalletRepository;
import com.aquariux.crypto.service.ITransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService implements ITransactionService {
    private final ITransactionRepository transactionRepository;
    private final IUserWalletRepository userWalletRepository;
    private final IPriceRepository priceRepository;

    @Transactional
    @Override
    public Transaction bid(String username, String symbol, double amount) throws Exception {
        return null;
    }

    @Transactional
    @Override
    public Transaction ask(String username, String symbol, double amount) throws Exception {
        return null;
    }

    @Override
    public List<Transaction> getTransactionsByUsername(String username) throws Exception {
        return transactionRepository.findAllByUsername(username);
    }
}
