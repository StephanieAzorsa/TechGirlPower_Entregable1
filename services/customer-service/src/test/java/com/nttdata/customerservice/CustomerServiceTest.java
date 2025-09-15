import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.exception.CustomerHasActiveAccountsException;
import com.nttdata.customerservice.exception.DniAlreadyExistsException;
import com.nttdata.customerservice.model.Customer;
import com.nttdata.customerservice.repository.CustomerRepository;
import com.nttdata.customerservice.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private org.springframework.web.client.RestTemplate restTemplate;

    @InjectMocks
    private CustomerService customerService;

    //DNI duplicado -Lanza excepción cuando DNI está duplicado
    @Test
    void updateCustomer_DniDuplicado_LanzaExcepcion() {
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
        when(customerRepository.existsByDniAndIdNot(dto.getDni(), id)).thenReturn(true);

        DniAlreadyExistsException ex = assertThrows(DniAlreadyExistsException.class,
                () -> customerService.updateCustomer(id, dto));
    }
// deleteCustomer() elimina cliente cuando no tiene cuentas activas

    @Test
    void deleteCustomer_SinCuentasActivas() {
        String id = "123";
        String url = "http://account-service/api/v1/accounts/customer/" + id;
        when(restTemplate.getForEntity(url, List.class))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "Not Found",
                        null,
                        null,
                        null
                ));
        doNothing().when(customerRepository).deleteById(id);
        assertDoesNotThrow(() -> customerService.deleteCustomer(id),
                "No debe lanzar excepción al eliminar cliente sin cuentas activas");
        verify(restTemplate, times(1)).getForEntity(url, List.class);
        verify(customerRepository, times(1)).deleteById(id);
    }

// deleteCustomer() lanza excepción cuando cliente tiene cuentas activas
    @Test
    void deleteCustomer_ConCuentasActivas_LanzaExcepcion() {
        String id = "123";
        String url = "http://account-service/api/v1/accounts/customer/" + id;
        ResponseEntity<List> response = new ResponseEntity<>(List.of("cuenta1"), HttpStatus.OK);
        when(restTemplate.getForEntity(eq(url), eq(List.class))).thenReturn(response);
        CustomerHasActiveAccountsException ex = assertThrows(CustomerHasActiveAccountsException.class,
                () -> customerService.deleteCustomer(id),
                "Se esperaba excepción cuando el cliente tiene cuentas activas");
        assertTrue(ex.getMessage().contains("No se puede eliminar el cliente"),
                "El mensaje debe indicar que no se puede eliminar por cuentas activas");
        verify(restTemplate, times(1)).getForEntity(url, List.class);
        verify(customerRepository, never()).deleteById(anyString());
    }
}