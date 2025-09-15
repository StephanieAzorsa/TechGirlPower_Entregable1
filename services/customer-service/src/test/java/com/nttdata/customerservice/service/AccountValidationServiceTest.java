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
class AccountValidationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AccountValidationServiceImpl accountValidationService;

    // CP-CS24: Debe lanzar excepción si tiene cuenta(s) asociadas
    @Test
    void validateCustomerHasNoAccounts_WhenAccountsExist_ShouldThrowException() {
        // Arrange
        String customerId = "123";
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

    // CP-CS25: Debe lanzar excepción si existe errores del servidor
    @Test
    void validateCustomerHasNoAccounts_WhenServerError_ShouldThrowResponseStatusException() {
        // Arrange
        String customerId = "123";
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

    // CP-CS26: Debe lanzar excepción si existe errores de conexión
    @Test
    void validateCustomerHasNoAccounts_WhenConnectionError_ShouldThrowResponseStatusException() {
        // Arrange
        String customerId = "123";
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

    // CP-CS27: Debe lanzar excepción si el Response es null
    @Test
    void validateCustomerHasNoAccounts_WhenNullResponse_ShouldNotThrowException() {
        // Arrange
        String customerId = "123";
        String url = "http://localhost:8082/api/v1/accounts/customer/" + customerId;

        when(restTemplate.getForObject(url, Object[].class))
                .thenReturn(null); // Respuesta null

        // Act & Assert
        assertDoesNotThrow(() ->
                accountValidationService.validateCustomerHasNoAccounts(customerId));
    }

    // CP-CS28: Debe lanzar excepción si el Response contenga el array vacío
    @Test
    void validateCustomerHasNoAccounts_WhenEmptyArray_ShouldNotThrowException() {
        // Arrange
        String customerId = "123";
        String url = "http://localhost:8082/api/v1/accounts/customer/" + customerId;

        when(restTemplate.getForObject(url, Object[].class))
                .thenReturn(new Object[0]); // Array vacío

        // Act & Assert
        assertDoesNotThrow(() ->
                accountValidationService.validateCustomerHasNoAccounts(customerId));
    }
}
