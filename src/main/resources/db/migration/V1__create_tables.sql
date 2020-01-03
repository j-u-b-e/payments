DROP SCHEMA revolut IF EXISTS;
CREATE SCHEMA revolut;

CREATE TABLE revolut.accounts (
  id BIGINT auto_increment,
  account_number VARCHAR(50),

  CONSTRAINT pk_accounts PRIMARY KEY (id)
);

CREATE TABLE revolut.transactions (
  id BIGINT auto_increment,
  account_number VARCHAR(50),
  amount NUMERIC(19,2),
  currency VARCHAR(10),
  occurred_at TIMESTAMP,

  CONSTRAINT pk_transactions PRIMARY KEY (id),
  FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);

CREATE TABLE revolut.payments (
  id BIGINT auto_increment,
  from_transaction_id BIGINT,
  to_transaction_id BIGINT,
  amount NUMERIC(19,2) CHECK (amount >= 0),
  currency VARCHAR(10),
  reference VARCHAR(100),
  occurred_at TIMESTAMP,

  CONSTRAINT pk_payments PRIMARY KEY (id)
);

ALTER TABLE revolut.payments ADD FOREIGN KEY (from_transaction_id) REFERENCES transactions(id);
ALTER TABLE revolut.payments ADD FOREIGN KEY (to_transaction_id) REFERENCES transactions(id);

CREATE VIEW revolut.balances AS SELECT account_number, sum(amount) AS balance from revolut.transactions group by account_number;