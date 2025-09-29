package com.nttdata.customerservice.service;

import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.service.strategy.ValidationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationContext {

    private final List<ValidationStrategy> validationStrategies;

    public void executeValidations(
            CustomerRequestDTO customerRequestDTO,
            String customerId) {
        log.info("Ejecutando validaciones para cliente: {}", customerId);

        for (ValidationStrategy strategy : validationStrategies) {
            try {
                log.debug("Ejecutando estrategia: {}", strategy.getStrategyName());
                strategy.validate(customerRequestDTO, customerId);
            } catch (RuntimeException ex) {
                log.warn("Validación fallida con estrategia {}: {}",
                        strategy.getStrategyName(), ex.getMessage());
                throw ex;
            }
        }

        log.info("Todas las validaciones pasaron exitosamente");
    }
}