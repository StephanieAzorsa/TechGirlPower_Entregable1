package com.nttdata.transactionservice.service.strategy;

import com.nttdata.transactionservice.dto.TransactionResponseDTO;
import reactor.core.publisher.Mono;

// Interfaz que define el contrato para las estrategias de procesamiento de transacciones.
// Implementa el patrón Strategy para manejar diferentes tipos de transacciones de forma desacoplada.
public interface TransactionStrategy<T> {

    // Ejecuta la transacción utilizando el patrón reactivo.
    Mono<TransactionResponseDTO> execute(T request);

    // Obtiene la clase del tipo de solicitud que esta estrategia soporta.
    Class<T> getSupportedType();

    // Obtiene el nombre identificativo de la estrategia.
    String getStrategyName();
}
