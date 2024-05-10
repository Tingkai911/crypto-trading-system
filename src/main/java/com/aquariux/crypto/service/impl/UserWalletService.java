package com.aquariux.crypto.service.impl;

import com.aquariux.crypto.exception.UserWalletNotFoundException;
import com.aquariux.crypto.model.UserWallet;
import com.aquariux.crypto.repository.UserWalletRepository;
import com.aquariux.crypto.service.IUserWalletService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserWalletService implements IUserWalletService {
    private final UserWalletRepository userWalletRepository;

    @Override
    public UserWallet getUserByUsername(String username) throws UserWalletNotFoundException {
        Optional<UserWallet> user3 = userWalletRepository.findByUsername(username);
        if (user3.isEmpty()) {
            throw new UserWalletNotFoundException(username + " is not found");
        }
        return user3.get();
    }
}
