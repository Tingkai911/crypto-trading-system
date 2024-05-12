package com.aquariux.crypto.service.impl;

import com.aquariux.crypto.exception.TransactionException;
import com.aquariux.crypto.model.Price;
import com.aquariux.crypto.model.Transaction;
import com.aquariux.crypto.model.UserWallet;
import com.aquariux.crypto.repository.IPriceRepository;
import com.aquariux.crypto.repository.ITransactionRepository;
import com.aquariux.crypto.repository.IUserWalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {

    @Mock
    private ITransactionRepository transactionRepository;

    @Mock
    private IUserWalletRepository userWalletRepository;

    @Mock
    private IPriceRepository priceRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void testBid_UserNotFound() {
        when(userWalletRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(TransactionException.class, () -> {
            transactionService.bid("unknownUser", "BTCUSDT", 1);
        });

        assertEquals("User not found: unknownUser", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testBid_PriceNotFound() {
        when(userWalletRepository.findByUsername("validUser")).thenReturn(Optional.of(new UserWallet()));
        when(priceRepository.findBySymbol("UnknownSymbol")).thenReturn(Optional.empty());

        Exception exception = assertThrows(TransactionException.class, () -> {
            transactionService.bid("validUser", "UnknownSymbol", 1);
        });

        assertEquals("Price not found: UnknownSymbol", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testBid_NotAllowTrading() {
        UserWallet wallet = new UserWallet("validUser", 0D, 0D, 0D);
        Price price = new Price("BTCUSDT", 3000D, 3000D, false);
        when(userWalletRepository.findByUsername("validUser")).thenReturn(Optional.of(wallet));
        when(priceRepository.findBySymbol("BTCUSDT")).thenReturn(Optional.of(price));

        Exception exception = assertThrows(TransactionException.class, () -> {
            transactionService.bid("validUser", "BTCUSDT", 1);
        });

        assertEquals("Trading is not allowed for symbol BTCUSDT", exception.getMessage());
        verify(userWalletRepository, never()).save(any(UserWallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testBid_InsufficientBalance() {
        UserWallet wallet = new UserWallet("validUser", 0D, 0D, 0D);
        Price price = new Price("BTCUSDT", 3000D, 3000D, true);
        when(userWalletRepository.findByUsername("validUser")).thenReturn(Optional.of(wallet));
        when(priceRepository.findBySymbol("BTCUSDT")).thenReturn(Optional.of(price));

        Exception exception = assertThrows(TransactionException.class, () -> {
            transactionService.bid("validUser", "BTCUSDT", 1);
        });

        assertEquals("Balance of BTC in wallet is less than the required amount", exception.getMessage());
        verify(userWalletRepository, never()).save(any(UserWallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testBid_SuccessfulTransaction() throws Exception {
        UserWallet userWallet = new UserWallet(); // Assume this has setters and getters
        userWallet.setUsername("testUser");
        userWallet.setEth(1000.0);
        userWallet.setBtc(500.0);
        userWallet.setUsdt(2000.0);
        Price price = new Price("ETHUSDT", 50000.0, 50500.0, true);
        when(userWalletRepository.findByUsername("testUser")).thenReturn(Optional.of(userWallet));
        when(priceRepository.findBySymbol("ETHUSDT")).thenReturn(Optional.of(price));
        Transaction expectedTransaction = new Transaction("testUser", Transaction.Type.BID, "BTCUSDT",
                10, 50500.0, 50000.0, Instant.now());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTransaction);

        Transaction result = transactionService.bid("testUser", "ETHUSDT", 10);

        assertEquals("testUser", result.getUsername());
        assertEquals(Transaction.Type.BID, result.getType());
        assertEquals("ETHUSDT", result.getSymbol());
        assertEquals(10, result.getAmount());
        assertEquals(990.0, userWallet.getEth());
        assertEquals(502000.0, userWallet.getUsdt());
        assertEquals(500.0, userWallet.getBtc());

        verify(userWalletRepository).save(userWallet);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testAsk_UserNotFound() {
        when(userWalletRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(TransactionException.class, () -> {
            transactionService.ask("unknownUser", "BTCUSDT", 1);
        });

        assertEquals("User not found: unknownUser", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testAsk_PriceNotFound() {
        when(userWalletRepository.findByUsername("validUser")).thenReturn(Optional.of(new UserWallet()));
        when(priceRepository.findBySymbol("UnknownSymbol")).thenReturn(Optional.empty());

        Exception exception = assertThrows(TransactionException.class, () -> {
            transactionService.ask("validUser", "UnknownSymbol", 1);
        });

        assertEquals("Price not found: UnknownSymbol", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testAsk_NotAllowTrading() {
        UserWallet wallet = new UserWallet("validUser", 0D, 0D, 0D);
        Price price = new Price("BTCUSDT", 3000D, 3000D, false);
        when(userWalletRepository.findByUsername("validUser")).thenReturn(Optional.of(wallet));
        when(priceRepository.findBySymbol("BTCUSDT")).thenReturn(Optional.of(price));

        Exception exception = assertThrows(TransactionException.class, () -> {
            transactionService.ask("validUser", "BTCUSDT", 1);
        });

        assertEquals("Trading is not allowed for symbol BTCUSDT", exception.getMessage());
        verify(userWalletRepository, never()).save(any(UserWallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testAsk_InsufficientFundsForPurchase() {
        UserWallet wallet = new UserWallet("validUser", 0D, 0D, 0D);
        Price price = new Price("BTCUSDT", 3000D, 3000D, true);
        when(userWalletRepository.findByUsername("validUser")).thenReturn(Optional.of(wallet));
        when(priceRepository.findBySymbol("BTCUSDT")).thenReturn(Optional.of(price));

        Exception exception = assertThrows(TransactionException.class, () -> {
            transactionService.ask("validUser", "BTCUSDT", 1);
        });

        assertEquals("Balance of USDT in wallet is less than the required amount", exception.getMessage());
        verify(userWalletRepository, never()).save(any(UserWallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testAsk_SuccessfulTransaction() throws Exception {
        UserWallet userWallet = new UserWallet();
        userWallet.setUsername("testUser");
        userWallet.setBtc(1000.0);
        userWallet.setEth(500.0);
        userWallet.setUsdt(60000.0);
        Price price = new Price("BTCUSDT", 50000.0, 50500.0, true);
        when(userWalletRepository.findByUsername("testUser")).thenReturn(Optional.of(userWallet));
        when(priceRepository.findBySymbol("BTCUSDT")).thenReturn(Optional.of(price));
        when(userWalletRepository.findByUsername("testUser")).thenReturn(Optional.of(userWallet));
        when(priceRepository.findBySymbol("BTCUSDT")).thenReturn(Optional.of(price));
        Transaction expectedTransaction = new Transaction("testUser", Transaction.Type.ASK, "BTCUSDT",
                1, 50500.0, 50000.0, Instant.now());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTransaction);

        Transaction result = transactionService.ask("testUser", "BTCUSDT", 1);

        assertEquals("testUser", result.getUsername());
        assertEquals(Transaction.Type.ASK, result.getType());
        assertEquals("BTCUSDT", result.getSymbol());
        assertEquals(1, result.getAmount());
        assertEquals(9500.0, userWallet.getUsdt());
        assertEquals(1001.0, userWallet.getBtc());
        assertEquals(500.0, userWallet.getEth());

        verify(userWalletRepository).save(userWallet);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testGetTransactionsByUsername_Success() throws Exception {
        Transaction transaction = new Transaction();
        List<Transaction> transactionList = Arrays.asList(transaction);
        when(transactionRepository.findAllByUsername(anyString())).thenReturn(transactionList);
        List<Transaction> results = transactionService.getTransactionsByUsername("user1");
        assertNotEquals(0, results.size());
        assertEquals(transactionList, results);
        verify(transactionRepository).findAllByUsername("user1");
    }

    @Test
    void testGetPaginatedTransactionsByUsername_Success() throws Exception {
        Transaction transaction = new Transaction();
        List<Transaction> transactionList = Arrays.asList(transaction);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> transactionPage = new PageImpl<>(transactionList, pageable, 1);
        when(transactionRepository.findByUsername(anyString(), any(Pageable.class))).thenReturn(transactionPage);
        Page<Transaction> results = transactionService.getTransactionsByUsername("user1", pageable);
        assertNotNull(results);
        assertEquals(transactionList, results.getContent());
        verify(transactionRepository).findByUsername("user1", pageable);
    }

    @Test
    void testGetTransactionsByUsernameFailure() {
        when(transactionRepository.findAllByUsername(anyString())).thenThrow(new RuntimeException("Database error"));
        Exception exception = assertThrows(TransactionException.class, () -> {
            transactionService.getTransactionsByUsername("user1");
        });
        assertEquals("Error fetching transactions", exception.getMessage());
    }

    @Test
    void testGetPaginatedTransactionsByUsernameFailure() {
        Pageable pageable = PageRequest.of(0, 10);
        when(transactionRepository.findByUsername(anyString(), any(Pageable.class))).thenThrow(new RuntimeException("Database error"));
        Exception exception = assertThrows(TransactionException.class, () -> {
            transactionService.getTransactionsByUsername("user1", pageable);
        });
        assertEquals("Error fetching transactions", exception.getMessage());
    }
}
