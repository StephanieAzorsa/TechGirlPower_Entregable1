package com.nttdata.customerservice.service.impl;

import com.nttdata.customerservice.exception.CustomerHasActiveAccountsException;
import com.nttdata.customerservice.service.AccountValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class AccountValidationServiceImpl implements AccountValidationService {

    private final RestTemplate restTemplate;

    @Override
    public void validateCustomerHasNoAccounts(String customerId) {
        String url = "http://localhost:8082/api/v1/accounts/customer/" + customerId;

        try {
            Object[] accounts = restTemplate.getForObject(url, Object[].class);

            if (accounts != null && accounts.length > 0) {
                throw new CustomerHasActiveAccountsException("Cliente tiene cuentas activas");
            }

        } catch (HttpClientErrorException.NotFound ex) {
            // 404 → no hay cuentas → OK
        } catch (CustomerHasActiveAccountsException ex) {
            // IMPORTANTE: Relanzar la excepción de negocio
            throw ex;
        } catch (Exception ex) {
            // Solo errores técnicos
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error técnico al verificar cuentas: " + ex.getMessage());
        }
    }
}