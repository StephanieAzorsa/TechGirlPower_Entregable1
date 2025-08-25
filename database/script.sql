------------------ DDL ------------------
-- Descripción: Creación de tablas Cliente y CuentaBancaria

CREATE DATABASE IF NOT EXISTS BDBank;
USE BDBank;

-- Tabla de Clientes
CREATE TABLE IF NOT EXISTS Customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    dni         VARCHAR(20)  NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    lastname    VARCHAR(100) NOT NULL,
    email       VARCHAR(100) NOT NULL
);

-- Tabla de Cuentas Bancarias
CREATE TABLE IF NOT EXISTS BankAccounts (
    bankAccount_id INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(20) NOT NULL,
    accountNumber VARCHAR(20) NOT NULL UNIQUE,
    balance DOUBLE NOT NULL DEFAULT 0.0,
    accountType ENUM('AHORROS', 'CORRIENTE') NOT NULL,
    FOREIGN KEY (dni) REFERENCES Customers (dni) ON DELETE CASCADE
);

------------------ DML ------------------
-- Datos de prueba para testing

-- Inserta clientes
INSERT INTO Customers (dni, name, lastname, email)
VALUES ('12345678', 'Angie', 'Loa Pacora', 'angie@gmail.com'),
       ('23456789', 'Andrea', 'Molina Valverde', 'andrea@gmail.com'),
       ('34567891', 'Aracely', 'Coronel Villar', 'aracely@gmail.com'),
       ('45678912', 'Stephanie', 'Azorsa Salazar', 'stephanie@gmail.com');

-- Inserta cuentas bancarias
INSERT INTO BankAccounts (dni, accountNumber, balance, accountType)
VALUES ('12345678', '7E06E81A', 1000.00, 'AHORROS'),
       ('23456789', '9590C82F', 850.00, 'CORRIENTE'),
       ('34567891', 'FED908DD', 1200.00, 'CORRIENTE'),
       ('45678912', '6662B5B6', -300.00, 'CORRIENTE');

------------------ Queries ----------------------
-- Consultas básicas

-- Lista todos los Clientes
SELECT *
FROM Customers;

-- Lista todas las Cuentas Bancarias
SELECT *
FROM BankAccounts;

-- Depositar dinero
UPDATE BankAccounts
SET balance = balance + 100.00
WHERE accountNumber = '7E06E81A';

-- Retirar dinero
UPDATE BankAccounts
SET balance = balance - 20.00
WHERE accountNumber = '7E06E81A';

-- Consultar saldo
SELECT balance
FROM BankAccounts
WHERE accountNumber = '7E06E81A';

