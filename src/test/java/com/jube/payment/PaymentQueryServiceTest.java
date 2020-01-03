package com.jube.payment;

import com.jube.payment.payment.PaymentQueryService;
import com.jube.payment.payment.api.TransactionDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PaymentQueryServiceTest {

    @Test
    public void testGetPayment_withIdProvided_correctAccountTransactionsReturned() {
        List<TransactionDto> transactionDtos = new PaymentQueryService().getPaymentTransactions("acc-with-money");
        Assertions.assertEquals("acc-with-money", transactionDtos.get(0).getToAccountNumber());
    }

    @Test
    public void testGetPayments_paymentsReturned() {
        List<TransactionDto> transactionDtos = new PaymentQueryService().getPaymentTransactions("");
        Assertions.assertTrue(transactionDtos.size() > 0);
    }
}
