package com.atm.model;

import java.math.BigDecimal;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String role; // "ADMIN" or "USER"
    private BigDecimal balance;

    public User() {}

    public User(String username, String passwordHash, String role, BigDecimal balance) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.balance = balance;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
