package com.aquariux.crypto.controller;

import com.aquariux.crypto.exception.UserWalletNotFoundException;
import com.aquariux.crypto.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorControllerAdvice {

    @ExceptionHandler(UserWalletNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public Response handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        Response response = new Response();
        response.setCode(HttpStatus.NOT_FOUND.value());
        response.setMessage(ex.getMessage());
        return response;
    }

}
