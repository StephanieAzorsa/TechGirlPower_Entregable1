package com.nttdata.transactionservice.service.strategy.impl;

import com.nttdata.transactionservice.client.Account;
import com.nttdata.transactionservice.client.AccountWebClient;
import com.nttdata.transactionservice.dto.TransactionRequestDTO;
import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.dto.TransferRequestDTO;
import com.nttdata.transactionservice.mapper.TransactionMapper;
import com.nttdata.transactionservice.model.Transaction;
import com.nttdata.transactionservice.model.TransactionType;
import com.nttdata.transactionservice.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferStrategyTest {

    @Mock
    private AccountWebClient accountWebClient;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransferStrategy transferStrategy;

    // CP-TS31	execute()	Debe procesar transferencia exitosamente
    @Test
    void testExecute_Success() {
        // Arrange
        TransferRequestDTO request = TransferRequestDTO.builder()
                .sourceAccountId("acc123")
                .destinationAccountId("acc456")
                .amount(new BigDecimal("25.00"))
                .build();

        Transaction transaction = Transaction.builder()
                .id("trans1")
                .transactionType(TransactionType.TRANSFERENCIA)
                .amount(new BigDecimal("25.00"))
                .date(LocalDateTime.now())
                .sourceAccountId("acc123")
                .destinationAccountId("acc456")
                .build();

        TransactionResponseDTO responseDTO = TransactionResponseDTO.builder()
                .id("trans1")
                .transactionType(TransactionType.TRANSFERENCIA)
                .amount(new BigDecimal("25.00"))
                .sourceAccountId("acc123")
                .destinationAccountId("acc456")
                .build();

        // Respuestas mock para los servicios web
        Account withdrawResponse = new Account();
        Account depositResponse = new Account();

        when(accountWebClient.withdrawBalanceAccount(any()))
                .thenReturn(Mono.just(withdrawResponse));
        when(accountWebClient.depositBalanceAccount(any()))
                .thenReturn(Mono.just(depositResponse));
        when(transactionRepository.save(any())).thenReturn(Mono.just(transaction));
        when(transactionMapper.toDTO(any())).thenReturn(responseDTO);

        // Act & Assert
        StepVerifier
                .create(transferStrategy.execute(request))
                .expectNext(responseDTO)
                .verifyComplete();

        verify(accountWebClient).withdrawBalanceAccount(any(TransactionRequestDTO.class));
        verify(accountWebClient).depositBalanceAccount(any(TransactionRequestDTO.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    // CP-TS32	execute()	Debe manejar error del AccountWebClient correctamente
    @Test
    void execute_ShouldHandleAccountWebClientError() {
        // Arrange
        TransferRequestDTO request = TransferRequestDTO.builder()
                .sourceAccountId("acc123")
                .destinationAccountId("acc456")
                .amount(new BigDecimal("100.00"))
                .build();

        // Simular error en el primer llamado (withdraw)
        when(accountWebClient.withdrawBalanceAccount(any(TransactionRequestDTO.class)))
                .thenReturn(Mono.error(new RuntimeException("Service unavailable")));

        StepVerifier.create(transferStrategy.execute(request))
                .expectErrorMatches(throwable
                        -> throwable instanceof RuntimeException
                        && throwable.getMessage()
                        .equals("Service unavailable"))
                .verify();

        // Verify que se llamó a withdraw pero NO a deposit
        verify(accountWebClient).withdrawBalanceAccount(any(TransactionRequestDTO.class));
        verify(accountWebClient, never()).depositBalanceAccount(any(TransactionRequestDTO.class));
        verify(transactionRepository, never()).save(any());
    }

    // CP-TS33	getSupportedType()	Debe retornar TransferRequestDTO como tipo soportado
    @Test
    void testGetSupportedType_ShouldReturnTransferRequestDTO() {
        assert transferStrategy.getSupportedType().equals(TransferRequestDTO.class);
    }

    // CP-TS34	getStrategyName()	Debe retornar "TRANSFERENCIA" con nombre de estrategia
    @Test
    void testGetStrategyName_ShouldReturnTransfer() {
        assert "TRANSFERENCIA".equals(transferStrategy.getStrategyName());
    }

    @Test
    void debugMethodReturnTypes() {
        try {
            Class<?> clazz = accountWebClient.getClass();
            java.lang.reflect.Method withdrawMethod =
                    clazz.getMethod("withdrawBalanceAccount", TransactionRequestDTO.class);
            java.lang.reflect.Method depositMethod =
                    clazz.getMethod("depositBalanceAccount", TransactionRequestDTO.class);

            System.out.println("withdrawBalanceAccount return type: " + withdrawMethod.getReturnType());
            System.out.println("depositBalanceAccount return type: " + depositMethod.getReturnType());

        } catch (NoSuchMethodException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Test
    void debugMethodGenericTypes() {
        try {
            Class<?> clazz = accountWebClient.getClass();
            java.lang.reflect.Method withdrawMethod =
                    clazz.getMethod("withdrawBalanceAccount", TransactionRequestDTO.class);
            java.lang.reflect.Method depositMethod =
                    clazz.getMethod("depositBalanceAccount", TransactionRequestDTO.class);

            System.out.println("withdrawBalanceAccount generic return type: " + withdrawMethod.getGenericReturnType());
            System.out.println("depositBalanceAccount generic return type: " + depositMethod.getGenericReturnType());

        } catch (NoSuchMethodException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
