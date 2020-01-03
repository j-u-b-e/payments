package com.jube.payment.balance.api;

import java.math.BigDecimal;

public class BalanceDto {

    public BalanceDto(String accountNumber, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    private String accountNumber;
    private BigDecimal balance;

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
