package com.mycompany.stims.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The `DatabaseConnection` class provides functionality to establish a
 * connection to a database using configuration details loaded from a properties
 * file. It includes methods to load the configuration and retrieve a database
 * connection.
 */
public class DatabaseConnection {

    private static final String CONFIG_FILE = "config.properties";
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;

    static {
        loadConfig();
    }

    /**
     * Loads the database configuration (URL, username, and password) from the
     * properties file specified by `CONFIG_FILE`. This method is automatically
     * called when the class is loaded.
     *
     * @throws RuntimeException if the configuration file cannot be found or if
     * there is an error reading the file.
     */
    private static void loadConfig() {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            Properties properties = new Properties();
            if (input == null) {
                throw new RuntimeException("Unable to find " + CONFIG_FILE);
            }
            properties.load(input);
            URL = properties.getProperty("db.url");
            USERNAME = properties.getProperty("db.username");
            PASSWORD = properties.getProperty("db.password");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file", e);
        }
    }

    /**
     * Establishes and returns a connection to the database using the
     * configuration details loaded from the properties file. If the connection
     * fails, an error message is printed to the standard error stream.
     *
     * @return A {@link Connection} object representing the database connection,
     * or `null` if the connection could not be established.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connection established successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection.");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
        }
        return connection;
    }
}
