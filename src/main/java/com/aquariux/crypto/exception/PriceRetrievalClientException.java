package com.aquariux.crypto.exception;

public class PriceRetrievalClientException extends Exception {
    public PriceRetrievalClientException(String message) {
        super(message);
    }

    public PriceRetrievalClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
