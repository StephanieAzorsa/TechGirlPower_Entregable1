package com.nttdata.customerservice.exception;

public class CustomerHasActiveAccountsException extends RuntimeException {
    public CustomerHasActiveAccountsException(String message) {
        super(message);
    }
}
