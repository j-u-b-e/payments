package com.jube.payment.balance;

import com.jube.payment.balance.api.BalanceDto;
import org.jooq.example.flyway.db.h2.tables.records.BalancesRecord;

public class BalanceMapper {

    public static BalanceDto entity2dto(BalancesRecord balanceRecord){
        return new BalanceDto(balanceRecord.getAccountNumber(), balanceRecord.getBalance());
    }
}
