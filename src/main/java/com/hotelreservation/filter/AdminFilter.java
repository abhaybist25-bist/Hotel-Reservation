package com.hotelreservation.filter;

import com.hotelreservation.model.User;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Filter restricting administrative dashboards and management features strictly to ADMIN users.
 */
@WebFilter(urlPatterns = {"/admin/*"})
public class AdminFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AdminFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            String requestedUri = request.getRequestURI();
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("redirectAfterLogin", requestedUri);
            newSession.setAttribute("flashError", "Administrator access required. Please sign in with staff credentials.");

            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!currentUser.isAdmin()) {
            LOGGER.warning("Unauthorized administrative access attempt by customer: " + currentUser.getEmail() +
                    " on URI: " + request.getRequestURI());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrative privileges required.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}

