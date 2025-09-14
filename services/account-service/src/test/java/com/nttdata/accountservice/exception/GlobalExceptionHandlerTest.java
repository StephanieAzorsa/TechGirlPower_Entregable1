package com.nttdata.accountservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    // CP-AS30: Retorna 400 con errores de validación
    @Test
    void handleMethodArgumentNotValidException_ShouldReturnValidationErrors() {
        // Arrange
        FieldError fieldError = new FieldError(
                "accountRequestDTO",
                "initialBalance",
                "El saldo inicial debe ser mayor a 0"
        );

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<Map<String, String>> response =
                exceptionHandler.handleMethodArgumentNotValidException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("initialBalance"));
        assertEquals("El saldo inicial debe ser mayor a 0",
                response.getBody().get("initialBalance"));
    }

    // CP-AS31: Retorna 400 con mensaje apropiado
    @Test
    void handleAccountNotFoundException_ShouldReturnErrorMessage() {
        // Arrange
        AccountNotFoundException exception =
                new AccountNotFoundException("Cuenta no encontrada");

        // Act
        ResponseEntity<Map<String, String>> response =
                exceptionHandler.handleAccountNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("Cuenta no encontrada", response.getBody().get("message"));
    }

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
