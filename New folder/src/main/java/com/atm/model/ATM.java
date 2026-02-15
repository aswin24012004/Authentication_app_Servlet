package com.atm.model;

import java.math.BigDecimal;

public class ATM {
    private int id;
    private BigDecimal amount;

    public ATM() {}

    public ATM(int id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
