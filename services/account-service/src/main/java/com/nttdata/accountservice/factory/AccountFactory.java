package com.nttdata.accountservice.factory;

import com.nttdata.accountservice.model.Account;
import com.nttdata.accountservice.model.AccountType;

import java.math.BigDecimal;

public class AccountFactory {

    public static Account createAccount(AccountType type,
                                        String accountNumber,
                                        String customerId,
                                        BigDecimal initialBalance) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setCustomerId(customerId);

        switch (type) {
            case AHORROS -> {
                account.setBalance(initialBalance.compareTo(BigDecimal.ZERO) > 0
                        ? initialBalance
                        : BigDecimal.valueOf(50));
                account.setAccountType(AccountType.AHORROS);
            }
            case CORRIENTE -> {

                account.setBalance(initialBalance);
                account.setAccountType(AccountType.CORRIENTE);
            }
            default -> throw new IllegalArgumentException("Tipo de cuenta no soportado");
        }

        return account;
    }
}
