package com.nttdata.transactionservice.service;

import com.nttdata.transactionservice.dto.TransactionRequestDTO;
import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.dto.TransferRequestDTO;
import com.nttdata.transactionservice.model.TransactionType;
import com.nttdata.transactionservice.service.strategy.TransactionStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TransactionContextTest {
    @Mock
    private TransactionStrategy<TransactionRequestDTO> depositStrategy;

    @Mock
    private TransactionStrategy<TransactionRequestDTO> withdrawalStrategy;

    @Mock
    private TransactionStrategy<TransferRequestDTO> transferStrategy;

    //CP-TS18	Debe ejecutar estrategia de depósito correctamente
    @Test
    void executeStrategy_shouldExecuteDepositStrategy() {
        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .accountId("acc123")
                .amount(new BigDecimal("100.00"))
                .build();

        TransactionResponseDTO response = TransactionResponseDTO.builder()
                .transactionType(TransactionType.DEPOSITO)
                .amount(new BigDecimal("100.00"))
                .build();

        when(depositStrategy.getSupportedType()).thenReturn(TransactionRequestDTO.class);
        when(depositStrategy.getStrategyName()).thenReturn("DEPOSITO");
        when(depositStrategy.execute(any())).thenReturn(Mono.just(response));
        // mocks secundarios para evitar NPE
        when(withdrawalStrategy.getSupportedType()).thenReturn(TransactionRequestDTO.class);
        when(withdrawalStrategy.getStrategyName()).thenReturn("RETIRO");
        when(transferStrategy.getSupportedType()).thenReturn(TransferRequestDTO.class);
        when(transferStrategy.getStrategyName()).thenReturn("TRANSFERENCIA");

        TransactionContext transactionContext = new TransactionContext(List.of(
                depositStrategy,
                withdrawalStrategy,
                transferStrategy));

        StepVerifier.create(transactionContext.executeStrategy(
                        request, "DEPOSITO"))
                .expectNext(response)
                .verifyComplete();

        verify(depositStrategy).execute(request);
    }

    //CP-TS19	Debe ejecutar estrategia de retiro correctamente
    @Test
    void executeStrategy_shouldExecuteWithdrawStrategy() {
        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .accountId("123")
                .amount(BigDecimal.valueOf(50))
                .build();

        TransactionResponseDTO response = TransactionResponseDTO.builder()
                .transactionType(TransactionType.RETIRO)
                .amount(BigDecimal.valueOf(50))
                .build();

        when(withdrawalStrategy.getSupportedType()).thenReturn(TransactionRequestDTO.class);
        when(withdrawalStrategy.getStrategyName()).thenReturn("RETIRO");
        when(withdrawalStrategy.execute(any())).thenReturn(Mono.just(response));
        // mocks secundarios para evitar NPE
        when(depositStrategy.getSupportedType()).thenReturn(TransactionRequestDTO.class);
        when(depositStrategy.getStrategyName()).thenReturn("DEPOSITO");
        when(transferStrategy.getSupportedType()).thenReturn(TransferRequestDTO.class);
        when(transferStrategy.getStrategyName()).thenReturn("TRANSFERENCIA");

        TransactionContext transactionContext = new TransactionContext(List.of(depositStrategy, withdrawalStrategy, transferStrategy));

        StepVerifier.create(transactionContext.executeStrategy(request, "RETIRO"))
                .expectNext(response)
                .verifyComplete();

        verify(withdrawalStrategy).execute(request);
    }

    //CP-TS20	Debe ejecutar estrategia de transferencia correctamente
    @Test
    void executeStrategy_shouldExecuteTransferStrategy() {
        TransferRequestDTO request = TransferRequestDTO.builder()
                .sourceAccountId("123")
                .destinationAccountId("456")
                .amount(BigDecimal.valueOf(200))
                .build();

        TransactionResponseDTO response = TransactionResponseDTO.builder()
                .transactionType(TransactionType.TRANSFERENCIA)
                .amount(BigDecimal.valueOf(200))
                .build();

        when(transferStrategy.getSupportedType()).thenReturn(TransferRequestDTO.class);
        when(transferStrategy.getStrategyName()).thenReturn("TRANSFERENCIA");
        when(transferStrategy.execute(any())).thenReturn(Mono.just(response));
        // mocks secundarios para evitar NPE
        when(depositStrategy.getSupportedType()).thenReturn(TransactionRequestDTO.class);
        when(depositStrategy.getStrategyName()).thenReturn("DEPOSITO");
        when(withdrawalStrategy.getSupportedType()).thenReturn(TransactionRequestDTO.class);
        when(withdrawalStrategy.getStrategyName()).thenReturn("RETIRO");

        TransactionContext transactionContext = new TransactionContext(List.of(depositStrategy, withdrawalStrategy, transferStrategy));

        StepVerifier.create(transactionContext.executeStrategy(request, "TRANSFERENCIA"))
                .expectNext(response)
                .verifyComplete();

        verify(transferStrategy).execute(request);
    }

    //CP-TS21	Debe manejar error cuando no encuentra estrategia
    @Test
    void executeStrategy_shouldThrowWhenStrategyNotFound() {
        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .accountId("123")
                .amount(BigDecimal.valueOf(100))
                .build();

        when(withdrawalStrategy.getSupportedType()).thenReturn(TransactionRequestDTO.class);
        when(withdrawalStrategy.getStrategyName()).thenReturn("RETIRO");
        when(transferStrategy.getSupportedType()).thenReturn(TransferRequestDTO.class);
        when(transferStrategy.getStrategyName()).thenReturn("TRANSFERENCIA");

        TransactionContext transactionContext = new TransactionContext(List.of(withdrawalStrategy, transferStrategy));

        StepVerifier.create(transactionContext.executeStrategy(request, "DEPOSITO"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("No se encontró estrategia"))
                .verify();
    }

    //CP-TS22	Debe manejar error cuando hay tipo incompatible
    @Test
    void executeStrategy_shouldThrowWhenTypeIncompatible() {
        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .accountId("123")
                .amount(BigDecimal.valueOf(100))
                .build();

        @SuppressWarnings("unchecked")
        TransactionStrategy<TransactionRequestDTO> incompatibleStrategy = mock(TransactionStrategy.class);

        when(incompatibleStrategy.getSupportedType()).thenReturn((Class) TransferRequestDTO.class);
        when(incompatibleStrategy.getStrategyName()).thenReturn("DEPOSITO");

        TransactionContext transactionContext = new TransactionContext(List.of(incompatibleStrategy));

        StepVerifier.create(transactionContext.executeStrategy(request, "DEPOSITO"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("No se encontró estrategia"))
                .verify();
    }
}