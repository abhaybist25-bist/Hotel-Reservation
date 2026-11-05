package com.hotelreservation.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hotelreservation.model.Role;
import com.hotelreservation.model.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Base class for all REST API servlets.
 * Provides shared JSON utilities, session helpers, and standardized response builders.
 */
public abstract class ApiBaseServlet extends HttpServlet {

    protected static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    // -------------------------------------------------------------------------
    // Response helpers
    // -------------------------------------------------------------------------

    /**
     * Writes a JSON success response with the given data payload.
     */
    protected void ok(HttpServletResponse res, Object data) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("data", data);
        writeJson(res, HttpServletResponse.SC_OK, body);
    }

    /**
     * Writes a JSON success response with a custom HTTP status code.
     */
    protected void ok(HttpServletResponse res, int status, Object data) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("data", data);
        writeJson(res, status, body);
    }

    /**
     * Writes a JSON success response with a plain message string.
     */
    protected void okMessage(HttpServletResponse res, String message) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("message", message);
        writeJson(res, HttpServletResponse.SC_OK, body);
    }

    /**
     * Writes a JSON error response with the given HTTP status and error message.
     */
    protected void error(HttpServletResponse res, int status, String message) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("error", message);
        writeJson(res, status, body);
    }

    /**
     * Writes a 400 Bad Request JSON error.
     */
    protected void badRequest(HttpServletResponse res, String message) throws IOException {
        error(res, HttpServletResponse.SC_BAD_REQUEST, message);
    }

    /**
     * Writes a 401 Unauthorized JSON error.
     */
    protected void unauthorized(HttpServletResponse res) throws IOException {
        error(res, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required. Please login via POST /api/auth/login.");
    }

    /**
     * Writes a 403 Forbidden JSON error.
     */
    protected void forbidden(HttpServletResponse res) throws IOException {
        error(res, HttpServletResponse.SC_FORBIDDEN, "Access denied. You do not have permission to perform this action.");
    }

    /**
     * Writes a 404 Not Found JSON error.
     */
    protected void notFound(HttpServletResponse res, String resource) throws IOException {
        error(res, HttpServletResponse.SC_NOT_FOUND, resource + " not found.");
    }

    /**
     * Writes a 500 Internal Server Error JSON error.
     */
    protected void serverError(HttpServletResponse res, String message) throws IOException {
        error(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }

    /**
     * Writes a JSON body with the given HTTP status code, setting content type.
     */
    protected void writeJson(HttpServletResponse res, int status, Object body) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        MAPPER.writeValue(res.getWriter(), body);
    }

    // -------------------------------------------------------------------------
    // Session / Auth helpers
    // -------------------------------------------------------------------------

    /**
     * Returns the currently authenticated user from the HTTP session, or null if not logged in.
     */
    protected User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("currentUser");
    }

    /**
     * Returns true if the current session user has one of the allowed roles.
     */
    protected boolean hasRole(HttpServletRequest req, Role... roles) {
        User user = getCurrentUser(req);
        if (user == null) {
            return false;
        }
        for (Role role : roles) {
            if (user.getRole() == role) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reads the full request body as a string.
     */
    protected String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (java.io.BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /**
     * Parses the request body JSON into a Map.
     * Returns an empty map on empty body or parse failure.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> parseBody(HttpServletRequest req) throws IOException {
        String body = readBody(req);
        if (body == null || body.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            return MAPPER.readValue(body, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON body: " + e.getMessage());
        }
    }

    /**
     * Safely extracts a string value from a parsed JSON map.
     */
    protected String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString().trim() : null;
    }

    /**
     * Safely extracts an integer value from a parsed JSON map, returning defaultVal on missing/invalid.
     */
    protected int getInt(Map<String, Object> map, String key, int defaultVal) {
        Object v = map.get(key);
        if (v == null) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(v.toString().trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}

