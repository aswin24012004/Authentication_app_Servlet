package com.atm.controller;

import com.atm.dao.UserDAO;
import com.atm.model.User;
import com.atm.service.ATMService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/user/*")
public class UserServlet extends HttpServlet {
    private ATMService atmService = new ATMService();
    private UserDAO userDAO = new UserDAO();

    public void setATMService(ATMService atmService) {
        this.atmService = atmService;
    }

    public void setUserDAO(com.atm.dao.UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    private Gson gson = new Gson();

    private User getSessionUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        User sessionUser = getSessionUser(req);
        if (sessionUser == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            if ("/balance".equals(pathInfo)) {
                // Refresh user from DB to get latest balance

                User latestUser = userDAO.findById(sessionUser.getId());

                resp.getWriter().write(gson.toJson(latestUser));
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
        User sessionUser = getSessionUser(req);
        if (sessionUser == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            BufferedReader reader = req.getReader();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            BigDecimal amount = json.get("amount").getAsBigDecimal();

            if ("/withdraw".equals(pathInfo)) {
                String result = atmService.withdraw(sessionUser.getId(), amount);
                if ("SUCCESS".equals(result)) {
                    resp.getWriter().write("{\"message\": \"Withdrawal Successful\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"error\": \"" + result + "\"}");
                }
            } else if ("/deposit".equals(pathInfo) || "/credit".equals(pathInfo)) {
                String result = atmService.deposit(sessionUser.getId(), amount);
                if ("SUCCESS".equals(result)) {
                    resp.getWriter().write("{\"message\": \"Deposit Successful\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"error\": \"" + result + "\"}");
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
