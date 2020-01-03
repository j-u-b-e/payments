## Money Transfer

Implementation of RESTful API (including data model and the backing implementation) for money transfers between accounts.

Project uses: Spark, Gson, Jooq, Flyway, H2, JUnit.

- Transaction management is done on server side
- It is assumed, that only one application which is processing payments is running
- Application can securely handle incoming payment initiation request from multiple threads 
- Solution was created and tested on Windows machine. Configuration changes might be needed to run on Linux

Data model:
- Accounts are stored in Accounts table.
- Each payment request creates 3 rows in 2 tables:
    - 2 rows in Transactions table. One for "FROM" account and another for "TO" account
    - 1 row in Payments table. It has references to debit and credit transactions in Transactions table.
- To get account balance a DB view is used which groups transactions based on account number and sums amount of transactions 

## How to Build
    mvn clean package

## How to Run
    java -jar .\target\payment-service-1.0-SNAPSHOT.jar

## End Points

### Accounts
    GET /accounts
    GET /accounts/:id
    POST /accounts

### Balances    
    GET /accounts/:id/balances
    GET /balances

### Transactions
    GET /transactions
    GET /accounts/:id/transactions
    POST /init-payment
    
#### Account creation
    No request body is needed

#### Payment initiation
```
{
    "fromAccountNumber" : "89965b04-116e-4251-94c4-3b1684bfcb6c",
    "toAccountNumber" : "acc-with-money",
    "reference" : "payment reference",
    "amount" : 123.45
}
```
### How to use
1. Create Account: POST http://localhost:4567/accounts
2. Initiate payment to newly created account. Use account id which was returned in step 1. There is an existing account with positive balance (accountNumber="acc-with-money") which could be used for payment.
3. Check balance of your account: GET http://localhost:4567/accounts/{accountNumber}/balances
4. Check transactions of your account: GET http://localhost:4567/accounts/{accountNumber}/transactions
5. GET /balances and GET /transactions could be used to see all data.

