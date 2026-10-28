package com.hotelreservation;

import com.hotelreservation.util.PasswordUtil;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Automated Unit Tests for BCrypt password hashing and verification.
 */
public class PasswordUtilTest {

    @Test
    public void testHashAndVerifyValidPassword() {
        String plainPassword = "SecretPassword@2026";
        String hash = PasswordUtil.hashPassword(plainPassword);

        assertNotNull(hash);
        assertTrue("Hash should start with BCrypt identifier", hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"));
        assertTrue("Valid password should verify successfully", PasswordUtil.verifyPassword(plainPassword, hash));
    }

    @Test
    public void testRejectInvalidPassword() {
        String plainPassword = "CorrectPassword123";
        String hash = PasswordUtil.hashPassword(plainPassword);

        assertFalse("Wrong password must be rejected", PasswordUtil.verifyPassword("WrongPassword456", hash));
        assertFalse("Empty candidate must be rejected", PasswordUtil.verifyPassword("", hash));
        assertFalse("Null candidate must be rejected", PasswordUtil.verifyPassword(null, hash));
        assertFalse("Null hash must be rejected", PasswordUtil.verifyPassword(plainPassword, null));
    }

    @Test
    public void testSaltsAreUniquePerInvocation() {
        String password = "IdenticalPassword";
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);

        assertNotEquals("Each BCrypt hash must use a unique random salt", hash1, hash2);
        assertTrue(PasswordUtil.verifyPassword(password, hash1));
        assertTrue(PasswordUtil.verifyPassword(password, hash2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPasswordThrows() {
        PasswordUtil.hashPassword(null);
    }
}

