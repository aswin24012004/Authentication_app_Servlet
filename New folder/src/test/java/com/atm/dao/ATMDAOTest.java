package com.atm.dao;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ATMDAOTest {

    private ATMDAO atmDAO;
    private MockedStatic<DBConnection> mockedDBConnection;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        atmDAO = new ATMDAO();
        mockedDBConnection = mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(connection);
    }

    @AfterEach
    void tearDown() {
        mockedDBConnection.close();
    }

    @Test
    void testGetATMBalance() throws SQLException {
        // Arrange
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBigDecimal("amount")).thenReturn(new BigDecimal("10000.00"));

        // Act
        BigDecimal balance = atmDAO.getATMBalance();

        // Assert
        assertEquals(new BigDecimal("10000.00"), balance);
        verify(connection).prepareStatement(anyString());
        verify(preparedStatement).executeQuery();
    }

    @Test
    void testUpdateATMBalance() throws SQLException {
        // Arrange
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Act
        atmDAO.updateATMBalance(new BigDecimal("15000.00"));

        // Assert
        verify(preparedStatement).setBigDecimal(1, new BigDecimal("15000.00"));
        verify(preparedStatement).executeUpdate();
    }
}