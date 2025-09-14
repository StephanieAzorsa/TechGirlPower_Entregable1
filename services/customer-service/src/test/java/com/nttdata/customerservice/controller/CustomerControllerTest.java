package com.nttdata.customerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.dto.CustomerResponseDTO;
import com.nttdata.customerservice.exception.CustomerNotFoundException;
import com.nttdata.customerservice.exception.DniAlreadyExistsException;
import com.nttdata.customerservice.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getCustomers_returnListOfCustomerResponseDTOs() throws Exception {
        CustomerResponseDTO customer1 = new CustomerResponseDTO();
        customer1.setId("1");
        customer1.setName("Pepito");
        customer1.setLastName("Pedraza");
        customer1.setDni("123456789");
        customer1.setEmail("pepito-pedraza25@yahoo.com");

        CustomerResponseDTO customer2 = new CustomerResponseDTO();
        customer2.setId("2");
        customer2.setName("Lupita");
        customer2.setLastName("Hidalgo");
        customer2.setDni("10203040");
        customer2.setEmail("lupita_2025@gmail.com");

        when(customerService.getCustomers()).thenReturn(Arrays.asList(customer1, customer2));

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pepito"))
                .andExpect(jsonPath("$[1].name").value("Lupita"));
    }

    @Test
    void getCustomers_returnEmptyList_whenNoCustomerFoundExist() throws Exception {
        when(customerService.getCustomers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getCustomerById_returnsCustomer_whenExists() throws Exception {
        String id = "1";
        CustomerResponseDTO response = new CustomerResponseDTO();
        response.setId(id);
        response.setName("Belinda");
        response.setLastName("Perez");
        response.setDni("12345678");
        response.setEmail("beli@gmail.com");

        when(customerService.getCustomerById(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Belinda"))
                .andExpect(jsonPath("$.dni").value("12345678"));
    }

    @Test
    void getCustomerById_throwsException_whenCustomerDoesNotExist() throws Exception {
        String id = "99";
        when(customerService.getCustomerById(id))
                .thenThrow(new CustomerNotFoundException("Cliente no encontrado con ID: 99"));

        mockMvc.perform(get("/api/v1/customers/{id}", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCustomer_shouldThrowWhenDniExists() throws Exception {
        CustomerRequestDTO request = new CustomerRequestDTO();
        request.setName("Ana");
        request.setLastname("Gomez");
        request.setDni("65321485");
        request.setEmail("ana-95@gmail.com");
        request.setRegisteredDate(LocalDate.now().toString());

        when(customerService.createCustomer(any(CustomerRequestDTO.class)))
                .thenThrow(new DniAlreadyExistsException("El DNI ya existe"));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El DNI ya existe"));
    }

    @Test
    void updateCustomer_shouldUpdateWhenExistsAndDniNotDuplicated() throws Exception {
        String customerId = "1";
        CustomerRequestDTO request = new CustomerRequestDTO();
        request.setName("Pepito P. Actualizado");
        request.setLastname("Pedraza");
        request.setDni("10203040");
        request.setEmail("pepito_pedraza@gmail.com");
        request.setRegisteredDate(LocalDate.now().toString());

        CustomerResponseDTO response = new CustomerResponseDTO();
        response.setId("1");
        response.setName("Pepito P. Actualizado");
        response.setLastName("Pedraza");
        response.setDni("10203040");
        response.setEmail("pepito_pedraza@gmail.com");

        when(customerService.updateCustomer(eq(customerId), any(CustomerRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pepito P. Actualizado"))
                .andExpect(jsonPath("$.dni").value("10203040"));
    }

    @Test
    void updateCustomer_shouldThrowExceptionWhenCustomerDoesNotExist() throws Exception {
        String customerId = "99";
        CustomerRequestDTO request = new CustomerRequestDTO();
        request.setName("Cliente Inexistente");
        request.setLastname("Sin Apellido");
        request.setDni("99999999");
        request.setEmail("noemail@example.com");
        request.setRegisteredDate(LocalDate.now().toString());

        when(customerService.updateCustomer(eq(customerId), any(CustomerRequestDTO.class)))
                .thenThrow(new CustomerNotFoundException("Cliente no encontrado"));

        mockMvc.perform(put("/api/v1/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cliente no encontrado"));
    }
}
