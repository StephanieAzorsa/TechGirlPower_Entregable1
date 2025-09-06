package com.nttdata.transactionservice.dto;

import com.nttdata.transactionservice.model.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TransactionResponseDTO {
    private String id;
    private TransactionType transactionType;
    private Double amount;
    private LocalDate date;
    private String sourceAccountId;
    private String destinationAccountId;
}
