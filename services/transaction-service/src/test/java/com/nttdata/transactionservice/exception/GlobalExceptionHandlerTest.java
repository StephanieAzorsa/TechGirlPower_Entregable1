package com.nttdata.transactionservice.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private WebExchangeBindException webExchangeBindException;

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

    // CP-TS37	TransactionNotValidException	Retorna 400 con mensaje apropiado
    @Test
    void handleTransactionExecutionException_ShouldReturnInternalServerError() {
        // Arrange
        TransactionExecutionException ex = new TransactionExecutionException("Error de transacción");

        // Act
        Mono<ResponseEntity<Map<String, String>>> result = handler.handleTransactionException(ex);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals("TRANSACTION_ERROR", response.getBody().get("code"));
                })
                .verifyComplete();
    }

    // CP-TS38	GenericException	Retorna 500 con mensaje apropiado
    @Test
    void handleGenericException_ShouldReturnInternalError() {
        // Arrange
        TransactionExecutionException ex = new TransactionExecutionException("Error interno del servidor");

        // Act
        Mono<ResponseEntity<Map<String, String>>> result = handler.handleGenericException(ex);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals("INTERNAL_ERROR", response.getBody().get("code"));
                })
                .verifyComplete();
    }

    // CP-TS39 - handleValidationException con múltiples field errors
    @Test
    void handleValidationException_WithMultipleFieldErrors_ShouldReturnBadRequest() {
        // Arrange
        FieldError fieldError1 = new FieldError(
                "transactionRequestDTO",
                "amount",
                "Monto debe ser mayor a 0");
        FieldError fieldError2 = new FieldError(
                "transactionRequestDTO",
                "accountId",
                "Account ID es requerido");

        when(webExchangeBindException.getFieldErrors())
                .thenReturn(List.of(fieldError1, fieldError2));

        // Act
        Mono<ResponseEntity<Map<String, String>>> result =
                handler.handleValidationException(webExchangeBindException);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertNotNull(response.getBody());

                    Map<String, String> body = response.getBody();
                    assertEquals(2, body.size());
                    assertEquals("Monto debe ser mayor a 0", body.get("amount"));
                    assertEquals("Account ID es requerido", body.get("accountId"));
                })
                .verifyComplete();
    }

}
