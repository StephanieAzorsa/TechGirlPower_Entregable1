package com.nttdata.transactionservice.mapper;

import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    // Convierte Transaction a TransactionResponseDTO
    public TransactionResponseDTO toDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .sourceAccountId(transaction.getSourceAccountId())
                .destinationAccountId(transaction.getDestinationAccountId())
                .build();
    }

}
