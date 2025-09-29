package com.nttdata.customerservice.service.impl;

import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.dto.CustomerResponseDTO;
import com.nttdata.customerservice.exception.CustomerNotFoundException;
import com.nttdata.customerservice.mapper.CustomerMapper;
import com.nttdata.customerservice.model.Customer;
import com.nttdata.customerservice.repository.CustomerRepository;
import com.nttdata.customerservice.service.AccountValidationService;
import com.nttdata.customerservice.service.CustomerService;
import com.nttdata.customerservice.service.ValidationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountValidationService accountValidationService;
    private final ValidationContext validationContext;

    @Override
    public List<CustomerResponseDTO> getCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(CustomerMapper::toDTO).toList();
    }

    @Override
    public CustomerResponseDTO getCustomerById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("El cliente con ID " +
                        "[" + id + "] no se encontró"));

        return CustomerMapper.toDTO(customer);
    }

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        // Ejecutar todas las validaciones (para creación, customerId es null)
        validationContext.executeValidations(customerRequestDTO, null);

        Customer newCustomer = customerRepository
                .save(CustomerMapper.toModel(customerRequestDTO));

        return CustomerMapper.toDTO(newCustomer);
    }

    @Override
    public CustomerResponseDTO updateCustomer(
            String id,
            CustomerRequestDTO customerRequestDTO) {

        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("El cliente con ID " +
                        "[" + id + "] no se encontró"));

        // Ejecutar validaciones (para actualización, pasamos el ID)
        validationContext.executeValidations(customerRequestDTO, id);

        customer.setName(customerRequestDTO.getName());
        customer.setLastName(customerRequestDTO.getLastname());
        customer.setDni(customerRequestDTO.getDni());
        customer.setEmail(customerRequestDTO.getEmail());

        Customer updateCustomer = customerRepository.save(customer);
        return CustomerMapper.toDTO(updateCustomer);
    }

    @Override
    public void deleteCustomer(String id) {
        accountValidationService.validateCustomerHasNoAccounts(id);
        customerRepository.deleteById(id);
    }
}
