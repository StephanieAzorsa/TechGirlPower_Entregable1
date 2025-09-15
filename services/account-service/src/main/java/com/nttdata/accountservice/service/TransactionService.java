package com.nttdata.accountservice.service;

import com.nttdata.accountservice.dto.AccountResponseDTO;
import com.nttdata.accountservice.dto.TransactionRequestDTO;

public interface TransactionService {

    AccountResponseDTO deposit(String accountId,
                               TransactionRequestDTO transactionRequestDTO);

    AccountResponseDTO withdraw(String accountId,
                                TransactionRequestDTO transactionRequestDTO);

}
