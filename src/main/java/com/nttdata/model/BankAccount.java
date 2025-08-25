package com.nttdata.model;

public class BankAccount {
    private Customer customer;
    private String accountNumber;
    private Double balance;
    private AccountType accountType;

    public BankAccount() {
    }

    public BankAccount(Customer customer, String accountNumber, Double balance, AccountType accountType) {
        this.customer = customer;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }


    @Override
    public String toString() {
        return "Cuenta Bancaria: " +
                "  " + customer +
                ", Tipo de cuenta: " + accountType +
                ", Numero de cuenta: " + accountNumber +
                ", Saldo: " + balance;
    }
}