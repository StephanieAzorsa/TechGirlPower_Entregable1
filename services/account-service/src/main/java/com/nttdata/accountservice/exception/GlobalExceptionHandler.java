package com.nttdata.accountservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAccountNotFoundException(
            AccountNotFoundException ex) {

        log.warn("Cuenta no encontrada: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientBalanceException(
            InsufficientBalanceException ex) {

        log.warn("Saldo insuficiente: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCustomerNotFoundException(
            CustomerNotFoundException ex) {

        log.warn("Cliente no encontrado: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        errors.put("errorCode", "CUSTOMER_NOT_FOUND");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}