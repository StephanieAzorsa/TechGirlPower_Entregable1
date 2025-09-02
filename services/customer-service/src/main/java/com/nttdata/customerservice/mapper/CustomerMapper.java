package com.nttdata.customerservice.mapper;

import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.dto.CustomerResponseDTO;
import com.nttdata.customerservice.model.Customer;

import java.time.LocalDate;

public class CustomerMapper {

    /**
     * Convierte una entidad Customer a un DTO
     *
     * @param customer: La entidad Customer que se convertirá a DTO.
     * @return customerDTO: Objeto DTO con los datos del cliente para respuesta.
     */
    public static CustomerResponseDTO toDTO(Customer customer) {
        CustomerResponseDTO customerDTO = new CustomerResponseDTO();

        customerDTO.setId(customer.getId());
        customerDTO.setName(customer.getName());
        customerDTO.setLastName(customer.getLastName());
        customerDTO.setDni(customer.getDni());
        customerDTO.setEmail(customer.getEmail());

        return customerDTO;
    }

    public static Customer toModel(CustomerRequestDTO customerRequestDTO) {
        Customer customer = new Customer();

        customer.setName(customerRequestDTO.getName());
        customer.setLastName(customerRequestDTO.getLastname());
        customer.setDni(customerRequestDTO.getDni());
        customer.setEmail(customerRequestDTO.getEmail());
        customer.setRegisteredDate(LocalDate.parse(customerRequestDTO.getRegisteredDate()));

        return customer;
    }
}
