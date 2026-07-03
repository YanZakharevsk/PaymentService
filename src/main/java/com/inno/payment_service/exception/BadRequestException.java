package com.inno.payment_service.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException (String errorMessage){
        super(errorMessage);
    }
}
