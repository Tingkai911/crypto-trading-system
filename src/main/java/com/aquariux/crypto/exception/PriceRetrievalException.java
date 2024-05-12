package com.aquariux.crypto.exception;

public class PriceRetrievalException extends Exception {
    public PriceRetrievalException(String message) {
        super(message);
    }

    public PriceRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
