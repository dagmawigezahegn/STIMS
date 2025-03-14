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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * The AssignedCoursesController class manages the interface for teachers to
 * view their assigned courses. It fetches assigned course data from the
 * database and displays it in a table.
 */
public class AssignedCoursesController implements Initializable {

    @FXML
    private TableView<AssignedCourse> assignedCoursesTable;
    @FXML
    private TableColumn<AssignedCourse, String> courseCodeColumn;
    @FXML
    private TableColumn<AssignedCourse, String> courseNameColumn;
    @FXML
    private TableColumn<AssignedCourse, Integer> creditHoursColumn;
    @FXML
    private TableColumn<AssignedCourse, Integer> academicYearColumn;
    @FXML
    private TableColumn<AssignedCourse, Integer> semesterColumn;
    @FXML
    private TableColumn<AssignedCourse, String> assignedDateColumn;

    /**
     * Initializes the controller class. Sets up the table columns and loads
     * assigned course data.
     *
     * @param url the location used to resolve relative paths for the root
     * object
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the columns
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        creditHoursColumn.setCellValueFactory(new PropertyValueFactory<>("creditHours"));
        academicYearColumn.setCellValueFactory(new PropertyValueFactory<>("academicYear"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        assignedDateColumn.setCellValueFactory(new PropertyValueFactory<>("assignedDate"));

        // Fetch and populate the table with data
        loadAssignedCourses();
    }

    /**
     * Loads the assigned courses for the logged-in teacher from the database
     * and populates the table.
     */
    private void loadAssignedCourses() {
        ObservableList<AssignedCourse> assignedCourses = FXCollections.observableArrayList();
        String loggedInTeacherEmailOrUsername = Session.getLoggedInTeacherEmailOrUsername();

        if (loggedInTeacherEmailOrUsername != null) {
            String query = "SELECT c.course_code, c.course_name, c.credits, co.academic_year, co.semester, tc.assigned_date "
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
                    assignedCourses.add(new AssignedCourse(
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getInt("credits"),
                            rs.getInt("academic_year"),
                            rs.getInt("semester"),
                            rs.getString("assigned_date")
                    ));
                }

                assignedCoursesTable.setItems(assignedCourses);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Model class for the TableView. Represents an assigned course.
     */
    public static class AssignedCourse {

        private final String courseCode;
        private final String courseName;
        private final int creditHours;
        private final int academicYear;
        private final int semester;
        private final String assignedDate;

        /**
         * Constructs an AssignedCourse object.
         *
         * @param courseCode the course code
         * @param courseName the course name
         * @param creditHours the credit hours for the course
         * @param academicYear the academic year of the course offering
         * @param semester the semester of the course offering
         * @param assignedDate the date the course was assigned to the teacher
         */
        public AssignedCourse(String courseCode, String courseName, int creditHours, int academicYear, int semester, String assignedDate) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.creditHours = creditHours;
            this.academicYear = academicYear;
            this.semester = semester;
            this.assignedDate = assignedDate;
        }

        /**
         * Gets the course code.
         *
         * @return the course code
         */
        public String getCourseCode() {
            return courseCode;
        }

        /**
         * Gets the course name.
         *
         * @return the course name
         */
        public String getCourseName() {
            return courseName;
        }

        /**
         * Gets the credit hours for the course.
         *
         * @return the credit hours
         */
        public int getCreditHours() {
            return creditHours;
        }

        /**
         * Gets the academic year of the course offering.
         *
         * @return the academic year
         */
        public int getAcademicYear() {
            return academicYear;
        }

        /**
         * Gets the semester of the course offering.
         *
         * @return the semester
         */
        public int getSemester() {
            return semester;
        }

        /**
         * Gets the date the course was assigned to the teacher.
         *
         * @return the assigned date
         */
        public String getAssignedDate() {
            return assignedDate;
        }
    }
}
