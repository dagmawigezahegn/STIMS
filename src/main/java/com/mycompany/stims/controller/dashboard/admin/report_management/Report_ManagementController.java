package com.mycompany.stims.controller.dashboard.admin.report_management;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import com.mycompany.stims.database.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

/**
 * The Report_ManagementController class manages the report management interface
 * for administrators. It displays the total number of students, teachers, and
 * courses, and allows navigation to various report-related screens.
 */
public class Report_ManagementController implements Initializable {

    @FXML
    private Label totalStudents;
    @FXML
    private Label totalTeachers;
    @FXML
    private Label totalCourses;

    @FXML
    private AnchorPane contentArea;
    @FXML
    private StackPane dynamicContentContainer;

    /**
     * Initializes the controller class. Loads the total number of students,
     * teachers, and courses from the database and displays them in the UI.
     *
     * @param url the location used to resolve relative paths for the root
     * object
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadTotalTeachers();
        loadTotalCourses();
        loadTotalStudents();
    }

    /**
     * Loads the total number of teachers from the database and updates the UI.
     */
    private void loadTotalTeachers() {
        String sql = "SELECT COUNT(*) AS total FROM teacher"; // Adjust the table name if necessary

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                int total = rs.getInt("total");
                totalTeachers.setText(String.valueOf(total));
            } else {
                totalTeachers.setText("0"); // Default value if no result is found
            }
        } catch (Exception e) {
            System.err.println("Failed to load total teachers: " + e.getMessage());
            e.printStackTrace();
            totalTeachers.setText("0"); // Set a default value in case of an error
        }
    }

    /**
     * Loads the total number of courses from the database and updates the UI.
     */
    private void loadTotalCourses() {
        String sql = "SELECT COUNT(*) AS total FROM course";

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                int total = rs.getInt("total");
                totalCourses.setText(String.valueOf(total));
            } else {
                totalCourses.setText("0"); // Default value if no result is found
            }
        } catch (Exception e) {
            System.err.println("Failed to load total courses: " + e.getMessage());
            e.printStackTrace();
            totalCourses.setText("0"); // Set a default value in case of an error
        }
    }

    /**
     * Loads the total number of students from the database and updates the UI.
     */
    private void loadTotalStudents() {
        String sql = "SELECT COUNT(*) AS total FROM student"; // Adjust the table name if necessary

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                int total = rs.getInt("total");
                totalStudents.setText(String.valueOf(total));
            } else {
                totalStudents.setText("0"); // Default value if no result is found
            }
        } catch (Exception e) {
            System.err.println("Failed to load total students: " + e.getMessage());
            e.printStackTrace();
            totalStudents.setText("0"); // Set a default value in case of an error
        }
    }

    /**
     * Loads the Student Report screen.
     */
    @FXML
    public void showStudentReport() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/report_management/Student_Report.fxml");
    }

    /**
     * Loads the Course Report screen.
     */
    @FXML
    public void showCourseReport() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/report_management/Course_Report.fxml");
    }

    /**
     * Loads the Semester Report Card screen.
     */
    @FXML
    public void showSemesterReportCard() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/report_management/Semester_Report_Card.fxml");
    }

    /**
     * Loads the Academic Transcript screen.
     */
    @FXML
    public void showAcademicTranscript() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/report_management/Academic_Transcript.fxml");
    }

    /**
     * Loads a new UI screen into the dynamic content container.
     *
     * @param fxmlPath the path to the FXML file for the new UI screen
     */
    private void loadUI(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newContent = loader.load();

            // Clear the previous content
            dynamicContentContainer.getChildren().clear();

            // Add the new content
            dynamicContentContainer.getChildren().add(newContent);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle loading errors
        }
    }
}
