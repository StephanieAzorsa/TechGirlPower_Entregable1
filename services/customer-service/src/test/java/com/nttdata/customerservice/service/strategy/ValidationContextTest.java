package com.nttdata.customerservice.service.strategy;

import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.exception.DniAlreadyExistsException;
import com.nttdata.customerservice.service.ValidationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidationContextTest {

    @Mock
    private ValidationStrategy strategy1;

    @Mock
    private ValidationStrategy strategy2;

    @Mock
    private ValidationStrategy strategy3;

    @InjectMocks
    private ValidationContext validationContext;

    // CP-CS29: Debe llamar a todas las estrategias en orden
    @Test
    void executeValidations_shouldCallAllStrategiesInOrder() {
        // Arrange
        CustomerRequestDTO dto = new CustomerRequestDTO();
        String customerId = "123";

        // Configurar mocks
        when(strategy1.getStrategyName()).thenReturn("STRATEGY_1");
        when(strategy2.getStrategyName()).thenReturn("STRATEGY_2");
        when(strategy3.getStrategyName()).thenReturn("STRATEGY_3");

        // Inyectar lista de estrategias
        validationContext = new ValidationContext(List.of(strategy1, strategy2, strategy3));

        // Act
        validationContext.executeValidations(dto, customerId);

        // Assert
        verify(strategy1, times(1)).validate(dto, customerId);
        verify(strategy2, times(1)).validate(dto, customerId);
        verify(strategy3, times(1)).validate(dto, customerId);

        // Verificar orden de ejecución
        verify(strategy1, description("Primera estrategia debería ejecutarse primero"))
                .validate(any(), anyString());
        verify(strategy2, description("Segunda estrategia debería ejecutarse después"))
                .validate(any(), anyString());
        verify(strategy3, description("Tercera estrategia debería ejecutarse al final"))
                .validate(any(), anyString());
    }

    // CP-CS30: Debe para en la primera excepción
    @Test
    void executeValidations_shouldStopOnFirstException() {
        // Arrange
        CustomerRequestDTO dto = new CustomerRequestDTO();
        String customerId = "123";

        validationContext = new ValidationContext(List.of(strategy1, strategy2, strategy3));

        // Configurar primera estrategia para lanzar excepción
        doThrow(new DniAlreadyExistsException("DNI duplicado"))
                .when(strategy1).validate(any(), anyString());

        // Act & Assert
        DniAlreadyExistsException exception = assertThrows(
                DniAlreadyExistsException.class,
                () -> validationContext.executeValidations(dto, customerId)
        );

        assertEquals("DNI duplicado", exception.getMessage());

        // Verificar que solo se ejecutó la primera estrategia
        verify(strategy1, times(1)).validate(any(), anyString());
        verify(strategy2, never()).validate(any(), anyString());
        verify(strategy3, never()).validate(any(), anyString());
    }

    // CP-CS31: Debe manejar las estrategias de lista vacía
    @Test
    void executeValidations_shouldHandleEmptyStrategiesList() {
        // Arrange
        CustomerRequestDTO dto = new CustomerRequestDTO();
        String customerId = "123";

        // Contexto con lista vacía de estrategias
        validationContext = new ValidationContext(List.of());

        // Act & Assert (no debería lanzar excepción)
        assertDoesNotThrow(() -> validationContext.executeValidations(dto, customerId));
    }

    // CP-CS32: Debe manejar el customerId  nulo
    @Test
    void executeValidations_shouldHandleNullCustomerId() {
        // Arrange
        CustomerRequestDTO dto = new CustomerRequestDTO();

        validationContext = new ValidationContext(List.of(strategy1));

        // Act & Assert
        assertDoesNotThrow(() -> validationContext
                .executeValidations(dto, null));
        verify(strategy1, times(1))
                .validate(any(), isNull());
    }
}
