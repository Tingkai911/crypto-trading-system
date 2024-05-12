package com.aquariux.crypto.runner;

import com.aquariux.crypto.model.Transaction;
import com.aquariux.crypto.model.UserWallet;
import com.aquariux.crypto.repository.ITransactionRepository;
import com.aquariux.crypto.repository.IUserWalletRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

// Run once at the start to load user data
@Slf4j
@Component
@AllArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final IUserWalletRepository userRepository;
    private final ITransactionRepository transactionRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        userRepository.save(new UserWallet("user1", 50000D, 0D, 0D));
        userRepository.save(new UserWallet("user2", 50000D, 0D, 0D));

        transactionRepository.save(new Transaction("user1", Transaction.Type.ASK, "ETHUSDT", 1, 0, 0, Instant.now()));
        transactionRepository.save(new Transaction("user1", Transaction.Type.BID, "BTCUSDT", 2, 0, 0, Instant.now()));
        transactionRepository.save(new Transaction("user1", Transaction.Type.ASK, "BTCUSDT", 3, 0, 0, Instant.now()));
        transactionRepository.save(new Transaction("user1", Transaction.Type.BID, "BTCUSDT", 4, 0, 0, Instant.now()));
        transactionRepository.save(new Transaction("user1", Transaction.Type.ASK, "BTCUSDT", 5, 0, 0, Instant.now()));
        transactionRepository.save(new Transaction("user1", Transaction.Type.BID, "BTCUSDT", 6, 0, 0, Instant.now()));
        transactionRepository.save(new Transaction("user1", Transaction.Type.ASK, "BTCUSDT", 7, 0, 0, Instant.now()));
        transactionRepository.save(new Transaction("user1", Transaction.Type.BID, "BTCUSDT", 8, 0, 0, Instant.now()));
        transactionRepository.save(new Transaction("user1", Transaction.Type.ASK, "BTCUSDT", 9, 0, 0, Instant.now()));
        transactionRepository.save(new Transaction("user1", Transaction.Type.BID, "BTCUSDT", 10, 0, 0, Instant.now()));
    }
}