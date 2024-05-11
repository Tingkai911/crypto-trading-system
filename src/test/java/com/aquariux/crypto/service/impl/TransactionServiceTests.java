package com.aquariux.crypto.service.impl;

import com.aquariux.crypto.exception.TransactionException;
import com.aquariux.crypto.model.Transaction;
import com.aquariux.crypto.model.UserWallet;
import com.aquariux.crypto.model.Price;
import com.aquariux.crypto.repository.IPriceRepository;
import com.aquariux.crypto.repository.ITransactionRepository;
import com.aquariux.crypto.repository.IUserWalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        assertEquals("User not found", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testBid_PriceNotFound() {
        when(userWalletRepository.findByUsername("validUser")).thenReturn(Optional.of(new UserWallet()));
        when(priceRepository.findBySymbol("UnknownSymbol")).thenReturn(Optional.empty());

        Exception exception = assertThrows(TransactionException.class, () -> {
            transactionService.bid("validUser", "UnknownSymbol", 1);
        });

        assertEquals("Price not found", exception.getMessage());
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
}
