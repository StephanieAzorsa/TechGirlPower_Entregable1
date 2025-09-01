package com.nttdata.accountservice.service;

import com.nttdata.accountservice.dto.AccountRequestDTO;
import com.nttdata.accountservice.dto.AccountResponseDTO;
import com.nttdata.accountservice.dto.TransactionRequestDTO;
import com.nttdata.accountservice.exception.AccountNotFoundException;
import com.nttdata.accountservice.exception.InsufficientBalanceException;
import com.nttdata.accountservice.mapper.AccountMapper;
import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final RestTemplate restTemplate;

    public AccountService(AccountRepository accountRepository, RestTemplate restTemplate) {
        this.accountRepository = accountRepository;
        this.restTemplate = restTemplate;
    }

    public List<AccountResponseDTO> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(AccountMapper::toDTO)
                .toList();
    }

    public AccountResponseDTO getAccountById(String id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con ID: " + id));

        return AccountMapper.toDTO(account);
    }

    public List<AccountResponseDTO> getAccountsByCustomerId(String customerId) {
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        return accounts.stream()
                .map(AccountMapper::toDTO)
                .toList();
    }

    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {
        String customerServiceUrl = "http://localhost:4000/api/v1/customers/" + accountRequestDTO.getCustomerId();

        try {
            restTemplate.getForEntity(customerServiceUrl, Object.class);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El cliente con ID " + accountRequestDTO.getCustomerId() + " no existe");
        }

        Account newAccount = accountRepository.save(AccountMapper.toModel(accountRequestDTO));
        return AccountMapper.toDTO(newAccount);
    }

    public AccountResponseDTO deposit(String accountId,
                                      TransactionRequestDTO transactionRequestDTO) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con ID: " + accountId));

        BigDecimal newBalance = account.getBalance().add(transactionRequestDTO.getAmount());
        account.setBalance(newBalance);

        Account updatedAccount = accountRepository.save(account);

        return AccountMapper.toDTO(updatedAccount);
    }

    public AccountResponseDTO withdraw(String accountId,
                                       TransactionRequestDTO transactionRequestDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con ID: " + accountId));

        if (account.getBalance().compareTo(transactionRequestDTO.getAmount()) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente para realizar el retiro");
        }

        BigDecimal newBalance = account.getBalance().subtract(transactionRequestDTO.getAmount());
        account.setBalance(newBalance);

        Account updatedAccount = accountRepository.save(account);
        return AccountMapper.toDTO(updatedAccount);
    }

    public void deleteAccount(String id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException("Cuenta no encontrada con ID: " + id);
        }
        accountRepository.deleteById(id);
    }
}