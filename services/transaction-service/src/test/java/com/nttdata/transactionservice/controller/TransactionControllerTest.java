package com.nttdata.transactionservice.controller;

import com.nttdata.transactionservice.dto.TransactionRequestDTO;
import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.dto.TransferRequestDTO;
import com.nttdata.transactionservice.model.TransactionType;
import com.nttdata.transactionservice.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(TransactionController.class)
public class TransactionControllerTest {

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private WebTestClient webTestClient;


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


        when(transactionService.registerDeposit(any(TransactionRequestDTO.class)))
                .thenReturn(Mono.just(transaction));

        // Act & Assert usando WebTestClient
        webTestClient.post()
                .uri("/api/v1/transactions/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionResponseDTO.class)
                .value(response -> {
                    assertEquals("tx-1", response.getId());
                    assertEquals("account-1", response.getSourceAccountId());
                    assertEquals(new BigDecimal("150.00"), response.getAmount());
                    assertEquals(TransactionType.DEPOSITO, response.getTransactionType());
                });

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
        when(transactionService.registerDeposit(any(TransactionRequestDTO.class)))
                .thenReturn(Mono.empty());

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/transactions/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
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

        when(transactionService.registerWithdrawal(any(TransactionRequestDTO.class)))
                .thenReturn(Mono.just(transaction));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/transactions/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionResponseDTO.class)
                .value(response -> {
                    assertEquals("tx-2", response.getId());
                    assertEquals("account-2", response.getSourceAccountId());
                    assertEquals(new BigDecimal("50.00"), response.getAmount());
                    assertEquals(TransactionType.RETIRO, response.getTransactionType());
                });

        verify(transactionService, times(1)).registerWithdrawal(any(TransactionRequestDTO.class));
    }

    // CP-TS04: Debe manejar error de validación 400 Bad Request
    @Test
    void withdraw_ShouldReturnBadRequest_OnValidationError() {
        // Arrange
        TransactionRequestDTO invalidRequest = new TransactionRequestDTO();
        invalidRequest.setAccountId(null);
        invalidRequest.setAmount(BigDecimal.ZERO);

        when(transactionService.registerWithdrawal(any(TransactionRequestDTO.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Datos inválidos")));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/transactions/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * CP-TS05 - POST /transactions/transfer - Debe procesar transferencia exitosamente (200 OK)
     */
    @Test
    void testRegisterTransfer_Success() {
        TransactionResponseDTO mockResponse = TransactionResponseDTO.builder()
                .id("TX123")
                .transactionType(TransactionType.TRANSFERENCIA)
                .amount(new BigDecimal("100"))
                .date(LocalDateTime.now())
                .sourceAccountId("123")
                .destinationAccountId("456")
                .build();

        when(transactionService.registerTransfer(any(TransferRequestDTO.class)))
                .thenReturn(Mono.just(mockResponse));

        webTestClient.post()
                .uri("/api/v1/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"sourceAccountId\":\"123\",\"destinationAccountId\":\"456\",\"amount\":100}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("TX123")
                .jsonPath("$.transactionType").isEqualTo("TRANSFERENCIA")
                .jsonPath("$.amount").isEqualTo(100)
                .jsonPath("$.sourceAccountId").isEqualTo("123")
                .jsonPath("$.destinationAccountId").isEqualTo("456");
    }

    /**
     * CP-TS06 - POST /transactions/transfer - Debe manejar error de validación (400 Bad Request)
     */
    @Test
    void testRegisterTransfer_BadRequest() {
        // Si el servicio no retorna nada, el controller debería responder 400
        webTestClient.post()
                .uri("/api/v1/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"sourceAccountId\":\"\",\"destinationAccountId\":\"\",\"amount\":0}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * CP-TS07 - GET /transactions/record - Debe listar transacciones exitosamente (200 OK)
     */
    @Test
    void testListTransactions_Success() {
        TransactionResponseDTO tx1 = TransactionResponseDTO.builder()
                .id("TX001")
                .transactionType(TransactionType.DEPOSITO)
                .amount(new BigDecimal("50"))
                .date(LocalDateTime.now())
                .sourceAccountId("111")
                .destinationAccountId(null)
                .build();

        TransactionResponseDTO tx2 = TransactionResponseDTO.builder()
                .id("TX002")
                .transactionType(TransactionType.TRANSFERENCIA)
                .amount(new BigDecimal("100"))
                .date(LocalDateTime.now())
                .sourceAccountId("123")
                .destinationAccountId("456")
                .build();

        when(transactionService.listTransactions())
                .thenReturn(Flux.just(tx1, tx2));

        webTestClient.get()
                .uri("/api/v1/transactions/record")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("TX001")
                .jsonPath("$[0].transactionType").isEqualTo("DEPOSITO")
                .jsonPath("$[1].id").isEqualTo("TX002")
                .jsonPath("$[1].transactionType").isEqualTo("TRANSFERENCIA");
    }

}
