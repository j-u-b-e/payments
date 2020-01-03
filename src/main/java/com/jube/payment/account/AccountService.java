package com.jube.payment.account;

import com.jube.payment.DBConnectionHelper;
import com.jube.payment.account.api.AccountDto;
import org.jooq.Result;
import org.jooq.example.flyway.db.h2.tables.records.AccountsRecord;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.jooq.example.flyway.db.h2.Tables.ACCOUNTS;

public class AccountService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

    public AccountDto createAccount() {
        String uuid = UUID.randomUUID().toString();
        AccountsRecord accountsRecord = null;

        LOG.info("Creating new account. ID: {}", uuid);
        try (Connection c = DBConnectionHelper.getNewConnection()) {
            accountsRecord = DSL.using(c)
                    .insertInto(ACCOUNTS)
                    .values(null, uuid)
                    .returning().fetchOne();
        } catch (SQLException e) {
            LOG.error("SQLException", e);
            throw new RuntimeException(e);
        }
        return AccountMapper.entity2dto(accountsRecord);
    }

    public List<AccountDto> getAccounts() {
        Result<AccountsRecord> accountRecords = null;
        try (Connection c = DBConnectionHelper.getNewConnection()) {
            accountRecords = DSL.using(c)
                    .selectFrom(ACCOUNTS).fetch();
        } catch (SQLException e) {
            LOG.error("SQLException", e);
        }
        List<AccountDto> accountsList = new ArrayList<>();
        accountRecords.forEach(accountsRecord -> accountsList.add(AccountMapper.entity2dto(accountsRecord)));
        return accountsList;
    }

    public AccountDto getAccount(String accountNumber) {
        Optional<AccountsRecord> accountRecord = null;
        try (Connection c = DBConnectionHelper.getNewConnection()) {
            accountRecord = DSL.using(c)
                    .selectFrom(ACCOUNTS).where(ACCOUNTS.ACCOUNT_NUMBER.eq(accountNumber)).fetchOptional();
        } catch (SQLException e) {
            LOG.error("SQLException", e);
            throw new RuntimeException(e);
        }
        if (accountRecord.isPresent()) {
            return AccountMapper.entity2dto(accountRecord.get());
        } else {
            throw new AccountNotFoundException("Account not found");
        }
    }

    public boolean isExistingAccount(String accountNumber) {
        try {
            getAccount(accountNumber);
        } catch (AccountNotFoundException ex) {
            return false;
        }
        return true;
    }
}
