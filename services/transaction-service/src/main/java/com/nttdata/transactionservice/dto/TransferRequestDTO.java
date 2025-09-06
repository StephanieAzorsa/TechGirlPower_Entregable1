package com.nttdata.transactionservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferRequestDTO {
    private String sourceAccountId;
    private String destinationAccountId;
    private String amount;
}
