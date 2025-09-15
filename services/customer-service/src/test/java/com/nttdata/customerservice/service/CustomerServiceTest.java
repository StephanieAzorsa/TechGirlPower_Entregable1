package com.nttdata.customerservice.service;

import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.dto.CustomerResponseDTO;
import com.nttdata.customerservice.exception.CustomerHasActiveAccountsException;
import com.nttdata.customerservice.exception.CustomerNotFoundException;
import com.nttdata.customerservice.exception.DniAlreadyExistsException;
import com.nttdata.customerservice.model.Customer;
import com.nttdata.customerservice.repository.CustomerRepository;
import com.nttdata.customerservice.service.impl.CustomerServiceImpl;
import com.nttdata.customerservice.service.strategy.ValidationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountValidationService accountValidationService;

    @Mock
    private ValidationContext validationContext;

    @InjectMocks
    private CustomerServiceImpl customerService;

    // CP-CS01: Retorna lista de clientes cuando existen
    @Test
    void getCustomers_returnListOfCustomerResponseDTOs() {
        // Arrange: simulando clientes en la base de datos
        Customer customer1 = new Customer();
        customer1.setId("1");
        customer1.setName("Pepito");
        customer1.setLastName("Pedraza");
        customer1.setDni("123456789");
        customer1.setEmail("pepito-pedraza25@yahoo.com");
        customer1.setRegisteredDate(LocalDate.of(2023, 1, 15));

        Customer customer2 = new Customer();
        customer2.setId("2");
        customer2.setName("Lupita");
        customer2.setLastName("Hidalgo");
        customer2.setDni("10203040");
        customer2.setEmail("lupita_2025@gmail.com");
        customer2.setRegisteredDate(LocalDate.of(2023, 2, 10));

        List<Customer> mockCustomers = Arrays.asList(customer1, customer2);

        // Simulamos que el repositorio devuelve esa lista
        when(customerRepository.findAll()).thenReturn(mockCustomers);

        // Act: llamamos al servicio ejecutándolo
        List<CustomerResponseDTO> result = customerService.getCustomers();

        // Assert: verificamos que el resultado no sea nulo tenga los datos esperados   // assert tru o false
        assertNotNull(result, "La lista de clientes no debe ser nula");
        assertEquals(2, result.size(), "Debe haber dos clientes en la lista");

        assertEquals("Pepito", result.get(0).getName(), "El nombre del primer cliente debe ser Pepito");
        assertEquals("Lupita", result.get(1).getName(), "El nombre del primer cliente debe ser Lupita");

        // Verificamos que el repositorio fue llamado exactamente una vez
        verify(customerRepository, times(1)).findAll();

    }

    // CP-CS02: Retorna lista vacía cuando no hay clientes
    @Test
    //arrange: Simulamos que el repositorio devuelve lista vacía
    void getCustomers_returnEmptyList_whenNoCustomerFoundExist() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        // Act: llamamos al servicio
        List<CustomerResponseDTO> result = customerService.getCustomers();

        // Assert: verificamos que el resultado sea una lista vacía
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify: comprobamos que se llamó al repositorio
        verify(customerRepository).findAll();
    }

    // CP-CS03: Retorna cliente cuando existe
    @Test
    void getCustomerById_returnsCustomer_whenExists() {
        String id = "1";
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("Belinda");
        customer.setLastName("Perez");
        customer.setDni("12345678");
        customer.setEmail("beli@gmail.com");
        customer.setRegisteredDate(LocalDate.now());

        // Arrange: configuramos el mock para que devuelva un cliente válido
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        // Act: llamamos al servicio
        CustomerResponseDTO result = customerService.getCustomerById(id);

        // Assert: comprobamos que se devuelve el cliente correcto
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Belinda", result.getName());
        assertEquals("Perez", result.getLastName());

        // Verify: validamos que el repositorio fue llamado una vez
        verify(customerRepository, times(1)).findById(id);
    }

    // CP-CS04: Lanza excepción cuando cliente no existe
    @Test
    void getCustomerById_throwsException_whenCustomerDoesNotExist() {
        // Arrange: configuramos el mock para devolver Optional vacío
        String id = "452";
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> customerService.getCustomerById(id));

        assertEquals("El cliente con ID [452] no se encontró", exception.getMessage());

        verify(customerRepository, times(1)).findById(id);
    }

    // CP-CS05: Debe crear cliente cuando las validaciones pasan
    @Test
    void createCustomer_shouldCreateCustomerWhenValidationsPass() {
        // Arrange
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setName("Margarita");
        dto.setLastname("Zapata");
        dto.setDni("20304050");
        dto.setEmail("margarita_45@gmail.com");
        dto.setRegisteredDate(LocalDate.now().toString());

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> {
                    Customer c = invocation.getArgument(0);
                    c.setId("2");
                    return c;
                });

        // Act
        CustomerResponseDTO responseDTO = customerService.createCustomer(dto);

        // Assert
        assertNotNull(responseDTO, "La respuesta no debe ser nula");
        assertEquals("2", responseDTO.getId(), "El ID debe ser el generado");
        assertEquals("20304050", responseDTO.getDni(), "El DNI debe ser el mismo");
        assertEquals("Margarita", responseDTO.getName(), "El nombre debe ser el mismo");
        assertEquals("Zapata", responseDTO.getLastName(), "El apellido debe ser el mismo");
        assertEquals("margarita_45@gmail.com", responseDTO.getEmail(), "El email debe ser el mismo");

        // Verify
        verify(validationContext, times(1))
                .executeValidations(any(CustomerRequestDTO.class), isNull());
        verify(customerRepository, times(1))
                .save(any(Customer.class));
    }

    // CP-CS06: Lanza excepción cuando las validaciones fallan
    @Test
    void createCustomer_shouldThrowWhenValidationFails() {
        // Arrange: simulamos un DTO con un DNI ya registrado
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setName("Ana");
        dto.setLastname("Gomez");
        dto.setDni("65321485");
        dto.setEmail("ana-95@gmail.com");
        dto.setRegisteredDate(LocalDate.now().toString());

        // Sobrescribir el stubbing para este test específico
        doThrow(new DniAlreadyExistsException("DNI ya existe"))
                .when(validationContext)
                .executeValidations(any(CustomerRequestDTO.class), isNull());

        DniAlreadyExistsException exception = assertThrows(
                DniAlreadyExistsException.class,
                () -> customerService.createCustomer(dto)
        );

        assertEquals("DNI ya existe", exception.getMessage());

        verify(validationContext, times(1))
                .executeValidations(any(CustomerRequestDTO.class), isNull());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // CP-CS07: Actualiza cliente cuando las validaciones pasan
    @Test
    void updateCustomer_shouldUpdateCustomerWhenExistsAndValidationsPass() {
        // Arrange
        String customerId = "1";
        CustomerRequestDTO updateDTO = new CustomerRequestDTO();
        updateDTO.setName("Juan Actualizado");
        updateDTO.setLastname("Perez");
        updateDTO.setDni("12345678");
        updateDTO.setEmail("juan.actualizado@example.com");
        updateDTO.setRegisteredDate(LocalDate.now().toString());

        Customer existingCustomer = new Customer();
        existingCustomer.setId(customerId);
        existingCustomer.setName("Juan");
        existingCustomer.setLastName("Perez");
        existingCustomer.setDni("12345678");
        existingCustomer.setEmail("juan@example.com");
        existingCustomer.setRegisteredDate(LocalDate.now());

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(customerId);
        updatedCustomer.setName("Juan Actualizado");
        updatedCustomer.setLastName("Perez");
        updatedCustomer.setDni("12345678");
        updatedCustomer.setEmail("juan.actualizado@example.com");
        updatedCustomer.setRegisteredDate(LocalDate.now());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // Act
        CustomerResponseDTO result = customerService.updateCustomer(customerId, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Juan Actualizado", result.getName());
        assertEquals("Perez", result.getLastName());
        assertEquals("12345678", result.getDni());
        assertEquals("juan.actualizado@example.com", result.getEmail());

        // Verify
        verify(customerRepository, times(1))
                .findById(customerId);
        verify(validationContext, times(1))
                .executeValidations(any(CustomerRequestDTO.class), eq(customerId));
        verify(customerRepository, times(1))
                .save(any(Customer.class));
    }

    // CP-CS08: Lanza excepción cuando cliente no existe al actualizar
    @Test
    void updateCustomer_shouldThrowWhenCustomerNotFound() {
        // Arrange: ID de cliente inexistente
        String customerId = "99";

        CustomerRequestDTO updateDto = new CustomerRequestDTO();
        updateDto.setName("Cliente Inexistente");
        updateDto.setLastname("Sin Apellido");
        updateDto.setDni("99999999");
        updateDto.setEmail("noemail@example.com");
        updateDto.setRegisteredDate(LocalDate.now().toString());

        // Simulamos que no existe el cliente con ese ID
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert: esperamos que se lance la excepción personalizada
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.updateCustomer(customerId, updateDto)
        );
        assertEquals("El cliente con ID [99] no se encontró", exception.getMessage());

        // Verify: se consultó por ID, pero no se intentó guardar
        verify(customerRepository, times(1)).findById(customerId);
        verify(validationContext, never()).executeValidations(any(), any());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // CP-CS09: Lanza excepción cuando validaciones fallan
    @Test
    void updateCustomer_shouldThrowWhenValidationFails() {
        String id = "123";
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setDni("12345678");
        dto.setName("Nombre");
        dto.setLastname("Apellido");
        dto.setEmail("email@example.com");
        dto.setRegisteredDate("2023-01-01");

        Customer existingCustomer = new Customer();
        existingCustomer.setId(id);

        when(customerRepository.findById(id)).thenReturn(java.util.Optional.of(existingCustomer));
        doThrow(new DniAlreadyExistsException("DNI ya existe"))
                .when(validationContext)
                .executeValidations(any(CustomerRequestDTO.class), eq(id));

        DniAlreadyExistsException ex = assertThrows(DniAlreadyExistsException.class,
                () -> customerService.updateCustomer(id, dto));

        verify(customerRepository, times(1))
                .findById(id);
        verify(validationContext, times(1))
                .executeValidations(any(CustomerRequestDTO.class), eq(id));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // CP-CS10: Elimina cliente cuando no tiene cuentas activas
    @Test
    void deleteCustomer_WhenNoAccounts_ShouldDeleteCustomer() {
        // Arrange
        String customerId = "123e4567-e89b-12d3-a456-426614174000";

        // No hacer nada cuando se valide (no hay cuentas)
        doNothing().when(accountValidationService).validateCustomerHasNoAccounts(customerId);

        // Act
        customerService.deleteCustomer(customerId);

        // Assert
        verify(accountValidationService).validateCustomerHasNoAccounts(customerId);
        verify(customerRepository).deleteById(customerId);
    }

    // CP-CS11: deleteCustomer() lanza excepción cuando cliente tiene cuentas activas
    @Test
    void deleteCustomer_WhenHasActiveAccounts_ShouldThrowException() {
        // Arrange
        String customerId = "123e4567-e89b-12d3-a456-426614174001";

        // Simular que hay cuentas activas
        doThrow(new CustomerHasActiveAccountsException("Cliente tiene cuentas activas"))
                .when(accountValidationService).validateCustomerHasNoAccounts(customerId);

        // Act & Assert
        assertThrows(CustomerHasActiveAccountsException.class, () ->
                customerService.deleteCustomer(customerId));

        // Verificar que NO se intentó eliminar
        verify(customerRepository, never()).deleteById(customerId);
    }
}