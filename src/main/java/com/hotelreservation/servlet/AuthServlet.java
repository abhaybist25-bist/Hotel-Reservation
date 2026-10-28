package com.hotelreservation.servlet;

import com.hotelreservation.model.User;
import com.hotelreservation.service.AuthService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller handling user authentication, customer registration, and session invalidation.
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/login" -> {
                // If user is already logged in, redirect to appropriate home
                HttpSession session = request.getSession(false);
                if (session != null && session.getAttribute("currentUser") != null) {
                    User user = (User) session.getAttribute("currentUser");
                    response.sendRedirect(request.getContextPath() + (user.isAdmin() ? "/admin/dashboard" : "/customer/dashboard"));
                    return;
                }
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            }
            case "/register" -> {
                request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            }
            case "/logout" -> handleLogout(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/login" -> handleLogin(request, response);
            case "/register" -> handleRegister(request, response);
            case "/logout" -> handleLogout(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User user = authService.authenticate(email, password);

            // Session Fixation Protection: Invalidate old session and generate a new one
            HttpSession oldSession = request.getSession(false);
            String redirectUrl = null;
            if (oldSession != null) {
                redirectUrl = (String) oldSession.getAttribute("redirectAfterLogin");
                oldSession.invalidate();
            }

            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("currentUser", user);
            newSession.setAttribute("flashSuccess", "Welcome back, " + user.getFullName() + "!");

            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                response.sendRedirect(redirectUrl);
            } else if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/customer/dashboard");
            }

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            User user = authService.registerCustomer(fullName, email, phone, password, confirmPassword);

            // Invalidate session and establish fresh customer session
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("currentUser", user);
            newSession.setAttribute("flashSuccess", "Registration successful! Welcome to " +
                    getServletContext().getAttribute("hotelName") + ".");

            response.sendRedirect(request.getContextPath() + "/customer/dashboard");

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("flashSuccess", "You have been successfully logged out.");
        response.sendRedirect(request.getContextPath() + "/login");
    }
}

