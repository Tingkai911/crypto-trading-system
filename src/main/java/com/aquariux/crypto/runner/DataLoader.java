package com.aquariux.crypto.runner;

import com.aquariux.crypto.model.UserWallet;
import com.aquariux.crypto.repository.UserWalletRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// Run once at the start to load user data
@Slf4j
@Component
@AllArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final UserWalletRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        userRepository.save(new UserWallet("user1", 50000, 0, 0));
    }
}