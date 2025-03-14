package com.mycompany.stims.controller.dashboard.admin.report_management;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import com.mycompany.stims.report.ReportCardGenerator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

/**
 * The SemesterReportCardController class handles the generation of semester
 * report cards for students. It allows the user to input a student ID, select a
 * year and semester, and generate a report card.
 */
public class SemesterReportCardController implements Initializable {

    @FXML
    private VBox contentArea; // Container for the content area
    @FXML
    private TextField studentIdNoField; // TextField for entering student ID number
    @FXML
    private ComboBox<String> yearComboBox; // ComboBox for selecting the academic year
    @FXML
    private ComboBox<String> semesterComboBox; // ComboBox for selecting the semester

    /**
     * Initializes the controller class. Sets up the ComboBoxes with predefined
     * values.
     *
     * @param url The location used to resolve relative paths for the root
     * object, or null if unknown.
     * @param rb The resources used to localize the root object, or null if
     * none.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Populate the year ComboBox with values 1 through 10
        yearComboBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

        // Populate the semester ComboBox with values 1 and 2
        semesterComboBox.getItems().addAll("1", "2");
    }

    /**
     * Handles the action when the generate report card button is clicked.
     * Validates the input and generates a semester report card for the
     * specified student.
     */
    @FXML
    private void generateReportCard() {
        String studentIdNo = studentIdNoField.getText().trim();
        String yearInput = yearComboBox.getValue();
        String semesterInput = semesterComboBox.getValue();

        // Validate student ID
        if (studentIdNo.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter Student ID NO.");
            return;
        }

        // Validate year input
        if (yearInput == null) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please select a valid year.");
            return;
        }
        int year = Integer.parseInt(yearInput);

        // Validate semester input
        if (semesterInput == null) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please select a valid semester.");
            return;
        }
        int semester = Integer.parseInt(semesterInput);

        // Call the report generation method
        boolean isSuccess = ReportCardGenerator.generateReportCard(studentIdNo, year, semester);
        if (isSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Semester Report Card Generated", "Report generated successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Report Generation Failed", "An error occurred while generating the report.");
        }
    }

    /**
     * Handles the action when the back button is clicked. Navigates back to the
     * Report Management screen.
     */
    @FXML
    private void handleBackButton() {
        try {
            // Load the Report Management FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/stims/fxml/dashboard/admin/report_management/Report_Management.fxml"));
            Parent reportManagementPane = loader.load();

            // Get the parent container of the contentArea (dynamicContentContainer)
            StackPane parentContainer = (StackPane) contentArea.getParent();

            // Clear the existing content and add the new content
            parentContainer.getChildren().clear();
            parentContainer.getChildren().add(reportManagementPane);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load Report Management screen: " + e.getMessage());
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
