package com.nttdata.customerservice.service;

import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.exception.DniAlreadyExistsException;
import com.nttdata.customerservice.repository.CustomerRepository;
import com.nttdata.customerservice.service.strategy.DniValidationStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DniValidationStrategyTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private DniValidationStrategy dniValidationStrategy;

    @Test
    void validate_shouldThrowWhenDniExistsForNewCustomer() {
        // Arrange
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setDni("65321485");

        when(customerRepository.existsByDni("65321485")).thenReturn(true);

        // Act & Assert
        DniAlreadyExistsException exception = assertThrows(
                DniAlreadyExistsException.class,
                () -> dniValidationStrategy.validate(dto, null)
        );

        assertTrue(exception.getMessage().contains("65321485"));
        verify(customerRepository, times(1))
                .existsByDni("65321485");
    }

    @Test
    void validate_shouldThrowWhenDniExistsForOtherCustomer() {
        // Arrange
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setDni("65321485");
        String currentCustomerId = "123";

        when(customerRepository.existsByDniAndIdNot("65321485", "123"))
                .thenReturn(true);

        // Act & Assert
        DniAlreadyExistsException exception = assertThrows(
                DniAlreadyExistsException.class,
                () -> dniValidationStrategy.validate(dto, currentCustomerId)
        );

        assertTrue(exception.getMessage().contains("65321485"));
        verify(customerRepository, times(1))
                .existsByDniAndIdNot("65321485", "123");
    }

    @Test
    void validate_shouldPassWhenDniNotExists() {
        // Arrange
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setDni("65321485");

        when(customerRepository.existsByDni("65321485")).thenReturn(false);

        // Act & Assert (no debe lanzar excepción)
        assertDoesNotThrow(() -> dniValidationStrategy.validate(dto, null));
        verify(customerRepository, times(1))
                .existsByDni("65321485");
    }
}
