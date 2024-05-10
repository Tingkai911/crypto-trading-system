package com.aquariux.crypto.repository;

import com.aquariux.crypto.model.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {
    Optional<UserWallet> findByUsername(String username);
}
