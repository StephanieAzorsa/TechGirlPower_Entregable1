package com.nttdata.customerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private String id;

    @NotNull
    private String name;

    @NotNull
    private String lastName;

    @NotNull
    @Column(unique = true)
    private String dni;

    @NotNull
    @Email // (usuario@dominio.com)
    private String email;

    @NotNull
    private LocalDate registeredDate;
}
