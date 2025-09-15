package com.nttdata.accountservice.service.impl;

import com.nttdata.accountservice.dto.AccountResponseDTO;
import com.nttdata.accountservice.dto.TransactionRequestDTO;
import com.nttdata.accountservice.exception.AccountNotFoundException;
import com.nttdata.accountservice.exception.InsufficientBalanceException;
import com.nttdata.accountservice.mapper.AccountMapper;
import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.repository.AccountRepository;
import com.nttdata.accountservice.service.TransactionService;
import com.nttdata.accountservice.util.AccountConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;

    /**
     * Realiza un depósito en una cuenta específica.
     * - Valida la existencia de la cuenta
     * - Valida que el monto del depósito sea positivo
     * - Actualiza el balance de la cuenta
     */
    @Override
    public AccountResponseDTO deposit(String accountId,
                                      TransactionRequestDTO transactionRequestDTO) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con ID: " + accountId));

        if (transactionRequestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del depósito debe ser mayor a 0");
        }

        BigDecimal newBalance = account.getBalance().add(transactionRequestDTO.getAmount());
        account.setBalance(newBalance);

        Account updatedAccount = accountRepository.save(account);

        return AccountMapper.toDTO(updatedAccount);
    }

    /**
     * Realiza un retiro de una cuenta específica con validaciones de negocio.
     * - Valida la existencia de la cuenta
     * - Aplica reglas específicas según el tipo de cuenta:
     * • Ahorros -> No permite saldo negativo
     * • Corriente -> Permite sobregiro hasta el límite establecido
     */
    @Override
    public AccountResponseDTO withdraw(String accountId,
                                       TransactionRequestDTO transactionRequestDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Cuenta no encontrada con ID: " + accountId));

        BigDecimal newBalance = account.getBalance().subtract(transactionRequestDTO.getAmount());

        // Validaciones específicas por tipo de cuenta
        switch (account.getAccountType()) {
            case AHORROS -> {
                if (newBalance.compareTo(AccountConstants.MIN_SAVINGS_BALANCE) < 0) {
                    throw new InsufficientBalanceException(
                            "Las cuentas de ahorro no pueden tener saldo negativo");
                }
            }
            case CORRIENTE -> {
                if (newBalance.compareTo(AccountConstants.MAX_OVERDRAFT) < 0) {
                    throw new InsufficientBalanceException(
                            "Las cuentas corrientes no pueden tener un sobregiro mayor a " +
                                    AccountConstants.MAX_OVERDRAFT.abs());
                }
            }
        }

        account.setBalance(newBalance);
        Account updatedAccount = accountRepository.save(account);
        return AccountMapper.toDTO(updatedAccount);
    }

}
