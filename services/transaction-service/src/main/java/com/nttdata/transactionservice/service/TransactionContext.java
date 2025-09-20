package com.nttdata.transactionservice.service;

import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.service.strategy.TransactionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionContext {

    private final List<TransactionStrategy<?>> strategies;

    @SuppressWarnings("unchecked")
    public <T> Mono<TransactionResponseDTO> executeStrategy(T request, String expectedStrategyName) {

        Optional<TransactionStrategy<?>> matchingStrategy = strategies.stream()
                .filter(strategy -> strategy
                        .getSupportedType()
                        .equals(request.getClass()))
                .filter(strategy -> strategy
                        .getStrategyName()
                        .equals(expectedStrategyName))
                .findFirst();

        return matchingStrategy
                .map(strategy -> {
                    TransactionStrategy<T> typedStrategy = (TransactionStrategy<T>) strategy;
                    return typedStrategy.execute(request);
                })
                .orElseGet(() -> Mono.error(
                        new IllegalArgumentException("No se encontró estrategia para el tipo: "
                                + request.getClass().getSimpleName())
                ));
    }
}
