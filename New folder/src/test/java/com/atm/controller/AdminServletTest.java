package com.atm.controller;

import com.atm.model.User;
import com.atm.service.ATMService;
import com.atm.service.UserService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServletTest {

    private AdminServlet adminServlet;

    @Mock
    private UserService userService;

    @Mock
    private ATMService atmService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        adminServlet = new AdminServlet();
        adminServlet.setUserService(userService);
        adminServlet.setATMService(atmService);
    }

    private void mockJsonRequest(String json) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);
    }

    @Test
    public void testGetAllUsers() throws IOException, SQLException, javax.servlet.ServletException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/users");

        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");
        List<User> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        adminServlet.doGet(request, response);

        // Assert
        verify(userService).getAllUsers();
        assertTrue(stringWriter.toString().contains("user1"));
        assertTrue(stringWriter.toString().contains("user2"));
    }

    @Test
    public void testGetATMStatus() throws IOException, SQLException, javax.servlet.ServletException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/atm");

        when(atmService.getATMBalance()).thenReturn(new BigDecimal("50000.00"));
        when(atmService.isATMFundCritical()).thenReturn(false);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        adminServlet.doGet(request, response);

        // Assert
        verify(atmService).getATMBalance();
        assertTrue(stringWriter.toString().contains("50000.00"));
        assertTrue(stringWriter.toString().contains("false"));
    }

    @Test
    public void testAddATMFunds() throws IOException, SQLException, javax.servlet.ServletException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/atm/fund");
        mockJsonRequest("{\"amount\": 10000.00}");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        adminServlet.doPost(request, response);

        // Assert
        verify(atmService).addATMFunds(eq(new BigDecimal("10000.00")));
        assertTrue(stringWriter.toString().contains("ATM Funds Added Successfully"));
    }

    @Test
    public void testDeleteUser() throws IOException, SQLException, javax.servlet.ServletException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/users/123");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        adminServlet.doDelete(request, response);

        // Assert
        verify(userService).deleteUser(123);
        assertTrue(stringWriter.toString().contains("User deleted successfully"));
    }
}