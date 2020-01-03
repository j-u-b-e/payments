package com.jube.payment.payment;

import com.jube.payment.DBConnectionHelper;
import com.jube.payment.payment.api.TransactionDto;
import org.jooq.Result;
import org.jooq.example.flyway.db.h2.tables.records.PaymentsRecord;
import org.jooq.example.flyway.db.h2.tables.records.TransactionsRecord;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.example.flyway.db.h2.Tables.PAYMENTS;
import static org.jooq.example.flyway.db.h2.Tables.TRANSACTIONS;

public class PaymentQueryService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentQueryService.class);

    public List<TransactionDto> getPaymentTransactions(String accountNumber) {
        try (Connection c = DBConnectionHelper.getNewConnection()) {
            return DSL.using(c).connectionResult(connection -> getTransactionsWithConnection(connection, accountNumber));
        } catch (SQLException e) {
            LOG.error("SQLException", e);
            throw new RuntimeException(e);
        }
    }

    private List<TransactionDto> getTransactionsWithConnection(Connection connection, String accountNumber) {
        Result<TransactionsRecord> transactionsRecordResult = null;
        if (StringUtils.isEmpty(accountNumber)) {
            transactionsRecordResult = DSL.using(connection).selectFrom(TRANSACTIONS).fetch();
        } else {
            transactionsRecordResult = DSL.using(connection)
                    .selectFrom(TRANSACTIONS).where(TRANSACTIONS.ACCOUNT_NUMBER.eq(accountNumber)).fetch();
        }
        return processTransactionRecords(connection, transactionsRecordResult);
    }

    private List<TransactionDto> processTransactionRecords(Connection connection, Result<TransactionsRecord> transactionsRecordResult) {
        List<TransactionDto> transactionList = new ArrayList();
        transactionsRecordResult.forEach(transactionsRecord -> {
            //fetch payment transaction
            PaymentsRecord paymentsRecord = fetchPaymentsRecord(transactionsRecord.getId());
            //get other transaction details, create dto and add to list
            transactionList.add(createTransactionDetails(connection, paymentsRecord, transactionsRecord));
        });
        return transactionList;
    }

    private TransactionDto createTransactionDetails(Connection connection, PaymentsRecord paymentsRecord, TransactionsRecord transactionsRecord) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setCurrency(transactionsRecord.getCurrency());
        transactionDto.setAmount(transactionsRecord.getAmount());
        transactionDto.setOccurredAt(transactionsRecord.getOccurredAt());

        //Covering case then there was no transfer made, but there is a single payment transaction which was created in data setup sql
        if (paymentsRecord == null) {
            transactionDto.setToAccountNumber(transactionsRecord.getAccountNumber());
            transactionDto.setFromAccountNumber("MANUAL DEPOSIT");
            return transactionDto;
        }

        if (transactionsRecord.getId() == paymentsRecord.getFromTransactionId()) {
            transactionDto.setFromAccountNumber(transactionsRecord.getAccountNumber());
            transactionDto.setToAccountNumber(fetchPaymentTransactionRecord(connection, paymentsRecord.getToTransactionId()).getAccountNumber());
        } else {
            transactionDto.setToAccountNumber(transactionsRecord.getAccountNumber());
            transactionDto.setFromAccountNumber(fetchPaymentTransactionRecord(connection, paymentsRecord.getFromTransactionId()).getAccountNumber());
        }
        return transactionDto;
    }

    private TransactionsRecord fetchPaymentTransactionRecord(Connection connection, Long id) {
        return DSL.using(connection).selectFrom(TRANSACTIONS).where(TRANSACTIONS.ID.eq(id)).fetchOne();
    }

    private PaymentsRecord fetchPaymentsRecord(Long id) {
        try (Connection c = DBConnectionHelper.getNewConnection()) {
            return DSL.using(c)
                    .selectFrom(PAYMENTS)
                    .where(PAYMENTS.FROM_TRANSACTION_ID.eq(id)
                            .or(PAYMENTS.TO_TRANSACTION_ID.eq(id)))
                    .fetchOne();
        } catch (SQLException e) {
            LOG.error("SQLException", e);
            throw new RuntimeException(e);
        }
    }
}
