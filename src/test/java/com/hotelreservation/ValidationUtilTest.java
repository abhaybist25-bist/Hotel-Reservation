package com.hotelreservation;

import com.hotelreservation.util.ValidationUtil;
import java.math.BigDecimal;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Automated Unit Tests for validation, normalization, and XSS sanitization.
 */
public class ValidationUtilTest {

    @Test
    public void testEmailValidation() {
        assertTrue(ValidationUtil.isValidEmail("guest@grandazure.com"));
        assertTrue(ValidationUtil.isValidEmail("john.doe+stay@hotel.org"));
        assertFalse(ValidationUtil.isValidEmail("plainaddress"));
        assertFalse(ValidationUtil.isValidEmail("@missingusername.com"));
        assertFalse(ValidationUtil.isValidEmail("guest@.com"));
        assertFalse(ValidationUtil.isValidEmail(null));
        assertFalse(ValidationUtil.isValidEmail(""));
    }

    @Test
    public void testEmailNormalization() {
        assertEquals("guest@hotel.com", ValidationUtil.normalizeEmail("  Guest@Hotel.COM  "));
        assertEquals("john.doe@example.org", ValidationUtil.normalizeEmail("JOHN.DOE@example.org"));
        assertNull(ValidationUtil.normalizeEmail(null));
    }

    @Test
    public void testPhoneValidation() {
        assertTrue(ValidationUtil.isValidPhone("+1 (555) 234-5678"));
        assertTrue(ValidationUtil.isValidPhone("5551234567"));
        assertTrue(ValidationUtil.isValidPhone("+44-20-7946-0958"));
        assertFalse(ValidationUtil.isValidPhone("abc-def-ghij"));
        assertFalse(ValidationUtil.isValidPhone("12")); // Too short
        assertFalse(ValidationUtil.isValidPhone(null));
    }

    @Test
    public void testXssSanitization() {
        String dangerousInput = "<script>alert('XSS')</script>&\"'";
        String sanitized = ValidationUtil.sanitize(dangerousInput);

        assertFalse("Script tag must not remain", sanitized.contains("<script>"));
        assertTrue(sanitized.contains("&lt;script&gt;"));
        assertTrue(sanitized.contains("&quot;"));
        assertTrue(sanitized.contains("&#x27;"));
    }

    @Test
    public void testNumberParsing() {
        assertEquals(5, ValidationUtil.parsePositiveInt("5", 1));
        assertEquals(1, ValidationUtil.parsePositiveInt("-3", 1));
        assertEquals(2, ValidationUtil.parsePositiveInt("invalid", 2));
        assertEquals(new BigDecimal("150.50"), ValidationUtil.parsePositiveBigDecimal("150.50"));
        assertNull(ValidationUtil.parsePositiveBigDecimal("-10.00"));
        assertNull(ValidationUtil.parsePositiveBigDecimal("abc"));
    }
}

