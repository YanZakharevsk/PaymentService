package com.inno.payment_service.exception;

import jakarta.validation.ConstraintViolationException;

public class MyValidationException extends RuntimeException {

    private final ConstraintViolationException constraintViolationException;

    public MyValidationException(ConstraintViolationException constraintViolationException) {
        super(constraintViolationException.getMessage());
        this.constraintViolationException = constraintViolationException;
    }

    public ConstraintViolationException getConstraintViolationException() {
        return constraintViolationException;
    }
}
