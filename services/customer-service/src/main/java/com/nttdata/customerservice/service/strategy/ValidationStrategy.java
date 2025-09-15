package com.nttdata.customerservice.service.strategy;

import com.nttdata.customerservice.dto.CustomerRequestDTO;

public interface ValidationStrategy {
    void validate(CustomerRequestDTO customerRequestDTO, String customerId);
    String getStrategyName();
}