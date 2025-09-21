package com.nttdata.transactionservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // CP-TS36: Debe retornar 400 Bad Request
    @Test
    void handleInsufficientBalance_ShouldReturnBadRequest() {
        // Arrange
        InsufficientBalanceException ex = new InsufficientBalanceException("Saldo insuficiente");

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleInsufficientBalance(ex).block();

        // Assert
        assertNotNull(response, "La respuesta no debería ser null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "El status debería ser 400");
        assertNotNull(response.getBody(), "El body no debería ser null");
        assertEquals("Fondos insuficientes", response.getBody().get("error"));
        assertEquals("Saldo insuficiente", response.getBody().get("message"));
        assertEquals("INSUFFICIENT_BALANCE", response.getBody().get("code"));
    }
}
