package com.mycompany.stims.controller.dashboard.admin.course_management;

import com.mycompany.stims.model.StudentCourse;
import com.mycompany.stims.database.StudentCourseDAO;
import com.mycompany.stims.database.CourseOfferingDAO;
import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.model.CourseOffering;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * The EnrollStudentsController class handles the enrollment of students into
 * course offerings. It provides functionality to add, update, delete, and view
 * student-course enrollments.
 */
public class EnrollStudentsController implements Initializable {

    // FXML Components
    @FXML
    private TableView<CourseOffering> courseOfferingTable; // Table to display course offerings
    @FXML
    private TableColumn<CourseOffering, Integer> courseOfferingIdColumn; // Column for course offering ID
    @FXML
    private TableColumn<CourseOffering, String> courseCodeColumn; // Column for course code
    @FXML
    private TableColumn<CourseOffering, Integer> academicYearColumn; // Column for academic year
    @FXML
    private TableColumn<CourseOffering, Integer> semesterColumn; // Column for semester

    @FXML
    private TableView<StudentCourse> studentCourseTable; // Table to display student-course enrollments
    @FXML
    private TableColumn<StudentCourse, Integer> studentCourseIdColumn; // Column for student-course ID
    @FXML
    private TableColumn<StudentCourse, Integer> studentIdColumn; // Column for student ID
    @FXML
    private TableColumn<StudentCourse, Integer> offeringIdColumn; // Column for offering ID
    @FXML
    private TableColumn<StudentCourse, Date> enrollmentDateColumn; // Column for enrollment date

    @FXML
    private TextField studentIdNoField; // TextField for entering student ID number
    @FXML
    private TextField offeringIdField; // TextField for entering offering ID
    @FXML
    private DatePicker enrollmentDatePicker; // DatePicker for selecting enrollment date

    @FXML
    private Button enrollButton; // Button to enroll a student
    @FXML
    private Button updateButton; // Button to update an enrollment
    @FXML
    private Button deleteButton; // Button to delete an enrollment
    @FXML
    private Button refreshButton; // Button to refresh the table
    @FXML
    private Button backButton; // Button to navigate back

    @FXML
    private VBox contentArea; // Container for the content area

    private StudentCourseDAO studentCourseDAO; // DAO for student-course enrollments
    private CourseOfferingDAO courseOfferingDAO; // DAO for course offerings

    private ObservableList<StudentCourse> studentCourseList; // List of student-course enrollments
    private ObservableList<CourseOffering> courseOfferingList; // List of course offerings

    /**
     * Initializes the controller class. Sets up the table columns and loads
     * initial data.
     *
     * @param url The location used to resolve relative paths for the root
     * object, or null if unknown.
     * @param rb The resources used to localize the root object, or null if
     * none.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            // Initialize the StudentCourseDAO with the database connection
            studentCourseDAO = new StudentCourseDAO(connection);
            courseOfferingDAO = new CourseOfferingDAO(connection);

            // Initialize Course Offerings table columns
            courseOfferingIdColumn.setCellValueFactory(new PropertyValueFactory<>("offeringId"));
            courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
            academicYearColumn.setCellValueFactory(new PropertyValueFactory<>("academicYear"));
            semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));

            // Initialize the table columns
            studentCourseIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentCourseId"));
            studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
            offeringIdColumn.setCellValueFactory(new PropertyValueFactory<>("offeringId"));
            enrollmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));

            // Load all student-course enrollments into the table
            loadStudentCourses();

            // Add listener to pre-fill fields when an enrollment is selected for update
            studentCourseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    prefillUpdateFields(newSelection);
                }
            });
        } else {
            System.err.println("Failed to initialize controller due to database connection error.");
        }
    }

    /**
     * Loads all student-course enrollments and course offerings into the
     * respective tables.
     */
    private void loadStudentCourses() {
        studentCourseList = FXCollections.observableArrayList(studentCourseDAO.getAllStudentCourses());
        studentCourseTable.setItems(studentCourseList);

        // Refresh Course Offerings table
        try {
            List<CourseOffering> courseOfferings = courseOfferingDAO.getAllCourseOfferings();
            courseOfferingList = FXCollections.observableArrayList(courseOfferings);
            courseOfferingTable.setItems(courseOfferingList);
        } catch (Exception e) {
            System.err.println("Failed to load course offerings: " + e.getMessage());
        }
    }

    /**
     * Pre-fills the input fields with the details of the selected enrollment
     * for updating.
     *
     * @param studentCourse the selected student-course enrollment
     */
    private void prefillUpdateFields(StudentCourse studentCourse) {
        String studentIdNo = getStudentIdNo(studentCourse.getStudentId());
        studentIdNoField.setText(studentIdNo);
        offeringIdField.setText(String.valueOf(studentCourse.getOfferingId()));
        enrollmentDatePicker.setValue(studentCourse.getEnrollmentDate().toLocalDate());
    }

    /**
     * Clears all input fields.
     */
    private void clearInputFields() {
        studentIdNoField.clear();
        offeringIdField.clear();
        enrollmentDatePicker.setValue(null);
    }

    /**
     * Handles the action when the enroll button is clicked. Enrolls a student
     * into a course offering.
     */
    @FXML
    private void handleEnrollButton() {
        try {
            String studentIdNo = studentIdNoField.getText();
            int studentId = getStudentId(studentIdNo);
            if (studentId == -1) {
                showErrorDialog("Invalid Student ID No", "The entered student ID number does not exist.");
                return;
            }

            int offeringId = Integer.parseInt(offeringIdField.getText());
            Date enrollmentDate = Date.valueOf(enrollmentDatePicker.getValue());

            // Confirmation dialog before enrolling
            Alert confirmation = new Alert(AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Enrollment");
            confirmation.setHeaderText("Enroll Student in Course Offering");
            confirmation.setContentText("Are you sure you want to enroll this student?");
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    StudentCourse newEnrollment = new StudentCourse(studentId, offeringId, enrollmentDate);
                    studentCourseDAO.addStudentCourse(newEnrollment);

                    // Refresh the table
                    loadStudentCourses();

                    // Clear the input fields
                    clearInputFields();
                }
            });
        } catch (NumberFormatException e) {
            showErrorDialog("Invalid Input", "Please enter valid numeric values for Offering ID.");
        } catch (NullPointerException e) {
            showErrorDialog("Invalid Input", "Please select a valid enrollment date.");
        }
    }

    /**
     * Handles the action when the update button is clicked. Updates an existing
     * student-course enrollment.
     */
    @FXML
    private void handleUpdateButton() {
        StudentCourse selectedEnrollment = studentCourseTable.getSelectionModel().getSelectedItem();
        if (selectedEnrollment != null) {
            try {
                String studentIdNo = studentIdNoField.getText();
                int studentId = getStudentId(studentIdNo);
                if (studentId == -1) {
                    showErrorDialog("Invalid Student ID No", "The entered student ID number does not exist.");
                    return;
                }

                int offeringId = Integer.parseInt(offeringIdField.getText());
                Date enrollmentDate = Date.valueOf(enrollmentDatePicker.getValue());

                // Confirmation dialog before updating
                Alert confirmation = new Alert(AlertType.CONFIRMATION);
                confirmation.setTitle("Confirm Update");
                confirmation.setHeaderText("Update Enrollment");
                confirmation.setContentText("Are you sure you want to update this enrollment?");
                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        selectedEnrollment.setStudentId(studentId);
                        selectedEnrollment.setOfferingId(offeringId);
                        selectedEnrollment.setEnrollmentDate(enrollmentDate);

                        studentCourseDAO.updateStudentCourse(selectedEnrollment);

                        // Refresh the table
                        loadStudentCourses();

                        // Clear the input fields
                        clearInputFields();
                    }
                });
            } catch (NumberFormatException e) {
                showErrorDialog("Invalid Input", "Please enter valid numeric values for Offering ID.");
            } catch (NullPointerException e) {
                showErrorDialog("Invalid Input", "Please select a valid enrollment date.");
            }
        } else {
            showErrorDialog("No Selection", "Please select an enrollment to update.");
        }
    }

    /**
     * Handles the action when the delete button is clicked. Deletes an existing
     * student-course enrollment.
     */
    @FXML
    private void handleDeleteButton() {
        StudentCourse selectedEnrollment = studentCourseTable.getSelectionModel().getSelectedItem();
        if (selectedEnrollment != null) {
            // Confirmation dialog before deletion
            Alert confirmation = new Alert(AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Delete");
            confirmation.setHeaderText("Delete Enrollment");
            confirmation.setContentText("Are you sure you want to delete this enrollment?");
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Attempt to delete the enrollment
                    boolean isDeleted = studentCourseDAO.deleteStudentCourse(selectedEnrollment.getStudentCourseId());

                    if (isDeleted) {
                        // Refresh the table
                        loadStudentCourses();

                        // Clear the input fields
                        clearInputFields();

                        // Show success message
                        Alert successAlert = new Alert(AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("Enrollment deleted successfully.");
                        successAlert.showAndWait();
                    } else {
                        // Show error message if the enrollment has an associated grade
                        Alert errorAlert = new Alert(AlertType.ERROR);
                        errorAlert.setTitle("Deletion Failed");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("Enrollment cannot be deleted because it has an associated grade.");
                        errorAlert.showAndWait();
                    }
                }
            });
        } else {
            // Show error message if no enrollment is selected
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setTitle("No Selection");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please select an enrollment to delete.");
            errorAlert.showAndWait();
        }
    }

    /**
     * Handles the action when the refresh button is clicked. Refreshes the
     * student-course enrollments table.
     */
    @FXML
    private void handleRefreshButton() {
        // Refresh the table
        loadStudentCourses();
    }

    /**
     * Handles the action when the back button is clicked. Navigates back to the
     * Course Management screen.
     */
    @FXML
    private void handleBackButton() {
        try {
            // Load the Student Management FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/stims/fxml/dashboard/admin/course_management/Course_Management.fxml"));
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
     * Retrieves the student ID associated with the given student ID number.
     *
     * @param studentIdNo the student ID number
     * @return the student ID, or -1 if not found
     */
    private int getStudentId(String studentIdNo) {
        String sql = "SELECT student_id FROM Student WHERE studentId_No = ?";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentIdNo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("student_id");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student ID: " + e.getMessage());
        }
        return -1; // Return -1 if the student ID number is not found
    }

    /**
     * Retrieves the student ID number associated with the given student ID.
     *
     * @param studentId the student ID
     * @return the student ID number, or an empty string if not found
     */
    private String getStudentIdNo(int studentId) {
        String sql = "SELECT studentId_No FROM Student WHERE student_id = ?";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("studentId_No");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student ID number: " + e.getMessage());
        }
        return ""; // Return an empty string if the student ID is not found
    }

    /**
     * Displays an error dialog with the specified title and message.
     *
     * @param title the title of the error dialog
     * @param message the message to display in the error dialog
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
