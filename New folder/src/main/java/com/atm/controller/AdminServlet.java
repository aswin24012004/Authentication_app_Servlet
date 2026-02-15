package com.atm.controller;

import com.atm.model.User;
import com.atm.service.ATMService;
import com.atm.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {
    private UserService userService = new UserService();
    private ATMService atmService = new ATMService();
    private Gson gson = new Gson();

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setATMService(ATMService atmService) {
        this.atmService = atmService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");

        try {
            if ("/users".equals(pathInfo)) {
                List<User> users = userService.getAllUsers();
                resp.getWriter().write(gson.toJson(users));
            } else if ("/atm".equals(pathInfo)) {
                // Return ATM status
                BigDecimal balance = atmService.getATMBalance();
                boolean isCritical = atmService.isATMFundCritical();
                resp.getWriter().write(String.format("{\"balance\": %.2f, \"critical\": %b}", balance, isCritical));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");

        try {
            if ("/atm/fund".equals(pathInfo)) {
                BufferedReader reader = req.getReader();
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                BigDecimal amount = json.get("amount").getAsBigDecimal();

                atmService.addATMFunds(amount);
                resp.getWriter().write("{\"message\": \"ATM Funds Added Successfully\"}");
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json"); // Ensure content type is set

        try {
            if (pathInfo != null && pathInfo.startsWith("/users/")) {
                // Extract ID from /users/{id}
                String idStr = pathInfo.substring("/users/".length());
                try {
                    int userId = Integer.parseInt(idStr);
                    userService.deleteUser(userId);
                    resp.getWriter().write("{\"message\": \"User deleted successfully\"}");
                } catch (NumberFormatException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"error\": \"Invalid User ID\"}");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(e.getMessage()));
        }
    }
}
