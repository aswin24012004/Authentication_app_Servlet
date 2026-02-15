package com.atm.controller;

import com.atm.model.User;
import com.atm.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {
    private UserService userService = new UserService();
    private Gson gson = new Gson();

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");

        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);

            if ("/register".equals(pathInfo)) {
                String username = jsonRequest.get("username").getAsString();
                String password = jsonRequest.get("password").getAsString();
                String role = jsonRequest.has("role") ? jsonRequest.get("role").getAsString() : "USER";

                boolean success = userService.registerUser(username, password, role);
                if (success) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    resp.getWriter().write("{\"message\": \"User registered successfully\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    resp.getWriter().write("{\"message\": \"User already exists\"}");
                }

            } else if ("/login".equals(pathInfo)) {
                String username = jsonRequest.get("username").getAsString();
                String password = jsonRequest.get("password").getAsString();

                User user = userService.loginUser(username, password);
                if (user != null) {
                    HttpSession session = req.getSession();
                    session.setAttribute("user", user);
                    resp.getWriter().write(gson.toJson(user)); // Return user info (exclude password hash in real app)
                } else {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().write("{\"message\": \"Invalid credentials\"}");
                }
            } else if ("/logout".equals(pathInfo)) {
                HttpSession session = req.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                resp.getWriter().write("{\"message\": \"Logged out successfully\"}");
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
