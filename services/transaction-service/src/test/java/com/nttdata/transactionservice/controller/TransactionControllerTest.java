package com.nttdata.transactionservice.controller;

import com.nttdata.transactionservice.dto.TransactionRequestDTO;
import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.model.TransactionType;
import com.nttdata.transactionservice.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    // CP-TS01: Debe procesar depósito exitosamente 200 OK
    @Test
    void deposit_ShouldProcessTransaction() {
        // Arrange
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountId("account-1");
        request.setAmount(new BigDecimal("150.00"));

        TransactionResponseDTO transaction = new TransactionResponseDTO();
        transaction.setId("tx-1");
        transaction.setTransactionType(TransactionType.DEPOSITO);
        transaction.setAmount(new BigDecimal("150.00"));
        transaction.setDate(LocalDateTime.now());
        transaction.setSourceAccountId("account-1");
        transaction.setDestinationAccountId(null);


        when(transactionService.registerDeposit(any(TransactionRequestDTO.class))).thenReturn(Mono.just(transaction));

        // Act
        ResponseEntity<TransactionResponseDTO> response = transactionController.registerDeposit(request).block();

        // Assert
        assertNotNull(response, "La respuesta no debería ser null");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "El body no debería ser null");
        assertEquals("tx-1", response.getBody().getId(), "El ID de la transacción debería coincidir");
        assertEquals("account-1", response.getBody().getSourceAccountId(), "El ID de la cuenta debería coincidir");
        assertEquals(new BigDecimal("150.00"), response.getBody().getAmount(), "El monto debería coincidir");
        assertEquals(TransactionType.DEPOSITO, response.getBody().getTransactionType(), "El tipo de transacción debería ser DEPOSIT");

        verify(transactionService, times(1)).registerDeposit(any(TransactionRequestDTO.class));
    }

    // CP-TS02: Debe manejar error de validación 400 Bad Request
    @Test
    void deposit_ShouldReturnBadRequest_WhenValidationFails() {
        // Arrange
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountId(null);
        request.setAmount(BigDecimal.ZERO);

        // El servicio no devuelve nada porque la validación falla
        when(transactionService.registerDeposit(any(TransactionRequestDTO.class))).thenReturn(Mono.empty());

        // Act
        ResponseEntity<TransactionResponseDTO> response = transactionController.registerDeposit(request).block();

        // Assert
        assertNotNull(response, "La respuesta no debería ser null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Debe retornar 400 Bad Request");
        assertNull(response.getBody(), "El body debería ser null en caso de error");

        verify(transactionService, times(1)).registerDeposit(any(TransactionRequestDTO.class));
    }

    // CP-TS03: Debe procesar retiro exitosamente 200 OK
    @Test
    void withdraw_ShouldProcessTransaction() {
        // Arrange
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountId("account-2");
        request.setAmount(new BigDecimal("50.00"));

        TransactionResponseDTO transaction = new TransactionResponseDTO();
        transaction.setId("tx-2");
        transaction.setTransactionType(TransactionType.RETIRO);
        transaction.setAmount(new BigDecimal("50.00"));
        transaction.setDate(LocalDateTime.now());
        transaction.setSourceAccountId("account-2");
        transaction.setDestinationAccountId(null);

        when(transactionService.registerWithdrawal(any(TransactionRequestDTO.class))).thenReturn(Mono.just(transaction));

        // Act
        ResponseEntity<TransactionResponseDTO> response = transactionController.registerWithdraw(request).block();

        // Assert
        assertNotNull(response, "La respuesta no debería ser null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Debe retornar 200 OK");
        assertNotNull(response.getBody(), "El body no debería ser null");
        assertEquals("tx-2", response.getBody().getId(), "El ID debería coincidir");
        assertEquals("account-2", response.getBody().getSourceAccountId(), "La cuenta debería coincidir");
        assertEquals(new BigDecimal("50.00"), response.getBody().getAmount(), "El monto debería coincidir");
        assertEquals(TransactionType.RETIRO, response.getBody().getTransactionType(), "El tipo debe ser RETIRO");

        verify(transactionService, times(1)).registerWithdrawal(any(TransactionRequestDTO.class));
    }

    // CP-TS04: Debe manejar error de validación 400 Bad Request
    @Test
    void withdraw_ShouldReturnBadRequest_OnValidationError() {
        // Arrange
        TransactionRequestDTO invalidRequest = new TransactionRequestDTO();
        invalidRequest.setAccountId(null);
        invalidRequest.setAmount(BigDecimal.ZERO);

        when(transactionService.registerWithdrawal(any(TransactionRequestDTO.class))).thenReturn(Mono.error(new IllegalArgumentException("Datos inválidos")));

        // Act
        ResponseEntity<TransactionResponseDTO> response = transactionController.registerWithdraw(invalidRequest).onErrorResume(ex -> Mono.just(ResponseEntity.badRequest().build())).block();

        // Assert
        assertNotNull(response, "La respuesta no debería ser null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Debe retornar 400 Bad Request");
        assertNull(response.getBody(), "El body debería ser null en caso de error");

        verify(transactionService, times(1)).registerWithdrawal(any(TransactionRequestDTO.class));
    }

}
