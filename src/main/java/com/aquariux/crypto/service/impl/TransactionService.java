package com.aquariux.crypto.service.impl;

import com.aquariux.crypto.exception.TransactionException;
import com.aquariux.crypto.exception.TransactionNotFoundException;
import com.aquariux.crypto.model.Price;
import com.aquariux.crypto.model.Transaction;
import com.aquariux.crypto.model.UserWallet;
import com.aquariux.crypto.repository.IPriceRepository;
import com.aquariux.crypto.repository.ITransactionRepository;
import com.aquariux.crypto.repository.IUserWalletRepository;
import com.aquariux.crypto.service.ITransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService implements ITransactionService {
    private final ITransactionRepository transactionRepository;
    private final IUserWalletRepository userWalletRepository;
    private final IPriceRepository priceRepository;

    // Sell/Bid
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public Transaction bid(String username, String symbol, double amount) throws TransactionException {
        Optional<UserWallet> userWalletOptional = userWalletRepository.findByUsername(username);
        if (userWalletOptional.isEmpty()) {
            log.error("User not found: " + username);
            throw new TransactionException("User not found: " + username);
        }
        UserWallet userWallet = userWalletOptional.get();

        Optional<Price> priceOptional = priceRepository.findBySymbol(symbol);
        if (priceOptional.isEmpty()) {
            log.error("Price not found: " + symbol);
            throw new TransactionException("Price not found: " + symbol);
        }
        Price price = priceOptional.get();
        if (!price.getAllowTrading()) {
            log.error("Trading is not allowed for symbol " + symbol);
            throw new TransactionException("Trading is not allowed for symbol " + symbol);
        }

        try {
            String selling = symbol.substring(0, 3);
            String buying = symbol.substring(3);

            double balance = getBalance(selling, userWallet);
            if (balance < amount) {
                throw new TransactionException(String.format("Balance of %s in wallet is less than the required amount", selling));
            }
            setBalance(selling, userWallet, balance - amount);
            setBalance(buying, userWallet, getBalance(buying, userWallet) + amount * price.getBid());
            userWalletRepository.save(userWallet);

            Transaction transaction = new Transaction(username, Transaction.Type.BID, symbol, amount,
                    price.getAsk(), price.getBid(), Instant.now());
            transactionRepository.save(transaction);

            log.info(transaction.toString());
            return transaction;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TransactionException(e.getMessage(), e);
        }
    }

    // Buy/Ask
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public Transaction ask(String username, String symbol, double amount) throws TransactionException {
        Optional<UserWallet> userWalletOptional = userWalletRepository.findByUsername(username);
        if (userWalletOptional.isEmpty()) {
            log.error("User not found: " + username);
            throw new TransactionException("User not found: " + username);
        }
        UserWallet userWallet = userWalletOptional.get();

        Optional<Price> priceOptional = priceRepository.findBySymbol(symbol);
        if (priceOptional.isEmpty()) {
            log.error("Price not found: " + symbol);
            throw new TransactionException("Price not found: " + symbol);
        }
        Price price = priceOptional.get();
        if (!price.getAllowTrading()) {
            log.error("Trading is not allowed for symbol " + symbol);
            throw new TransactionException("Trading is not allowed for symbol " + symbol);
        }

        try {
            String buying = symbol.substring(0, 3);
            String selling = symbol.substring(3);

            double funds = amount * price.getAsk();
            double balance = getBalance(selling, userWallet);
            if (balance < funds) {
                throw new TransactionException(String.format("Balance of %s in wallet is less than the required amount", selling));
            }
            setBalance(selling, userWallet, getBalance(selling, userWallet) - funds);
            setBalance(buying, userWallet, getBalance(buying, userWallet) + amount);
            userWalletRepository.save(userWallet);

            Transaction transaction = new Transaction(username, Transaction.Type.ASK, symbol, amount,
                    price.getAsk(), price.getBid(), Instant.now());
            transactionRepository.save(transaction);

            log.info(transaction.toString());
            return transaction;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TransactionException(e.getMessage(), e);
        }
    }

    @Override
    public List<Transaction> getTransactionsByUsername(String username) throws TransactionNotFoundException {
        try {
            return transactionRepository.findAllByUsername(username);
        } catch (Exception e) {
            throw new TransactionNotFoundException("Error fetching transactions", e);
        }
    }

    @Override
    public Page<Transaction> getTransactionsByUsername(String username, Pageable pageable) throws TransactionNotFoundException {
        try {
            return transactionRepository.findByUsername(username, pageable);
        } catch (Exception e) {
            throw new TransactionNotFoundException("Error fetching transactions", e);
        }
    }

    private double getBalance(String symbol, UserWallet userWallet) throws Exception {
        return switch (symbol) {
            case "BTC" -> userWallet.getBtc();
            case "ETH" -> userWallet.getEth();
            case "USDT" -> userWallet.getUsdt();
            default -> throw new TransactionException("Unrecognised crypto symbol: " + symbol);
        };
    }

    private void setBalance(String symbol, UserWallet userWallet, double amount) throws Exception {
        switch (symbol) {
            case "BTC" -> userWallet.setBtc(amount);
            case "ETH" -> userWallet.setEth(amount);
            case "USDT" -> userWallet.setUsdt(amount);
            default -> throw new TransactionException("Unrecognised crypto symbol: " + symbol);
        }
    }
}
