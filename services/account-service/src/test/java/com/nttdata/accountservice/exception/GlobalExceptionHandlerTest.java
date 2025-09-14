package com.nttdata.accountservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    // CP-AS32: CustomerNotFoundException [Retorna 400 con mensaje apropiado]
    @Test
    void handleCustomerNotFoundException_ShouldReturnErrorMessage() {
        // Arrange (Organizar/Preparar)
        // Crea la excepción que será manejada por el controlador
        InsufficientBalanceException exception =
                new InsufficientBalanceException("Cliente no encontrado");

        // Act (Actuar)
        // Ejecutar el métod que queremos probar
        ResponseEntity<Map<String, String>> response =
                exceptionHandler.handleInsufficientBalanceException(exception);

        // Assert (Afirmar/Verificar)
        // Verifica que el código de estado HTTP sea 400 (BAD_REQUEST)
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        // Verifica que el cuerpo contenga la clave "message"
        assertTrue(response.getBody().containsKey("message"));
        // Verifica que el mensaje sea el esperado
        assertEquals("Cliente no encontrado", response.getBody().get("message"));
    }

    // CP-AS33: InsufficientBalanceException [Retorna 400 con mensaje apropiado]
    @Test
    void handleInsufficientBalanceException_ShouldReturnErrorMessage() {
        // Arrange
        InsufficientBalanceException exception = new
                InsufficientBalanceException("Saldo insuficiente");

        // Act
        ResponseEntity<Map<String, String>> response =
                exceptionHandler.handleInsufficientBalanceException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("Saldo insuficiente", response.getBody().get("message"));
    }
}
