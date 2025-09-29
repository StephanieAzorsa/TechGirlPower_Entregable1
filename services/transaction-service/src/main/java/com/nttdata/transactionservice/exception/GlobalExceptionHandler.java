package com.nttdata.transactionservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleValidationException(WebExchangeBindException ex) {
        return Mono.fromCallable(() -> {
            Map<String, String> errors = new HashMap<>();
            ex.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            log.warn("Validation errors: {}", errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        });
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleAccountNotFound(AccountNotFoundException ex) {
        return Mono.fromCallable(() -> {
            log.warn("Account not found: {}", ex.getMessage());

            Map<String, String> response = new HashMap<>();
            response.put("error", "Recurso no encontrado");
            response.put("message", ex.getMessage());
            response.put("code", "ACCOUNT_NOT_FOUND");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        });
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleInsufficientBalance(InsufficientBalanceException ex) {
        return Mono.fromCallable(() -> {
            log.warn("Insufficient balance: {}", ex.getMessage());

            Map<String, String> response = new HashMap<>();
            response.put("error", "Fondos insuficientes");
            response.put("message", ex.getMessage());
            response.put("code", "INSUFFICIENT_BALANCE");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        });
    }

    @ExceptionHandler(TransactionExecutionException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleTransactionException(TransactionExecutionException ex) {
        return Mono.fromCallable(() -> {
            log.warn("Error en la transacción: {}", ex.getMessage());

            Map<String, String> response = new HashMap<>();
            response.put("error", "Error del sistema");
            response.put("message", ex.getMessage());
            response.put("code", "TRANSACTION_ERROR");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        });
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, String>>> handleGenericException(Exception ex) {
        return Mono.fromCallable(() -> {
            log.warn("Error interno del servidor: {}", ex.getMessage());

            Map<String, String> response = new HashMap<>();
            response.put("error", "Error interno del servidor");
            response.put("message", "Ocurrió un error inesperado. Por favor, intente más tarde.");
            response.put("code", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        });
    }

}