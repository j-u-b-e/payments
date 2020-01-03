package com.jube.payment.balance.api;


import com.google.gson.Gson;
import com.jube.payment.balance.BalanceService;

import static spark.Spark.get;

public class BalanceRoutes {

    public static void initBalanceRoutes() {

        get("/balances", (request, response) -> {
            response.type("application/json");
            BalanceService balanceService = new BalanceService();

            return new Gson().toJsonTree(balanceService.getBalances());
        });

        get("/accounts/:id/balances", (request, response) -> {
            response.type("application/json");
            BalanceService balanceService = new BalanceService();

            return new Gson().toJsonTree(balanceService.getBalance(request.params(":id")));
        });
    }

}
