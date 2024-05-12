package com.aquariux.crypto.exception;

public class UserWalletNotFoundException extends Exception{
    public UserWalletNotFoundException(String message) {
        super(message);
    }

    public UserWalletNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
