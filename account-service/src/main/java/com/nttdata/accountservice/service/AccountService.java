package com.nttdata.accountservice.service;

import com.nttdata.accountservice.dto.AccountRequestDTO;
import com.nttdata.accountservice.dto.AccountResponseDTO;
import com.nttdata.accountservice.exception.AccountNotFoundException;
import com.nttdata.accountservice.mapper.AccountMapper;
import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

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


}
