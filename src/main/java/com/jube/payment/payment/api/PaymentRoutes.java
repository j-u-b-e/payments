package com.jube.payment.payment.api;

import com.google.gson.Gson;
import com.jube.payment.payment.PaymentCommandService;
import com.jube.payment.payment.PaymentQueryService;

import static spark.Spark.get;
import static spark.Spark.post;

public class PaymentRoutes {

    public static void initPaymentRoutes() {
        PaymentCommandService paymentCommandService = new PaymentCommandService();
        PaymentQueryService paymentQueryService = new PaymentQueryService();

        post("/init-payment", (request, response) -> {
            response.type("application/json");

            PaymentRequestDto paymentRequestDto = new Gson().fromJson(request.body(), PaymentRequestDto.class);
            paymentCommandService.processPaymentTransaction(paymentRequestDto);

            return new Gson().toJson("SUCCESS");
        });

        get("/transactions", (request, response) -> {
            response.type("application/json");

            return new Gson().toJsonTree(paymentQueryService.getPaymentTransactions(""));
        });

        get("/accounts/:id/transactions", (request, response) -> {
            response.type("application/json");

            return new Gson().toJsonTree(paymentQueryService.getPaymentTransactions(request.params(":id")));
        });
    }
}
