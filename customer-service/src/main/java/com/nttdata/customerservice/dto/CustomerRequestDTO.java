package com.nttdata.customerservice.dto;

import com.nttdata.customerservice.dto.validators.CreateCustomerValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequestDTO {
    @NotBlank(message = "Nombre requerido")
    @Size(max = 100, message = "Nombre no puede exceder 100 caracteres")
    private String name;

    @NotBlank(message = "Apellidos requerido")
    @Size(max = 100, message = "Apellidos no puede exceder 100 caracteres")
    private String lastname;

    @NotBlank(message = "Email requerido")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "DNI requerido")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe ser de 8 dígitos")
    private String dni;

    @NotBlank(groups = CreateCustomerValidationGroup.class,
            message = "Registro de la fecha es requerido")
    private String registeredDate;
}
