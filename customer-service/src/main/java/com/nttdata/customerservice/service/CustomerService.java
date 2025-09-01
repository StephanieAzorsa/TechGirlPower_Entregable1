package com.nttdata.customerservice.service;

import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.dto.CustomerResponseDTO;
import com.nttdata.customerservice.exception.CustomerHasActiveAccountsException;
import com.nttdata.customerservice.exception.CustomerNotFoundException;
import com.nttdata.customerservice.exception.DniAlreadyExistsException;
import com.nttdata.customerservice.mapper.CustomerMapper;
import com.nttdata.customerservice.model.Customer;
import com.nttdata.customerservice.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;

    public CustomerService(CustomerRepository customerRepository, RestTemplate restTemplate) {
        this.customerRepository = customerRepository;
        this.restTemplate = restTemplate;
    }

    public List<CustomerResponseDTO> getCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(CustomerMapper::toDTO).toList();
    }

    public CustomerResponseDTO getCustomerById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente no encontrado con ID: " + id));

        return CustomerMapper.toDTO(customer);
    }

    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        if (customerRepository.existsByDni(customerRequestDTO.getDni())) {
            throw new DniAlreadyExistsException("Un cliente con este DNI ya existe "
                    + customerRequestDTO.getDni());
        }

        Customer newCustomer = customerRepository
                .save(CustomerMapper.toModel(customerRequestDTO));

        return CustomerMapper.toDTO(newCustomer);
    }

    public CustomerResponseDTO updateCustomer(String id,
                                              CustomerRequestDTO customerRequestDTO) {

        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("El cliente con ID [" + id + "] no se encontró"));

        if (customerRepository.existsByDniAndIdNot(customerRequestDTO.getDni(), id)) {
            throw new DniAlreadyExistsException("Un cliente con este DNI ya existe "
                    + customerRequestDTO.getDni());
        }

        customer.setName(customerRequestDTO.getName());
        customer.setLastName(customerRequestDTO.getLastname());
        customer.setDni(customerRequestDTO.getDni());
        customer.setEmail(customerRequestDTO.getEmail());

        Customer updateCustomer = customerRepository.save(customer);
        return CustomerMapper.toDTO(updateCustomer);
    }

    public void deleteCustomer(String id) {
        String accountsServiceUrl = "http://account-service/api/v1/accounts/customer/" + id;

        try {
            ResponseEntity<List> response = restTemplate
                    .getForEntity(accountsServiceUrl, List.class);

            if (response.getStatusCode().is2xxSuccessful() &&
                    response.getBody() != null &&
                    !response.getBody().isEmpty()) {
                throw new CustomerHasActiveAccountsException("No se puede eliminar el " +
                        "cliente porque tiene cuentas activas");
            }
            customerRepository.deleteById(id);

        } catch (HttpClientErrorException.NotFound ex) {
            customerRepository.deleteById(id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al verificar cuentas del cliente: " + ex.getMessage());
        }
    }

}