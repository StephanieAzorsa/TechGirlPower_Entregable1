package com.nttdata.customerservice.service.impl;

import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.dto.CustomerResponseDTO;
import com.nttdata.customerservice.exception.CustomerNotFoundException;
import com.nttdata.customerservice.exception.DniAlreadyExistsException;
import com.nttdata.customerservice.mapper.CustomerMapper;
import com.nttdata.customerservice.model.Customer;
import com.nttdata.customerservice.repository.CustomerRepository;
import com.nttdata.customerservice.service.AccountValidationService;
import com.nttdata.customerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountValidationService accountValidationService;

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
        if (customerRepository.existsByDni(customerRequestDTO.getDni())) {
            throw new DniAlreadyExistsException("Un cliente con este DNI ya existe "
                    + customerRequestDTO.getDni());
        }

        Customer newCustomer = customerRepository
                .save(CustomerMapper.toModel(customerRequestDTO));

        return CustomerMapper.toDTO(newCustomer);
    }

    @Override
    public CustomerResponseDTO updateCustomer(String id, CustomerRequestDTO customerRequestDTO) {
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

    @Override
    public void deleteCustomer(String id) {
        accountValidationService.validateCustomerHasNoAccounts(id);
        customerRepository.deleteById(id);
    }
}
