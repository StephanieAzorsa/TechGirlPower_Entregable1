package com.nttdata.transactionservice;

import com.nttdata.transactionservice.dto.TransactionRequestDTO;
import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.dto.TransferRequestDTO;
import com.nttdata.transactionservice.model.TransactionType;
import com.nttdata.transactionservice.service.TransactionContext;
import com.nttdata.transactionservice.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

public class TransactionContextTest {

    // CP-TS18 Debe ejecutar estrategia de depósito correctamente
    @Test
    void executeStrategy_shouldExecuteDepositStrategy() {
        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .accountId("123")
                .amount(BigDecimal.valueOf(100))
                .build();

        TransactionResponseDTO responseDTO = TransactionResponseDTO.builder()
                .id("tx123")
                .transactionType(TransactionType.DEPOSITO)
                .amount(BigDecimal.valueOf(100))
                .date(LocalDateTime.now())
                .sourceAccountId("123")
                .destinationAccountId("123")
                .build();

        TransactionContext transactionContext = mock(TransactionContext.class);
        when(transactionContext.executeStrategy(request, "DEPOSITO"))
                .thenReturn(Mono.just(responseDTO));

        TransactionServiceImpl service = new TransactionServiceImpl(transactionContext, null, null);

        StepVerifier.create(service.registerDeposit(request))
                .expectNextMatches(r -> r.getTransactionType() == TransactionType.DEPOSITO)
                .verifyComplete();

        verify(transactionContext).executeStrategy(request, "DEPOSITO");
    }

    // CP-TS19 Debe ejecutar estrategia de retiro correctamente
    @Test
    void executeStrategy_shouldExecuteWithdrawStrategy() {
        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .accountId("123")
                .amount(BigDecimal.valueOf(50))
                .build();

        TransactionResponseDTO responseDTO = TransactionResponseDTO.builder()
                .id("tx124")
                .transactionType(TransactionType.RETIRO)
                .amount(BigDecimal.valueOf(50))
                .date(LocalDateTime.now())
                .sourceAccountId("123")
                .destinationAccountId("123")
                .build();

        TransactionContext transactionContext = mock(TransactionContext.class);
        when(transactionContext.executeStrategy(request, "RETIRO"))
                .thenReturn(Mono.just(responseDTO));

        TransactionServiceImpl service = new TransactionServiceImpl(transactionContext, null, null);

        StepVerifier.create(service.registerWithdrawal(request))
                .expectNextMatches(r -> r.getTransactionType() == TransactionType.RETIRO)
                .verifyComplete();

        verify(transactionContext).executeStrategy(request, "RETIRO");
    }

    // CP-TS20 Debe ejecutar estrategia de transferencia correctamente
    @Test
    void executeStrategy_shouldExecuteTransferStrategy() {
        TransferRequestDTO request = TransferRequestDTO.builder()
                .sourceAccountId("123")
                .destinationAccountId("456")
                .amount(BigDecimal.valueOf(200))
                .build();

        TransactionResponseDTO responseDTO = TransactionResponseDTO.builder()
                .id("tx125")
                .transactionType(TransactionType.TRANSFERENCIA)
                .amount(BigDecimal.valueOf(200))
                .date(LocalDateTime.now())
                .sourceAccountId("123")
                .destinationAccountId("456")
                .build();

        TransactionContext transactionContext = mock(TransactionContext.class);
        when(transactionContext.executeStrategy(request, "TRANSFERENCIA"))
                .thenReturn(Mono.just(responseDTO));

        TransactionServiceImpl service = new TransactionServiceImpl(transactionContext, null, null);

        StepVerifier.create(service.registerTransfer(request))
                .expectNextMatches(r -> r.getTransactionType() == TransactionType.TRANSFERENCIA)
                .verifyComplete();

        verify(transactionContext).executeStrategy(request, "TRANSFERENCIA");
    }

    // CP-TS21 Debe manejar error cuando no encuentra estrategia
    @Test
    void executeStrategy_shouldThrowWhenStrategyNotFound() {
        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .accountId("123")
                .amount(BigDecimal.valueOf(100))
                .build();

        TransactionContext transactionContext = mock(TransactionContext.class);
        when(transactionContext.executeStrategy(request, "DEPOSITO"))
                .thenReturn(Mono.error(new IllegalArgumentException("Estrategia no encontrada")));

        TransactionServiceImpl service = new TransactionServiceImpl(transactionContext, null, null);

        StepVerifier.create(service.registerDeposit(request))
                .expectErrorMessage("Estrategia no encontrada")
                .verify();

        verify(transactionContext).executeStrategy(request, "DEPOSITO");
    }

    // CP-TS22 Debe manejar error cuando hay tipo incompatible
    @Test
    void executeStrategy_shouldThrowWhenTypeIncompatible() {
        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .accountId("123")
                .amount(BigDecimal.valueOf(100))
                .build();

        TransactionContext transactionContext = mock(TransactionContext.class);
        when(transactionContext.executeStrategy(request, "DEPOSITO"))
                .thenReturn(Mono.error(new IllegalArgumentException("Tipo incompatible")));

        TransactionServiceImpl service = new TransactionServiceImpl(transactionContext, null, null);

        StepVerifier.create(service.registerDeposit(request))
                .expectErrorMessage("Tipo incompatible")
                .verify();

        verify(transactionContext).executeStrategy(request, "DEPOSITO");
    }
}