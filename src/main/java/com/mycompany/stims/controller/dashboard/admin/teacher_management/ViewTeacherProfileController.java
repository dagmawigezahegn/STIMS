package com.mycompany.stims.controller.dashboard.admin.teacher_management;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.database.TeacherDAO;
import com.mycompany.stims.model.Teacher;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

/**
 * Controller class for the View Teacher Profile screen in the admin dashboard.
 * This class handles the display and management of teacher profiles, including
 * searching, viewing details, and navigating back to the teacher management
 * screen.
 */
public class ViewTeacherProfileController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<Teacher> teacherListView;

    @FXML
    private VBox teacherDetailsBox;

    @FXML
    private Label teacherIdLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label dobLabel;

    @FXML
    private Label departmentLabel;

    @FXML
    private AnchorPane contentArea;

    private TeacherDAO teacherDAO;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded. It sets up the database connection,
     * loads all teachers into the ListView, and configures event listeners.
     *
     * @param url The location used to resolve relative paths for the root
     * object, or null if unknown.
     * @param rb The resources used to localize the root object, or null if not
     * localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize database connection
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            teacherDAO = new TeacherDAO(connection);
        } else {
            System.out.println("Failed to connect to the database.");
        }

        // Load all teachers into the ListView
        loadTeachers();

        // Set up event listener for ListView selection
        teacherListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayTeacherDetails(newValue);
            }
        });

        // Customize ListView display
        teacherListView.setCellFactory(param -> new ListCell<Teacher>() {
            @Override
            protected void updateItem(Teacher teacher, boolean empty) {
                super.updateItem(teacher, empty);
                if (empty || teacher == null) {
                    setText(null);
                } else {
                    setText(teacher.getFirstName() + " " + teacher.getLastName() + " (" + teacher.getTeacherId() + ")");
                }
            }
        });
    }

    /**
     * Loads all teachers from the database and populates the ListView. If an
     * error occurs, an alert is shown to the user.
     */
    private void loadTeachers() {
        try {
            List<Teacher> teachers = teacherDAO.getAllTeachers();
            teacherListView.getItems().clear();
            teacherListView.getItems().addAll(teachers);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load teachers: " + e.getMessage());
        }
    }

    /**
     * Displays the details of the selected teacher in the details section.
     *
     * @param teacher The teacher whose details are to be displayed.
     */
    private void displayTeacherDetails(Teacher teacher) {
        // Fetch department name
        String departmentName = teacherDAO.getDepartmentNameById(teacher.getDepartmentId());

        // Build the full name including middle name (if available)
        StringBuilder fullName = new StringBuilder();
        fullName.append(teacher.getFirstName() != null ? teacher.getFirstName() : "");
        if (teacher.getMiddleName() != null && !teacher.getMiddleName().isEmpty()) {
            fullName.append(" ").append(teacher.getMiddleName());
        }
        fullName.append(" ").append(teacher.getLastName() != null ? teacher.getLastName() : "");

        // Display teacher details
        teacherIdLabel.setText("Teacher ID: " + (teacher.getTeacherId() != 0 ? teacher.getTeacherId() : "N/A"));
        nameLabel.setText("Name: " + fullName.toString().trim()); // Display full name including middle name
        emailLabel.setText("Email: " + (teacher.getEmail() != null ? teacher.getEmail() : "N/A"));
        phoneLabel.setText("Phone: " + (teacher.getPhoneNumber() != null ? teacher.getPhoneNumber() : "N/A"));
        addressLabel.setText("Address: " + (teacher.getAddress() != null ? teacher.getAddress() : "N/A"));
        dobLabel.setText("Date of Birth: " + (teacher.getDateOfBirth() != null ? teacher.getDateOfBirth() : "N/A"));
        departmentLabel.setText("Department: " + (departmentName != null ? departmentName : "N/A")); // Display department name
    }

    /**
     * Searches for teachers based on the search term entered in the search
     * field. The search is case-insensitive and matches against teacher ID,
     * first name, and last name. If the search term is empty, all teachers are
     * reloaded.
     */
    @FXML
    private void searchTeacher() {
        String searchTerm = searchField.getText().trim().toLowerCase(); // Convert to lowercase for case-insensitive search
        if (searchTerm.isEmpty()) {
            loadTeachers(); // Reload all teachers if the search term is empty
            return;
        }

        try {
            // Fetch all teachers from the database
            List<Teacher> allTeachers = teacherDAO.getAllTeachers();

            // Filter teachers based on the search term
            List<Teacher> filteredTeachers = allTeachers.stream()
                    .filter(teacher
                            -> (String.valueOf(teacher.getTeacherId()).toLowerCase().contains(searchTerm))
                    || (teacher.getFirstName() != null && teacher.getFirstName().toLowerCase().contains(searchTerm))
                    || (teacher.getLastName() != null && teacher.getLastName().toLowerCase().contains(searchTerm))
                    )
                    .collect(java.util.stream.Collectors.toList()); // Use collect(Collectors.toList()) instead of toList()

            // Update the ListView with the filtered teachers
            teacherListView.getItems().clear();
            teacherListView.getItems().addAll(filteredTeachers);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Search Error", "Failed to search teachers: " + e.getMessage());
        }
    }

    /**
     * Handles the action for the back button. Navigates back to the Teacher
     * Management screen. If an error occurs during navigation, an alert is
     * shown to the user.
     */
    @FXML
    private void handleBackButton() {
        try {
            // Load the Student Management FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/stims/fxml/dashboard/admin/teacher_management/Teacher_Management.fxml"));
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
     * Displays an alert dialog with the specified type, title, and message.
     *
     * @param alertType The type of alert (e.g., ERROR, INFORMATION).
     * @param title The title of the alert dialog.
     * @param message The message to be displayed in the alert dialog.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
