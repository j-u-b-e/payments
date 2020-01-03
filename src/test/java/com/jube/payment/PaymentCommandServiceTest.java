package com.jube.payment;

import com.jube.payment.account.AccountNotFoundException;
import com.jube.payment.balance.BalanceService;
import com.jube.payment.payment.PaymentCommandService;
import com.jube.payment.payment.api.PaymentRequestDto;
import com.jube.payment.payment.exception.NotEnoughMoneyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class PaymentCommandServiceTest {

    @Test
    public void testMakePayment_notEnoughBalance_exceptionThrown() {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setFromAccountNumber("acc-with-no-money-payment-test");
        paymentRequestDto.setToAccountNumber("acc-with-money-payment-test");
        paymentRequestDto.setAmount(BigDecimal.valueOf(123));
        paymentRequestDto.setReference("TEST");

        Executable executable = () -> new PaymentCommandService().processPaymentTransaction(paymentRequestDto);

        Assertions.assertThrows(NotEnoughMoneyException.class, executable);
    }

    @Test
    public void testMakePayment_fromAccountNotExistent_exceptionThrown() {
        Logger LOG = LoggerFactory.getLogger(PaymentCommandService.class);
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setFromAccountNumber("NO_ACCOUNT");
        paymentRequestDto.setToAccountNumber("acc-with-money-payment-test");
        paymentRequestDto.setAmount(BigDecimal.valueOf(123));
        paymentRequestDto.setReference("TEST");

        Executable executable = () -> new PaymentCommandService().processPaymentTransaction(paymentRequestDto);

        Assertions.assertThrows(AccountNotFoundException.class, executable);
    }

    @Test
    public void testMakePayment_toAccountNotExistent_exceptionThrown() {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setFromAccountNumber("acc-with-money-payment-test");
        paymentRequestDto.setToAccountNumber("NO-ACCOUNT");
        paymentRequestDto.setAmount(BigDecimal.valueOf(123));
        paymentRequestDto.setReference("TEST");

        Executable executable = () -> new PaymentCommandService().processPaymentTransaction(paymentRequestDto);

        Assertions.assertThrows(AccountNotFoundException.class, executable);
    }

    @Test
    public void testMakePayment_bothAccountsNotExistent_exceptionThrown() {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setFromAccountNumber("NO_ACCOUNT");
        paymentRequestDto.setToAccountNumber("NO-ACCOUNT");
        paymentRequestDto.setAmount(BigDecimal.valueOf(123));
        paymentRequestDto.setReference("TEST");

        Executable executable = () -> new PaymentCommandService().processPaymentTransaction(paymentRequestDto);

        Assertions.assertThrows(AccountNotFoundException.class, executable);
    }

    @Test
    public void testMakePayment_paymentIsDone_balancesAreCorrect() {
        String fromAccount = "acc-with-money-payment-test";
        String toAccount = "acc-with-no-money-payment-test";

        BalanceService balanceService = new BalanceService();
        BigDecimal fromAmountBeforeTest = balanceService.getBalance(fromAccount).getBalance();
        BigDecimal toAmountBeforeTest = balanceService.getBalance(toAccount).getBalance();
        BigDecimal transferAmount = BigDecimal.valueOf(13.45);

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setFromAccountNumber(fromAccount);
        paymentRequestDto.setToAccountNumber(toAccount);
        paymentRequestDto.setAmount(transferAmount);
        paymentRequestDto.setReference("TEST");

        new PaymentCommandService().processPaymentTransaction(paymentRequestDto);

        Assertions.assertEquals(fromAmountBeforeTest.subtract(transferAmount), balanceService.getBalance(fromAccount).getBalance());
        Assertions.assertEquals(toAmountBeforeTest.add(transferAmount), balanceService.getBalance(toAccount).getBalance());
    }

    @Test
    public void testMakePaymentFromMultipleThreads_paymentsAreDone_balanceIsPositive() throws InterruptedException {
        String fromAccount = "acc-with-money-thread-test";
        String toAccount = "acc-with-no-money-thread-test";

        BigDecimal transferAmount = BigDecimal.valueOf(10);

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setFromAccountNumber(fromAccount);
        paymentRequestDto.setToAccountNumber(toAccount);
        paymentRequestDto.setAmount(transferAmount);
        paymentRequestDto.setReference("TEST");

        PaymentCommandService paymentCommandService = new PaymentCommandService();

        //Start 3 Threads where each thread will try to make 5 payments
        Thread t1 = new Thread(new PaymentRunnerForTest(paymentCommandService, paymentRequestDto));
        Thread t2 = new Thread(new PaymentRunnerForTest(paymentCommandService, paymentRequestDto));
        Thread t3 = new Thread(new PaymentRunnerForTest(paymentCommandService, paymentRequestDto));
        t1.setName("First");
        t2.setName("Second");
        t3.setName("Third");

        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();

        //Assert that remaining balance is 3.00. Balance before test is 53.
        Assertions.assertEquals(BigDecimal.valueOf(300,2), new BalanceService().getBalance(fromAccount).getBalance());
    }

    class PaymentRunnerForTest implements Runnable{
        private PaymentCommandService paymentCommandService;
        private PaymentRequestDto paymentRequestDto;

        public PaymentRunnerForTest(PaymentCommandService paymentCommandService, PaymentRequestDto paymentRequestDto) {
            this.paymentCommandService = paymentCommandService;
            this.paymentRequestDto = paymentRequestDto;
        }

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                paymentCommandService.processPaymentTransaction(paymentRequestDto);
            }
        }
    }

}
