package com.hotelreservation.api;

import com.hotelreservation.dao.UserDAO;
import com.hotelreservation.model.Role;
import com.hotelreservation.model.User;
import com.hotelreservation.service.AdminService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;

/**
 * REST API endpoint for User management (Admin only).
 *
 * <p>Endpoints:
 * <ul>
 *   <li>GET /api/users            — List all customers (ADMIN only), supports ?page=&pageSize=&keyword=</li>
 *   <li>GET /api/users/{id}       — Get a specific user by ID (ADMIN only)</li>
 *   <li>GET /api/users/me         — Get current user's own profile (any authenticated user)</li>
 * </ul>
 */
@WebServlet(urlPatterns = {"/api/users", "/api/users/*"})
public class ApiUserServlet extends ApiBaseServlet {

    private static final Logger LOGGER = Logger.getLogger(ApiUserServlet.class.getName());

    private AdminService adminService;
    private UserDAO      userDAO;

    @Override
    public void init() throws ServletException {
        this.adminService = new AdminService();
        this.userDAO      = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser == null) {
            unauthorized(res);
            return;
        }

        String pathInfo = req.getPathInfo();

        // GET /api/users/me — any authenticated user can view their own profile
        if ("/me".equals(pathInfo)) {
            ok(res, userToMap(currentUser));
            return;
        }

        // All other /api/users/* require ADMIN
        if (!currentUser.isAdmin()) {
            forbidden(res);
            return;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            handleList(req, res);
        } else {
            String id = pathInfo.substring(1);
            handleGetById(req, res, id);
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/users  (ADMIN only)
    // -------------------------------------------------------------------------

    /**
     * Lists customers with pagination.
     * Query params: page (default 1), pageSize (default 20), keyword (search by name/email)
     */
    private void handleList(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int page     = 1;
        int pageSize = 20;
        String keyword = req.getParameter("keyword");

        try { page     = Integer.parseInt(req.getParameter("page")); }     catch (Exception ignored) {}
        try { pageSize = Integer.parseInt(req.getParameter("pageSize")); } catch (Exception ignored) {}

        try {
            List<User> users = adminService.getCustomers(page, pageSize, keyword);
            long total = userDAO.countCustomers();

            Map<String, Object> result = new HashMap<>();
            result.put("users",    users.stream().map(this::userToMap).toList());
            result.put("total",    total);
            result.put("page",     page);
            result.put("pageSize", pageSize);
            ok(res, result);
        } catch (Exception e) {
            LOGGER.warning("Error listing users: " + e.getMessage());
            serverError(res, "Failed to retrieve users.");
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/users/{id}  (ADMIN only)
    // -------------------------------------------------------------------------

    private void handleGetById(HttpServletRequest req, HttpServletResponse res, String id) throws IOException {
        if (!ObjectId.isValid(id)) {
            badRequest(res, "Invalid user ID format.");
            return;
        }
        User user = userDAO.findById(new ObjectId(id));
        if (user == null) {
            notFound(res, "User");
            return;
        }
        ok(res, userToMap(user));
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /** Converts a User to a safe serializable Map (omits passwordHash). */
    private Map<String, Object> userToMap(User user) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",        user.getIdString());
        m.put("fullName",  user.getFullName());
        m.put("email",     user.getEmail());
        m.put("phone",     user.getPhone());
        m.put("role",      user.getRole() != null ? user.getRole().name() : null);
        m.put("hotelId",   user.getHotelIdString());
        // Note: User model does not expose an active/status flag in the current schema
        m.put("createdAt", user.getCreatedAt());
        m.put("updatedAt", user.getUpdatedAt());
        return m;
    }
}
