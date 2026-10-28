package com.hotelreservation.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class providing secure password hashing and verification using BCrypt.
 */
public final class PasswordUtil {

    private static final int BCRYPT_LOG_ROUNDS = 12;

    private PasswordUtil() {
        // Prevent instantiation
    }

    /**
     * Hashes a plaintext password using BCrypt with a secure salt.
     *
     * @param plainPassword the plaintext password
     * @return salted BCrypt hash string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
    }

    /**
     * Verifies a candidate plaintext password against an existing BCrypt hash.
     *
     * @param candidatePlain candidate plaintext password
     * @param storedHash     stored BCrypt hash
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String candidatePlain, String storedHash) {
        if (candidatePlain == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(candidatePlain, storedHash);
        } catch (IllegalArgumentException e) {
            // Malformed hash
            return false;
        }
    }
}

