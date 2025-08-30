package com.nttdata.customerservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory
            .getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DniAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleDniAlreadyExistsException(
            DniAlreadyExistsException ex) {

        log.warn("El DNI ya existe {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "El DNI ya existe");

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCustomerNotFoundException(
            CustomerNotFoundException ex){

        log.warn("El cliente no se encontró con ID: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Cliente no encontrado");

        return ResponseEntity.badRequest().body(errors);
    }
}
