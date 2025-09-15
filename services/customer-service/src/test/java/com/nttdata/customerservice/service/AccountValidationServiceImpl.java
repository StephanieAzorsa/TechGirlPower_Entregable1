package com.nttdata.customerservice.service;

import com.nttdata.customerservice.exception.CustomerHasActiveAccountsException;
import com.nttdata.customerservice.service.impl.AccountValidationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountValidationServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AccountValidationServiceImpl accountValidationService;

    // CP-CS21:
    @Test
    void validateCustomerHasNoAccounts_WhenNoAccounts_ShouldNotThrowException() {
        // Arrange
        String customerId = "123e4567-e89b-12d3-a456-426614174000";
        String url = "http://localhost:8082/api/v1/accounts/customer/" + customerId;

        when(restTemplate.getForObject(url, Object[].class))
                .thenReturn(new Object[0]); // Array vacío = no hay cuentas

        // Act & Assert
        assertDoesNotThrow(() ->
                accountValidationService.validateCustomerHasNoAccounts(customerId));
    }

    // CP-CS22:
    @Test
    void validateCustomerHasNoAccounts_WhenAccountsExist_ShouldThrowException() {
        // Arrange
        String customerId = "123e4567-e89b-12d3-a456-426614174001";
        String url = "http://localhost:8082/api/v1/accounts/customer/" + customerId;

        Object[] accounts = {new Object(), new Object()}; // Array con 2 cuentas
        when(restTemplate.getForObject(url, Object[].class))
                .thenReturn(accounts);

        // Act & Assert
        CustomerHasActiveAccountsException exception = assertThrows(
                CustomerHasActiveAccountsException.class,
                () -> accountValidationService.validateCustomerHasNoAccounts(customerId)
        );

        assertEquals("Cliente tiene cuentas activas", exception.getMessage());
    }

    // CP-CS23:
    @Test
    void validateCustomerHasNoAccounts_WhenServerError_ShouldThrowResponseStatusException() {
        // Arrange
        String customerId = "123e4567-e89b-12d3-a456-426614174003";
        String url = "http://localhost:8082/api/v1/accounts/customer/" + customerId;

        when(restTemplate.getForObject(url, Object[].class))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> accountValidationService.validateCustomerHasNoAccounts(customerId)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Error técnico al verificar cuentas"));
    }

    // CP-CS24:
    @Test
    void validateCustomerHasNoAccounts_WhenConnectionError_ShouldThrowResponseStatusException() {
        // Arrange
        String customerId = "123e4567-e89b-12d3-a456-426614174004";
        String url = "http://localhost:8082/api/v1/accounts/customer/" + customerId;

        when(restTemplate.getForObject(url, Object[].class))
                .thenThrow(new ResourceAccessException("Connection timeout"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> accountValidationService.validateCustomerHasNoAccounts(customerId)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Error técnico al verificar cuentas"));
        assertTrue(exception.getReason().contains("Connection timeout"));
    }

    // CP-CS25:
    @Test
    void validateCustomerHasNoAccounts_WhenNullResponse_ShouldNotThrowException() {
        // Arrange
        String customerId = "123e4567-e89b-12d3-a456-426614174005";
        String url = "http://localhost:8082/api/v1/accounts/customer/" + customerId;

        when(restTemplate.getForObject(url, Object[].class))
                .thenReturn(null); // Respuesta null

        // Act & Assert
        assertDoesNotThrow(() ->
                accountValidationService.validateCustomerHasNoAccounts(customerId));
    }

    // CP-CS26: Debe lanzar excepción si el Response contenga el array vacío
    @Test
    void validateCustomerHasNoAccounts_WhenEmptyArray_ShouldNotThrowException() {
        // Arrange
        String customerId = "123e4567-e89b-12d3-a456-426614174006";
        String url = "http://localhost:8082/api/v1/accounts/customer/" + customerId;

        when(restTemplate.getForObject(url, Object[].class))
                .thenReturn(new Object[0]); // Array vacío

        // Act & Assert
        assertDoesNotThrow(() ->
                accountValidationService.validateCustomerHasNoAccounts(customerId));
    }
}
