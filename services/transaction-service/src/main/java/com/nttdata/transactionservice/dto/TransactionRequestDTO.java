package com.nttdata.transactionservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionRequestDTO {
    @NotBlank(message = "Account ID es requerido")
    private String accountId;

    @NotNull(message = "Monto es requerido")
    @Positive(message = "Monto debe ser mayor a 0")
    private BigDecimal amount;

}
