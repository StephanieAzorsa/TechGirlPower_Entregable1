package com.nttdata.customerservice.service;

import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.dto.CustomerResponseDTO;
import com.nttdata.customerservice.exception.CustomerNotFoundException;
import com.nttdata.customerservice.exception.DniAlreadyExistsException;
import com.nttdata.customerservice.mapper.CustomerMapper;
import com.nttdata.customerservice.model.Customer;
import com.nttdata.customerservice.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustomerService {
    //Inyeccion de dependecia
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
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
        customerRepository.deleteById(id);
    }

}