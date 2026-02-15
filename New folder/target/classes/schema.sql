-- Create Database
CREATE DATABASE IF NOT EXISTS atm_db;
USE atm_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER') NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00
);

-- ATM Fund Table (Single row for overall balance)
CREATE TABLE IF NOT EXISTS atm_fund (
    id INT PRIMARY KEY,
    amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00
);

-- Initialize ATM Fund if not exists
INSERT IGNORE INTO atm_fund (id, amount) VALUES (1, 0.00);

-- Transactions Table (Optional for history, good for tracking)
CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    type ENUM('DEPOSIT', 'WITHDRAWAL', 'CREDIT') NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
