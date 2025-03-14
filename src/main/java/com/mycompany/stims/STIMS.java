package com.mycompany.stims;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.database.AdminDAO;
import com.mycompany.stims.model.Admin;
import java.sql.Connection;
import java.util.Date;

/**
 * The `STIMS` class is the core application class for the Student Information
 * Management System (STIMS).
 */
public class STIMS {

    /**
     * The `main` method is the entry point for the application. It establishes
     * a database connection, creates an instance of `AdminDAO`, and adds a new
     * admin to the database.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Get a database connection
        Connection connection = DatabaseConnection.getConnection();

        if (connection != null) {
            // Create an instance of AdminDAO
            AdminDAO adminDAO = new AdminDAO(connection);

            // Create a new admin 
            Admin admin = new Admin();
            admin.setFirstName("FirstName"); // Example: "First Name"
            admin.setMiddleName("MiddleName"); // Example: "Middle Name"
            admin.setLastName("LastName"); // Example: "Last Name"
            admin.setSex("M");  // Sex M - Male , F - Female
            admin.setDateOfBirth(new Date()); // Example: Current date
            admin.setEmail("email@example.com"); // Example email
            admin.setPhoneNumber("0987654321"); // Example phone number
            admin.setAddress("Addis Ababa, Ethiopia"); // Example Address
            admin.setRoleId(1);/* +---------+-------------+
                                  | role_id | role_name   | 
                                  +---------+-------------+
                                  |       1 | Super Admin |
                                  |       2 | Admin       | 
                                  +---------+-------------+
             */
            // Add the admin to the database
            adminDAO.addAdmin(admin);

        } else {
            System.err.println("Database connection failed.");
        }
    }
}
