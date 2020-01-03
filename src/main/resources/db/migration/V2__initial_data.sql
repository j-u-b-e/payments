--account for account service testing
INSERT INTO revolut.accounts VALUES(null, 'account-one');
--accounts for balance service testing
INSERT INTO revolut.accounts VALUES(null, 'no-entries-account');
--Account with default balance
INSERT INTO revolut.accounts VALUES(null, 'acc-with-money');
INSERT INTO revolut.transactions VALUES(null, 'acc-with-money', 2351, 'EUR', null);
--Accounts for payment service testing
INSERT INTO revolut.accounts VALUES(null, 'acc-with-money-payment-test');
INSERT INTO revolut.accounts VALUES(null, 'acc-with-no-money-payment-test');
INSERT INTO revolut.transactions VALUES(null, 'acc-with-money-payment-test', 100, 'EUR', null);
--Accounts for multiple payment test by threads
INSERT INTO revolut.accounts VALUES(null, 'acc-with-money-thread-test');
INSERT INTO revolut.accounts VALUES(null, 'acc-with-no-money-thread-test');
INSERT INTO revolut.transactions VALUES(null, 'acc-with-money-thread-test', 53, 'EUR', null);