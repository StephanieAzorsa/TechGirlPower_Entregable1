package com.nttdata.accountservice.service;

import com.nttdata.accountservice.dto.AccountResponseDTO;
import com.nttdata.accountservice.dto.TransactionRequestDTO;
import com.nttdata.accountservice.exception.AccountNotFoundException;
import com.nttdata.accountservice.exception.InsufficientBalanceException;
import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.model.AccountType;
import com.nttdata.accountservice.repository.AccountRepository;
import com.nttdata.accountservice.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    // ------------------------ Pruebas para depósito ------------------------

    // CP-AS13: Debe aumentar saldo correctamente
    @Test
    void deposit_ShouldIncreaseBalance_WhenAmountIsValid() {
        // Arrange
        String accountId = "account-1";
        Account account = new Account(
                accountId,
                "1000000001",
                BigDecimal.valueOf(100.0),
                AccountType.AHORROS,
                "customer-1"
        );

        TransactionRequestDTO transaction = new TransactionRequestDTO();
        transaction.setAmount(BigDecimal.valueOf(50.0));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AccountResponseDTO result = transactionService.deposit(accountId, transaction);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(150.0), result.getBalance(), "El saldo debe aumentar correctamente");
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    // CP-AS14: Debe lanzar excepción si cuenta no existe
    @Test
    void deposit_WhenAccountNotExists_ShouldThrowException() {
        // Arrange
        String accountId = "non-existent";
        TransactionRequestDTO transaction = new TransactionRequestDTO();
        transaction.setAmount(new BigDecimal("50.00"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class,
                () -> transactionService.deposit(accountId, transaction));
        verify(accountRepository, never()).save(any(Account.class));
    }

    // CP-AS15: Debe rechazar depósito negativo
    @Test
    void deposit_ShouldThrowException_WhenAmountIsNegative() {
        // Arrange
        String accountId = "account-1";
        Account account = new Account(
                accountId,
                "1000000001",
                BigDecimal.valueOf(100.0),
                AccountType.AHORROS,
                "customer-1"
        );

        TransactionRequestDTO transaction = new TransactionRequestDTO();
        transaction.setAmount(BigDecimal.valueOf(-50.0));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.deposit(accountId, transaction),
                "Debe lanzar excepción cuando el monto es negativo");

        verify(accountRepository, never()).save(any(Account.class));
    }

    // CP-AS16: Debe rechazar depósito cero
    @Test
    void deposit_WhenZeroAmount_ShouldThrowException() {
        // Arrange
        String accountId = "account-1";
        Account account = new Account(
                accountId,
                "1000000001",
                BigDecimal.valueOf(100.0),
                AccountType.AHORROS,
                "customer-1");

        TransactionRequestDTO transaction = new TransactionRequestDTO();
        transaction.setAmount(BigDecimal.ZERO);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.deposit(accountId, transaction));
        verify(accountRepository, never()).save(any(Account.class));
    }

    // ------------------------ Pruebas para retiro ------------------------

    // CP-AS17: Cuentas de AHORRO: Debe permitir retiro con saldo suficiente
    @Test
    void withdraw_FromSavingsWithSufficientBalance_ShouldUpdateBalance() {
        // Arrange
        String accountId = "account-1";
        Account account = new Account(
                accountId,
                "1000000001",
                BigDecimal.valueOf(100.0),
                AccountType.AHORROS,
                "customer-1");

        TransactionRequestDTO transaction = new TransactionRequestDTO();
        transaction.setAmount(new BigDecimal("50.00"));

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AccountResponseDTO result = transactionService.withdraw(accountId, transaction);

        // Assert
        assertEquals(new BigDecimal("50.00"), result.getBalance());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    // CP-AS18: Debe rechazar retiro con saldo insuficiente
    @Test
    void withdraw_SavingsAccount_ShouldThrowException_WhenBalanceIsInsufficient() {
        // Arrange
        String accountId = "account-1";
        Account account = new Account(
                accountId,
                "1000000001",
                BigDecimal.valueOf(100.0),
                AccountType.AHORROS,
                "customer-1"
        );

        TransactionRequestDTO transaction = new TransactionRequestDTO();
        transaction.setAmount(BigDecimal.valueOf(200.0));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        assertThrows(InsufficientBalanceException.class,
                () -> transactionService.withdraw(accountId, transaction));

        verify(accountRepository, never()).save(any(Account.class));
    }

    // CP-AS19: Cuentas de AHORRO: Debe rechazar retiro que deje saldo negativo
    @Test
    void withdraw_FromSavingsWithInsufficientBalance_ShouldThrowException() {
        // Arrange
        String accountId = "account-1";
        Account account = new Account(
                accountId,
                "1000000001",
                BigDecimal.valueOf(100.0),
                AccountType.AHORROS,
                "customer-1");

        TransactionRequestDTO transaction = new TransactionRequestDTO();
        transaction.setAmount(new BigDecimal("150.00"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        assertThrows(InsufficientBalanceException.class,
                () -> transactionService.withdraw(accountId, transaction));
        verify(accountRepository, never()).save(any(Account.class));
    }

    // CP-AS20: Cuenta CORRIENTE: Debe permitir sobregiro dentro del límite (-500)
    @Test
    void withdraw_CheckingAccount_ShouldAllowOverdraftWithinLimit() {
        // Arrange
        String accountId = "account-1";
        Account account = new Account(
                accountId,
                "2000000001",
                BigDecimal.valueOf(100.0),
                AccountType.CORRIENTE,
                "customer-1"
        );

        TransactionRequestDTO transaction = new TransactionRequestDTO();
        transaction.setAmount(BigDecimal.valueOf(550.0));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AccountResponseDTO result = transactionService.withdraw(accountId, transaction);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("-450.0"), result.getBalance());
        verify(accountRepository, times(1)).save(account);
    }

    // CP-AS21: Cuenta CORRIENTE: Debe rechazar sobregiro fuera del límite (-500)
    @Test
    void withdraw_FromCheckingWithOverdraftExceedingLimit_ShouldThrowException() {
        // Arrange
        String accountId = "account-1";
        Account account = new Account(
                accountId,
                "1000000001",
                BigDecimal.valueOf(100.0),
                AccountType.CORRIENTE,
                "customer-1");


        TransactionRequestDTO transaction = new TransactionRequestDTO();
        transaction.setAmount(new BigDecimal("610.00"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        assertThrows(InsufficientBalanceException.class,
                () -> transactionService.withdraw(accountId, transaction));
        verify(accountRepository, never()).save(any(Account.class));
    }

    // CP-AS22: Cuenta CORRIENTE: Debe tener éxito en caso de límite exacto
    @Test
    void withdraw_FromCheckingAtOverdraftLimit_ShouldSucceed() {
        // Arrange
        String accountId = "account-1";
        Account account = new Account(
                accountId,
                "1000000001",
                BigDecimal.valueOf(100.0),
                AccountType.CORRIENTE,
                "customer-1");

        TransactionRequestDTO transaction = new TransactionRequestDTO();
        transaction.setAmount(new BigDecimal("600.00"));

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AccountResponseDTO result = transactionService.withdraw(accountId, transaction);

        // Assert
        assertEquals(new BigDecimal("-500.00"), result.getBalance());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(any(Account.class));
    }


}
