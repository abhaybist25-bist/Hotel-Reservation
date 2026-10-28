package com.hotelreservation.servlet;

import com.hotelreservation.model.User;
import com.hotelreservation.service.AdminService;
import com.hotelreservation.util.ValidationUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller displaying registered customer directory for administrators.
 */
@WebServlet(name = "AdminCustomerServlet", urlPatterns = {"/admin/customers"})
public class AdminCustomerServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = ValidationUtil.parsePositiveInt(request.getParameter("page"), 1);
        int pageSize = 15;
        String keyword = request.getParameter("q");

        List<User> customers = adminService.getCustomers(page, pageSize, keyword);

        request.setAttribute("customers", customers);
        request.setAttribute("currentPage", page);
        request.setAttribute("keyword", keyword);

        request.getRequestDispatcher("/WEB-INF/views/admin/customers.jsp").forward(request, response);
    }
}

