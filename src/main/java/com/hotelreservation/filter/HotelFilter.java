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
 * Filter restricting Hotel Partner portal and property editing features strictly to HOTEL or ADMIN users.
 */
@WebFilter(urlPatterns = {"/hotel/*"})
public class HotelFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(HotelFilter.class.getName());

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
            String queryString = request.getQueryString();
            String redirectUrl = requestedUri + (queryString != null ? "?" + queryString : "");

            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("redirectAfterLogin", redirectUrl);
            newSession.setAttribute("flashError", "Hotel partner access required. Please sign in with your hotel credentials.");

            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!currentUser.isHotel() && !currentUser.isAdmin()) {
            LOGGER.warning("Unauthorized hotel portal access attempt by customer: " + currentUser.getEmail() +
                    " on URI: " + request.getRequestURI());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Hotel partner account required.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
