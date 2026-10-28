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
 * Controller displaying customer profile account details.
 */
@WebServlet(name = "CustomerProfileServlet", urlPatterns = {"/customer/profile"})
public class CustomerProfileServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (sessionUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Fetch fresh state from database
        User freshUser = authService.getUserById(sessionUser.getIdString());
        if (freshUser != null) {
            freshUser.setPasswordHash(null); // Never expose hash
            request.setAttribute("profileUser", freshUser);
        } else {
            request.setAttribute("profileUser", sessionUser);
        }

        request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
    }
}

