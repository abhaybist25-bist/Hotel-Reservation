package com.hotelreservation.util;

import java.security.SecureRandom;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Utility class providing Cross-Site Request Forgery (CSRF) protection token management.
 */
public final class CsrfUtil {

    public static final String CSRF_SESSION_ATTR = "CSRF_TOKEN";
    public static final String CSRF_PARAM_NAME = "_csrf";
    public static final String CSRF_HEADER_NAME = "X-CSRF-Token";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private CsrfUtil() {
        // Prevent instantiation
    }

    /**
     * Generates a new cryptographically secure random token.
     */
    public static String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Retrieves the existing CSRF token from session, or generates and stores a new one.
     */
    public static String getToken(HttpSession session) {
        if (session == null) {
            return null;
        }
        synchronized (session) {
            String token = (String) session.getAttribute(CSRF_SESSION_ATTR);
            if (token == null || token.isEmpty()) {
                token = generateToken();
                session.setAttribute(CSRF_SESSION_ATTR, token);
            }
            return token;
        }
    }

    /**
     * Refreshes the CSRF token stored in the session.
     */
    public static String refreshToken(HttpSession session) {
        if (session == null) {
            return null;
        }
        synchronized (session) {
            String token = generateToken();
            session.setAttribute(CSRF_SESSION_ATTR, token);
            return token;
        }
    }

    /**
     * Validates that the submitted token matches the session token.
     */
    public static boolean isValidToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute(CSRF_SESSION_ATTR);
        if (sessionToken == null || sessionToken.isEmpty()) {
            return false;
        }

        String requestToken = request.getParameter(CSRF_PARAM_NAME);
        if (requestToken == null || requestToken.isEmpty()) {
            requestToken = request.getHeader(CSRF_HEADER_NAME);
        }

        return requestToken != null && sessionToken.equals(requestToken);
    }
}

