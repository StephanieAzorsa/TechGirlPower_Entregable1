package com.nttdata.service;

import com.nttdata.model.AccountType;
import com.nttdata.model.BankAccount;
import com.nttdata.model.Customer;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class BankAccountService {
    private final ArrayList<BankAccount> accounts = new ArrayList<>();
    private final CustomerService customerService;
    private final Scanner scanner = new Scanner(System.in).useDelimiter("\r?\n");

    public BankAccountService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void openBankAccount() {
        Customer customer;
        while (true) {
            System.out.print("Ingrese su DNI: ");
            String dni = scanner.nextLine().trim();
            Optional<Customer> optionalCustomer = customerService.findCustomerByDni(dni);
            if (optionalCustomer.isEmpty()) {
                System.out.println("No existe, intente nuevamente.");
                continue;
            }
            customer = optionalCustomer.get();
            break;
        }

        String accountNumber = generateAccountNumber();
        AccountType accountType = chooseAccountType();
        BankAccount account = new BankAccount(customer, accountNumber, 0.0, accountType);

        accounts.add(account);
        System.out.println("Cuenta registrada, con DNI: "
                + customer.getDni()
                + " con número de cuenta: "
                + account.getAccountNumber());
    }

    private String generateAccountNumber() {
        // UUID: Identificador Único Universal
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private AccountType chooseAccountType() {
        while (true) {
            System.out.print("Seleccione el tipo de cuenta (1 = AHORROS, 2 = CORRIENTE): ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    return AccountType.AHORROS;
                case "2":
                    return AccountType.CORRIENTE;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }

}