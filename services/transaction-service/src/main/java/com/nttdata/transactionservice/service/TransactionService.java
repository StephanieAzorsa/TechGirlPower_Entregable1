package com.nttdata.transactionservice.service;

import com.nttdata.transactionservice.client.Account;
import com.nttdata.transactionservice.client.AccountWebClient;
import com.nttdata.transactionservice.dto.*;
import com.nttdata.transactionservice.exception.AccountNotFoundException;
import com.nttdata.transactionservice.exception.InsufficientBalanceException;
import com.nttdata.transactionservice.exception.TransactionExecutionException;
import com.nttdata.transactionservice.mapper.TransactionMapper;
import com.nttdata.transactionservice.model.Transaction;
import com.nttdata.transactionservice.model.TransactionType;
import com.nttdata.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountWebClient accountWebClient;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public Mono<TransactionResponseDTO> registerDeposit(TransactionRequestDTO transactionRequest) {

        return accountWebClient
                .getAccountById(transactionRequest.getAccountId())
                .switchIfEmpty(Mono
                        .error(new AccountNotFoundException("Cuenta no encontrada: "
                                + transactionRequest.getAccountId())))
                .flatMap(account -> accountWebClient.depositBalanceAccount(transactionRequest))
                .flatMap(account -> {
                    Transaction transaction = Transaction.builder()
                            .transactionType(TransactionType.DEPOSITO)
                            .amount(transactionRequest.getAmount())
                            .date(LocalDateTime.now())
                            .sourceAccountId(transactionRequest.getAccountId())
                            .build();
                    return transactionRepository.save(transaction);
                })
                .flatMap(transactionMapper::toDTO)
                .onErrorResume(throwable -> {
                    if (throwable instanceof AccountNotFoundException)
                        return Mono.error(throwable);
                    return Mono.error(new TransactionExecutionException("Error al registrar depósito"));
                });
    }

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

    public Mono<TransactionResponseDTO> registerTransfer(TransferRequestDTO transferRequest) {

        Mono<Account> withdrawBalance = accountWebClient
                .withdrawBalanceAccount(createWithdrawRequest(transferRequest));

        Mono<Account> depositBalance = withdrawBalance
                .flatMap(account -> accountWebClient
                        .depositBalanceAccount(createDepositRequest(transferRequest)));

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.TRANSFERENCIA)
                .amount(transferRequest.getAmount())
                .date(LocalDateTime.now())
                .sourceAccountId(transferRequest.getSourceAccountId())
                .destinationAccountId(transferRequest.getDestinationAccountId())
                .build();

        return depositBalance
                .flatMap(account -> transactionRepository.save(transaction))
                .flatMap(transactionMapper::toDTO);
    }

    private TransactionRequestDTO createWithdrawRequest(TransferRequestDTO transferRequest) {
        return TransactionRequestDTO.builder()
                .accountId(transferRequest.getSourceAccountId())
                .amount(transferRequest.getAmount())
                .build();
    }

    private TransactionRequestDTO createDepositRequest(TransferRequestDTO transferRequest) {
        return TransactionRequestDTO.builder()
                .accountId(transferRequest.getDestinationAccountId())
                .amount(transferRequest.getAmount())
                .build();
    }

}
