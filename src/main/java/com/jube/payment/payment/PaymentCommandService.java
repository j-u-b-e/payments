package com.jube.payment.payment;

import com.jube.payment.DBConnectionHelper;
import com.jube.payment.account.AccountNotFoundException;
import com.jube.payment.account.AccountService;
import com.jube.payment.balance.BalanceService;
import com.jube.payment.payment.api.PaymentRequestDto;
import com.jube.payment.payment.api.PaymentValidator;
import com.jube.payment.payment.exception.NotEnoughMoneyException;
import org.jooq.Configuration;
import org.jooq.example.flyway.db.h2.tables.records.PaymentsRecord;
import org.jooq.example.flyway.db.h2.tables.records.TransactionsRecord;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.jooq.example.flyway.db.h2.Tables.PAYMENTS;
import static org.jooq.example.flyway.db.h2.Tables.TRANSACTIONS;

public class PaymentCommandService {

    public static final String DEFAULT_CURRENCY = "EUR";
    private static final Logger LOG = LoggerFactory.getLogger(PaymentCommandService.class);

    private BalanceService balanceService;
    private AccountService accountService;

    public PaymentCommandService() {
        balanceService = new BalanceService();
        accountService = new AccountService();
    }

    public synchronized void processPaymentTransaction(PaymentRequestDto paymentRequestDto) {
        PaymentValidator.validateTransferRequest(paymentRequestDto);
        //validate if from and to accounts exist
        if (!accountService.isExistingAccount(paymentRequestDto.getFromAccountNumber())
                || !accountService.isExistingAccount(paymentRequestDto.getToAccountNumber())) {
            throw new AccountNotFoundException("Account not found");
        }

        LOG.info("================Start Payment Transaction================");
        try (Connection c = DBConnectionHelper.getNewConnection()) {
            DSL.using(c).transaction(configuration -> {
                processPayment(configuration, paymentRequestDto);
            });
        } catch (SQLException e) {
            LOG.error("SQLException", e);
            throw new RuntimeException(e);
        }
        LOG.info("================End Payment Transaction================");
    }

    private void processPayment(Configuration configuration, PaymentRequestDto paymentRequestDto) {
        //Stop processing if balance is less than transfer amount
        if (balanceService.getBalance(paymentRequestDto.getFromAccountNumber()).getBalance().compareTo(paymentRequestDto.getAmount()) <= 0) {
            throw new NotEnoughMoneyException("Not enough money to complete transaction");
        }

        //Add 2 transactions: to and from account
        BigDecimal fromAmount = paymentRequestDto.getAmount().negate();
        long fromAccountTransactionId = addPaymentTransaction(configuration, paymentRequestDto.getFromAccountNumber(), fromAmount).getId();
        long toAccountTransactionId = addPaymentTransaction(configuration, paymentRequestDto.getToAccountNumber(), paymentRequestDto.getAmount()).getId();

        //Add payment reference with keys to 2 transactions
        addPayment(configuration, paymentRequestDto, fromAccountTransactionId, toAccountTransactionId);
    }

    private PaymentsRecord addPayment(Configuration configuration, PaymentRequestDto paymentRequestDto, long fromId, long toId) {
        LOG.info("Adding payment");
        PaymentsRecord paymentsRecord = null;
        paymentsRecord = DSL.using(configuration)
                .insertInto(PAYMENTS)
                .values(null, fromId, toId, paymentRequestDto.getAmount(), DEFAULT_CURRENCY, paymentRequestDto.getReference(), new Timestamp(System.currentTimeMillis()))
                .returning().fetchOne();

        return paymentsRecord;
    }

    private TransactionsRecord addPaymentTransaction(Configuration configuration, String accountNumber, BigDecimal amount) {
        LOG.info("Adding payment Transaction: " + accountNumber);
        TransactionsRecord transactionsRecord = null;
        transactionsRecord = DSL.using(configuration)
                .insertInto(TRANSACTIONS)
                .values(null, accountNumber, amount, DEFAULT_CURRENCY, new Timestamp(System.currentTimeMillis()))
                .returning().fetchOne();

        return transactionsRecord;
    }
}
