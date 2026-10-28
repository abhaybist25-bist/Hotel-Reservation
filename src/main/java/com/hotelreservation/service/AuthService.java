package com.hotelreservation.service;

import com.hotelreservation.dao.UserDAO;
import com.hotelreservation.model.Role;
import com.hotelreservation.model.User;
import com.hotelreservation.util.PasswordUtil;
import com.hotelreservation.util.ValidationUtil;
import java.util.logging.Logger;
import org.bson.types.ObjectId;

/**
 * Service managing customer registration, password encryption, and user authentication.
 */
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registers a new customer account with full input validation and password hashing.
     * Role is strictly enforced as CUSTOMER to prevent privilege escalation.
     */
    public User registerCustomer(String fullName, String email, String phone, String password, String confirmPassword) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("A valid email address is required.");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new IllegalArgumentException("A valid phone number (7-25 digits) is required.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        String normalizedEmail = ValidationUtil.normalizeEmail(email);

        // Pre-check for duplicate email
        User existingUser = userDAO.findByNormalizedEmail(normalizedEmail);
        if (existingUser != null) {
            throw new IllegalArgumentException("An account with this email address already exists.");
        }

        String passwordHash = PasswordUtil.hashPassword(password);
        User newUser = new User(
                fullName.trim(),
                email.trim(),
                phone.trim(),
                passwordHash,
                Role.CUSTOMER // Hardcoded to CUSTOMER to block unauthorized ADMIN assignment
        );

        boolean created = userDAO.create(newUser);
        if (!created) {
            throw new IllegalStateException("Failed to register customer account. Please try again.");
        }

        LOGGER.info("Successfully registered customer: " + normalizedEmail);
        return newUser;
    }

    /**
     * Authenticates a user using email and plaintext password.
     *
     * @return User on success
     * @throws IllegalArgumentException on authentication failure
     */
    public User authenticate(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Email and password are required.");
        }

        String normalizedEmail = ValidationUtil.normalizeEmail(email);
        User user = userDAO.findByNormalizedEmail(normalizedEmail);

        if (user == null || !PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            LOGGER.warning("Failed login attempt for email: " + normalizedEmail);
            throw new IllegalArgumentException("Invalid email or password.");
        }

        LOGGER.info("Successful login for user: " + normalizedEmail + " with role: " + user.getRole());
        return user;
    }

    /**
     * Retrieves user by MongoDB ObjectId.
     */
    public User getUserById(String idString) {
        if (idString == null || !ObjectId.isValid(idString)) {
            return null;
        }
        return userDAO.findById(new ObjectId(idString));
    }
}

