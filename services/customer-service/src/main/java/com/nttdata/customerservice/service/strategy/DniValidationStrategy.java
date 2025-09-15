package com.nttdata.customerservice.service.strategy;

import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.exception.DniAlreadyExistsException;
import com.nttdata.customerservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DniValidationStrategy implements ValidationStrategy {

    private final CustomerRepository customerRepository;

    @Override
    public void validate(CustomerRequestDTO customerRequestDTO, String customerId) {
        String dni = customerRequestDTO.getDni();
        log.debug("Validando DNI: {} para cliente: {}", dni, customerId);

        boolean dniExists;

        if (customerId == null) {
            // Creación: verificar si DNI existe en cualquier cliente
            dniExists = customerRepository.existsByDni(dni);
        } else {
            // Actualización: verificar si DNI existe en otro cliente
            dniExists = customerRepository.existsByDniAndIdNot(dni, customerId);
        }

        if (dniExists) {
            throw new DniAlreadyExistsException(
                    "El DNI " + dni + " ya está registrado en otro cliente");
        }
    }

    @Override
    public String getStrategyName() {
        return "DNI_VALIDATION";
    }
}
