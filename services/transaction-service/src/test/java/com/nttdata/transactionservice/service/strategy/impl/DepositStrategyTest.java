package com.nttdata.transactionservice.service.strategy.impl;

import com.nttdata.transactionservice.client.Account;
import com.nttdata.transactionservice.client.AccountWebClient;
import com.nttdata.transactionservice.dto.TransactionRequestDTO;
import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.mapper.TransactionMapper;
import com.nttdata.transactionservice.model.Transaction;
import com.nttdata.transactionservice.model.TransactionType;
import com.nttdata.transactionservice.repository.TransactionRepository;
import com.nttdata.transactionservice.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DepositStrategyTest {

    @Mock
    private AccountWebClient accountWebClient;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private DepositStrategy depositStrategy;

    // CP-TS23 Debe procesar depósito exitosamente
    @Test
    void execute_shouldExecuteDepositSuccessfully() {
        // Arrange
        String accountId = "account-1";
        BigDecimal depositAmount = new BigDecimal("150.00");

        TransactionRequestDTO request = new TransactionRequestDTO(accountId, depositAmount);

        Account updatedAccount = new Account();
        updatedAccount.setId(accountId);
        updatedAccount.setBalance(BigDecimal.valueOf(700));

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.DEPOSITO)
                .amount(depositAmount)
                .sourceAccountId(accountId)
                .build();

        TransactionResponseDTO responseDTO = TransactionResponseDTO.builder()
                .sourceAccountId(accountId)
                .amount(depositAmount)
                .transactionType(TransactionType.DEPOSITO)
                .build();

        when(accountWebClient.depositBalanceAccount(any(TransactionRequestDTO.class)))
                .thenReturn(Mono.just(updatedAccount));

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(Mono.just(transaction));

        when(transactionMapper.toDTO(any(Transaction.class)))
                .thenReturn(responseDTO);

        // Act + Assert
        StepVerifier.create(depositStrategy.execute(request))
                .expectNextMatches(r -> r.getTransactionType() == TransactionType.DEPOSITO
                        && r.getSourceAccountId().equals(accountId)
                        && r.getAmount().compareTo(depositAmount) == 0)
                .verifyComplete();
    }

    // CP-TS24 Debe manejar error del AccountWebClient correctamente
    @Test
    void execute_shouldHandleErrorFromAccountWebClient() {
        // Arrange
        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .accountId("123")
                .amount(BigDecimal.valueOf(200))
                .build();

        when(accountWebClient.depositBalanceAccount(any(TransactionRequestDTO.class)))
                .thenReturn(Mono.error(new RuntimeException("Account service unavailable")));

        // Act + Assert
        StepVerifier.create(depositStrategy.execute(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("Account service unavailable"))
                .verify();
    }

    // CP-TS25 Debe retornar TransactionRequestDTO como tipo soportado
    @Test
    void getSupportedType_shouldReturnSupportedType() {
        Class<?> supportedType = depositStrategy.getSupportedType();
        assertEquals(TransactionRequestDTO.class, supportedType);
    }

    // CP-TS26  Debe retornar "DEPOSITO" con nombre de estrategia
    @Test
    void getStrategyName_ShouldReturnDEPOSITO() {
        // Act
        String strategyName = depositStrategy.getStrategyName();

        // Assert
        assertEquals("DEPOSITO", strategyName, "La estrategia debe retornar 'DEPOSITO'");
    }
}