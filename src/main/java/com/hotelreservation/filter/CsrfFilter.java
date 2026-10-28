package com.hotelreservation.filter;

import com.hotelreservation.util.CsrfUtil;
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
 * Filter providing CSRF protection for all state-changing HTTP requests.
 */
@WebFilter("/*")
public class CsrfFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(CsrfFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI().substring(request.getContextPath().length());

        // Skip CSRF check for static assets
        if (path.startsWith("/assets/") || path.endsWith(".css") || path.endsWith(".js") ||
            path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".svg") || path.endsWith(".ico")) {
            chain.doFilter(request, response);
            return;
        }

        // Ensure session and CSRF token exist for GET requests
        HttpSession session = request.getSession(true);
        String csrfToken = CsrfUtil.getToken(session);
        request.setAttribute("csrfToken", csrfToken);

        String method = request.getMethod().toUpperCase();
        if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
            if (!CsrfUtil.isValidToken(request)) {
                LOGGER.warning("CSRF validation failure on URI: " + path + " from IP: " + request.getRemoteAddr());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Security validation error: Invalid or missing CSRF token.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}

