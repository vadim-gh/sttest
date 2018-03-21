CREATE TABLE accounts(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(20) NOT NULL UNIQUE,
  balance DECIMAL(15,2) NOT NULL
);

CREATE TABLE transfers(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_id_from BIGINT NOT NULL REFERENCES accounts(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  account_code_from VARCHAR(20) NOT NULL,
  account_id_to BIGINT NOT NULL REFERENCES accounts(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  account_code_to VARCHAR(20) NOT NULL,
  transfer_date TIMESTAMP NOT NULL,
  amount DECIMAL(15,2) NOT NULL
);