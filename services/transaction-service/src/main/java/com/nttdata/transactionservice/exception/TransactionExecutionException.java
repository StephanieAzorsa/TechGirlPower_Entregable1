package com.nttdata.transactionservice.exception;

public class TransactionExecutionException extends RuntimeException {
    public TransactionExecutionException(String message) {
        super(message);
    }
}
