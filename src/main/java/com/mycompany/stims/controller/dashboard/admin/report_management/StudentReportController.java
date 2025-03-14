package com.mycompany.stims.controller.dashboard.admin.report_management;

import com.mycompany.stims.report.StudentReport;
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
 * The `StudentReportController` class manages the functionality for generating
 * student reports in the admin dashboard. It allows generating reports for a
 * specific student or for all students.
 */
public class StudentReportController implements Initializable {

    @FXML
    private VBox contentArea;

    @FXML
    private TextField studentIdNoField;

    @FXML
    private Button generateStudentReportButton;

    @FXML
    private Button generateAllStudentReportButton;

    /**
     * Initializes the controller after its root element has been completely
     * processed. This method sets up event handlers for the report generation
     * buttons.
     *
     * @param url The location used to resolve relative paths for the root
     * object, or `null` if the location is not known.
     * @param rb The resources used to localize the root object, or `null` if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up event handlers for the buttons
        generateStudentReportButton.setOnAction(event -> handleGenerateStudentReport());
        generateAllStudentReportButton.setOnAction(event -> handleGenerateAllStudentsReport());
    }

    /**
     * Handles the "Back" button action. Navigates back to the Report Management
     * screen.
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
     * Handles the "Generate Student Report" button action. Generates a report
     * for the student with the specified ID number.
     */
    private void handleGenerateStudentReport() {
        String studentIdNo = studentIdNoField.getText().trim();

        if (studentIdNo.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a Student ID Number.");
            return;
        }

        try {
            // Call the report generation method
            StudentReport.generateStudentReport(studentIdNo);
            showAlert(Alert.AlertType.INFORMATION, "Report Generated", "Student report generated successfully.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Student Not Found", e.getMessage());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Report Generation Failed", "An error occurred while generating the student report: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Report Generation Failed", "An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Handles the "Generate All Students Report" button action. Generates a
     * report for all students.
     */
    private void handleGenerateAllStudentsReport() {
        try {
            StudentReport.generateAllStudentsReport();
            showAlert(Alert.AlertType.INFORMATION, "Report Generated", "All students report generated successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Report Generation Failed", "An error occurred while generating the all students report: " + e.getMessage());
        }
    }

    /**
     * Displays an alert dialog with the specified type, title, and message.
     *
     * @param alertType The type of alert (e.g., ERROR, INFORMATION).
     * @param title The title of the alert.
     * @param message The message to display in the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
