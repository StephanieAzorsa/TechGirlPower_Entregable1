package com.nttdata.transactionservice.service.strategy.impl;

import com.nttdata.transactionservice.client.AccountWebClient;
import com.nttdata.transactionservice.dto.TransactionRequestDTO;
import com.nttdata.transactionservice.dto.TransactionResponseDTO;
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
public class WithdrawalStrategy implements TransactionStrategy<TransactionRequestDTO> {

    private final AccountWebClient accountWebClient;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Mono<TransactionResponseDTO> execute(TransactionRequestDTO request) {
        return accountWebClient.withdrawBalanceAccount(request)
                .flatMap(account -> createAndSaveTransaction(request))
                .map(transactionMapper::toDTO);
    }

    @Override
    public Class<TransactionRequestDTO> getSupportedType() {
        return TransactionRequestDTO.class;
    }

    @Override
    public String getStrategyName() {
        return "RETIRO";
    }

    private Mono<Transaction> createAndSaveTransaction(
            TransactionRequestDTO request) {

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.RETIRO)
                .amount(request.getAmount())
                .date(LocalDateTime.now())
                .sourceAccountId(request.getAccountId())
                .destinationAccountId(null)
                .build();
        return transactionRepository.save(transaction);
    }
}
