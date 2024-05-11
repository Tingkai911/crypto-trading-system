package com.aquariux.crypto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TransactionTypeValidator implements ConstraintValidator<TransactionType, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.equals("BID") || s.equals("ASK");
    }
}
