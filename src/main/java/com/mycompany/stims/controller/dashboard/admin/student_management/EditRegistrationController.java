package com.mycompany.stims.controller.dashboard.admin.student_management;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.database.StudentDAO;
import com.mycompany.stims.model.Student;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.Parent;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for the Edit Registration feature in the admin dashboard. This
 * class allows administrators to search for students by their ID number, edit
 * their registration details, and delete student records.
 */
public class EditRegistrationController implements Initializable {

    // FXML Components
    /**
     * TextField for entering the student ID number.
     */
    @FXML
    private TextField studentIdNoField;

    /**
     * Button to initiate the search for a student.
     */
    @FXML
    private Button searchButton;

    /**
     * Button to navigate back to the Student Management screen.
     */
    @FXML
    private Button backButton;

    /**
     * AnchorPane used as a container for the content area.
     */
    @FXML
    private AnchorPane contentArea;

    /**
     * Database connection instance.
     */
    private Connection connection;

    /**
     * Data Access Object (DAO) for student-related database operations.
     */
    private StudentDAO studentDAO;

    /**
     * The currently selected student for editing.
     */
    private Student currentStudent;

    /**
     * Map to store program names and their corresponding IDs.
     */
    private Map<String, Integer> programMap = new HashMap<>();

    /**
     * Map to store department names and their corresponding IDs.
     */
    private Map<String, Integer> departmentMap = new HashMap<>();

    /**
     * Initializes the controller class. Sets up the database connection, loads
     * program and department data, and configures event handlers for buttons.
     *
     * @param url the location used to resolve relative paths for the root
     * object, or null if the location is not known
     * @param rb the resources used to localize the root object, or null if the
     * root object was not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connection = DatabaseConnection.getConnection();
        if (connection != null) {
            studentDAO = new StudentDAO(connection);
            loadPrograms();
            loadDepartments();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Unable to connect to the database.");
        }

        // Set up event handlers
        searchButton.setOnAction(event -> searchStudent());
        backButton.setOnAction(event -> handleBackButton());
    }

    /**
     * Loads program data from the database into the programMap.
     */
    private void loadPrograms() {
        String sql = "SELECT program_id, program_name FROM Program";
        try (var pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int programId = rs.getInt("program_id");
                String programName = rs.getString("program_name");
                programMap.put(programName, programId);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load programs: " + e.getMessage());
        }
    }

    /**
     * Loads department data from the database into the departmentMap.
     */
    private void loadDepartments() {
        String sql = "SELECT department_id, department_name FROM Department";
        try (var pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int departmentId = rs.getInt("department_id");
                String departmentName = rs.getString("department_name");
                departmentMap.put(departmentName, departmentId);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load departments: " + e.getMessage());
        }
    }

    /**
     * Handles the action for searching a student by their ID number. Displays a
     * dialog to edit the student's details if found.
     */
    @FXML
    private void searchStudent() {
        String studentIdNo = studentIdNoField.getText().trim();
        if (studentIdNo.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a Student ID No.");
            return;
        }

        try {
            // Fetch the student from the database
            currentStudent = studentDAO.getStudentByIdNo(studentIdNo);
            if (currentStudent == null) {
                showAlert(Alert.AlertType.WARNING, "Not Found", "No student found with the given ID.");
                return;
            }

            // Create a custom dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Student");
            dialog.setHeaderText("Edit Student Information");

            // Set the dialog content
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL, new ButtonType("Delete", ButtonBar.ButtonData.OTHER));

            // Create a GridPane for the form fields
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            // Add form fields
            TextField userNameField = new TextField(currentStudent.getUsername());
            TextField firstNameField = new TextField(currentStudent.getFirstName());
            TextField middleNameField = new TextField(currentStudent.getMiddleName());
            TextField lastNameField = new TextField(currentStudent.getLastName());

            ComboBox<String> sexComboBox = new ComboBox<>();
            sexComboBox.getItems().addAll("Male", "Female");
            sexComboBox.setValue(currentStudent.getSex().equals("M") ? "Male" : "Female");

            TextField emailField = new TextField(currentStudent.getEmail());
            TextField phoneField = new TextField(currentStudent.getPhoneNumber());
            TextField addressField = new TextField(currentStudent.getAddress());
            DatePicker dobPicker = new DatePicker(currentStudent.getDateOfBirth());

            ComboBox<String> programComboBox = new ComboBox<>();
            programComboBox.getItems().addAll(programMap.keySet());
            programComboBox.setValue(studentDAO.getProgramNameById(currentStudent.getProgramId()));

            ComboBox<String> departmentComboBox = new ComboBox<>();
            departmentComboBox.getItems().addAll(departmentMap.keySet());
            departmentComboBox.setValue(studentDAO.getDepartmentNameById(currentStudent.getDepartmentId()));

            DatePicker enrollmentDatePicker = new DatePicker(currentStudent.getEnrollmentDate());

            grid.add(new Label("Username:"), 0, 0);
            grid.add(userNameField, 1, 0);
            grid.add(new Label("First Name:"), 0, 1);
            grid.add(firstNameField, 1, 1);
            grid.add(new Label("Middle Name:"), 0, 2);
            grid.add(middleNameField, 1, 2);
            grid.add(new Label("Last Name:"), 0, 3);
            grid.add(lastNameField, 1, 3);
            grid.add(new Label("Sex:"), 0, 4);
            grid.add(sexComboBox, 1, 4);
            grid.add(new Label("Email:"), 0, 5);
            grid.add(emailField, 1, 5);
            grid.add(new Label("Phone Number:"), 0, 6);
            grid.add(phoneField, 1, 6);
            grid.add(new Label("Address:"), 0, 7);
            grid.add(addressField, 1, 7);
            grid.add(new Label("Date of Birth:"), 0, 8);
            grid.add(dobPicker, 1, 8);
            grid.add(new Label("Program:"), 0, 9);
            grid.add(programComboBox, 1, 9);
            grid.add(new Label("Department:"), 0, 10);
            grid.add(departmentComboBox, 1, 10);
            grid.add(new Label("Enrollment Date:"), 0, 11);
            grid.add(enrollmentDatePicker, 1, 11);

            dialogPane.setContent(grid);

            // Show the dialog and wait for user input
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Update the student object with the new values
                    currentStudent.setUsername(userNameField.getText().trim());
                    currentStudent.setFirstName(firstNameField.getText().trim());
                    currentStudent.setMiddleName(middleNameField.getText().trim());
                    currentStudent.setLastName(lastNameField.getText().trim());
                    currentStudent.setSex(sexComboBox.getValue().equals("Male") ? "M" : "F");
                    currentStudent.setEmail(emailField.getText().trim());
                    currentStudent.setPhoneNumber(phoneField.getText().trim());
                    currentStudent.setAddress(addressField.getText().trim());
                    currentStudent.setDateOfBirth(dobPicker.getValue());
                    currentStudent.setProgramId(programMap.get(programComboBox.getValue()));
                    currentStudent.setDepartmentId(departmentMap.get(departmentComboBox.getValue()));
                    currentStudent.setEnrollmentDate(enrollmentDatePicker.getValue());

                    // Save the updated student to the database
                    try {
                        studentDAO.updateStudent(currentStudent);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Student details updated successfully.");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to update student: " + e.getMessage());
                    }
                } else if (response.getButtonData() == ButtonBar.ButtonData.OTHER) {
                    // Handle the "Delete" button click
                    try {
                        studentDAO.deleteStudent(currentStudent.getStudentIdNo());
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Student deleted successfully.");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete student: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to search for student: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the action for navigating back to the Student Management screen.
     * Loads the Student Management FXML file and updates the UI.
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
