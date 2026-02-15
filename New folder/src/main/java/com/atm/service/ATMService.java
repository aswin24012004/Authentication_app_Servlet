package com.atm.service;

import com.atm.dao.ATMDAO;
import com.atm.dao.UserDAO;
import com.atm.model.User;
import java.math.BigDecimal;
import java.sql.SQLException;

public class ATMService {
    private ATMDAO atmDAO = new ATMDAO();
    private UserDAO userDAO = new UserDAO();

    public void setATMDAO(ATMDAO atmDAO) {
        this.atmDAO = atmDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Admin: Add funds to ATM
    public void addATMFunds(BigDecimal amount) throws SQLException {
        BigDecimal currentBalance = atmDAO.getATMBalance();
        atmDAO.updateATMBalance(currentBalance.add(amount));
    }

    public BigDecimal getATMBalance() throws SQLException {
        return atmDAO.getATMBalance();
    }

    public boolean isATMFundCritical() throws SQLException {
        return atmDAO.getATMBalance().compareTo(new BigDecimal("5000")) < 0;
    }

    // User: Withdraw
    public String withdraw(int userId, BigDecimal amount) throws SQLException {
        // 1. Check ATM Fund
        BigDecimal atmBalance = atmDAO.getATMBalance();
        if (atmBalance.compareTo(amount) < 0) {
            return "ATM Insufficient Funds";
        }

        // 2. Check User Balance
        User user = userDAO.findById(userId);
        if (user == null)
            return "User Not Found";

        if (user.getBalance().compareTo(amount) < 0) {
            return "Insufficient User Balance";
        }

        // 3. Perform Transaction (Ideally atomic)
        userDAO.updateUserBalance(userId, user.getBalance().subtract(amount));
        atmDAO.updateATMBalance(atmBalance.subtract(amount));

        return "SUCCESS";
    }

    // User: Deposit (Credit)
    public String deposit(int userId, BigDecimal amount) throws SQLException {
        // Check constraints if required (e.g. max deposit)

        User user = userDAO.findById(userId);
        if (user == null)
            return "User Not Found";

        BigDecimal atmBalance = atmDAO.getATMBalance();

        // Update Balances
        userDAO.updateUserBalance(userId, user.getBalance().add(amount));
        atmDAO.updateATMBalance(atmBalance.add(amount));

        return "SUCCESS";
    }
}
