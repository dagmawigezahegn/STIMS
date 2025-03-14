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
import javafx.scene.layout.GridPane;
import javafx.scene.Parent;
import javafx.geometry.Insets;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.scene.layout.StackPane;

/**
 * The EditTeacherController class handles the editing of teacher information.
 * It allows the user to search for a teacher by ID, update their details, or
 * delete their record.
 */
public class EditTeacherController implements Initializable {

    @FXML
    private TextField teacherIdField; // TextField for entering teacher ID
    @FXML
    private Button searchButton; // Button to search for a teacher
    @FXML
    private Button backButton; // Button to navigate back

    @FXML
    private AnchorPane contentArea; // Container for the content area

    private TeacherDAO teacherDAO; // DAO for teacher-related database operations
    private Teacher currentTeacher; // Current teacher being edited
    private Connection connection; // Database connection

    /**
     * Initializes the controller class. Sets up the database connection and
     * event handlers.
     *
     * @param url The location used to resolve relative paths for the root
     * object, or null if unknown.
     * @param rb The resources used to localize the root object, or null if
     * none.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connection = DatabaseConnection.getConnection();
        if (connection != null) {
            teacherDAO = new TeacherDAO(connection);
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Unable to connect to the database.");
        }

        // Set up event handlers
        searchButton.setOnAction(event -> searchTeacher());
        backButton.setOnAction(event -> handleBackButton());
    }

    /**
     * Searches for a teacher by ID and displays their details in a dialog for
     * editing.
     */
    @FXML
    private void searchTeacher() {
        String teacherIdStr = teacherIdField.getText().trim();
        if (teacherIdStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a Teacher ID.");
            return;
        }

        try {
            int teacherId = Integer.parseInt(teacherIdStr);
            // Fetch the teacher from the database
            currentTeacher = teacherDAO.getTeacher(teacherId);
            if (currentTeacher == null) {
                showAlert(Alert.AlertType.WARNING, "Not Found", "No teacher found with the given ID.");
                return;
            }

            // Create a custom dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Teacher");
            dialog.setHeaderText("Edit Teacher Information");

            // Set the dialog content
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL, new ButtonType("Delete", ButtonBar.ButtonData.OTHER));

            // Create a GridPane for the form fields
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            TextField userNameField = new TextField(currentTeacher.getUsername());
            TextField firstNameField = new TextField(currentTeacher.getFirstName());
            TextField middleNameField = new TextField(currentTeacher.getMiddleName());
            TextField lastNameField = new TextField(currentTeacher.getLastName());

            ComboBox<String> sexComboBox = new ComboBox<>();
            ObservableList<String> sexOptions = FXCollections.observableArrayList("Male", "Female");
            sexComboBox.setItems(sexOptions);
            sexComboBox.setValue(currentTeacher.getSex().equals("M") ? "Male" : "Female");

            TextField emailField = new TextField(currentTeacher.getEmail());
            TextField phoneField = new TextField(currentTeacher.getPhoneNumber());
            TextField addressField = new TextField(currentTeacher.getAddress());

            java.sql.Date dateOfBirth = (java.sql.Date) currentTeacher.getDateOfBirth();
            LocalDate localDate = dateOfBirth.toLocalDate();
            DatePicker dobPicker = new DatePicker(localDate);

            ComboBox<String> departmentComboBox = new ComboBox<>();
            initializeDepartmentComboBox(departmentComboBox);
            departmentComboBox.setValue(currentTeacher.getDepartmentId() + " - " + teacherDAO.getDepartmentNameById(currentTeacher.getDepartmentId()));

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
            grid.add(new Label("Department:"), 0, 9);
            grid.add(departmentComboBox, 1, 9);

            dialogPane.setContent(grid);

            // Show the dialog and wait for user input
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Update the teacher object with the new values
                    currentTeacher.setUsername(userNameField.getText().trim());
                    currentTeacher.setFirstName(firstNameField.getText().trim());
                    currentTeacher.setMiddleName(middleNameField.getText().trim());
                    currentTeacher.setLastName(lastNameField.getText().trim());
                    currentTeacher.setSex(sexComboBox.getValue().equals("Male") ? "M" : "F");
                    currentTeacher.setEmail(emailField.getText().trim());
                    currentTeacher.setPhoneNumber(phoneField.getText().trim());
                    currentTeacher.setAddress(addressField.getText().trim());
                    currentTeacher.setDateOfBirth(java.sql.Date.valueOf(dobPicker.getValue())); // Convert LocalDate back to java.sql.Date
                    currentTeacher.setDepartmentId(Integer.parseInt(departmentComboBox.getValue().split(" - ")[0]));

                    // Save the updated teacher to the database
                    try {
                        teacherDAO.updateTeacher(currentTeacher);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Teacher details updated successfully.");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to update teacher: " + e.getMessage());
                    }
                } else if (response.getButtonData() == ButtonBar.ButtonData.OTHER) {
                    // Handle the Delete
                    try {
                        teacherDAO.deleteTeacher(currentTeacher.getTeacherId());
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Teacher deleted successfully.");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete teacher: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to search for teacher: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initializes the department ComboBox with department names and IDs.
     *
     * @param departmentComboBox the ComboBox to initialize
     */
    private void initializeDepartmentComboBox(ComboBox<String> departmentComboBox) {
        try {
            String sql = "SELECT department_id, department_name FROM Department";
            PreparedStatement pstmt = connection.prepareStatement(sql); // Use the connection object
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
     * Handles the action when the back button is clicked. Navigates back to the
     * Teacher Management screen.
     */
    @FXML
    private void handleBackButton() {
        try {
            // Load the Teacher Management FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/stims/fxml/dashboard/admin/teacher_management/Teacher_Management.fxml"));
            Parent teacherManagementPane = loader.load();

            // Get the parent container of the contentArea (dynamicContentContainer)
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
