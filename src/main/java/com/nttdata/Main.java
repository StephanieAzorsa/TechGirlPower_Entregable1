package com.nttdata;

import com.nttdata.model.BankAccount;
import com.nttdata.model.Customer;
import com.nttdata.service.BankAccountService;
import com.nttdata.service.CustomerService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CustomerService customerService = new CustomerService();
        BankAccountService bankAccountService = new BankAccountService(customerService);
        Scanner scanner = new Scanner(System.in).useDelimiter("\r?\n");

        int option;
        do {
            System.out.println("\n===== MENÚ PRINCIPAL =====");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Buscar cliente por DNI");
            System.out.println("3. Abrir cuenta bancaria");
            System.out.println("4. Depositar dinero");
            System.out.println("5. Retirar dinero");
            System.out.println("6. Consultar saldo");
            System.out.println("7. Listar Clientes");
            System.out.println("8. Listar Cuentas bancarias");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            while (!scanner.hasNextInt()) {
                System.out.println("Por favor, ingrese un número válido.");
                scanner.next();
            }
            option = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer
            System.out.println("========================");

            String dni;

            switch (option) {
                case 1:
                    customerService.createCustomer();
                    break;
                case 2:
                    System.out.print("Ingrese el DNI a buscar: ");
                    dni = scanner.nextLine().trim();
                    Optional<Customer> customerOpt = customerService.findCustomerByDni(dni);
                    if (customerOpt.isPresent()) {
                        System.out.println("Cliente encontrado: " + customerOpt.get());
                    } else {
                        System.out.println("No se encontró ningún cliente con el DNI proporcionado.");
                    }
                    break;
                case 3:
                    bankAccountService.openBankAccount();
                    break;
                case 4:
                    try {
                        System.out.print("Ingrese el DNI del cliente: ");
                        dni = scanner.nextLine().trim();

                        List<BankAccount> clientAccounts = bankAccountService.findAccountsByDni(dni);
                        BankAccount selectedAccount = bankAccountService.selectAccount(clientAccounts);

                        System.out.print("Ingrese el monto a depositar de la cuenta " + selectedAccount.getAccountNumber() + ": ");
                        double amount = scanner.nextDouble();
                        scanner.nextLine();

                        double newBalance = bankAccountService.deposit(selectedAccount, amount);
                        System.out.println("Deposito realizado. Saldo actual: " + newBalance);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 5:
                    try {
                        System.out.print("Ingrese el DNI del cliente: ");
                        dni = scanner.nextLine().trim();

                        // Primero buscar las cuentas para mostrar la selección
                        List<BankAccount> clientAccounts = bankAccountService.findAccountsByDni(dni);
                        BankAccount selectedAccount = bankAccountService.selectAccount(clientAccounts);

                        // Luego pide el monto
                        System.out.print("Ingrese el monto a retirar de la cuenta " + selectedAccount.getAccountNumber() + ": ");
                        double amount = scanner.nextDouble();
                        scanner.nextLine();

                        double newBalance = bankAccountService.withdraw(selectedAccount, amount);
                        System.out.println("Retiro realizado. Saldo actual: " + newBalance);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 0:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        } while (option != 0);

        scanner.close();
    }
}