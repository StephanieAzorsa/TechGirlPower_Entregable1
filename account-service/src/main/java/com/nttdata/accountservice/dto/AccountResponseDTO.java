package com.nttdata.accountservice.dto;

import com.nttdata.accountservice.model.AccountType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountResponseDTO {
    private String id;
    private String accountNumber;
    private BigDecimal balance;
    private AccountType accountType;
    private String customerId;
}
