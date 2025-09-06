package com.nttdata.transactionservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequestDTO {
    @NotBlank
    private String accountId;

    @NotNull
    @Positive(message = "El monto debe ser positivo")
    private Double monto;

}
