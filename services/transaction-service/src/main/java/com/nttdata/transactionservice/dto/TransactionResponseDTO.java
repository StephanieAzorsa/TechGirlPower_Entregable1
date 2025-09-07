package com.nttdata.transactionservice.dto;

import com.nttdata.transactionservice.model.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionResponseDTO {
    private String id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDate date;
    private String sourceAccountId;
    private String destinationAccountId;
}
