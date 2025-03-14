package com.mycompany.stims.controller.dashboard.admin.report_management;

import com.mycompany.stims.report.Transcript_Generator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Academic Transcript generation feature in the admin
 * dashboard. This class handles the generation of academic transcripts for
 * students based on their ID number and provides navigation back to the Report
 * Management screen.
 */
public class AcademicTranscriptController implements Initializable {

    /**
     * VBox container for the content area.
     */
    @FXML
    private VBox contentArea;

    /**
     * TextField for entering the student ID number.
     */
    @FXML
    private TextField studentIdNoField;

    /**
     * Initializes the controller class.
     *
     * @param url the location used to resolve relative paths for the root
     * object, or null if the location is not known
     * @param rb the resources used to localize the root object, or null if the
     * root object was not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization logic (if needed)
    }

    /**
     * Handles the action for generating an academic transcript. Validates the
     * input, calls the transcript generation method, and displays appropriate
     * alerts.
     */
    @FXML
    private void generateAcademicTranscript() {
        String studentIdNo = studentIdNoField.getText().trim();

        if (studentIdNo.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a Student ID Number.");
            return;
        }

        try {
            // Call the transcript generation method
            Transcript_Generator.generateTranscript(studentIdNo);
            showAlert(Alert.AlertType.INFORMATION, "Academic Transcript Generated", "Transcript generated successfully.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Student Not Found", e.getMessage());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Transcript Generation Failed", "An error occurred while generating the transcript: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Transcript Generation Failed", "An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Handles the action for navigating back to the Report Management screen.
     * Loads the Report Management FXML file and updates the UI.
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
     * Utility method to display alerts.
     *
     * @param alertType the type of alert (e.g., ERROR, WARNING, INFORMATION)
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
