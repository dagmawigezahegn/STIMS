package com.mycompany.stims.controller.dashboard.teacher.assigned_courses;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.utils.Session;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * The ViewEnrolledStudentsController class handles the display of students
 * enrolled in courses assigned to the logged-in teacher. It allows filtering by
 * course and refreshing the table.
 */
public class ViewEnrolledStudentsController implements Initializable {

    @FXML
    private ComboBox<String> courseFilter; // ComboBox for selecting a course to filter
    @FXML
    private TableView<EnrolledStudent> studentsTable; // Table to display enrolled students
    @FXML
    private TableColumn<EnrolledStudent, String> studentIdNoColumn; // Column for student ID number
    @FXML
    private TableColumn<EnrolledStudent, String> fullNameColumn; // Column for student full name
    @FXML
    private TableColumn<EnrolledStudent, String> emailColumn; // Column for student email
    @FXML
    private TableColumn<EnrolledStudent, String> courseNameColumn; // Column for course name
    @FXML
    private Button searchButton; // Button to search for enrolled students
    @FXML
    private Button refreshButton; // Button to refresh the table

    /**
     * Initializes the controller class. Sets up the table columns, populates
     * the course filter, and configures event handlers for buttons.
     *
     * @param url The location used to resolve relative paths for the root
     * object, or null if unknown.
     * @param rb The resources used to localize the root object, or null if
     * none.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize TableColumn cell value factories
        studentIdNoColumn.setCellValueFactory(new PropertyValueFactory<>("studentIdNo"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        // Populate the course filter ComboBox
        populateCourseFilter();

        // Set up event handlers for buttons
        searchButton.setOnAction(event -> loadEnrolledStudents());
        refreshButton.setOnAction(event -> refreshTable());
    }

    /**
     * Populates the course filter ComboBox with courses assigned to the
     * logged-in teacher.
     */
    private void populateCourseFilter() {
        ObservableList<String> courses = FXCollections.observableArrayList();
        String loggedInTeacherEmailOrUsername = Session.getLoggedInTeacherEmailOrUsername();

        if (loggedInTeacherEmailOrUsername != null) {
            String query = "SELECT c.course_name "
                    + "FROM teachercourse tc "
                    + "JOIN courseoffering co ON tc.offering_id = co.offering_id "
                    + "JOIN course c ON co.course_id = c.course_id "
                    + "JOIN teacher t ON tc.teacher_id = t.teacher_id "
                    + "WHERE t.email = ? OR t.username = ?";

            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, loggedInTeacherEmailOrUsername);
                pstmt.setString(2, loggedInTeacherEmailOrUsername);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    courses.add(rs.getString("course_name"));
                }

                courseFilter.setItems(courses);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads the enrolled students for the selected course into the table.
     */
    private void loadEnrolledStudents() {
        String selectedCourse = courseFilter.getSelectionModel().getSelectedItem();

        if (selectedCourse != null) {
            ObservableList<EnrolledStudent> enrolledStudents = FXCollections.observableArrayList();
            String query = "SELECT s.studentId_No, CONCAT(s.first_name, ' ', s.last_name) AS full_name, s.email, c.course_name "
                    + "FROM studentcourse sc "
                    + "JOIN student s ON sc.student_id = s.student_id "
                    + "JOIN courseoffering co ON sc.offering_id = co.offering_id "
                    + "JOIN course c ON co.course_id = c.course_id "
                    + "WHERE c.course_name = ?";

            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, selectedCourse);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    enrolledStudents.add(new EnrolledStudent(
                            rs.getString("studentId_No"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("course_name")
                    ));
                }

                studentsTable.setItems(enrolledStudents);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Refreshes the table by reloading the enrolled students.
     */
    private void refreshTable() {
        loadEnrolledStudents();
    }

    /**
     * Handles the action when the export to CSV button is clicked. Displays a
     * notification that CSV export is not implemented.
     */
    @FXML
    private void exportToCSV() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText("CSV export not implemented.");
        alert.showAndWait();
    }

    /**
     * Model class for the TableView representing enrolled students.
     */
    public static class EnrolledStudent {

        private final String studentIdNo; // Student ID number
        private final String fullName; // Student full name
        private final String email; // Student email
        private final String courseName; // Course name

        /**
         * Constructs an EnrolledStudent object.
         *
         * @param studentIdNo the student ID number
         * @param fullName the student's full name
         * @param email the student's email
         * @param courseName the course name
         */
        public EnrolledStudent(String studentIdNo, String fullName, String email, String courseName) {
            this.studentIdNo = studentIdNo;
            this.fullName = fullName;
            this.email = email;
            this.courseName = courseName;
        }

        // Getters

        /**
         *
         * @return
         */
        public String getStudentIdNo() {
            return studentIdNo;
        }

        /**
         *
         * @return
         */
        public String getFullName() {
            return fullName;
        }

        /**
         *
         * @return
         */
        public String getEmail() {
            return email;
        }

        /**
         *
         * @return
         */
        public String getCourseName() {
            return courseName;
        }
    }
}
