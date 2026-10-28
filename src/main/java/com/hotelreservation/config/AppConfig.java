package com.hotelreservation.config;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application configuration loader. Reads environment variables first,
 * falling back to src/main/resources/application.properties.
 */
public final class AppConfig {

    private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream is = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                PROPERTIES.load(is);
                LOGGER.info("application.properties loaded successfully.");
            } else {
                LOGGER.warning("application.properties not found in classpath. Relying on environment variables or defaults.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load application.properties", e);
        }
    }

    private AppConfig() {
        // Prevent instantiation
    }

    private static String get(String envKey, String propKey, String defaultValue) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal.trim();
        }
        String propVal = PROPERTIES.getProperty(propKey);
        if (propVal != null && !propVal.trim().isEmpty()) {
            return propVal.trim();
        }
        return defaultValue;
    }

    public static String getMongoUri() {
        return get("MONGODB_URI", "mongodb.uri", "mongodb://localhost:27017");
    }

    public static String getDatabaseName() {
        return get("MONGODB_DATABASE", "mongodb.database", "hotel_reservation_db");
    }

    public static String getAdminBootstrapEmail() {
        return get("ADMIN_BOOTSTRAP_EMAIL", "admin.bootstrap.email", "admin@hotelreservation.com");
    }

    public static String getAdminBootstrapPassword() {
        return get("ADMIN_BOOTSTRAP_PASSWORD", "admin.bootstrap.password", "Admin@12345");
    }

    public static String getAdminBootstrapName() {
        return get("ADMIN_BOOTSTRAP_NAME", "admin.bootstrap.name", "Hotel General Manager");
    }

    public static String getAdminBootstrapPhone() {
        return get("ADMIN_BOOTSTRAP_PHONE", "admin.bootstrap.phone", "+1-555-019-9000");
    }

    public static BigDecimal getTaxPercentage() {
        String val = get("HOTEL_TAX_PERCENTAGE", "hotel.tax.percentage", "12.0");
        try {
            return new BigDecimal(val);
        } catch (Exception e) {
            return new BigDecimal("12.0");
        }
    }

    public static int getSessionTimeoutMinutes() {
        String val = get("SESSION_TIMEOUT_MINUTES", "session.timeout.minutes", "30");
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 30;
        }
    }

    public static String getHotelName() {
        return get("HOTEL_NAME", "hotel.name", "The Grand Azure Hotel & Suites");
    }

    public static String getHotelEmail() {
        return get("HOTEL_CONTACT_EMAIL", "hotel.contact.email", "reservations@grandazure.com");
    }

    public static String getHotelPhone() {
        return get("HOTEL_CONTACT_PHONE", "hotel.contact.phone", "+1 (800) 555-0199");
    }

    public static String getHotelAddress() {
        return get("HOTEL_CONTACT_ADDRESS", "hotel.contact.address", "777 Oceanfront Boulevard, Marina Bay, CA 90291");
    }
}

