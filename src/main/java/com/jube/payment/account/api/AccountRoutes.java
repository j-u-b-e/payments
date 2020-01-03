package com.jube.payment.account.api;

import com.google.gson.Gson;
import com.jube.payment.account.AccountService;

import static spark.Spark.get;
import static spark.Spark.post;

public class AccountRoutes {

    public static void initAccountRoutes() {

        final AccountService accountService = new AccountService();

        get("/accounts", (request, response) -> {
            response.type("application/json");

            return new Gson().toJsonTree(accountService.getAccounts());
        });

        get("/accounts/:id", (request, response) -> {
            response.type("application/json");

            return new Gson().toJsonTree(accountService.getAccount(request.params(":id")));
        });

        post("/accounts", (request, response) -> {
            response.type("application/json");

            AccountDto accountDto = accountService.createAccount();

            return new Gson().toJsonTree(accountDto);
        });
    }
}
