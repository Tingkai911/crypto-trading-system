package com.aquariux.crypto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE_PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TransactionTypeValidator.class)
public @interface TransactionType {
    String message() default "Only 'BID' or 'ASK' are allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
