package com.jube.payment;

import com.jube.payment.account.AccountNotFoundException;
import com.jube.payment.account.AccountService;
import com.jube.payment.account.api.AccountDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;

public class AccountServiceTest {
    private static final String ACCOUNT_ID = "account-one";
    private static final String NOT_EXISTING_ACCOUNT_ID = "NO_ACCOUNT";

    @Test
    public void testCreateNewAccount_returnsCreatedAccount() {
        AccountService accountService = new AccountService();
        AccountDto accountDto = accountService.createAccount();
        Assertions.assertEquals(36, accountDto.getAccountNumber().length());
    }

    @Test
    public void testAccountExists_returnsTrueForExistingAccount() {
        AccountService accountService = new AccountService();
        boolean result = accountService.isExistingAccount(ACCOUNT_ID);
        Assertions.assertTrue(result);
    }

    @Test
    public void testAccountExists_returnsFalseForNonExistingAccount() {
        AccountService accountService = new AccountService();
        AccountDto accountDto = accountService.createAccount();
        boolean result = accountService.isExistingAccount(NOT_EXISTING_ACCOUNT_ID);
        Assertions.assertFalse(result);
    }

    @Test
    public void testGetSingleAccount_returnsAccount() {
        AccountService accountService = new AccountService();
        AccountDto accountDto = accountService.getAccount(ACCOUNT_ID);
        Assertions.assertEquals(ACCOUNT_ID, accountDto.getAccountNumber());
    }

    @Test
    public void testGetSingleNotExistingAccount_exceptionThrown() {
        AccountService accountService = new AccountService();
        Executable executable = () -> accountService.getAccount(NOT_EXISTING_ACCOUNT_ID);
        Assertions.assertThrows(AccountNotFoundException.class, executable);
    }

    @Test
    public void testGetAccounts_accountsReturned() {
        AccountService accountService = new AccountService();
        List<AccountDto> accountDtos = accountService.getAccounts();

        Assertions.assertTrue(accountDtos.size() > 0);
    }
}
