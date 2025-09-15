package com.nttdata.accountservice.service;

import com.nttdata.accountservice.dto.AccountRequestDTO;
import com.nttdata.accountservice.dto.AccountResponseDTO;
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

    // CP-AS01: Debe retornar lista de cuentas cuando existen
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

    // CP-AS02: Debe retornar lista vacía cuando no hay cuentas
    @Test
    void getAccounts_ShouldReturnEmptyList_WhenNoAccountsExist() {
        // Arrange
        when(accountRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<AccountResponseDTO> result = accountService.getAllAccounts();

        // Assert
        assertNotNull(result, "El resultado no debe ser null");
        assertTrue(result.isEmpty(), "La lista debe estar vacía");
        verify(accountRepository, times(1)).findAll();
    }

    // CP-AS03: Debe retornar cuenta cuando existe
    @Test
    void getAccountById_ShouldReturnAccount_WhenExists() {
        // Arrange
        String accountId = "account-1";
        Account account = new Account(
                accountId,
                "1000000001",
                BigDecimal.valueOf(500.0),
                AccountType.AHORROS,
                "customer-1"
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act
        AccountResponseDTO result = accountService.getAccountById(accountId);

        // Assert
        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(accountId, result.getId(), "El ID de la cuenta debe coincidir");
        verify(accountRepository, times(1)).findById(accountId);
    }

    // CP-AS04: Debe lanzar excepción cuando no existe
    @Test
    void getAccountById_WhenNotExists_ShouldThrowException() {
        // Arrange
        String accountId = "non-existent";
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class,
                () -> accountService.getAccountById(accountId),
                "Debe lanzar AccountNotFoundException si la cuenta no existe");

        verify(accountRepository, times(1)).findById(accountId);
    }

    // -------------------- Pruebas para mostrar las cuentas asociadas a un cliente --------------------

    // CP-AS05: Debe retornar cuentas del cliente
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

    // CP-AS06: Debe retornar lista vacía del cliente con sus cuentas
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

    // CP-AS07: Debe crear cuenta cuando un cliente existe
    @Test
    void createAccount_ShouldCreateAccount_WhenCustomerExists() {
        // Arrange
        String accountId = "a1b2c3d4-e5f6-7890-1234-ef1234567893";

        AccountRequestDTO request = new AccountRequestDTO();
        request.setInitialBalance(BigDecimal.valueOf(100.0));
        request.setAccountType(AccountType.AHORROS);
        request.setCustomerId("customer-1");

        Account savedAccount = new Account(
                accountId,
                "1000000001",
                BigDecimal.valueOf(100.0),
                AccountType.AHORROS,
                "customer-1"
        );

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        // Act
        AccountResponseDTO result = accountService.createAccount(request);

        // Assert
        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(accountId, result.getId(), "El ID de la cuenta debe coincidir");
        assertEquals("customer-1", result.getCustomerId(), "El customerId debe coincidir");

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    // -------------------- Pruebas para la creación de una cuenta bancaria --------------------

    // CP-AS08: Debe lanzar excepción cuando cliente no existe
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

    // CP-AS09: Debe validar saldo inicial mayor a 0
    @Test
    void createAccount_ShouldThrowException_WhenInitialBalanceIsZeroOrNegative() {
        // Arrange
        AccountRequestDTO request = new AccountRequestDTO();
        request.setInitialBalance(BigDecimal.ZERO);
        request.setAccountType(AccountType.AHORROS);
        request.setCustomerId("customer-1");

        // Act & Assert
        assertThrows(InsufficientBalanceException.class,
                () -> accountService.createAccount(request),
                "Debe lanzar excepción cuando el saldo inicial es 0 o negativo");

        verify(accountRepository, never()).save(any(Account.class));
    }


    // CP-AS10: Debe rechazar saldo inicial negativo
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

    // ------------------------ Pruebas para deleteAccount ------------------------

    // CP-AS11: Debe eliminar cuenta existente
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

    // CP-AS12: Debe lanzar excepción si cuenta no existe
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
