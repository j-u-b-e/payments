package com.jube.payment;

import com.jube.payment.account.AccountNotFoundException;
import com.jube.payment.payment.exception.NotEnoughMoneyException;
import com.jube.payment.payment.exception.PaymentValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Response;
import spark.Spark;

public class ExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

    public static void registerExceptionHandlers() {
        Spark.exception(PaymentValidationException.class, (exception, request, response) -> {
            processClientException(exception, response);
        });

        Spark.exception(AccountNotFoundException.class, (exception, request, response) -> {
            processClientException(exception, response);
        });

        Spark.exception(NotEnoughMoneyException.class, (exception, request, response) -> {
            processClientException(exception, response);
        });

        Spark.exception(Exception.class, (exception, request, response) -> {
            LOG.error(exception.getMessage(), exception);
            exception.printStackTrace();
            response.status(500);
            response.body(exception.getMessage());
        });
    }

    private static void processClientException(Exception exception, Response response) {
        LOG.error(exception.getMessage(), exception);
        response.status(400);
        response.body(exception.getMessage());
    }
}
