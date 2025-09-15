package com.nttdata.accountservice.service.impl;

import com.nttdata.accountservice.dto.AccountRequestDTO;
import com.nttdata.accountservice.dto.AccountResponseDTO;
import com.nttdata.accountservice.exception.AccountNotFoundException;
import com.nttdata.accountservice.exception.CustomerNotFoundException;
import com.nttdata.accountservice.exception.InsufficientBalanceException;
import com.nttdata.accountservice.service.factory.AccountFactory;
import com.nttdata.accountservice.mapper.AccountMapper;
import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.repository.AccountRepository;
import com.nttdata.accountservice.service.AccountService;
import com.nttdata.accountservice.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

// Implementación del servicio de gestión de cuentas bancarias.
// Proporciona operaciones CRUD y transaccionales para cuentas de ahorros y corrientes.
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final RestTemplate restTemplate;

    // Obtiene todas las cuentas existentes en el sistema
    // retorna Lista de DTO con la información de todas las cuentas
    @Override
    public List<AccountResponseDTO> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(AccountMapper::toDTO)
                .toList();
    }

    // Obtiene una cuenta específica por su ID
    @Override
    public AccountResponseDTO getAccountById(String id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con ID: " + id));

        return AccountMapper.toDTO(account);
    }

    // Obtiene todas las cuentas asociadas a un cliente específico
    @Override
    public List<AccountResponseDTO> getAccountsByCustomerId(String customerId) {
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        return accounts.stream()
                .map(AccountMapper::toDTO)
                .toList();
    }

    /**
     * Crea una nueva cuenta bancaria con validaciones de negocio
     * - Valida que el saldo inicial sea positivo (>= 0.01)
     * - Verifica la existencia del cliente mediante servicio externo
     * - Genera un número de cuenta único
     */
    @Override
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {
        if (accountRequestDTO.getInitialBalance().compareTo(new BigDecimal("0.01")) < 0)
            throw new InsufficientBalanceException("El saldo inicial debe ser mayor a 0");

        // Validación externa del cliente
        String customerServiceUrl = "http://localhost:8082/api/v1/customers/"
                + accountRequestDTO.getCustomerId();
        try {
            restTemplate.getForEntity(customerServiceUrl, Object.class);
        } catch (Exception ex) {
            throw new CustomerNotFoundException("El cliente con ID "
                    + accountRequestDTO.getCustomerId() + " no existe");
        }

        // Generación de número de cuenta
        String accountNumber;
        do {
            accountNumber = AccountNumberGenerator.generateAccountNumber();
        } while (accountRepository.existsByAccountNumber(accountNumber));

        // Aquí entra el Factory
        Account newAccount = AccountFactory.createAccount(
                accountRequestDTO.getAccountType(),
                accountNumber,
                accountRequestDTO.getCustomerId(),
                accountRequestDTO.getInitialBalance()
        );

        accountRepository.save(newAccount);

        return AccountMapper.toDTO(newAccount);
    }

    // Elimina una cuenta específica del sistema.
    @Override
    public void deleteAccount(String id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException("Cuenta no encontrada con ID: " + id);
        }
        accountRepository.deleteById(id);
    }
}