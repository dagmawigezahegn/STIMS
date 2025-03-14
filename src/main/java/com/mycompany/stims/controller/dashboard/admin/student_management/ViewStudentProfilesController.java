package com.mycompany.stims.controller.dashboard.admin.student_management;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.database.StudentDAO;
import com.mycompany.stims.model.Student;
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
 * Controller class for viewing and managing student profiles in the admin
 * dashboard. This class handles the UI logic for displaying student details,
 * searching for students, and navigating back to the student management screen.
 */
public class ViewStudentProfilesController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<Student> studentListView;

    @FXML
    private VBox studentDetailsBox;

    @FXML
    private Label studentIdLabel;

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
    private Label programLabel;

    @FXML
    private Label departmentLabel;

    @FXML
    private Label enrollmentDateLabel;

    @FXML
    private AnchorPane contentArea;

    private StudentDAO studentDAO;

    /**
     * Initializes the controller and sets up the UI components.
     *
     * @param url the location used to resolve relative paths for the root
     * object
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize database connection
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            studentDAO = new StudentDAO(connection);
        } else {
            System.out.println("Failed to connect to the database.");
        }

        // Load all students into the ListView
        loadStudents();

        // Add event listener for ListView selection
        studentListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayStudentDetails(newValue);
            }
        });

        // Customize ListView display
        studentListView.setCellFactory(param -> new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText(null);
                } else {
                    setText(student.getFirstName() + " " + student.getLastName() + " (" + student.getStudentIdNo() + ")");
                }
            }
        });
    }

    /**
     * Loads all students from the database and populates the ListView.
     */
    private void loadStudents() {
        try {
            List<Student> students = studentDAO.getAllStudents();
            studentListView.getItems().clear();
            studentListView.getItems().addAll(students);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load students: " + e.getMessage());
        }
    }

    /**
     * Displays the details of the selected student in the UI.
     *
     * @param student the selected student
     */
    private void displayStudentDetails(Student student) {
        // Fetch program and department names
        String programName = studentDAO.getProgramNameById(student.getProgramId());
        String departmentName = studentDAO.getDepartmentNameById(student.getDepartmentId());

        // Build the full name including middle name
        StringBuilder fullName = new StringBuilder();
        fullName.append(student.getFirstName() != null ? student.getFirstName() : "");
        if (student.getMiddleName() != null && !student.getMiddleName().isEmpty()) {
            fullName.append(" ").append(student.getMiddleName());
        }
        fullName.append(" ").append(student.getLastName() != null ? student.getLastName() : "");

        // Display student details
        studentIdLabel.setText("Student ID: " + (student.getStudentIdNo() != null ? student.getStudentIdNo() : "N/A"));
        nameLabel.setText("Name: " + fullName.toString().trim()); // Display full name including middle name
        emailLabel.setText("Email: " + (student.getEmail() != null ? student.getEmail() : "N/A"));
        phoneLabel.setText("Phone: " + (student.getPhoneNumber() != null ? student.getPhoneNumber() : "N/A"));
        addressLabel.setText("Address: " + (student.getAddress() != null ? student.getAddress() : "N/A"));
        dobLabel.setText("Date of Birth: " + (student.getDateOfBirth() != null ? student.getDateOfBirth() : "N/A"));
        programLabel.setText("Program: " + (programName != null ? programName : "N/A")); // Display program name
        departmentLabel.setText("Department: " + (departmentName != null ? departmentName : "N/A")); // Display department name
        enrollmentDateLabel.setText("Enrollment Date: " + (student.getEnrollmentDate() != null ? student.getEnrollmentDate() : "N/A"));
    }

    /**
     * Handles the search functionality for students. Filters the ListView based
     * on the search term entered in the search field.
     */
    @FXML
    private void searchStudent() {
        String searchTerm = searchField.getText().trim().toLowerCase(); // Convert to lowercase for case-insensitive search
        if (searchTerm.isEmpty()) {
            loadStudents(); // Reload all students if the search term is empty
            return;
        }

        try {
            // Fetch all students from the database
            List<Student> allStudents = studentDAO.getAllStudents();

            // Filter students based on the search term
            List<Student> filteredStudents = allStudents.stream()
                    .filter(student
                            -> (student.getStudentIdNo() != null && student.getStudentIdNo().toLowerCase().contains(searchTerm))
                    || (student.getFirstName() != null && student.getFirstName().toLowerCase().contains(searchTerm))
                    || (student.getLastName() != null && student.getLastName().toLowerCase().contains(searchTerm))
                    )
                    .collect(java.util.stream.Collectors.toList());

            // Update the ListView with the filtered students
            studentListView.getItems().clear();
            studentListView.getItems().addAll(filteredStudents);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Search Error", "Failed to search students: " + e.getMessage());
        }
    }

    /**
     * Handles the "Back" button click event. Navigates back to the Student
     * Management screen.
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
