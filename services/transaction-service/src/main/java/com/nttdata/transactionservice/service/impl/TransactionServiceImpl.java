package com.nttdata.transactionservice.service.impl;

import com.nttdata.transactionservice.dto.TransactionRequestDTO;
import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.dto.TransferRequestDTO;
import com.nttdata.transactionservice.exception.AccountNotFoundException;
import com.nttdata.transactionservice.exception.InsufficientBalanceException;
import com.nttdata.transactionservice.exception.TransactionExecutionException;
import com.nttdata.transactionservice.mapper.TransactionMapper;
import com.nttdata.transactionservice.repository.TransactionRepository;
import com.nttdata.transactionservice.service.TransactionContext;
import com.nttdata.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionContext transactionContext;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    // Lista todas las transacciones
    @Override
    public Flux<TransactionResponseDTO> listTransactions() {
        return transactionRepository.findAll().map(transactionMapper::toDTO);
    }

    // Registra el depósito
    @Override
    public Mono<TransactionResponseDTO> registerDeposit(TransactionRequestDTO request) {
        return transactionContext
                .executeStrategy(request, "DEPOSITO")
                .onErrorMap(WebClientResponseException.class,
                        this::handleAccountServiceError);
    }

    // Registra el retiro
    @Override
    public Mono<TransactionResponseDTO> registerWithdrawal(TransactionRequestDTO request) {
        return transactionContext
                .executeStrategy(request, "RETIRO")
                .onErrorMap(WebClientResponseException.class,
                        this::handleAccountServiceError);
    }

    // Registra una transacción
    @Override
    public Mono<TransactionResponseDTO> registerTransfer(TransferRequestDTO request) {
        return transactionContext
                .executeStrategy(request, "TRANSFERENCIA")
                .onErrorMap(WebClientResponseException.class,
                        this::handleAccountServiceError);
    }

    // Manejador de errores: convierte excepciones HTTP en excepciones de dominio específicas
    public Throwable handleAccountServiceError(WebClientResponseException ex) {
        return switch (ex.getStatusCode().value()) {
            case 400 -> new InsufficientBalanceException("Saldo insuficiente o límite excedido");
            case 404 -> new AccountNotFoundException("La cuenta no existe");
            default -> new TransactionExecutionException("Error temporal en el sistema. Intente nuevamente");
        };
    }
}
