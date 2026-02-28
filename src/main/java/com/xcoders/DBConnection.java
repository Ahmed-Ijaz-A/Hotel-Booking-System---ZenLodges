package com.xcoders;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing the MySQL database connection
 * for the ZenLodges hotel booking system.
 *
 * Uses a singleton-style cached connection suitable for a small JavaFX
 * desktop application (single user, single thread at a time).
 */
public final class DBConnection {

    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "hotel_booking_system";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static final String USER     = "root";
    private static final String PASSWORD = "";          // change to your MySQL root password

    private static Connection connection;

    // Prevent instantiation
    private DBConnection() { }

    /**
     * Returns the shared database connection.
     * Creates a new one if the current connection is null or closed.
     *
     * @return a live {@link Connection} to hotel_booking_system
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully.");
            } catch (SQLException e) {
                System.err.println("Database connection failed!");
                System.err.println("URL  : " + URL);
                System.err.println("Error: " + e.getMessage());
                throw e;   // re-throw so the caller can react
            }
        }
        return connection;
    }

    /**
     * Closes the shared database connection if it is open.
     * Safe to call even when the connection is already closed or null.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Database connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
