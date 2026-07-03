package com.inno.payment_service.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String errorMessage){
        super(errorMessage);
    }
}
