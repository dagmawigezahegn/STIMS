package com.mycompany.stims.controller.dashboard.admin.report_management;

import com.mycompany.stims.report.CourseReport;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;

/**
 * Controller class for managing course reports in the admin dashboard. This
 * class handles the UI logic for generating course-specific reports, all
 * courses reports, and course statistics reports.
 */
public class CourseReportController implements Initializable {

    @FXML
    private VBox contentArea;

    @FXML
    private TextField courseCodeField;
    @FXML
    private Button generateCourseReportButton;
    @FXML
    private Button generateAllCourseReportButton;
    @FXML
    private Button generateCourseStatisticsButton;

    /**
     * Initializes the controller and sets up event handlers for the buttons.
     *
     * @param url the location used to resolve relative paths for the root
     * object
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up event handlers for the buttons
        generateCourseReportButton.setOnAction(event -> handleGenerateCourseReport());
        generateAllCourseReportButton.setOnAction(event -> handleGenerateAllCoursesReport());
        generateCourseStatisticsButton.setOnAction(event -> handleGenerateCourseStatistics());
    }

    /**
     * Handles the "Back" button click event. Navigates back to the Report
     * Management screen.
     */
    @FXML
    private void handleBackButton() {
        try {
            // Load the Student Management FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/stims/fxml/dashboard/admin/report_management/Report_Management.fxml"));
            Parent studentManagementPane = loader.load();

            // Get the parent container of the contentArea (dynamicContentContainer)
            StackPane parentContainer = (StackPane) contentArea.getParent();

            // Clear the existing content and add the new content
            parentContainer.getChildren().clear();
            parentContainer.getChildren().add(studentManagementPane);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load Student Management screen: " + e.getMessage());
        }
    }

    /**
     * Handles the "Generate Course Report" button click event. Validates input
     * and generates a report for the specified course.
     */
    private void handleGenerateCourseReport() {
        String courseCode = courseCodeField.getText().trim();

        if (courseCode.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a Course Code.");
            return;
        }

        try {
            // Call the report generation method
            CourseReport.generateCourseReport(courseCode);
            showAlert(Alert.AlertType.INFORMATION, "Report Generated", "Course report generated successfully.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Course Not Found", e.getMessage());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Report Generation Failed", "An error occurred while generating the course report: " + e.getMessage());
        }
    }

    /**
     * Handles the "Generate All Courses Report" button click event. Generates a
     * report for all courses.
     */
    private void handleGenerateAllCoursesReport() {
        try {
            CourseReport.generateAllCoursesReport();
            showAlert(Alert.AlertType.INFORMATION, "Report Generated", "All courses report generated successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Report Generation Failed", "An error occurred while generating the all courses report: " + e.getMessage());
        }
    }

    /**
     * Handles the "Generate Course Statistics" button click event. Generates a
     * report containing course statistics.
     */
    private void handleGenerateCourseStatistics() {
        try {
            CourseReport.generateCourseStatistics();
            showAlert(Alert.AlertType.INFORMATION, "Report Generated", "Course statistics report generated successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Report Generation Failed", "An error occurred while generating the course statistics report: " + e.getMessage());
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
