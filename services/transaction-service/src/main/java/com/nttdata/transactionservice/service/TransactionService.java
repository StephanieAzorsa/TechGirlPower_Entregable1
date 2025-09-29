package com.nttdata.transactionservice.service;

import com.nttdata.transactionservice.dto.TransactionRequestDTO;
import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.dto.TransferRequestDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {

    Flux<TransactionResponseDTO> listTransactions();

    Mono<TransactionResponseDTO> registerDeposit(TransactionRequestDTO transactionRequest);

    Mono<TransactionResponseDTO> registerWithdrawal(TransactionRequestDTO request);

    Mono<TransactionResponseDTO> registerTransfer(TransferRequestDTO transferRequest);

}
