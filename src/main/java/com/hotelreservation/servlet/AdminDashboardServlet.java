package com.hotelreservation.servlet;

import com.hotelreservation.model.Booking;
import com.hotelreservation.model.dto.AdminDashboardStats;
import com.hotelreservation.service.AdminService;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller displaying administrator KPI dashboard metrics and recent reservations.
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AdminDashboardStats stats = adminService.getDashboardStats();
        List<Booking> recentBookings = adminService.getBookings(1, 6, null, null);

        request.setAttribute("stats", stats);
        request.setAttribute("recentBookings", recentBookings);

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}

