package com.nttdata.accountservice.service;

import com.nttdata.accountservice.dto.AccountRequestDTO;
import com.nttdata.accountservice.dto.AccountResponseDTO;
import com.nttdata.accountservice.dto.TransactionRequestDTO;
import com.nttdata.accountservice.exception.AccountNotFoundException;
import com.nttdata.accountservice.exception.CustomerNotFoundException;
import com.nttdata.accountservice.exception.InsufficientBalanceException;
import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.model.AccountType;
import com.nttdata.accountservice.repository.AccountRepository;
import com.nttdata.accountservice.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AccountServiceImpl accountService;

    // ------------------------- Pruebas para get accounts -------------------------

    // Debe retornar lista de cuentas cuando existen
    @Test
    void getAllAccounts_ShouldReturnAllAccounts() {
        // Arrange
        Account account1 = new Account(
                "a1b2c3d4-e5f6-7890-1234-ef1234567890",
                "1000000001",
                BigDecimal.valueOf(250.0),
                AccountType.AHORROS,
                "a1b2c3d4-e5f6-7890-1234-ef1234567891");

        Account account2 = new Account(
                "a1b2c3d4-e5f6-7890-1234-ef1234567892",
                "1000000002",
                BigDecimal.valueOf(250.0),
                AccountType.CORRIENTE,
                "a1b2c3d4-e5f6-7890-1234-ef1234567893");

        when(accountRepository.findAll()).thenReturn(List.of(account1, account2));

        // Act
        List<AccountResponseDTO> result = accountService.getAllAccounts();

        // Assert
        assertEquals(2, result.size());
        verify(accountRepository, times(1)).findAll();
    }

    // TODO: Debe retornar lista vacía cuando no hay cuentas


    // -------------------- Pruebas para mostrar las cuentas asociadas a un cliente --------------------

    // Debe retornar cuentas del cliente
    @Test
    void getAccountByCustomerId_ShouldReturnCustomerAccounts() {
        // Arrange
        String customerId = "a1b2c3d4-e5f6-7890-1234-ef1234567893";

        Account account1 = new Account(
                "1",
                "1000000001",
                BigDecimal.valueOf(250.0),
                AccountType.CORRIENTE,
                customerId);
        Account account2 = new Account(
                "2",
                "1000000002",
                BigDecimal.valueOf(250.0),
                AccountType.CORRIENTE,
                customerId);

        when(accountRepository.findByCustomerId(customerId)).thenReturn(List.of(account1, account2));

        // Act
        List<AccountResponseDTO> result = accountService.getAccountsByCustomerId(customerId);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream()
                .allMatch(acc -> customerId.equals(acc.getCustomerId())));
        verify(accountRepository, times(1))
                .findByCustomerId(customerId);
    }

    // Debe retornar lista vacía del cliente con sus cuentas
    @Test
    void getAccountByCustomerId_WhenNoAccounts_ShouldReturnEmptyList() {
        // Arrange
        String customerId = "customer-with-no-accounts";

        // Mock del repositorio para retornar lista vacía
        when(accountRepository.findByCustomerId(customerId))
                .thenReturn(Collections.emptyList());

        // Act
        List<AccountResponseDTO> result = accountService.getAccountsByCustomerId(customerId);

        // Assert
        assertNotNull(result, "La lista no debería ser null");
        assertTrue(result.isEmpty(), "La lista debería estar vacía");

        // Verificar que se llamó al métod del repositorio
        verify(accountRepository, times(1)).findByCustomerId(customerId);
    }

    // -------------------- Pruebas para la creación de una cuenta bancaria --------------------

    // Debe lanzar excepción cuando cliente no existe
    @Test
    void createAccount_WhenCustomerNotExists_ShouldThrowException() {
        // Arrange
        AccountRequestDTO request = new AccountRequestDTO();
        request.setCustomerId("non-existent");
        request.setAccountType(AccountType.AHORROS);
        request.setInitialBalance(BigDecimal.valueOf(100.0)); // ← Añade saldo válido

        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenThrow(new RuntimeException("Customer not found"));

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> accountService.createAccount(request));
        verify(accountRepository, never()).save(any(Account.class));
    }

    // Debe rechazar saldo inicial negativo
    @Test
    void createAccount_WhenNegativeInitialBalance_ShouldThrowException() {
        // Arrange
        AccountRequestDTO request = new AccountRequestDTO();
        request.setCustomerId("existing-customer");
        request.setAccountType(AccountType.AHORROS);
        request.setInitialBalance(BigDecimal.valueOf(-100.0));

        // Act & Assert
        assertThrows(InsufficientBalanceException.class,
                () -> accountService.createAccount(request));

        // VERIFICAR que NUNCA se llama al restTemplate ni al repository
        verify(restTemplate, never()).getForEntity(anyString(), eq(Object.class));
        verify(accountRepository, never()).save(any(Account.class));
    }


    // ------------------------ Pruebas para depósito ------------------------

    // Debe lanzar excepción si cuenta no existe
    @Test
    void deposit_WhenAccountNotExists_ShouldThrowException() {
        // Arrange
        String accountId = "non-existent";
        TransactionRequestDTO transaction = new TransactionRequestDTO();
        transaction.setAmount(new BigDecimal("50.00"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class,
                () -> accountService.deposit(accountId, transaction));
        verify(accountRepository, never()).save(any(Account.class));
    }

    // Debe rechazar depósito cero
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
                () -> accountService.deposit(accountId, transaction));
        verify(accountRepository, never()).save(any(Account.class));
    }

    // ------------------------ Pruebas para retiro ------------------------

    //
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
        AccountResponseDTO result = accountService.withdraw(accountId, transaction);

        // Assert
        assertEquals(new BigDecimal("50.00"), result.getBalance());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    // Cuentas de AHORRO: Debe rechazar retiro que deje saldo negativo
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
                () -> accountService.withdraw(accountId, transaction));
        verify(accountRepository, never()).save(any(Account.class));
    }

    // Cuenta CORRIENTE: Debe rechazar sobregiro fuera del límite (-500)
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
                () -> accountService.withdraw(accountId, transaction));
        verify(accountRepository, never()).save(any(Account.class));
    }

    // Cuenta CORRIENTE: Debe tener éxito en caso de límite exacto
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
        AccountResponseDTO result = accountService.withdraw(accountId, transaction);

        // Assert
        assertEquals(new BigDecimal("-500.00"), result.getBalance());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    // ------------------------ Pruebas para deleteAccount ------------------------

    // Debe eliminar cuenta existente
    @Test
    void deleteAccount_WhenAccountExists_ShouldDelete() {
        // Arrange
        String accountId = "account-1";
        when(accountRepository.existsById(accountId)).thenReturn(true);

        // Act
        accountService.deleteAccount(accountId);

        // Assert
        verify(accountRepository, times(1)).existsById(accountId);
        verify(accountRepository, times(1)).deleteById(accountId);
    }

    // Debe lanzar excepción si cuenta no existe
    @Test
    void deleteAccount_WhenAccountNotExists_ShouldThrowException() {
        // Arrange
        String accountId = "non-existent";
        when(accountRepository.existsById(accountId)).thenReturn(false);

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> accountService.deleteAccount(accountId));
        verify(accountRepository, never()).deleteById(accountId);
    }

}
