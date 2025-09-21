package com.nttdata.transactionservice.service.strategy;

import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import reactor.core.publisher.Mono;

public interface TransactionStrategy<T> {

    Mono<TransactionResponseDTO> execute(T request);

    Class<T> getSupportedType();

    String getStrategyName();
}
