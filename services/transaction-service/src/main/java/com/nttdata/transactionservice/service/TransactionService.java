package com.nttdata.transactionservice.service;

import com.nttdata.transactionservice.client.AccountWebClient;
import com.nttdata.transactionservice.dto.*;
import com.nttdata.transactionservice.exception.AccountNotFoundException;
import com.nttdata.transactionservice.exception.InsufficientBalanceException;
import com.nttdata.transactionservice.mapper.TransactionMapper;
import com.nttdata.transactionservice.model.Transaction;
import com.nttdata.transactionservice.model.TransactionType;
import com.nttdata.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountWebClient accountWebClient;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;


    public Mono<TransactionResponseDTO> registerWithdrawal(TransactionRequestDTO request) {
        return accountWebClient.getAccountById(request.getAccountId())
                .switchIfEmpty(Mono.error(new AccountNotFoundException("Cuenta no encontrada")))
                .flatMap(account -> {
                    if (account.getBalance() == null || account.getBalance().compareTo(request.getAmount()) < 0) {
                        return Mono.error(new InsufficientBalanceException("Saldo insuficiente"));
                    }
                    return accountWebClient.withdrawBalanceAccount(request)
                            .flatMap(updatedAccount -> {
                                Transaction transaction = Transaction.builder()
                                        .id(UUID.randomUUID().toString())
                                        .transactionType(TransactionType.RETIRO)
                                        .amount(request.getAmount())
                                        .date(LocalDateTime.now())
                                        .sourceAccountId(request.getAccountId())
                                        .destinationAccountId(null)
                                        .build();
                                return transactionRepository.save(transaction)
                                        .flatMap(transactionMapper::toDTO);
                            });
                });
    }

}
