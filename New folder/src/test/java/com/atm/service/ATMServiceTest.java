package com.atm.service;

import com.atm.dao.ATMDAO;
import com.atm.dao.UserDAO;
import com.atm.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ATMServiceTest {

    private ATMService atmService;

    @Mock
    private ATMDAO atmDAO;

    @Mock
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        atmService = new ATMService();
        atmService.setATMDAO(atmDAO);
        atmService.setUserDAO(userDAO);
    }

    @Test
    void testAddATMFunds() throws SQLException {
        // Arrange
        when(atmDAO.getATMBalance()).thenReturn(new BigDecimal("10000.00"));

        // Act
        atmService.addATMFunds(new BigDecimal("5000.00"));

        // Assert
        verify(atmDAO).updateATMBalance(new BigDecimal("15000.00"));
    }

    @Test
    void testGetATMBalance() throws SQLException {
        // Arrange
        when(atmDAO.getATMBalance()).thenReturn(new BigDecimal("10000.00"));

        // Act
        BigDecimal balance = atmService.getATMBalance();

        // Assert
        assertEquals(new BigDecimal("10000.00"), balance);
    }

    @Test
    void testIsATMFundCritical() throws SQLException {
        // Arrange
        when(atmDAO.getATMBalance()).thenReturn(new BigDecimal("4000.00")); // Critical (< 5000)

        // Act
        boolean critical = atmService.isATMFundCritical();

        // Assert
        assertTrue(critical);
    }

    @Test
    void testWithdrawSuccess() throws SQLException {
        // Arrange
        int userId = 1;
        BigDecimal withdrawAmount = new BigDecimal("100.00");
        BigDecimal atmBalance = new BigDecimal("10000.00");
        BigDecimal userBalance = new BigDecimal("500.00");

        User user = new User();
        user.setId(userId);
        user.setBalance(userBalance);

        when(atmDAO.getATMBalance()).thenReturn(atmBalance);
        when(userDAO.findById(userId)).thenReturn(user);

        // Act
        String result = atmService.withdraw(userId, withdrawAmount);

        // Assert
        assertEquals("SUCCESS", result);
        verify(userDAO).updateUserBalance(userId, new BigDecimal("400.00"));
        verify(atmDAO).updateATMBalance(new BigDecimal("9900.00"));
    }

    @Test
    void testWithdrawInsufficientATMFunds() throws SQLException {
        // Arrange
        int userId = 1;
        BigDecimal withdrawAmount = new BigDecimal("20000.00");
        BigDecimal atmBalance = new BigDecimal("10000.00");

        when(atmDAO.getATMBalance()).thenReturn(atmBalance);

        // Act
        String result = atmService.withdraw(userId, withdrawAmount);

        // Assert
        assertEquals("ATM Insufficient Funds", result);
        verify(userDAO, never()).updateUserBalance(anyInt(), any(BigDecimal.class));
    }

    @Test
    void testWithdrawInsufficientUserFunds() throws SQLException {
        // Arrange
        int userId = 1;
        BigDecimal withdrawAmount = new BigDecimal("100.00");
        BigDecimal atmBalance = new BigDecimal("10000.00");
        BigDecimal userBalance = new BigDecimal("50.00"); // Insufficient

        User user = new User();
        user.setId(userId);
        user.setBalance(userBalance);

        when(atmDAO.getATMBalance()).thenReturn(atmBalance);
        when(userDAO.findById(userId)).thenReturn(user);

        // Act
        String result = atmService.withdraw(userId, withdrawAmount);

        // Assert
        assertEquals("Insufficient User Balance", result);
        verify(userDAO, never()).updateUserBalance(anyInt(), any(BigDecimal.class));
    }

    @Test
    void testDepositSuccess() throws SQLException {
        // Arrange
        int userId = 1;
        BigDecimal depositAmount = new BigDecimal("200.00");
        BigDecimal atmBalance = new BigDecimal("10000.00");
        BigDecimal userBalance = new BigDecimal("500.00");

        User user = new User();
        user.setId(userId);
        user.setBalance(userBalance);

        when(userDAO.findById(userId)).thenReturn(user);
        when(atmDAO.getATMBalance()).thenReturn(atmBalance);

        // Act
        String result = atmService.deposit(userId, depositAmount);

        // Assert
        assertEquals("SUCCESS", result);
        verify(userDAO).updateUserBalance(userId, new BigDecimal("700.00"));
        verify(atmDAO).updateATMBalance(new BigDecimal("10200.00"));
    }
}
