package com.mycompany.stims.controller.dashboard.admin.teacher_management;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.database.TeacherDAO;
import com.mycompany.stims.model.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Parent;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.scene.layout.StackPane;
import java.io.BufferedWriter;

/**
 * Controller class for adding new teachers in the admin dashboard. This class
 * handles the UI logic for registering new teachers, saving temporary
 * passwords, and navigating back to the teacher management screen.
 */
public class AddTeacherController implements Initializable {

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
    private ComboBox<String> departmentComboBox;
    @FXML
    private Button registerButton;
    @FXML
    private Button backButton;
    @FXML
    private AnchorPane contentArea;

    private Connection connection;
    private TeacherDAO teacherDAO;

    // File to store temporary passwords
    private static final String TEMP_PASSWORD_FILE = "temporary_passwords_teachers.txt";

    /**
     * Initializes the controller and sets up the UI components.
     *
     * @param url the location used to resolve relative paths for the root
     * object
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize database connection using DatabaseConnection
        connection = DatabaseConnection.getConnection();
        if (connection != null) {
            teacherDAO = new TeacherDAO(connection);
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Unable to connect to the database.");
        }

        // Initialize sex ComboBox
        ObservableList<String> sexOptions = FXCollections.observableArrayList("Male", "Female");
        sexComboBox.setItems(sexOptions);

        // Initialize department ComboBox
        initializeDepartmentComboBox();

        // Set up register button action
        registerButton.setOnAction(event -> registerTeacher());
        // Set up back button action
        backButton.setOnAction(event -> handleBackButton());
    }

    /**
     * Initializes the department ComboBox with department names and IDs from
     * the database.
     */
    private void initializeDepartmentComboBox() {
        try {
            String sql = "SELECT department_id, department_name FROM Department";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            ObservableList<String> departmentOptions = FXCollections.observableArrayList();
            while (rs.next()) {
                int departmentId = rs.getInt("department_id");
                String departmentName = rs.getString("department_name");
                departmentOptions.add(departmentId + " - " + departmentName);
            }
            departmentComboBox.setItems(departmentOptions);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load departments: " + e.getMessage());
        }
    }

    /**
     * Handles the "Register" button click event. Validates input fields and
     * registers a new teacher in the database.
     */
    private void registerTeacher() {
        // Validate input fields
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()
                || emailField.getText().isEmpty() || phoneField.getText().isEmpty()
                || addressField.getText().isEmpty() || dobPicker.getValue() == null
                || departmentComboBox.getValue() == null || sexComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All fields are required!");
            return;
        }

        int departmentId = Integer.parseInt(departmentComboBox.getValue().split(" - ")[0]);
        LocalDate localDate = dobPicker.getValue();
        java.sql.Date dateOfBirth = java.sql.Date.valueOf(localDate);

        // Create a new Teacher object
        Teacher teacher = new Teacher();
        teacher.setFirstName(firstNameField.getText());
        teacher.setMiddleName(middleNameField.getText());
        teacher.setLastName(lastNameField.getText());
        teacher.setSex(sexComboBox.getValue().equals("Male") ? "M" : "F");
        teacher.setDateOfBirth(dateOfBirth);
        teacher.setEmail(emailField.getText());
        teacher.setPhoneNumber(phoneField.getText());
        teacher.setAddress(addressField.getText());
        teacher.setDepartmentId(departmentId);

        // Add the teacher to the database
        try {
            String temporaryPassword = teacherDAO.addTeacher(teacher);
            saveTemporaryPasswordToFile(teacher.getTeacherId(), temporaryPassword); // Save to file
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Teacher registered successfully!\nTemporary Password: " + temporaryPassword);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to register teacher: " + e.getMessage());
        }
    }

    /**
     * Saves the temporary password for the teacher to a file.
     *
     * @param teacherId the ID of the registered teacher
     * @param temporaryPassword the temporary password generated for the teacher
     */
    private void saveTemporaryPasswordToFile(int teacherId, String temporaryPassword) {
        Path path = Paths.get(TEMP_PASSWORD_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            // Write the temporary password to the file
            writer.write("Teacher ID: " + teacherId + ", Temporary Password: " + temporaryPassword);
            writer.newLine();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Failed to save temporary password to file: " + e.getMessage());
        }
    }

    /**
     * Handles the "Back" button click event. Navigates back to the Teacher
     * Management screen.
     */
    @FXML
    private void handleBackButton() {
        try {
            // Load the Teacher Management FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/stims/fxml/dashboard/admin/teacher_management/Teacher_Management.fxml"));
            Parent teacherManagementPane = loader.load();

            StackPane parentContainer = (StackPane) contentArea.getParent();

            // Clear the existing content and add the new content
            parentContainer.getChildren().clear();
            parentContainer.getChildren().add(teacherManagementPane);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load Teacher Management screen: " + e.getMessage());
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
