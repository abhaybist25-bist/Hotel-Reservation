package com.hotelreservation.api;

import com.hotelreservation.model.Role;
import com.hotelreservation.model.User;
import com.hotelreservation.service.AuthService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * REST API endpoint for authentication (login, register, logout).
 *
 * <p>Endpoints:
 * <ul>
 *   <li>POST /api/auth/login    — Authenticate and receive a session cookie</li>
 *   <li>POST /api/auth/register — Register a new customer or hotel partner account</li>
 *   <li>POST /api/auth/logout   — Invalidate the current session</li>
 *   <li>GET  /api/auth/me       — Return the currently authenticated user's profile</li>
 * </ul>
 */
@WebServlet(urlPatterns = {"/api/auth/login", "/api/auth/register", "/api/auth/logout", "/api/auth/me"})
public class ApiAuthServlet extends ApiBaseServlet {

    private static final Logger LOGGER = Logger.getLogger(ApiAuthServlet.class.getName());

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/api/auth/me".equals(path)) {
            handleMe(req, res);
        } else {
            error(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Use POST for this endpoint.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String path = req.getServletPath();

        switch (path) {
            case "/api/auth/login":
                handleLogin(req, res);
                break;
            case "/api/auth/register":
                handleRegister(req, res);
                break;
            case "/api/auth/logout":
                handleLogout(req, res);
                break;
            default:
                error(res, HttpServletResponse.SC_NOT_FOUND, "Unknown auth endpoint: " + path);
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/auth/me
    // -------------------------------------------------------------------------

    /**
     * Returns the profile of the currently authenticated user.
     * Response: 200 with user data, or 401 if not logged in.
     */
    private void handleMe(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            unauthorized(res);
            return;
        }
        ok(res, buildUserMap(user));
    }

    // -------------------------------------------------------------------------
    // POST /api/auth/login
    // -------------------------------------------------------------------------

    /**
     * Authenticates a user and creates a session.
     *
     * <p>Request body:
     * <pre>{@code
     * {
     *   "email": "user@example.com",
     *   "password": "yourPassword"
     * }
     * }</pre>
     *
     * <p>Response body (200):
     * <pre>{@code
     * {
     *   "success": true,
     *   "data": {
     *     "id": "...", "fullName": "...", "email": "...", "role": "CUSTOMER"
     *   }
     * }
     * }</pre>
     */
    private void handleLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Map<String, Object> body;
        try {
            body = parseBody(req);
        } catch (IllegalArgumentException e) {
            badRequest(res, e.getMessage());
            return;
        }

        String email = getString(body, "email");
        String password = getString(body, "password");

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            badRequest(res, "Both 'email' and 'password' fields are required.");
            return;
        }

        try {
            User user = authService.authenticate(email, password);

            // Invalidate any existing session, then create a fresh one
            HttpSession existing = req.getSession(false);
            if (existing != null) {
                existing.invalidate();
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", user);
            session.setMaxInactiveInterval(60 * 60 * 8); // 8 hours

            LOGGER.info("API login: " + email + " from IP " + req.getRemoteAddr());
            ok(res, buildUserMap(user));
        } catch (IllegalArgumentException e) {
            error(res, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // POST /api/auth/register
    // -------------------------------------------------------------------------

    /**
     * Registers a new user account (customer or hotel partner).
     *
     * <p>Request body for customer:
     * <pre>{@code
     * {
     *   "accountType": "customer",
     *   "fullName": "Jane Doe",
     *   "email": "jane@example.com",
     *   "phone": "9876543210",
     *   "password": "Secret@123",
     *   "confirmPassword": "Secret@123"
     * }
     * }</pre>
     *
     * <p>Request body for hotel partner:
     * <pre>{@code
     * {
     *   "accountType": "hotel",
     *   "fullName": "John Smith",
     *   "email": "partner@myhotel.com",
     *   "phone": "9876543210",
     *   "password": "Secret@123",
     *   "confirmPassword": "Secret@123",
     *   "hotelName": "My Grand Hotel",
     *   "hotelCity": "Mumbai",
     *   "hotelAddress": "123 Marine Drive, Mumbai"
     * }
     * }</pre>
     */
    private void handleRegister(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Map<String, Object> body;
        try {
            body = parseBody(req);
        } catch (IllegalArgumentException e) {
            badRequest(res, e.getMessage());
            return;
        }

        String accountType = getString(body, "accountType");
        String fullName    = getString(body, "fullName");
        String email       = getString(body, "email");
        String phone       = getString(body, "phone");
        String password    = getString(body, "password");
        String confirm     = getString(body, "confirmPassword");

        if (accountType == null) {
            accountType = "customer"; // default
        }

        try {
            User newUser;
            if ("hotel".equalsIgnoreCase(accountType)) {
                String hotelName    = getString(body, "hotelName");
                String hotelCity    = getString(body, "hotelCity");
                String hotelAddress = getString(body, "hotelAddress");
                newUser = authService.registerHotelPartner(fullName, email, phone, password, confirm, hotelName, hotelCity, hotelAddress);
            } else {
                newUser = authService.registerCustomer(fullName, email, phone, password, confirm);
            }

            LOGGER.info("API register: " + email + " as " + accountType);
            ok(res, HttpServletResponse.SC_CREATED, buildUserMap(newUser));
        } catch (IllegalArgumentException | IllegalStateException e) {
            badRequest(res, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // POST /api/auth/logout
    // -------------------------------------------------------------------------

    /**
     * Invalidates the current session.
     * Response: 200 {"success": true, "message": "Logged out successfully."}
     */
    private void handleLogout(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            String email = "";
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                email = user.getEmail();
            }
            session.invalidate();
            LOGGER.info("API logout: " + email);
        }
        okMessage(res, "Logged out successfully.");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a safe, serializable Map representation of a User (omits passwordHash).
     */
    private Map<String, Object> buildUserMap(User user) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",          user.getIdString());
        m.put("fullName",    user.getFullName());
        m.put("email",       user.getEmail());
        m.put("phone",       user.getPhone());
        m.put("role",        user.getRole() != null ? user.getRole().name() : null);
        m.put("hotelId",     user.getHotelIdString());
        // Note: User model does not expose an active/status flag in the current schema
        return m;
    }
}
