package com.nttdata.accountservice.dto;

import com.nttdata.accountservice.model.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountRequestDTO {

    @DecimalMin(value = "0.0", message = "El saldo no puede ser negativo")
    private BigDecimal initialBalance = BigDecimal.ZERO;

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private AccountType accountType;

    @NotNull(message = "El ID del cliente es obligatorio")
    private String customerId;

}
