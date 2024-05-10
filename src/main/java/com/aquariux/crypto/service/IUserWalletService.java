package com.aquariux.crypto.service;

import com.aquariux.crypto.exception.UserWalletNotFoundException;
import com.aquariux.crypto.model.UserWallet;

public interface IUserWalletService {
    UserWallet getUserByUsername(String username) throws UserWalletNotFoundException;
}
