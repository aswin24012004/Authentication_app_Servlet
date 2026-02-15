package com.atm.controller;

import com.atm.model.User;
import com.atm.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServletTest {

    private AuthServlet authServlet;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @BeforeEach
    public void setUp() {
        authServlet = new AuthServlet();
        authServlet.setUserService(userService);
    }

    private void mockJsonRequest(String json) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);
    }

    @Test
    public void testLoginSuccess() throws IOException, SQLException, javax.servlet.ServletException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/login");
        mockJsonRequest("{\"username\":\"testUser\", \"password\":\"password123\"}");

        User mockUser = new User();
        mockUser.setUsername("testUser");
        mockUser.setRole("USER");

        when(userService.loginUser("testUser", "password123")).thenReturn(mockUser);
        when(request.getSession()).thenReturn(session);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        authServlet.doPost(request, response);

        // Assert
        verify(userService).loginUser("testUser", "password123");
        verify(session).setAttribute("user", mockUser);
        assertTrue(stringWriter.toString().contains("testUser"));
    }

    @Test
    public void testLoginFailure() throws IOException, SQLException, javax.servlet.ServletException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/login");
        mockJsonRequest("{\"username\":\"wrongUser\", \"password\":\"wrongPass\"}");

        when(userService.loginUser("wrongUser", "wrongPass")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        authServlet.doPost(request, response);

        // Assert
        verify(userService).loginUser("wrongUser", "wrongPass");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(stringWriter.toString().contains("Invalid credentials"));
    }

    @Test
    public void testRegisterSuccess() throws IOException, SQLException, javax.servlet.ServletException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/register");
        mockJsonRequest("{\"username\":\"newUser\", \"password\":\"newPass\", \"role\":\"USER\"}");

        when(userService.registerUser("newUser", "newPass", "USER")).thenReturn(true);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        authServlet.doPost(request, response);

        // Assert
        verify(userService).registerUser("newUser", "newPass", "USER");
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        assertTrue(stringWriter.toString().contains("User registered successfully"));
    }

    @Test
    public void testLogout() throws IOException, javax.servlet.ServletException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/logout");
        when(request.getSession(false)).thenReturn(session);
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        authServlet.doPost(request, response);

        // Assert
        verify(session).invalidate();
        assertTrue(stringWriter.toString().contains("Logged out successfully"));
    }
}