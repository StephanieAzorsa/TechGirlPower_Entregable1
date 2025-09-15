package com.nttdata.customerservice.controller;

import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.dto.CustomerResponseDTO;
import com.nttdata.customerservice.dto.validators.CreateCustomerValidationGroup;
import com.nttdata.customerservice.service.CustomerService;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @Validated({Default.class, CreateCustomerValidationGroup.class})
            @RequestBody CustomerRequestDTO customerRequestDTO) {

        CustomerResponseDTO customerResponseDTO = customerService
                .createCustomer(customerRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getCustomers() {
        List<CustomerResponseDTO> customers = customerService.getCustomers();
        return ResponseEntity.ok().body(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable String id) {
        CustomerResponseDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok().body(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable String id,
            @Validated({Default.class})
            @RequestBody CustomerRequestDTO customerRequestDTO) {

        CustomerResponseDTO customerResponseDTO = customerService.updateCustomer(id,
                customerRequestDTO);
        return ResponseEntity.ok().body(customerResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

}

