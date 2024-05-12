package com.aquariux.crypto.service;

import com.aquariux.crypto.exception.TransactionException;
import com.aquariux.crypto.model.Transaction;

import java.util.List;

public interface ITransactionService {
    Transaction bid(String username, String symbol, double amount) throws TransactionException;
    Transaction ask(String username, String symbol, double amount) throws TransactionException;
    List<Transaction> getTransactionsByUsername(String username) throws TransactionException;
}
