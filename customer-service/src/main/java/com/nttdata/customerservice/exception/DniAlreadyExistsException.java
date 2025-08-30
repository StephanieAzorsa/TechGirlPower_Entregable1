package com.nttdata.customerservice.exception;

public class DniAlreadyExistsException extends RuntimeException {
    public DniAlreadyExistsException(String message) {
        super(message);
    }
}