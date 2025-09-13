package com.nttdata.accountservice.service;

import com.nttdata.accountservice.dto.AccountRequestDTO;
import com.nttdata.accountservice.dto.AccountResponseDTO;
import com.nttdata.accountservice.dto.TransactionRequestDTO;

import java.util.List;

public interface AccountService {
    List<AccountResponseDTO> getAllAccounts();

    AccountResponseDTO getAccountById(String id);

    List<AccountResponseDTO> getAccountsByCustomerId(String customerId);

    AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO);

    AccountResponseDTO deposit(String accountId, TransactionRequestDTO transactionRequestDTO);

    AccountResponseDTO withdraw(String accountId, TransactionRequestDTO transactionRequestDTO);

    void deleteAccount(String id);
}
