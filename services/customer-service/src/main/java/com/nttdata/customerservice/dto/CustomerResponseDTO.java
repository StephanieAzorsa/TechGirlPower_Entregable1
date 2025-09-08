package com.nttdata.customerservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerResponseDTO {
    private String id;
    private String name;
    private String lastName;
    private String dni;
    private String email;
}
