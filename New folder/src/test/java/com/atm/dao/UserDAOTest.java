package com.atm.dao;

import com.atm.model.User;
import com.atm.util.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDAOTest {

    private UserDAO userDAO;
    private MockedStatic<DBConnection> mockedDBConnection;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private Statement statement;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        mockedDBConnection = mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(connection);
    }

    @AfterEach
    void tearDown() {
        mockedDBConnection.close();
    }

    @Test
    void testCreateUser() throws SQLException {
        // Arrange
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        User user = new User();
        user.setUsername("testUser");
        user.setPasswordHash("hash");
        user.setRole("USER");
        user.setBalance(new BigDecimal("100.00"));

        // Act
        userDAO.createUser(user);

        // Assert
        verify(preparedStatement).setString(1, "testUser");
        verify(preparedStatement).setBigDecimal(4, new BigDecimal("100.00"));
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testFindByUsername() throws SQLException {
        // Arrange
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("username")).thenReturn("testUser");
        when(resultSet.getBigDecimal("balance")).thenReturn(new BigDecimal("500.00"));

        // Act
        User user = userDAO.findByUsername("testUser");

        // Assert
        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
        assertEquals(new BigDecimal("500.00"), user.getBalance());
    }

    @Test
    void testFindById() throws SQLException {
        // Arrange
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(1);

        // Act
        User user = userDAO.findById(1);

        // Assert
        assertNotNull(user);
        assertEquals(1, user.getId());
    }

    @Test
    void testFindAllUsers() throws SQLException {
        // Arrange
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("username")).thenReturn("user1");

        // Act
        List<User> users = userDAO.findAllUsers();

        // Assert
        assertEquals(1, users.size());
        assertEquals("user1", users.get(0).getUsername());
    }

    @Test
    void testUpdateUserBalance() throws SQLException {
        // Arrange
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Act
        userDAO.updateUserBalance(1, new BigDecimal("1000.00"));

        // Assert
        verify(preparedStatement).setBigDecimal(1, new BigDecimal("1000.00"));
        verify(preparedStatement).setInt(2, 1);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testDeleteUser() throws SQLException {
        // Arrange
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Act
        userDAO.deleteUser(1);

        // Assert
        verify(preparedStatement).setInt(1, 1);
        verify(preparedStatement).executeUpdate();
    }
}
