package com.mycompany.stims.controller.dashboard.admin.student_management;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.database.StudentDAO;
import com.mycompany.stims.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * The `RegisterStudentController` class manages the functionality for
 * registering new students in the admin dashboard. It allows admins to input
 * student details, select programs and departments, and generate temporary
 * passwords for new students.
 */
public class RegisterStudentController implements Initializable {

    // FXML Components
    @FXML
    private TextField firstNameField;

    @FXML
    private TextField middleNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private ComboBox<String> sexComboBox;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private ComboBox<String> programComboBox;

    @FXML
    private ComboBox<String> departmentComboBox;

    @FXML
    private DatePicker enrollmentDatePicker;

    @FXML
    private Button registerButton;

    @FXML
    private Button backButton;

    @FXML
    private AnchorPane contentArea;

    // Database connection and DAO
    private Connection connection;
    private StudentDAO studentDAO;

    private Map<String, Integer> programMap = new HashMap<>();
    private Map<String, Integer> departmentMap = new HashMap<>();

    // File to store temporary passwords
    private static final String TEMP_PASSWORD_FILE = "temporary_passwords_students.txt";

    /**
     * Initializes the controller after its root element has been completely
     * processed. This method sets up the database connection, loads program and
     * department options, and configures button actions.
     *
     * @param url The location used to resolve relative paths for the root
     * object, or `null` if the location is not known.
     * @param rb The resources used to localize the root object, or `null` if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connection = DatabaseConnection.getConnection();
        if (connection != null) {
            studentDAO = new StudentDAO(connection);
            loadPrograms();
            loadDepartments();
            loadSexOptions();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Unable to connect to the database.");
        }

        // Set up register button action
        registerButton.setOnAction(event -> registerStudent());

        // Set up back button action
        backButton.setOnAction(event -> handleBackButton());
    }

    /**
     * Loads the sex options ("Male" and "Female") into the sex combo box.
     */
    private void loadSexOptions() {
        ObservableList<String> sexOptions = FXCollections.observableArrayList("Male", "Female");
        sexComboBox.setItems(sexOptions);
    }

    /**
     * Loads the available programs from the database into the program combo
     * box.
     */
    private void loadPrograms() {
        String sql = "SELECT program_id, program_name FROM Program";
        try (var pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            ObservableList<String> programNames = FXCollections.observableArrayList();
            while (rs.next()) {
                int programId = rs.getInt("program_id");
                String programName = rs.getString("program_name");
                programMap.put(programName, programId);
                programNames.add(programName);
            }
            programComboBox.setItems(programNames);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load programs: " + e.getMessage());
        }
    }

    /**
     * Loads the available departments from the database into the department
     * combo box.
     */
    private void loadDepartments() {
        String sql = "SELECT department_id, department_name FROM Department";
        try (var pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            ObservableList<String> departmentNames = FXCollections.observableArrayList();
            while (rs.next()) {
                int departmentId = rs.getInt("department_id");
                String departmentName = rs.getString("department_name");
                departmentMap.put(departmentName, departmentId);
                departmentNames.add(departmentName);
            }
            departmentComboBox.setItems(departmentNames);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load departments: " + e.getMessage());
        }
    }

    /**
     * Handles the "Register" button action. Validates input fields, creates a
     * new `Student` object, and registers the student in the database. A
     * temporary password is generated and saved to a file.
     */
    private void registerStudent() {
        // Validate input fields
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()
                || emailField.getText().isEmpty() || phoneField.getText().isEmpty()
                || addressField.getText().isEmpty() || dobPicker.getValue() == null
                || programComboBox.getValue() == null || departmentComboBox.getValue() == null
                || enrollmentDatePicker.getValue() == null || sexComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All fields are required!");
            return;
        }

        // Get selected program and department IDs
        int programId = programMap.get(programComboBox.getValue());
        int departmentId = departmentMap.get(departmentComboBox.getValue());

        // Map sex to "M" or "F"
        String sex = sexComboBox.getValue().equals("Male") ? "M" : "F";

        // Create a new Student object
        Student student = new Student(
                firstNameField.getText(),
                middleNameField.getText(),
                lastNameField.getText(),
                emailField.getText(),
                phoneField.getText(),
                addressField.getText(),
                dobPicker.getValue(),
                programId,
                departmentId,
                sex,
                enrollmentDatePicker.getValue()
        );

        // Add the student to the database
        try {
            String temporaryPassword = studentDAO.addStudent(student);
            saveTemporaryPasswordToFile(student.getStudentIdNo(), temporaryPassword);
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Student registered successfully!\nTemporary Password: " + temporaryPassword);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to register student: " + e.getMessage());
        }
    }

    /**
     * Saves the temporary password for a student to a file.
     *
     * @param studentIdNo The ID of the student.
     * @param temporaryPassword The temporary password generated for the
     * student.
     */
    private void saveTemporaryPasswordToFile(String studentIdNo, String temporaryPassword) {
        Path path = Paths.get(TEMP_PASSWORD_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write("Student ID: " + studentIdNo + ", Temporary Password: " + temporaryPassword);
            writer.newLine();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Failed to save temporary password to file: " + e.getMessage());
        }
    }

    /**
     * Handles the "Back" button action. Navigates back to the Student
     * Management screen.
     */
    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/stims/fxml/dashboard/admin/student_management/Student_Management.fxml"));
            Parent studentManagementPane = loader.load();
            StackPane parentContainer = (StackPane) contentArea.getParent();
            parentContainer.getChildren().clear();
            parentContainer.getChildren().add(studentManagementPane);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load Student Management screen: " + e.getMessage());
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
