package com.nttdata.transactionservice.mapper;

import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.model.Transaction;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TransactionMapper {

    // Convierte Transaction a TransactionResponseDTO reactivamente
    public Mono<TransactionResponseDTO> toDTO(Transaction transaction) {
        return Mono.just(TransactionResponseDTO.builder()
                .id(transaction.getId())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .sourceAccountId(transaction.getSourceAccountId())
                .destinationAccountId(transaction.getDestinationAccountId())
                .build());
    }

}
