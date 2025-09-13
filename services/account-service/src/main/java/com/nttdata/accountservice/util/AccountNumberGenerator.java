package com.nttdata.accountservice.util;

import java.security.SecureRandom;

public class AccountNumberGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generateAccountNumber() {
        StringBuilder accountNumber = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            accountNumber.append(random.nextInt(10));
        }

        return accountNumber.toString();
    }
}
