package com.nttdata.customerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.dto.CustomerResponseDTO;
import com.nttdata.customerservice.exception.CustomerNotFoundException;
import com.nttdata.customerservice.exception.DniAlreadyExistsException;
import com.nttdata.customerservice.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    // CP-CS12: POST /api/v1/customers retorna 201 created con cliente creado
    @Test
    void createCustomer_Retorna201Created() throws Exception {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO();
        requestDTO.setName("Juan");
        requestDTO.setLastname("Perez");
        requestDTO.setDni("12345678");
        requestDTO.setEmail("juan@example.com");
        requestDTO.setRegisteredDate("2023-01-01");

        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
        responseDTO.setId("abc123");
        responseDTO.setName("Juan");
        responseDTO.setLastName("Perez");
        responseDTO.setDni("12345678");
        responseDTO.setEmail("juan@example.com");

        Mockito.when(customerService.createCustomer(any(CustomerRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("abc123")))
                .andExpect(jsonPath("$.name", is("Juan")))
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertTrue(content.contains("Juan"), "La respuesta debe contener el nombre Juan");
                });
    }

    // CP-CS13: GET /api/v1/customers retorna 200 OK con lista de clientes
    @Test
    void getCustomers_Retorna200ConLista() throws Exception {
        CustomerResponseDTO customer1 = new CustomerResponseDTO();
        customer1.setId("1");
        customer1.setName("Juan");
        customer1.setLastName("Perez");
        customer1.setDni("12345678");
        customer1.setEmail("juan@example.com");

        CustomerResponseDTO customer2 = new CustomerResponseDTO();
        customer2.setId("2");
        customer2.setName("Ana");
        customer2.setLastName("Lopez");
        customer2.setDni("87654321");
        customer2.setEmail("ana@example.com");

        Mockito.when(customerService.getCustomers()).thenReturn(List.of(customer1, customer2));

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Juan")))
                .andExpect(jsonPath("$[1].name", is("Ana")))
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertTrue(content.contains("Juan") && content.contains("Ana"),
                            "La respuesta debe contener los nombres Juan y Ana");
                    System.out.println(" Test getCustomers_Retorna200ConLista pasó correctamente");
                });
    }

    // CP-CS14: GET /api/v1/customers/{id} retorna 200 OK con cliente específico
    @Test
    void getCustomerById_Retorna200ConCliente() throws Exception {
        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
        responseDTO.setId("1");
        responseDTO.setName("Juan");
        responseDTO.setLastName("Perez");
        responseDTO.setDni("12345678");
        responseDTO.setEmail("juan@example.com");

        Mockito.when(customerService.getCustomerById("1")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("Juan")))
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertTrue(content.contains("Juan"), "La respuesta debe contener el nombre Juan");
                });
    }

    // CP-CS15: PUT /api/v1/customers/{id} retorna 200 OK con cliente actualizado
    @Test
    void updateCustomer_Retorna200ConClienteActualizado() throws Exception {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO();
        requestDTO.setName("Juan");
        requestDTO.setLastname("Perez");
        requestDTO.setDni("12345678");
        requestDTO.setEmail("juan@example.com");
        requestDTO.setRegisteredDate("2023-01-01");

        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
        responseDTO.setId("1");
        responseDTO.setName("Juan");
        responseDTO.setLastName("Perez");
        responseDTO.setDni("12345678");
        responseDTO.setEmail("juan@example.com");

        Mockito.when(customerService.updateCustomer(eq("1"), any(CustomerRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("Juan")))
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertTrue(content.contains("Juan"), "La respuesta debe contener el nombre Juan");
                });
    }

    // CP-CS16: DELETE /api/v1/customers/{id} retorna 204 No Content
    @Test
    void deleteCustomer_Retorna204NoContent() throws Exception {
        Mockito.doNothing().when(customerService).deleteCustomer("1");

        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent())
                .andDo(result -> {
                    int status = result.getResponse().getStatus();
                    assertEquals(204, status, "El status debe ser 204 No Content");
                });

        verify(customerService, times(1)).deleteCustomer("1");
    }

    // CP-CS17: GET /api/v1/customers retorna 200 OK con lista vacía
    @Test
    void getCustomers_returnEmptyList_whenNoCustomerFoundExist() throws Exception {
        when(customerService.getCustomers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // CP-CS18: GET /api/v1/customers/{id} lanza una excepción si el cliente no existe
    @Test
    void getCustomerById_throwsException_whenCustomerDoesNotExist() throws Exception {
        String id = "99";
        when(customerService.getCustomerById(id))
                .thenThrow(new CustomerNotFoundException("Cliente no encontrado con ID: 99"));

        mockMvc.perform(get("/api/v1/customers/{id}", id))
                .andExpect(status().isNotFound());
    }

    // CP-CS19: POST /api/v1/customers lanza una excepción si DNI existe
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

    // CP-CS20: PUT /api/v1/customers/{id} lanza una excepción si el cliente no existe
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
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente no encontrado"));
    }
}

