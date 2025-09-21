package com.nttdata.transactionservice.service;

import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import com.nttdata.transactionservice.service.strategy.TransactionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * Contexto que gestiona y ejecuta las estrategias de transacción.
 * Implementa el patrón Context del patrón Strategy, actuando como orquestador.

 * Responsabilidades:
 * - Descubrir automáticamente todas las estrategias disponibles
 * - Seleccionar la estrategia apropiada basada en el tipo de request y nombre esperado
 * - Ejecutar la estrategia seleccionada
 */
@Component
@RequiredArgsConstructor
public class TransactionContext {

    // Lista de todas las estrategias inyectadas por Spring
    private final List<TransactionStrategy<?>> strategies;

    // Ejecuta la estrategia apropiada para el request y tipo de transacción.
    @SuppressWarnings("unchecked")
    public <T> Mono<TransactionResponseDTO> executeStrategy(T request, String expectedStrategyName) {

        // Busca la estrategia que coincida con el tipo de request y nombre esperado
        Optional<TransactionStrategy<?>> matchingStrategy = strategies.stream()
                .filter(strategy -> strategy
                        .getSupportedType()
                        .equals(request.getClass()))
                .filter(strategy -> strategy
                        .getStrategyName()
                        .equals(expectedStrategyName))
                .findFirst();

        // Ejecuta la estrategia o retorna error si no se encuentra
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
