package com.mycompany.stims.controller.dashboard.student.view_grades;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.utils.Session;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * The View_GradesController class manages the interface for students to view
 * their grades. It fetches grade data from the database, displays it in a
 * table, and allows filtering by course.
 */
public class View_GradesController implements Initializable {

    @FXML
    private TableView<GradeRecord> academicRecordsTable;

    @FXML
    private TableColumn<GradeRecord, String> courseCodeColumn;

    @FXML
    private TableColumn<GradeRecord, String> courseNameColumn;

    @FXML
    private TableColumn<GradeRecord, Integer> creditHourColumn;

    @FXML
    private TableColumn<GradeRecord, String> instructorColumn;

    @FXML
    private TableColumn<GradeRecord, String> gradeColumn;

    @FXML
    private TableColumn<GradeRecord, Double> gradePointsColumn;

    @FXML
    private ComboBox<String> courseComboBox;

    @FXML
    private Button clearFilterButton;

    private ObservableList<GradeRecord> gradeRecordsList = FXCollections.observableArrayList();
    private FilteredList<GradeRecord> filteredGradeRecords;

    private static final Map<String, Double> GRADE_POINTS = new HashMap<>();

    static {
        GRADE_POINTS.put("A+", 4.00);
        GRADE_POINTS.put("A", 4.00);
        GRADE_POINTS.put("A-", 3.70);
        GRADE_POINTS.put("B+", 3.50);
        GRADE_POINTS.put("B", 3.00);
        GRADE_POINTS.put("B-", 2.70);
        GRADE_POINTS.put("C+", 2.50);
        GRADE_POINTS.put("C", 2.00);
        GRADE_POINTS.put("C-", 1.75);
        GRADE_POINTS.put("D", 1.00);
        GRADE_POINTS.put("F", 0.00);
    }

    /**
     * Initializes the controller class. Sets up the table columns, filters, and
     * fetches grade data.
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
        creditHourColumn.setCellValueFactory(new PropertyValueFactory<>("creditHour"));
        instructorColumn.setCellValueFactory(new PropertyValueFactory<>("instructor"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        gradePointsColumn.setCellValueFactory(new PropertyValueFactory<>("gradePoints"));

        // Initialize filtered list
        filteredGradeRecords = new FilteredList<>(gradeRecordsList, p -> true);

        // Set the filtered list to the TableView
        academicRecordsTable.setItems(filteredGradeRecords);

        // Set up the course filter ComboBox
        courseComboBox.setOnAction(event -> filterTableByCourse());
        clearFilterButton.setOnAction(event -> clearFilter());

        // Fetch and display grades
        fetchGrades();
    }

    /**
     * Fetches grade data from the database for the logged-in student and
     * populates the table.
     */
    private void fetchGrades() {
        // Get the logged-in student's username or email
        String loggedInStudent = Session.getLoggedInStudentEmailOrUsername();
        if (loggedInStudent == null) {
            System.out.println("No student is logged in.");
            return;
        }

        String studentIdQuery = "SELECT student_id FROM student WHERE username = ? OR email = ?";
        int studentId = -1;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(studentIdQuery)) {

            pstmt.setString(1, loggedInStudent);
            pstmt.setString(2, loggedInStudent);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                studentId = rs.getInt("student_id");
            } else {
                System.out.println("Student not found.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student ID: " + e.getMessage());
            return;
        }

        String gradesQuery = "SELECT c.course_code, c.course_name, c.credits, "
                + "CONCAT(t.first_name, ' ', t.last_name) AS instructor, g.grade, "
                + "co.academic_year, co.semester "
                + "FROM studentcourse sc "
                + "JOIN courseoffering co ON sc.offering_id = co.offering_id "
                + "JOIN course c ON co.course_id = c.course_id "
                + "LEFT JOIN teachercourse tc ON co.offering_id = tc.offering_id "
                + "LEFT JOIN teacher t ON tc.teacher_id = t.teacher_id "
                + "JOIN grade g ON sc.student_course_id = g.student_course_id "
                + "WHERE sc.student_id = ? "
                + "ORDER BY co.academic_year ASC, co.semester ASC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(gradesQuery)) {

            pstmt.setInt(1, studentId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String courseCode = rs.getString("course_code");
                String courseName = rs.getString("course_name");
                int creditHour = rs.getInt("credits");
                String instructor = rs.getString("instructor");
                String grade = rs.getString("grade");

                double gradePoints = GRADE_POINTS.getOrDefault(grade, 0.0);

                gradeRecordsList.add(new GradeRecord(courseCode, courseName, creditHour, instructor, grade, gradePoints));
            }

            courseComboBox.setItems(FXCollections.observableArrayList(
                    gradeRecordsList.stream()
                            .map(GradeRecord::getCourseName)
                            .distinct()
                            .collect(Collectors.toList()))
            );

        } catch (SQLException e) {
            System.err.println("Error fetching grades: " + e.getMessage());
        }
    }

    /**
     * Filters the table to show only grades for the selected course.
     */
    private void filterTableByCourse() {
        String selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        if (selectedCourse != null && !selectedCourse.isEmpty()) {
            filteredGradeRecords.setPredicate(record -> record.getCourseName().equals(selectedCourse));
        } else {
            clearFilter();
        }
    }

    /**
     * Clears the course filter and shows all grades in the table.
     */
    private void clearFilter() {
        courseComboBox.getSelectionModel().clearSelection();
        filteredGradeRecords.setPredicate(record -> true);
    }

    /**
     * Model class for grade records.
     */
    public static class GradeRecord {

        private final String courseCode;
        private final String courseName;
        private final int creditHour;
        private final String instructor;
        private final String grade;
        private final double gradePoints;

        /**
         * Constructs a GradeRecord object.
         *
         * @param courseCode the course code
         * @param courseName the course name
         * @param creditHour the credit hours for the course
         * @param instructor the instructor's name
         * @param grade the grade received
         * @param gradePoints the grade points corresponding to the grade
         */
        public GradeRecord(String courseCode, String courseName, int creditHour, String instructor, String grade, double gradePoints) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.creditHour = creditHour;
            this.instructor = instructor;
            this.grade = grade;
            this.gradePoints = gradePoints;
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
        public int getCreditHour() {
            return creditHour;
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
        public String getGrade() {
            return grade;
        }

        /**
         *
         * @return
         */
        public double getGradePoints() {
            return gradePoints;
        }
    }
}
