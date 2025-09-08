package com.nttdata.transactionservice.client;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Account {

    private String id;
    private String accountNumber;
    private BigDecimal balance;
    private String customerId;

}
