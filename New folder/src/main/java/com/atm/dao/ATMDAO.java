package com.atm.dao;

import com.atm.util.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ATMDAO {
    private static final int ATM_ID = 1;

    public BigDecimal getATMBalance() throws SQLException {
        String query = "SELECT amount FROM atm_fund WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, ATM_ID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("amount");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public void updateATMBalance(BigDecimal newAmount) throws SQLException {
        String query = "UPDATE atm_fund SET amount = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, newAmount);
            stmt.setInt(2, ATM_ID);
            stmt.executeUpdate();
        }
    }
}
