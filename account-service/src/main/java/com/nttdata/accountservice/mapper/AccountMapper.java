package com.nttdata.accountservice.mapper;

import com.nttdata.accountservice.dto.AccountRequestDTO;
import com.nttdata.accountservice.dto.AccountResponseDTO;
import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.util.AccountNumberGenerator;

public class AccountMapper {

    public static AccountResponseDTO toDTO(Account account) {
        AccountResponseDTO accountDTO = new AccountResponseDTO();

        accountDTO.setId(account.getId());
        accountDTO.setAccountNumber(account.getAccountNumber());
        accountDTO.setBalance(account.getBalance());
        accountDTO.setAccountType(account.getAccountType());
        accountDTO.setCustomerId(account.getCustomerId());

        return accountDTO;
    }

    public static Account toModel(AccountRequestDTO accountRequestDTO) {
        Account account = new Account();

        account.setAccountType(accountRequestDTO.getAccountType());
        account.setCustomerId(accountRequestDTO.getCustomerId());
        account.setBalance(accountRequestDTO.getInitialBalance());
        account.setAccountNumber(AccountNumberGenerator.generateAccountNumber());

        return account;
    }
}
