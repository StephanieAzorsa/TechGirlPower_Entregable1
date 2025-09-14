package com.nttdata.accountservice.controller;

import com.nttdata.accountservice.dto.AccountResponseDTO;
import com.nttdata.accountservice.dto.TransactionRequestDTO;
import com.nttdata.accountservice.model.AccountType;
import com.nttdata.accountservice.service.AccountService;
import com.nttdata.accountservice.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    // TODO: GET /api/v1/accounts
    //  Retorna 200 OK con lista de cuentas
    @Test
    void getAllAccounts_ShouldReturnAccounts() {

    }

    // TODO: GET /api/v1/accounts/{id}
    //  Retorna 200 OK con cuenta
    @Test
    void getAccountById_ShouldReturnAccount() {

    }

    // GET /api/v1/accounts/customer/{customerId}
    // Retorna 200 OK con cuentas del cliente
    @Test
    void getAccountsByCustomerId_ShouldReturnAccounts() {
        // Arrange
        String customerId = "customer-1";
        AccountResponseDTO account1 = new AccountResponseDTO(
                "1",
                "1234567890",
                new BigDecimal("200.00"),
                AccountType.AHORROS,
                customerId);
        AccountResponseDTO account2 = new AccountResponseDTO(
                "2",
                "0987654321",
                new BigDecimal("200.00"),
                AccountType.CORRIENTE,
                customerId);

        when(accountService.getAccountsByCustomerId(customerId))
                .thenReturn(List.of(account1, account2));

        // Act
        ResponseEntity<List<AccountResponseDTO>> response = accountController
                .getAccountsByCustomerId(customerId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody()); // 'size' may produce 'NullPointerException'
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody()
                .stream()
                .allMatch(acc ->
                        customerId.equals(acc.getCustomerId())));
        verify(accountService, times(1))
                .getAccountsByCustomerId(customerId);
    }

    // TODO: POST /api/v1/accounts
    //  Retorna 201 Created con cuenta creada
    @Test
    void createAccount_ShouldCreateAccount() {

    }

    // TODO: PUT /api/v1/accounts/{accountId}/deposit
    //  Retorna 200 OK con saldo actualizado
    @Test
    void deposit_ShouldUpdateBalance() {

    }

    // PUT /api/v1/accounts/{accountId}/withdraw
    // Debe retorna 200 OK con saldo actualizado
    @Test
    void withdraw_ShouldUpdateBalance() {
        // Arrange
        String accountId = "1";
        TransactionRequestDTO transaction = new TransactionRequestDTO();
        transaction.setAmount(new BigDecimal("50.00"));

        AccountResponseDTO updatedAccount = new AccountResponseDTO(
                accountId,
                "1234567890",
                new BigDecimal("30.00"),
                AccountType.AHORROS,
                "customer-1");

        when(accountService
                .withdraw(eq(accountId), any(TransactionRequestDTO.class)))
                .thenReturn(updatedAccount);

        // Act
        ResponseEntity<AccountResponseDTO> response = accountController.withdraw(accountId, transaction);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(new BigDecimal("30.00"), response.getBody().getBalance());
        verify(accountService, times(1))
                .withdraw(eq(accountId), any(TransactionRequestDTO.class));
    }

    // DELETE /api/v1/accounts/{id}
    // Debe retornar 204 No Content
    @Test
    void deleteAccount_ShouldDeleteAccount() {
        // Arrange
        String accountId = "1";
        doNothing().when(accountService).deleteAccount(accountId);

        // Act
        ResponseEntity<Void> response = accountController
                .deleteAccount(accountId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT,
                response.getStatusCode());
        verify(accountService, times(1))
                .deleteAccount(accountId);
    }
}
