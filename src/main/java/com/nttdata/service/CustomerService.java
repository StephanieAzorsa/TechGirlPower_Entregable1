package com.nttdata.service;

import com.nttdata.model.Customer;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerService {
    private final ArrayList<Customer> customers = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in).useDelimiter("\r?\n");

    /**
     * Método para registrar un cliente
     */
    public void createCustomer() {
        Customer customer = new Customer();

        while (true) {
            System.out.print("Ingrese el DNI del cliente: ");
            String dni = scanner.nextLine().trim();

            if (dni.isEmpty()) {
                System.out.println("El DNI es obligatorio. Intente nuevamente.");
                continue;
            }
            if (!dni.matches("\\d{8}")) {
                System.out.println("El DNI debe contener exactamente 8 dígitos numéricos. Intente nuevamente.");
                continue;
            }
            if (isDniExists(dni)) {
                System.out.println("El DNI ya está registrado.");
                continue;
            }
            customer.setDni(dni);
            break;
        }

        while (true) {
            System.out.print("Ingrese el nombre del cliente: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("El nombre es obligatorio. Intente nuevamente.");
                continue;
            }
            customer.setName(name);
            break;
        }

        // Ingreso y validación de los apellidos
        while (true) {
            System.out.print("Ingrese los apellidos del cliente: ");
            String lastname = scanner.nextLine().trim();
            if (lastname.isEmpty()) {
                System.out.println("El apellido es obligatorio. Intente nuevamente.");
                continue;
            }
            customer.setLastName(lastname);
            break;
        }

        while (true) {
            System.out.print("Ingrese el email del cliente: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("El email es obligatorio. Intente nuevamente.");
                continue;
            }
            if (!isValidEmail(email)) {
                System.out.println("El formato de email es inválido. Intente nuevamente.");
                continue;
            }
            customer.setEmail(email);
            break;
        }

        customers.add(customer);
        System.out.println("Cliente registrado con éxito.");
    }

    public ArrayList<Customer> getAllCustomers() {
        return customers;
    }

    /**
     * Busca un cliente por su DNI.
     *
     * @param dni el DNI del cliente a buscar.
     * @return un {@link Optional} que contiene el cliente si existe, o vacío si no se encuentra.
     */
    public Optional<Customer> findCustomerByDni(String dni) {
        return customers.stream()
                .filter(c -> c.getDni().equals(dni))
                .findFirst();
    }

    /**
     * Verifica si un DNI ya se encuentra registrado.
     *
     * @param dni el DNI a verificar.
     * @return {@code true} si ya está registrado, {@code false} en caso contrario.
     */
    private boolean isDniExists(String dni) {
        return customers.stream()
                .anyMatch(c -> c.getDni().equals(dni));
    }

    /**
     * Valida el formato de un email utilizando una expresión regular.
     *
     * @param email el email a validar.
     * @return {@code true} si el formato es válido, {@code false} en caso contrario.
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}