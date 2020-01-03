package com.jube.payment;

import com.jube.payment.account.api.AccountRoutes;
import com.jube.payment.balance.api.BalanceRoutes;
import com.jube.payment.payment.api.PaymentRoutes;

public class Application {

    public static void main(String[] args) {
        AccountRoutes.initAccountRoutes();
        PaymentRoutes.initPaymentRoutes();
        BalanceRoutes.initBalanceRoutes();

        ExceptionHandler.registerExceptionHandlers();
    }
}
