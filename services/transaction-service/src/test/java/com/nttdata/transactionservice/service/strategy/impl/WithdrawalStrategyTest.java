package com.nttdata.transactionservice.service.strategy.impl;

import com.nttdata.transactionservice.client.Account;
import com.nttdata.transactionservice.client.AccountWebClient;
import com.nttdata.transactionservice.dto.TransactionRequestDTO;
import com.nttdata.transactionservice.dto.TransactionResponseDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WithdrawalStrategyTest {

    @Mock
    private AccountWebClient accountWebClient;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private WithdrawalStrategy withdrawalStrategy;

    // CP-TS27: Debe procesar retiro exitosamente
    @Test
    void execute_ShouldProcessWithdrawalSuccessfully() {
        // Arrange
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountId("account-1");
        request.setAmount(new BigDecimal("100.00"));

        Account accountAfterWithdraw = new Account();
        accountAfterWithdraw.setId("account-1");
        accountAfterWithdraw.setBalance(new BigDecimal("900.00"));

        Transaction transaction = new Transaction();
        transaction.setId("tx-1");
        transaction.setTransactionType(TransactionType.RETIRO);
        transaction.setAmount(request.getAmount());
        transaction.setDate(LocalDateTime.now());
        transaction.setSourceAccountId(request.getAccountId());

        TransactionResponseDTO transactionResponse = new TransactionResponseDTO();
        transactionResponse.setId("tx-1");
        transactionResponse.setTransactionType(TransactionType.RETIRO);
        transactionResponse.setAmount(request.getAmount());
        transactionResponse.setDate(transaction.getDate());
        transactionResponse.setSourceAccountId(request.getAccountId());

        when(accountWebClient.withdrawBalanceAccount(any(TransactionRequestDTO.class))).thenReturn(Mono.just(accountAfterWithdraw));

        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(transaction));

        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(transactionResponse);

        // Act + Assert
        StepVerifier.create(withdrawalStrategy.execute(request)).expectNextMatches(resp -> resp.getId().equals("tx-1") && resp.getTransactionType() == TransactionType.RETIRO && resp.getAmount().compareTo(new BigDecimal("100.00")) == 0 && resp.getSourceAccountId().equals("account-1")).verifyComplete();

        // Verify
        verify(accountWebClient, times(1)).withdrawBalanceAccount(any(TransactionRequestDTO.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(transactionMapper, times(1)).toDTO(any(Transaction.class));
    }

    // CP-TS28: Debe manejar error del AccountWebClient correctamente
    @Test
    void execute_ShouldHandleAccountWebClientError() {
        // Arrange
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountId("account-1");
        request.setAmount(new BigDecimal("100.00"));

        RuntimeException clientError = new RuntimeException("Error al comunicarse con AccountWebClient");

        when(accountWebClient.withdrawBalanceAccount(any(TransactionRequestDTO.class))).thenReturn(Mono.error(clientError));

        // Act + Assert
        StepVerifier.create(withdrawalStrategy.execute(request)).expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Error al comunicarse con AccountWebClient")).verify();

        // Verify
        verify(accountWebClient, times(1)).withdrawBalanceAccount(any(TransactionRequestDTO.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(transactionMapper, never()).toDTO(any(Transaction.class));
    }

    // CP-TS29: Debe retornar TransactionRequestDTO como tipo soportado
    @Test
    void getSupportedType_ShouldReturnTransactionRequestDTO() {
        // Act
        Class<TransactionRequestDTO> supportedType = withdrawalStrategy.getSupportedType();

        // Assert
        assertNotNull(supportedType, "El tipo soportado no debería ser null");
        assertEquals(TransactionRequestDTO.class, supportedType, "El tipo soportado debe ser TransactionRequestDTO");
    }

    // CP-TS30: Debe retornar "RETIRO" como nombre de estrategia
    @Test
    void getStrategyName_ShouldReturnRetiro() {
        // Act
        String strategyName = withdrawalStrategy.getStrategyName();

        // Assert
        assertNotNull(strategyName, "El nombre de la estrategia no debería ser null");
        assertEquals("RETIRO", strategyName, "El nombre de la estrategia debe ser 'RETIRO'");
    }

}
