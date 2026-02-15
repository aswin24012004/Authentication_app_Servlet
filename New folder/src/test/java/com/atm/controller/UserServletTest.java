package com.atm.controller;

import com.atm.dao.UserDAO;
import com.atm.model.User;
import com.atm.service.ATMService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServletTest {

    private UserServlet userServlet;

    @Mock
    private ATMService atmService;

    @Mock
    private UserDAO userDAO;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @BeforeEach
    public void setUp() {
        userServlet = new UserServlet();
        userServlet.setATMService(atmService);
        userServlet.setUserDAO(userDAO);
    }

    private void mockSessionUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("testUser");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
    }

    private void mockJsonRequest(String json) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);
    }

    @Test
    public void testGetBalance() throws IOException, SQLException, javax.servlet.ServletException {
        // Arrange
        mockSessionUser();
        when(request.getPathInfo()).thenReturn("/balance");

        User latestUser = new User();
        latestUser.setId(1);
        latestUser.setBalance(new BigDecimal("1000.00"));
        when(userDAO.findById(1)).thenReturn(latestUser);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        userServlet.doGet(request, response);

        // Assert
        verify(userDAO).findById(1);
        assertTrue(stringWriter.toString().contains("1000.00"));
    }

    @Test
    public void testWithdrawSuccess() throws IOException, SQLException, javax.servlet.ServletException {
        // Arrange
        mockSessionUser();
        when(request.getPathInfo()).thenReturn("/withdraw");
        mockJsonRequest("{\"amount\": 100.00}");

        when(atmService.withdraw(eq(1), any(BigDecimal.class))).thenReturn("SUCCESS");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        userServlet.doPost(request, response);

        // Assert
        verify(atmService).withdraw(eq(1), any(BigDecimal.class));
        assertTrue(stringWriter.toString().contains("Withdrawal Successful"));
    }

    @Test
    public void testWithdrawFailure() throws IOException, SQLException, javax.servlet.ServletException {
        // Arrange
        mockSessionUser();
        when(request.getPathInfo()).thenReturn("/withdraw");
        mockJsonRequest("{\"amount\": 10000.00}");

        when(atmService.withdraw(eq(1), any(BigDecimal.class))).thenReturn("Insufficient Funds");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        userServlet.doPost(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(stringWriter.toString().contains("Insufficient Funds"));
    }

    @Test
    public void testDepositSuccess() throws IOException, SQLException, javax.servlet.ServletException {
        // Arrange
        mockSessionUser();
        when(request.getPathInfo()).thenReturn("/deposit");
        mockJsonRequest("{\"amount\": 500.00}");

        when(atmService.deposit(eq(1), any(BigDecimal.class))).thenReturn("SUCCESS");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        userServlet.doPost(request, response);

        // Assert
        verify(atmService).deposit(eq(1), any(BigDecimal.class));
        assertTrue(stringWriter.toString().contains("Deposit Successful"));
    }
}