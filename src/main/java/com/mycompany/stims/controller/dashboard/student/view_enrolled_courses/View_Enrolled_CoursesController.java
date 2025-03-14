package com.mycompany.stims.controller.dashboard.student.view_enrolled_courses;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.utils.Session;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.concurrent.Task;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Controller class for viewing enrolled courses in the student dashboard. This
 * class handles the UI logic for displaying courses the student is enrolled in,
 * filtering courses by semester, and clearing filters.
 */
public class View_Enrolled_CoursesController implements Initializable {

    @FXML
    private TableView<EnrolledCourse> enrolledCoursesTable;

    @FXML
    private TableColumn<EnrolledCourse, String> courseCodeColumn;

    @FXML
    private TableColumn<EnrolledCourse, String> courseNameColumn;

    @FXML
    private TableColumn<EnrolledCourse, Integer> creditHoursColumn;

    @FXML
    private TableColumn<EnrolledCourse, String> instructorColumn;

    @FXML
    private TableColumn<EnrolledCourse, String> academicYearColumn;

    @FXML
    private TableColumn<EnrolledCourse, String> semesterColumn;

    @FXML
    private ComboBox<String> semesterComboBox;

    @FXML
    private Button clearFilterButton;

    private ObservableList<EnrolledCourse> enrolledCoursesList = FXCollections.observableArrayList();
    private FilteredList<EnrolledCourse> filteredCoursesList;

    /**
     * Initializes the controller and sets up the UI components.
     *
     * @param url the location used to resolve relative paths for the root
     * object
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize TableView columns
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        creditHoursColumn.setCellValueFactory(new PropertyValueFactory<>("creditHours"));
        instructorColumn.setCellValueFactory(new PropertyValueFactory<>("instructor"));
        academicYearColumn.setCellValueFactory(new PropertyValueFactory<>("academicYear"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));

        // Initialize filtered list
        filteredCoursesList = new FilteredList<>(enrolledCoursesList, p -> true);
        enrolledCoursesTable.setItems(filteredCoursesList);

        // Fetch and display enrolled courses
        fetchEnrolledCourses();

        // Initialize semester filter ComboBox
        initializeSemesterFilter();

        // Set up clear filter button
        clearFilterButton.setOnAction(event -> clearFilter());
    }

    /**
     * Fetches the enrolled courses for the logged-in student from the database.
     */
    private void fetchEnrolledCourses() {
        Task<Void> fetchTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String loggedInStudent = Session.getLoggedInStudentEmailOrUsername();
                if (loggedInStudent == null) {
                    Platform.runLater(() -> showErrorAlert("No student is logged in."));
                    return null;
                }

                try (Connection conn = DatabaseConnection.getConnection()) {

                    int studentId = fetchStudentId(conn, loggedInStudent);
                    if (studentId == -1) {
                        Platform.runLater(() -> showErrorAlert("Student not found."));
                        return null;
                    }

                    // Fetch enrolled courses
                    ObservableList<EnrolledCourse> courses = fetchCourses(conn, studentId);

                    // Update UI
                    Platform.runLater(() -> {
                        enrolledCoursesList.setAll(courses);
                        initializeSemesterFilter();
                    });
                } catch (SQLException e) {
                    Platform.runLater(() -> showErrorAlert("Database error: " + e.getMessage()));
                }
                return null;
            }
        };

        new Thread(fetchTask).start();
    }

    /**
     * Fetches the student ID for the logged-in student.
     *
     * @param conn the database connection
     * @param loggedInStudent the username or email of the logged-in student
     * @return the student ID, or -1 if not found
     * @throws SQLException if a database error occurs
     */
    private int fetchStudentId(Connection conn, String loggedInStudent) throws SQLException {
        String studentIdQuery = "SELECT student_id FROM student WHERE username = ? OR email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(studentIdQuery)) {
            pstmt.setString(1, loggedInStudent);
            pstmt.setString(2, loggedInStudent);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("student_id") : -1;
        }
    }

    /**
     * Fetches the enrolled courses for the specified student.
     *
     * @param conn the database connection
     * @param studentId the ID of the student
     * @return a list of enrolled courses
     * @throws SQLException if a database error occurs
     */
    private ObservableList<EnrolledCourse> fetchCourses(Connection conn, int studentId) throws SQLException {
        String enrolledCoursesQuery = "SELECT c.course_code AS Course_Code, c.course_name AS Course_Name, "
                + "c.credits AS Credit_Hours, CONCAT(t.first_name, ' ', t.last_name) AS Instructor, "
                + "co.academic_year AS Academic_Year, co.year AS Year, co.semester AS Semester "
                + "FROM studentcourse sc "
                + "JOIN courseoffering co ON sc.offering_id = co.offering_id "
                + "JOIN course c ON co.course_id = c.course_id "
                + "LEFT JOIN teachercourse tc ON co.offering_id = tc.offering_id "
                + "LEFT JOIN teacher t ON tc.teacher_id = t.teacher_id "
                + "WHERE sc.student_id = ? "
                + "ORDER BY co.year ASC, co.semester ASC";

        ObservableList<EnrolledCourse> courses = FXCollections.observableArrayList();
        try (PreparedStatement pstmt = conn.prepareStatement(enrolledCoursesQuery)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                EnrolledCourse course = new EnrolledCourse(
                        rs.getString("Course_Code"),
                        rs.getString("Course_Name"),
                        rs.getInt("Credit_Hours"),
                        rs.getString("Instructor"),
                        rs.getString("Academic_Year"),
                        "Year:" + rs.getInt("Year") + " Semester: " + rs.getInt("Semester")
                );
                courses.add(course);
            }
        }
        return courses;
    }

    /**
     * Initializes the semester filter ComboBox with unique semester values.
     */
    private void initializeSemesterFilter() {
        Set<String> semesterSet = new HashSet<>();
        for (EnrolledCourse course : enrolledCoursesList) {
            semesterSet.add(course.getSemester());
        }

        Platform.runLater(() -> {
            semesterComboBox.getItems().clear();
            semesterComboBox.getItems().add("All"); // Add "All" option
            semesterComboBox.getItems().addAll(semesterSet);
            semesterComboBox.setValue("All"); // Default to "All"
        });

        semesterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.equals("All")) {
                filteredCoursesList.setPredicate(course -> true);
            } else {
                filteredCoursesList.setPredicate(course -> course.getSemester().equals(newVal));
            }
        });
    }

    /**
     * Clears the semester filter and displays all enrolled courses.
     */
    private void clearFilter() {
        semesterComboBox.setValue("All");
        filteredCoursesList.setPredicate(course -> true);
    }

    /**
     * Displays an error alert with the specified message.
     *
     * @param message the error message to display
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Model class for enrolled courses.
     */
    public static class EnrolledCourse {

        private final String courseCode;
        private final String courseName;
        private final Integer creditHours;
        private final String instructor;
        private final String academicYear;
        private final String semester;

        /**
         * Constructs an EnrolledCourse object.
         *
         * @param courseCode the course code
         * @param courseName the course name
         * @param creditHours the number of credit hours
         * @param instructor the instructor's name
         * @param academicYear the academic year
         * @param semester the semester
         */
        public EnrolledCourse(String courseCode, String courseName, Integer creditHours, String instructor, String academicYear, String semester) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.creditHours = creditHours;
            this.instructor = instructor;
            this.academicYear = academicYear;
            this.semester = semester;
        }

        // Getters

        /**
         *
         * @return
         */
        public String getCourseCode() {
            return courseCode;
        }

        /**
         *
         * @return
         */
        public String getCourseName() {
            return courseName;
        }

        /**
         *
         * @return
         */
        public Integer getCreditHours() {
            return creditHours;
        }

        /**
         *
         * @return
         */
        public String getInstructor() {
            return instructor;
        }

        /**
         *
         * @return
         */
        public String getAcademicYear() {
            return academicYear;
        }

        /**
         *
         * @return
         */
        public String getSemester() {
            return semester;
        }
    }
}
