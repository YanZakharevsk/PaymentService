package com.inno.payment_service.exception;

public class ServerException extends RuntimeException {

    public ServerException(String errorMessage){
        super(errorMessage);
    }
}
