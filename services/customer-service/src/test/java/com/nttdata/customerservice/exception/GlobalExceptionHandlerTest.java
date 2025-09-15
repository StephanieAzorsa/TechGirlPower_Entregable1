package com.nttdata.customerservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // CP-CS17: MethodArgumentNotValidException retorna 400 con errores de validación
    @Test
    void handleMethodArgumentNotValidException_Returns400witherrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "field1", "error1");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, String>> response = handler.handleMethodArgumentNotValidException(ex);

        assertEquals(400, response.getStatusCodeValue(), "Debe retornar código 400");
        assertTrue(response.getBody().containsKey("field1"), "Debe contener error para 'field1'");
        assertEquals("error1", response.getBody().get("field1"), "El mensaje de error debe ser 'error1'");

        System.out.println("Errores de validación: " + response.getBody());
    }

    // CP-CS18: DniAlreadyExistsException retorna 400 con mensaje apropiado
    @Test
    void handleDniAlreadyExistsException_Returns400WithMessage() {
        DniAlreadyExistsException ex = new DniAlreadyExistsException("DNI duplicado");

        ResponseEntity<Map<String, String>> response = handler.handleDniAlreadyExistsException(ex);

        assertEquals(400, response.getStatusCodeValue(), "Debe retornar código 400");
        assertTrue(response.getBody().containsKey("message"), "Debe contener clave 'message'");
        assertEquals("El DNI ya existe", response.getBody().get("message"), "Mensaje debe ser 'El DNI ya existe'");

        System.out.println("Mensaje excepción DNI duplicado: " + response.getBody().get("message"));
    }

    // CP-CS19: CustomerNotFoundException retorna 404 con mensaje apropiado
    @Test
    void handleCustomerNotFoundException_Returns404WithMessage() {
        CustomerNotFoundException ex = new CustomerNotFoundException("Cliente no encontrado");

        ResponseEntity<Map<String, String>> response = handler.handleCustomerNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        assertTrue(response.getBody().containsKey("message"), "Debe contener clave 'message'");
        assertEquals("Cliente no encontrado", response.getBody().get("message"), "Mensaje debe ser 'Cliente no encontrado'");

        System.out.println("Mensaje excepción cliente no encontrado: " + response.getBody().get("message"));
    }

}
