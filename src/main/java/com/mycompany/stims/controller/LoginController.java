package com.mycompany.stims.controller;

import com.mycompany.stims.App;
import com.mycompany.stims.database.AdminDAO;
import com.mycompany.stims.database.TeacherDAO;
import com.mycompany.stims.database.StudentDAO;
import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;

/**
 * The LoginController class handles the login functionality for the
 * application. It authenticates users (teachers, admins, and students) and
 * navigates them to their respective dashboards.
 */
public class LoginController {

    @FXML
    private TextField emailField;       // TextField for entering email or username
    @FXML
    private PasswordField passwordField; // PasswordField for entering password

    private TeacherDAO teacherDAO;      // DAO for teacher-related database operations
    private AdminDAO adminDAO;          // DAO for admin-related database operations
    private StudentDAO studentDAO;      // DAO for student-related database operations

    private static final String TEACHER_DASHBOARD = "fxml/dashboard/teacher/Teacher_Dashboard"; // Path to teacher dashboard
    private static final String SUPER_ADMIN_DASHBOARD = "fxml/dashboard/super_admin/SuperAdmin_Dashboard"; // Path to super admin dashboard
    private static final String REGULAR_ADMIN_DASHBOARD = "fxml/dashboard/admin/Admin_Dashboard"; // Path to regular admin dashboard
    private static final String STUDENT_DASHBOARD = "fxml/dashboard/student/Student_Dashboard"; // Path to student dashboard

    /**
     * Default constructor for the LoginController class. Initializes the DAOs
     * using a database connection.
     */
    public LoginController() {
        // Use the DatabaseConnection class to get a connection
        Connection connection = DatabaseConnection.getConnection();

        if (connection != null) {
            teacherDAO = new TeacherDAO(connection);
            adminDAO = new AdminDAO(connection);
            studentDAO = new StudentDAO(connection);
        } else {
            System.err.println("Failed to initialize DAOs due to a database connection issue.");
            showAlert(Alert.AlertType.ERROR, "Database Error", "Unable to connect to the database.");
        }
    }

    /**
     * Handles the login process when the login button is clicked. Authenticates
     * the user based on the provided email/username and password.
     */
    @FXML
    private void handleLogin() {
        String emailOrUsername = emailField.getText();
        String password = passwordField.getText();

        if (emailOrUsername.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter both email/username and password.");
            return;
        }

        try {
            // Authenticate as Teacher
            if (teacherDAO != null && teacherDAO.authenticateTeacher(emailOrUsername, password) != null) {
                // Store the logged-in teacher's email or username in the Session
                Session.setLoggedInTeacherEmailOrUsername(emailOrUsername);

                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, Teacher!");
                navigateToDashboard(TEACHER_DASHBOARD);
                return;
            }

            // Authenticate as Admin
            if (adminDAO != null) {
                com.mycompany.stims.model.Admin admin = adminDAO.authenticateAdmin(emailOrUsername, password);
                if (admin != null) {
                    // Store the logged-in admin's email or username in the Session
                    Session.setLoggedInAdminEmailOrUsername(emailOrUsername);

                    // Check the role of the admin
                    int roleId = admin.getRoleId();
                    if (roleId == 1) { // Super Admin
                        showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, Super Admin!");
                        navigateToDashboard(SUPER_ADMIN_DASHBOARD);
                    } else if (roleId == 2) { // Regular Admin
                        showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, Admin!");
                        navigateToDashboard(REGULAR_ADMIN_DASHBOARD);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Login Failed", "Unknown role!");
                    }
                    return;
                }
            }

            // Authenticate as Student
            if (studentDAO != null && studentDAO.authenticateStudent(emailOrUsername, password) != null) {
                // Store the logged-in student's email or username in the Session
                Session.setLoggedInStudentEmailOrUsername(emailOrUsername);

                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, Student!");
                navigateToDashboard(STUDENT_DASHBOARD);
                return;
            }

            // If none of the above authentications succeed, show an error
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email/username or password!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred during login. Please try again.");
        }
    }

    /**
     * Navigates the user to the specified dashboard.
     *
     * @param dashboardPath the path to the dashboard FXML file
     */
    private void navigateToDashboard(String dashboardPath) {
        try {
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            App.setRoot(currentStage, dashboardPath);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the dashboard.");
        }
    }

    /**
     * Displays an alert dialog with the specified type, title, and message.
     *
     * @param alertType the type of alert (e.g., ERROR, INFORMATION)
     * @param title the title of the alert
     * @param message the message to display in the alert
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
