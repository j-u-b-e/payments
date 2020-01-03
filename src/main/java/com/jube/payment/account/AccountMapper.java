package com.jube.payment.account;

import com.jube.payment.account.api.AccountDto;
import org.jooq.example.flyway.db.h2.tables.records.AccountsRecord;

public class AccountMapper {

    public static AccountDto entity2dto(AccountsRecord accountsRecord) {
        return new AccountDto(accountsRecord.getId(), accountsRecord.getAccountNumber());
    }
}
