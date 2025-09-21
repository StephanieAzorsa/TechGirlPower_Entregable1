package com.nttdata.transactionservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {
    @NotBlank(message = "Cuenta origen es requerida")
    private String sourceAccountId;

    @NotBlank(message = "Cuenta destino es requerida")
    private String destinationAccountId;

    @NotNull(message = "Monto es requerido")
    @Positive(message = "Monto debe ser mayor a 0")
    private BigDecimal amount;
}
