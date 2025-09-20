package com.nttdata.transactionservice.service.strategy.impl;

import com.nttdata.transactionservice.client.AccountWebClient;
import com.nttdata.transactionservice.dto.TransactionRequestDTO;
import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.dto.TransferRequestDTO;
import com.nttdata.transactionservice.mapper.TransactionMapper;
import com.nttdata.transactionservice.model.Transaction;
import com.nttdata.transactionservice.model.TransactionType;
import com.nttdata.transactionservice.repository.TransactionRepository;
import com.nttdata.transactionservice.service.strategy.TransactionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TransferStrategy implements TransactionStrategy<TransferRequestDTO> {

    private final AccountWebClient accountWebClient;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Mono<TransactionResponseDTO> execute(TransferRequestDTO request) {
        TransactionRequestDTO withdrawRequest = TransactionRequestDTO.builder()
                .accountId(request.getSourceAccountId())
                .amount(request.getAmount())
                .build();

        TransactionRequestDTO depositRequest = TransactionRequestDTO.builder()
                .accountId(request.getDestinationAccountId())
                .amount(request.getAmount())
                .build();

        return accountWebClient.withdrawBalanceAccount(withdrawRequest)
                .then(accountWebClient.depositBalanceAccount(depositRequest))
                .then(createAndSaveTransaction(request))
                .map(transactionMapper::toDTO);
    }

    @Override
    public Class<TransferRequestDTO> getSupportedType() {
        return TransferRequestDTO.class;
    }

    @Override
    public String getStrategyName() {
        return "TRANSFERENCIA";
    }

    private Mono<Transaction> createAndSaveTransaction(TransferRequestDTO request) {
        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.TRANSFERENCIA)
                .amount(request.getAmount())
                .date(LocalDateTime.now())
                .sourceAccountId(request.getSourceAccountId())
                .destinationAccountId(request.getDestinationAccountId())
                .build();
        return transactionRepository.save(transaction);
    }
}