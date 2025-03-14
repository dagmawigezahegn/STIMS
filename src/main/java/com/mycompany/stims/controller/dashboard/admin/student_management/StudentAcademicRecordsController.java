package com.mycompany.stims.controller.dashboard.admin.student_management;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.database.StudentAcademicRecordsDAO;
import com.mycompany.stims.model.StudentAcademicRecord;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The StudentAcademicRecordsController class handles the management of student
 * academic records. It allows the user to load, add, and update academic
 * records for a specific student.
 */
public class StudentAcademicRecordsController implements Initializable {

    // FXML Components
    @FXML
    private AnchorPane contentArea; // Container for the content area
    @FXML
    private TextField studentIdNoField; // TextField for entering student ID number
    @FXML
    private TableView<StudentAcademicRecord> academicRecordsTable; // Table to display academic records
    @FXML
    private TableColumn<StudentAcademicRecord, Integer> academicYearColumn; // Column for academic year
    @FXML
    private TableColumn<StudentAcademicRecord, Integer> yearColumn; // Column for year
    @FXML
    private TableColumn<StudentAcademicRecord, Integer> semesterColumn; // Column for semester
    @FXML
    private TableColumn<StudentAcademicRecord, Integer> totalCreditsColumn; // Column for total credits
    @FXML
    private TableColumn<StudentAcademicRecord, Double> sgpaColumn; // Column for SGPA
    @FXML
    private Label cgpaLabel; // Label to display CGPA
    @FXML
    private TextField academicYearField; // TextField for entering academic year
    @FXML
    private ComboBox<Integer> yearField; // ComboBox for selecting year
    @FXML
    private ComboBox<Integer> semesterField; // ComboBox for selecting semester
    @FXML
    private Button loadRecordsButton; // Button to load academic records
    @FXML
    private Button addRecordButton; // Button to add a new academic record
    @FXML
    private Button updateRecordButton; // Button to update an existing academic record
    @FXML
    private Button backButton; // Button to navigate back

    private StudentAcademicRecordsDAO academicRecordsDAO; // DAO for student academic records

    /**
     * Initializes the controller class. Sets up the table columns, ComboBoxes,
     * and event handlers.
     *
     * @param url The location used to resolve relative paths for the root
     * object, or null if unknown.
     * @param rb The resources used to localize the root object, or null if
     * none.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize DAO with database connection
        Connection connection = DatabaseConnection.getConnection();
        academicRecordsDAO = new StudentAcademicRecordsDAO(connection);

        // Set up table columns
        academicYearColumn.setCellValueFactory(new PropertyValueFactory<>("academicYear"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        totalCreditsColumn.setCellValueFactory(new PropertyValueFactory<>("totalCredits"));
        sgpaColumn.setCellValueFactory(new PropertyValueFactory<>("sgpa"));

        // Populate ComboBoxes
        yearField.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9);
        semesterField.getItems().addAll(1, 2);

        // Add event handlers
        loadRecordsButton.setOnAction(event -> loadRecords());
        addRecordButton.setOnAction(event -> addRecord());
        updateRecordButton.setOnAction(event -> updateRecord());
        backButton.setOnAction(event -> handleBackButton());
    }

    /**
     * Loads academic records for the specified student ID number.
     */
    private void loadRecords() {
        String studentIdNo = studentIdNoField.getText().trim();

        if (studentIdNo.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a Student ID No.");
            return;
        }

        try {
            // Fetch student_id from studentId_No
            int studentId = academicRecordsDAO.getStudentIdByStudentIdNo(studentIdNo);

            // Fetch records from the database
            List<StudentAcademicRecord> records = academicRecordsDAO.getAcademicRecordsByStudentId(studentId);

            // Clear the table and populate it with the fetched records
            academicRecordsTable.getItems().clear();
            academicRecordsTable.getItems().addAll(records);

            // Display CGPA
            double cgpa = academicRecordsDAO.calculateCGPA(studentId);
            cgpaLabel.setText(String.format("%.2f", cgpa));

            if (records.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Records", "No academic records found for the given Student ID No.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load academic records: " + e.getMessage());
        }
    }

    /**
     * Adds a new academic record for the specified student.
     */
    private void addRecord() {
        if (!validateInputs()) {
            return;
        }

        try {
            String studentIdNo = studentIdNoField.getText().trim();
            int studentId = academicRecordsDAO.getStudentIdByStudentIdNo(studentIdNo);

            int academicYear = Integer.parseInt(academicYearField.getText().trim());
            int year = yearField.getValue();
            int semester = semesterField.getValue();

            // Check if the student already has a record for the given academic year, year, and semester
            List<StudentAcademicRecord> records = academicRecordsDAO.getAcademicRecordsByStudentId(studentId);
            boolean recordExists = records.stream()
                    .anyMatch(record -> record.getAcademicYear() == academicYear
                    && record.getYear() == year
                    && record.getSemester() == semester);

            if (recordExists) {
                showAlert(Alert.AlertType.WARNING, "Record Exists", "A record already exists for the given academic year, year, and semester.");
                return;
            }

            // Add the record using the DAO
            academicRecordsDAO.updateStudentAcademicRecord(studentId, academicYear, year, semester);

            // Check if the record was actually added
            records = academicRecordsDAO.getAcademicRecordsByStudentId(studentId);
            recordExists = records.stream()
                    .anyMatch(record -> record.getAcademicYear() == academicYear
                    && record.getYear() == year
                    && record.getSemester() == semester);

            if (recordExists) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Academic record added successfully. SGPA and CGPA have been calculated.");
            } else {
                showAlert(Alert.AlertType.WARNING, "No Courses Found", "No courses found for the given academic year, year, and semester. No record was added.");
            }

            // Refresh the table
            loadRecords();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numeric values.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add academic record: " + e.getMessage());
        }
    }

    /**
     * Updates the selected academic record.
     */
    private void updateRecord() {
        StudentAcademicRecord selectedRecord = academicRecordsTable.getSelectionModel().getSelectedItem();
        if (selectedRecord == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a record from the table to update.");
            return;
        }

        try {
            int studentId = selectedRecord.getStudentId();
            int academicYear = selectedRecord.getAcademicYear();
            int year = selectedRecord.getYear();
            int semester = selectedRecord.getSemester();

            // Update the record using the DAO
            academicRecordsDAO.updateStudentAcademicRecord(studentId, academicYear, year, semester);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Academic record updated successfully. SGPA and CGPA have been recalculated.");

            // Refresh the table
            loadRecords();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update academic record: " + e.getMessage());
        }
    }

    /**
     * Validates the input fields for adding or updating an academic record.
     *
     * @return true if the inputs are valid, otherwise false
     */
    private boolean validateInputs() {
        if (studentIdNoField.getText().trim().isEmpty() || academicYearField.getText().trim().isEmpty()
                || yearField.getValue() == null || semesterField.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in all fields.");
            return false;
        }

        try {
            int academicYear = Integer.parseInt(academicYearField.getText().trim());

            if (academicYear < 1900 || academicYear > 2100) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Academic year must be between 1900 and 2100.");
                return false;
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numeric values.");
            return false;
        }

        return true;
    }

    /**
     * Handles the action when the back button is clicked. Navigates back to the
     * Student Management screen.
     */
    @FXML
    private void handleBackButton() {
        try {
            // Load the Student Management FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/stims/fxml/dashboard/admin/student_management/Student_Management.fxml"));
            Parent studentManagementPane = loader.load();

            StackPane parentContainer = (StackPane) contentArea.getParent();

            // Clear the existing content and add the new content
            parentContainer.getChildren().clear();
            parentContainer.getChildren().add(studentManagementPane);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load Student Management screen: " + e.getMessage());
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
