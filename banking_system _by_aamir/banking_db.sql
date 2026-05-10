-- ============================================================
--  banking_db.sql
--  Banking Management System — Database Setup Script
--  Run this once in MySQL before starting the application.
-- ============================================================

CREATE DATABASE IF NOT EXISTS banking_db;
USE banking_db;

-- ── Admin Table ───────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS Admin (
    Admin_ID   INT          AUTO_INCREMENT PRIMARY KEY,
    Username   VARCHAR(50)  NOT NULL UNIQUE,
    Password   VARCHAR(100) NOT NULL,
    Role       ENUM('ADMIN','TELLER') NOT NULL DEFAULT 'TELLER'
);

-- ── Customer Table ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS Customer (
    Customer_ID INT          AUTO_INCREMENT PRIMARY KEY,
    Name        VARCHAR(100) NOT NULL,
    CNIC        VARCHAR(15)  NOT NULL UNIQUE,
    Phone       VARCHAR(15)
);

-- ── Account Table ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS Account (
    Account_ID   INT           AUTO_INCREMENT PRIMARY KEY,
    Customer_ID  INT           NOT NULL,
    Account_Type ENUM('SAVINGS','CURRENT','FIXED_DEPOSIT') NOT NULL,
    Balance      DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_account_customer FOREIGN KEY (Customer_ID)
        REFERENCES Customer(Customer_ID) ON DELETE CASCADE
);

-- ── Transactions Table ────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS Transactions (
    Transaction_ID  INT           AUTO_INCREMENT PRIMARY KEY,
    From_Account_ID INT           NOT NULL DEFAULT 0,
    To_Account_ID   INT           NOT NULL DEFAULT 0,
    Admin_ID        INT           NOT NULL,
    Type            ENUM('DEPOSIT','WITHDRAWAL','TRANSFER') NOT NULL,
    Amount          DECIMAL(15,2) NOT NULL,
    DateTime        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tx_admin FOREIGN KEY (Admin_ID)
        REFERENCES Admin(Admin_ID)
);

-- ── Seed Data ─────────────────────────────────────────────────────────────────

-- Default admin account  (username: admin | password: admin123)
INSERT IGNORE INTO Admin (Username, Password, Role)
VALUES ('admin', 'admin123', 'ADMIN');

-- Default teller account  (username: teller | password: teller123)
INSERT IGNORE INTO Admin (Username, Password, Role)
VALUES ('teller', 'teller123', 'TELLER');

-- Sample customers
INSERT IGNORE INTO Customer (Name, CNIC, Phone)
VALUES
    ('Ali Hassan',   '35202-1234567-1', '0300-1234567'),
    ('Sara Ahmed',   '42101-9876543-2', '0321-9876543'),
    ('Usman Khan',   '61101-5556667-3', '0333-5556667'),
    ('Fatima Malik', '35202-7778889-4', '0345-7778889');

-- Sample accounts
INSERT IGNORE INTO Account (Customer_ID, Account_Type, Balance)
VALUES
    (1, 'SAVINGS',  50000.00),
    (1, 'CURRENT',  15000.00),
    (2, 'SAVINGS',  80000.00),
    (3, 'FIXED_DEPOSIT', 200000.00),
    (4, 'SAVINGS',  30000.00);

-- ── Verify ────────────────────────────────────────────────────────────────────
SELECT 'Setup complete. Tables created and seed data inserted.' AS Status;
