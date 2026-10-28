package com.hotelreservation.config;

import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener managing startup database connectivity,
 * schema/index verification, bootstrap seeding, and graceful shutdown of MongoClient.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("Hotel Reservation System web application starting up...");

        // Initialize shared single application-wide MongoClient
        DatabaseConnection.initialize();

        // Verify collections, indexes, and seed bootstrap data
        DatabaseInitializer.initializeDatabase();

        // Bind global application constants to ServletContext for JSP EL access
        ServletContext ctx = sce.getServletContext();
        ctx.setAttribute("hotelName", AppConfig.getHotelName());
        ctx.setAttribute("hotelEmail", AppConfig.getHotelEmail());
        ctx.setAttribute("hotelPhone", AppConfig.getHotelPhone());
        ctx.setAttribute("hotelAddress", AppConfig.getHotelAddress());
        ctx.setAttribute("taxPercentage", AppConfig.getTaxPercentage());

        LOGGER.info("Hotel Reservation System web application initialized successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Hotel Reservation System web application shutting down...");

        // Gracefully close single application-wide MongoClient
        DatabaseConnection.close();

        LOGGER.info("Hotel Reservation System web application resources cleaned up.");
    }
}

