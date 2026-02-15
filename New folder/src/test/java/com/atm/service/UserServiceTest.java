package com.atm.service;

import com.atm.dao.UserDAO;
import com.atm.model.User;
import com.atm.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;

    @Mock
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        userService.setUserDAO(userDAO);
    }

    @Test
    void testRegisterUserSuccess() throws SQLException {
        // Arrange
        when(userDAO.findByUsername("newUser")).thenReturn(null);
        // SecurityUtil hashPassword is static, but logic is simple enough we can rely on it or mock it.
        // For unit testing UserService, we care that it calls DAO.
        
        // Act
        boolean result = userService.registerUser("newUser", "password", "USER");

        // Assert
        assertTrue(result);
        verify(userDAO).createUser(any(User.class));
    }

    @Test
    void testRegisterUserAlreadyExists() throws SQLException {
        // Arrange
        when(userDAO.findByUsername("existingUser")).thenReturn(new User());

        // Act
        boolean result = userService.registerUser("existingUser", "password", "USER");

        // Assert
        assertFalse(result);
        verify(userDAO, never()).createUser(any(User.class));
    }

    @Test
    void testLoginUserSuccess() throws SQLException {
        // Arrange
        User mockUser = new User();
        mockUser.setUsername("testUser");
        // We need to match the hashed password.
        // Real SecurityUtil.hashPassword("password") -> let's say "hashed_password"
        // But SecurityUtil.checkPassword uses BCrypt.
        // We can't easily mock static SecurityUtil without mockito-inline (which we
        // added).
        // However, better to rely on actual BCrypt if possible or mock it.
        // Let's rely on actual BCrypt logic if testing integration, but for pure unit
        // test matching hashed pass is hard without control.
        // I will trust SecurityUtil works and just set a hash I know?
        // No, checkPassword verify hash against plain text.
        // So I must set the hash in mockUser to be what "password" hashes to.
        String hashedPassword = SecurityUtil.hashPassword("password");
        mockUser.setPasswordHash(hashedPassword);

        when(userDAO.findByUsername("testUser")).thenReturn(mockUser);

        // Act
        User result = userService.loginUser("testUser", "password");

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void testLoginUserFailure() throws SQLException {
        // Arrange
        when(userDAO.findByUsername("wrongUser")).thenReturn(null);

        // Act
        User result = userService.loginUser("wrongUser", "password");

        // Assert
        assertNull(result);
    }

    @Test
    void testLoginUserWrongPassword() throws SQLException {
        // Arrange
        User mockUser = new User();
        mockUser.setUsername("testUser");
        String hashedPassword = SecurityUtil.hashPassword("correctPassword");
        mockUser.setPasswordHash(hashedPassword);

        when(userDAO.findByUsername("testUser")).thenReturn(mockUser);

        // Act
        User result = userService.loginUser("testUser", "wrongPassword");

        // Assert
        assertNull(result);
    }

    @Test
    void testGetAllUsers() throws SQLException {
        // Arrange
        List<User> users = Arrays.asList(new User(), new User());
        when(userDAO.findAllUsers()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        verify(userDAO).findAllUsers();
    }
}