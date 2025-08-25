package com.nttdata.service;

import com.nttdata.model.AccountType;
import com.nttdata.model.BankAccount;
import com.nttdata.model.Customer;

import java.util.*;
import java.util.stream.Collectors;

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

    // Para obtener todas la cuentas bancarias
    public List<BankAccount> getAllAccounts() {
        return new ArrayList<>(accounts);
    }

    public double deposit(BankAccount account, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("El monto a depositar debe ser positivo.");

        account.setBalance(account.getBalance() + amount);
        return account.getBalance();
    }

    public double withdraw(BankAccount account, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("El monto a retirar debe ser positivo.");

        double newBalance = account.getBalance() - amount;

        if (account.getAccountType() == AccountType.AHORROS && newBalance < 0)
            throw new IllegalArgumentException("Las cuentas de ahorro no permiten saldo negativo.");

        if (account.getAccountType() == AccountType.CORRIENTE && newBalance < -500.00)
            throw new IllegalArgumentException("Las cuentas corrientes no pueden exceder el sobregiro de -500.00.");

        account.setBalance(newBalance);
        return account.getBalance();
    }

    public void checkBalances(String dni) {
        List<BankAccount> clientAccounts = findAccountsByDni(dni);

        for (BankAccount account : clientAccounts) {
            System.out.printf("Cuenta: %-14s %s - Saldo: %.2f%n",
                    account.getAccountNumber(),
                    account.getAccountType(),
                    account.getBalance());
        }
    }

    // Busca todas las cuentas bancarias por el DNI del cliente.
    public List<BankAccount> findAccountsByDni(String dni) {
        List<BankAccount> clientAccounts = accounts.stream()
                .filter(a -> a.getCustomer() != null && dni.equals(a.getCustomer().getDni()))
                .collect(Collectors.toList());

        if (clientAccounts.isEmpty())
            throw new NoSuchElementException(
                    "No se encontró ninguna cuenta ahorro o corriente para el DNI: " + dni);

        return clientAccounts;
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

    public BankAccount selectAccount(List<BankAccount> clientAccounts) {
        if (clientAccounts.isEmpty())
            throw new NoSuchElementException("No se encontró ninguna cuenta para ese DNI.");

        if (clientAccounts.size() == 1)
            return clientAccounts.get(0); // Si solo hay una, se devuelve directamente

        System.out.println("Tus cuentas asociadas: ");
        for (int i = 0; i < clientAccounts.size(); i++) {
            BankAccount acc = clientAccounts.get(i);
            System.out.printf("%d. %-14s  %s - Saldo: %.2f%n",
                    i + 1,
                    acc.getAccountNumber(),
                    acc.getAccountType(),
                    acc.getBalance());
        }

        int opcion = 0;
        while (true) {  // Repite hasta que ingrese una opción válida
            System.out.print("Elija una opción (1-" + clientAccounts.size() + "): ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
                if (opcion >= 1 && opcion <= clientAccounts.size())
                    break;  // Salir del while cuando la opción sea válida
                else
                    System.out.println("Opción fuera de rango.");
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número.");
            }
        }
        return clientAccounts.get(opcion - 1);
    }

}