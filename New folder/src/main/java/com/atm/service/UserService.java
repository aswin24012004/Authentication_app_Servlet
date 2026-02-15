package com.atm.service;

import com.atm.dao.UserDAO;
import com.atm.model.User;
import com.atm.util.SecurityUtil;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserDAO userDAO = new UserDAO();

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean registerUser(String username, String password, String role) throws SQLException {
        if (userDAO.findByUsername(username) != null) {
            return false; // User already exists
        }
        String hashedPassword = SecurityUtil.hashPassword(password);
        User newUser = new User(username, hashedPassword, role, BigDecimal.ZERO);
        userDAO.createUser(newUser);
        return true;
    }

    public User loginUser(String username, String password) throws SQLException {
        User user = userDAO.findByUsername(username);
        if (user != null && SecurityUtil.checkPassword(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.findAllUsers();
    }

    public void deleteUser(int userId) throws SQLException {
        userDAO.deleteUser(userId);
    }
}
