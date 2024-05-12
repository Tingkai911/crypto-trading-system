package com.aquariux.crypto.service.impl;

import com.aquariux.crypto.exception.UserWalletNotFoundException;
import com.aquariux.crypto.model.UserWallet;
import com.aquariux.crypto.repository.IUserWalletRepository;
import com.aquariux.crypto.service.IUserWalletService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserWalletService implements IUserWalletService {
    private final IUserWalletRepository userWalletRepository;

    @Override
    public UserWallet getUserByUsername(String username) throws UserWalletNotFoundException {
        Optional<UserWallet> userWallet;

        try {
            userWallet = userWalletRepository.findByUsername(username);
        } catch (Exception e) {
            log.error("Error while accessing database for user: {}", username, e);
            throw new UserWalletNotFoundException("Failed to retrieve user data for username: " + username, e);
        }

        if (userWallet.isEmpty()) {
            log.warn("No user wallet found for username: {}", username);
            throw new UserWalletNotFoundException("User wallet for username '" + username + "' is not found");
        }

        return userWallet.get();
    }
}
