package com.nttdata.customerservice.service.strategy.impl;

import com.nttdata.customerservice.dto.CustomerRequestDTO;
import com.nttdata.customerservice.service.strategy.ValidationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailValidationStrategy implements ValidationStrategy {

    @Override
    public void validate(CustomerRequestDTO customerRequestDTO, String customerId) {
        String email = customerRequestDTO.getEmail();
        log.debug("Validando formato de email: {}", email);

        // Validación básica de formato
        // (Spring Validation ya lo hace, pero podemos agregar más)
        if (!isValidEmailFormat(email)) {
            throw new IllegalArgumentException("Formato de email inválido: "
                    + email);
        }
    }

    private boolean isValidEmailFormat(String email) {
        // Se puede implementar validaciones adicionales aquí
        return email != null && email.contains("@") && email.contains(".");
    }

    @Override
    public String getStrategyName() {
        return "EMAIL_VALIDATION";
    }
}
