package com.jube.payment.balance;

import com.jube.payment.DBConnectionHelper;
import com.jube.payment.balance.api.BalanceDto;
import org.jooq.Result;
import org.jooq.example.flyway.db.h2.tables.records.BalancesRecord;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.jooq.example.flyway.db.h2.Tables.BALANCES;

public class BalanceService {

    private static final Logger LOG = LoggerFactory.getLogger(BalanceService.class);

    public List<BalanceDto> getBalances() {
        Result<BalancesRecord> balancesRecords = null;
        try (Connection c = DBConnectionHelper.getNewConnection()) {
            balancesRecords = DSL.using(c)
                    .selectFrom(BALANCES).fetch();
        } catch (SQLException e) {
            LOG.error("SQLException", e);
            throw new RuntimeException(e);
        }
        List<BalanceDto> balancesList = new ArrayList<>();
        balancesRecords.forEach(balancesRecord -> balancesList.add(BalanceMapper.entity2dto(balancesRecord)));
        return balancesList;
    }

    public BalanceDto getBalance(String accountNumber) {
        Optional<BalancesRecord> balancesRecord = null;
        try (Connection c = DBConnectionHelper.getNewConnection()) {
            balancesRecord = DSL.using(c)
                    .selectFrom(BALANCES).where(BALANCES.ACCOUNT_NUMBER.eq(accountNumber)).fetchOptional();
        } catch (SQLException e) {
            LOG.error("SQLException", e);
            throw new RuntimeException(e);
        }
        if (balancesRecord.isPresent()) {
            return BalanceMapper.entity2dto(balancesRecord.get());
        }
        return new BalanceDto(accountNumber, BigDecimal.ZERO);
    }
}
