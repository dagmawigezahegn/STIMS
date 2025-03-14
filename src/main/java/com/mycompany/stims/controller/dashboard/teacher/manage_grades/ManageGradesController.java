package com.mycompany.stims.controller.dashboard.teacher.manage_grades;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.database.GradeDAO;
import com.mycompany.stims.model.Grade;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * The `ManageGradesController` class manages the functionality for teachers to
 * view, add, and update grades for students enrolled in their courses. It
 * provides a user interface for selecting a course, viewing enrolled students,
 * and managing their grades.
 */
public class ManageGradesController implements Initializable {

    @FXML
    private ComboBox<String> courseComboBox;

    @FXML
    private TableView<EnrolledStudent> studentsTable;

    @FXML
    private TableColumn<EnrolledStudent, String> studentIdNoColumn;

    @FXML
    private TableColumn<EnrolledStudent, String> fullNameColumn;

    @FXML
    private TableColumn<EnrolledStudent, String> emailColumn;

    @FXML
    private TableColumn<EnrolledStudent, String> gradeColumn;

    @FXML
    private TextField inputStudentId;

    @FXML
    private ComboBox<String> inputGradeComboBox;

    @FXML
    private Button submitButton;

    @FXML
    private TextField editStudentId;

    @FXML
    private ComboBox<String> editGradeComboBox;

    @FXML
    private Button updateButton;

    private GradeDAO gradeDAO;

    /**
     * Initializes the controller after its root element has been completely
     * processed. This method sets up the database connection, initializes UI
     * components, and configures event handlers for buttons and table
     * selection.
     *
     * @param url The location used to resolve relative paths for the root
     * object, or `null` if the location is not known.
     * @param rb The resources used to localize the root object, or `null` if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize GradeDAO
        Connection connection = DatabaseConnection.getConnection();
        gradeDAO = new GradeDAO(connection);

        // Initialize grade ComboBoxes
        ObservableList<String> grades = FXCollections.observableArrayList(
                "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D", "F"
        );
        inputGradeComboBox.setItems(grades);
        editGradeComboBox.setItems(grades);

        // Initialize TableColumn cell value factories
        studentIdNoColumn.setCellValueFactory(new PropertyValueFactory<>("studentIdNo"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

        // Populate the course filter ComboBox
        populateCourseFilter();

        // Set up event handlers for buttons
        courseComboBox.setOnAction(event -> loadEnrolledStudents());
        submitButton.setOnAction(event -> addGrade());
        updateButton.setOnAction(event -> updateGrade());

        // Add listener for table row selection
        studentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Clear both input and edit fields
                clearFields();

                // If the selected student has a grade, populate the Edit fields
                if (newSelection.getGrade() != null && !newSelection.getGrade().isEmpty()) {
                    editStudentId.setText(newSelection.getStudentIdNo());
                    editGradeComboBox.setValue(newSelection.getGrade());
                } else {
                    // If the selected student does not have a grade, populate the Input fields
                    inputStudentId.setText(newSelection.getStudentIdNo());
                }
            }
        });
    }

    /**
     * Populates the course filter ComboBox with courses taught by the logged-in
     * teacher.
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

            try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
                pstmt.setString(1, loggedInTeacherEmailOrUsername);
                pstmt.setString(2, loggedInTeacherEmailOrUsername);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    courses.add(rs.getString("course_name"));
                }

                courseComboBox.setItems(courses);

            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Database Error", "Failed to fetch courses: " + e.getMessage());
            }
        }
    }

    /**
     * Loads the enrolled students for the selected course into the TableView.
     */
    private void loadEnrolledStudents() {
        String selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();

        if (selectedCourse != null) {
            ObservableList<EnrolledStudent> enrolledStudents = FXCollections.observableArrayList();
            String query = "SELECT s.studentId_No, CONCAT(s.first_name, ' ', s.last_name) AS full_name, s.email, g.grade "
                    + "FROM studentcourse sc "
                    + "JOIN student s ON sc.student_id = s.student_id "
                    + "JOIN courseoffering co ON sc.offering_id = co.offering_id "
                    + "JOIN course c ON co.course_id = c.course_id "
                    + "LEFT JOIN Grade g ON sc.student_course_id = g.student_course_id "
                    + "WHERE c.course_name = ?";

            try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
                pstmt.setString(1, selectedCourse);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    enrolledStudents.add(new EnrolledStudent(
                            rs.getString("studentId_No"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("grade")
                    ));
                }

                studentsTable.setItems(enrolledStudents);

            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Database Error", "Failed to fetch enrolled students: " + e.getMessage());
            }
        }
    }

    /**
     * Handles the "Add Grade" button action. Adds a grade for the selected
     * student in the selected course.
     */
    private void addGrade() {
        String studentId = inputStudentId.getText();
        String grade = inputGradeComboBox.getSelectionModel().getSelectedItem();

        if (studentId != null && !studentId.isEmpty() && grade != null) {
            // Fetch student_course_id for the given student and course
            String query = "SELECT sc.student_course_id "
                    + "FROM studentcourse sc "
                    + "JOIN student s ON sc.student_id = s.student_id "
                    + "JOIN courseoffering co ON sc.offering_id = co.offering_id "
                    + "JOIN course c ON co.course_id = c.course_id "
                    + "WHERE s.studentId_No = ? AND c.course_name = ?";

            try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
                pstmt.setString(1, studentId);
                pstmt.setString(2, courseComboBox.getSelectionModel().getSelectedItem());
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    int studentCourseId = rs.getInt("student_course_id");
                    Grade newGrade = new Grade(0, studentCourseId, grade, null, null);
                    gradeDAO.addGrade(newGrade);
                    showAlert(AlertType.INFORMATION, "Success", "Grade added successfully.");
                    loadEnrolledStudents();
                    clearFields();
                } else {
                    showAlert(AlertType.ERROR, "Error", "Student or course not found.");
                }

            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Database Error", "Failed to add grade: " + e.getMessage());
            }
        } else {
            showAlert(AlertType.ERROR, "Error", "Please fill in all fields.");
        }
    }

    /**
     * Handles the "Update Grade" button action. Updates the grade for the
     * selected student in the selected course.
     */
    private void updateGrade() {
        String studentId = editStudentId.getText();
        String grade = editGradeComboBox.getSelectionModel().getSelectedItem();

        if (studentId != null && !studentId.isEmpty() && grade != null) {
            // Fetch student_course_id for the given student and course
            String query = "SELECT sc.student_course_id "
                    + "FROM studentcourse sc "
                    + "JOIN student s ON sc.student_id = s.student_id "
                    + "JOIN courseoffering co ON sc.offering_id = co.offering_id "
                    + "JOIN course c ON co.course_id = c.course_id "
                    + "WHERE s.studentId_No = ? AND c.course_name = ?";

            try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
                pstmt.setString(1, studentId);
                pstmt.setString(2, courseComboBox.getSelectionModel().getSelectedItem());
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    int studentCourseId = rs.getInt("student_course_id");
                    Grade existingGrade = gradeDAO.getGradeByStudentCourseId(studentCourseId);
                    if (existingGrade != null) {
                        existingGrade.setGrade(grade);
                        gradeDAO.updateGrade(existingGrade);
                        showAlert(AlertType.INFORMATION, "Success", "Grade updated successfully.");
                        loadEnrolledStudents();
                        clearFields();
                    } else {
                        showAlert(AlertType.ERROR, "Error", "Grade not found for the selected student.");
                    }
                } else {
                    showAlert(AlertType.ERROR, "Error", "Student or course not found.");
                }

            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Database Error", "Failed to update grade: " + e.getMessage());
            }
        } else {
            showAlert(AlertType.ERROR, "Error", "Please fill in all fields.");
        }
    }

    /**
     * Clears the input and edit fields.
     */
    private void clearFields() {
        inputStudentId.clear();
        inputGradeComboBox.getSelectionModel().clearSelection();
        editStudentId.clear();
        editGradeComboBox.getSelectionModel().clearSelection();
    }

    /**
     * Displays an alert dialog with the specified type, title, and message.
     *
     * @param type The type of alert (e.g., ERROR, INFORMATION).
     * @param title The title of the alert.
     * @param message The message to display in the alert.
     */
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Model class for the TableView. Represents an enrolled student with their
     * ID, full name, email, and grade.
     */
    public static class EnrolledStudent {

        private final String studentIdNo;
        private final String fullName;
        private final String email;
        private final String grade;

        /**
         *
         * @param studentIdNo
         * @param fullName
         * @param email
         * @param grade
         */
        public EnrolledStudent(String studentIdNo, String fullName, String email, String grade) {
            this.studentIdNo = studentIdNo;
            this.fullName = fullName;
            this.email = email;
            this.grade = grade;
        }

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
        public String getGrade() {
            return grade;
        }
    }
}
