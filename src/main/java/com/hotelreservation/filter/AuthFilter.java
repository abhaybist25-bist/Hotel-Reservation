package com.hotelreservation.filter;

import com.hotelreservation.model.User;
import java.io.IOException;
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
 * Filter restricting customer features to authenticated users.
 */
@WebFilter(urlPatterns = {"/customer/*", "/book", "/booking/*"})
public class AuthFilter implements Filter {

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
            // Save requested URL for redirecting after successful login
            String requestedUri = request.getRequestURI();
            String queryString = request.getQueryString();
            String redirectUrl = requestedUri + (queryString != null ? "?" + queryString : "");

            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("redirectAfterLogin", redirectUrl);
            newSession.setAttribute("flashError", "Please sign in to access your reservations and account.");

            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}

