package com.jube.payment.account.api;

public class AccountDto {

    public AccountDto(long id, String accountNumber) {
        this.id = id;
        this.accountNumber = accountNumber;
    }

    private long id;
    private String accountNumber;

    public long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
