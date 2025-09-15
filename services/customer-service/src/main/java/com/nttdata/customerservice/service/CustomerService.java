package com.nttdata.customerservice.service;

import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.dto.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {

    List<CustomerResponseDTO> getCustomers();

    CustomerResponseDTO getCustomerById(String id);

    CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO);

    CustomerResponseDTO updateCustomer(String id, CustomerRequestDTO customerRequestDTO);

    void deleteCustomer(String id);

}