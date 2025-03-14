package com.mycompany.stims.controller.dashboard.student.academic_records;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.utils.Session;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

/**
 * The AcademicRecordsController class handles the display of academic records
 * for a student. It fetches and displays the student's academic records,
 * including SGPA and CGPA.
 */
public class AcademicRecordsController implements Initializable {

    @FXML
    private TableView<AcademicRecord> academicRecordsTable; // Table to display academic records
    @FXML
    private TableColumn<AcademicRecord, String> academicYearColumn; // Column for academic year
    @FXML
    private TableColumn<AcademicRecord, Integer> yearColumn; // Column for year
    @FXML
    private TableColumn<AcademicRecord, Integer> semesterColumn; // Column for semester
    @FXML
    private TableColumn<AcademicRecord, Integer> totalCreditsColumn; // Column for total credits
    @FXML
    private TableColumn<AcademicRecord, Double> sgpaColumn; // Column for SGPA
    @FXML
    private Label cgpaLabel; // Label to display CGPA
    @FXML
    private StackPane dynamicContentContainer; // Container for dynamic content

    private ObservableList<AcademicRecord> academicRecordsList = FXCollections.observableArrayList(); // List of academic records

    /**
     * Initializes the controller class. Sets up the table columns and fetches
     * academic records.
     *
     * @param url The location used to resolve relative paths for the root
     * object, or null if unknown.
     * @param rb The resources used to localize the root object, or null if
     * none.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize TableView columns
        academicYearColumn.setCellValueFactory(new PropertyValueFactory<>("academicYear"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        totalCreditsColumn.setCellValueFactory(new PropertyValueFactory<>("totalCredits"));
        sgpaColumn.setCellValueFactory(new PropertyValueFactory<>("sgpa"));

        // Fetch and display academic records
        fetchAcademicRecords();
    }

    /**
     * Fetches the academic records for the logged-in student from the database.
     */
    private void fetchAcademicRecords() {
        // Get the logged-in student's username or email
        String loggedInStudent = Session.getLoggedInStudentEmailOrUsername();
        if (loggedInStudent == null) {
            System.out.println("No student is logged in.");
            return;
        }

        // Query to fetch the student ID
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

        // Query to fetch academic records and CGPA
        String academicRecordsQuery = "SELECT academic_year, year, semester, total_credits, sgpa, cgpa "
                + "FROM studentacademicrecord "
                + "WHERE student_id = ? "
                + "ORDER BY academic_year ASC, year ASC, semester ASC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(academicRecordsQuery)) {
            pstmt.setInt(1, studentId);

            ResultSet rs = pstmt.executeQuery();

            // Populate the ObservableList with the results
            while (rs.next()) {
                String academicYear = rs.getString("academic_year");
                int year = rs.getInt("year");
                int semester = rs.getInt("semester");
                int totalCredits = rs.getInt("total_credits");
                double sgpa = rs.getDouble("sgpa");
                double cgpa = rs.getDouble("cgpa");

                // Add the record to the list
                academicRecordsList.add(new AcademicRecord(academicYear, year, semester, totalCredits, sgpa));

                // Set the CGPA label with the latest CGPA value
                cgpaLabel.setText(String.format("%.2f", cgpa));
            }

            // Set the data to the TableView
            academicRecordsTable.setItems(academicRecordsList);

        } catch (SQLException e) {
            System.err.println("Error fetching academic records: " + e.getMessage());
        }
    }

    /**
     * Handles the action when the view report button is clicked. Loads the View
     * Reports screen.
     */
    @FXML
    private void viewReport() {
        loadUI("/com/mycompany/stims/fxml/dashboard/student/academic_records/View_Reports.fxml");
    }

    /**
     * Loads the specified FXML file into the dynamic content container.
     *
     * @param fxmlPath the path to the FXML file
     */
    private void loadUI(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newContent = loader.load();

            // Clear the previous content
            dynamicContentContainer.getChildren().clear();

            // Add the new content
            dynamicContentContainer.getChildren().add(newContent);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle loading errors
        }
    }

    /**
     * Model class for academic records.
     */
    public static class AcademicRecord {

        private final String academicYear;
        private final int year;
        private final int semester;
        private final int totalCredits;
        private final double sgpa;

        /**
         * Constructs an AcademicRecord object.
         *
         * @param academicYear the academic year
         * @param year the year
         * @param semester the semester
         * @param totalCredits the total credits
         * @param sgpa the SGPA
         */
        public AcademicRecord(String academicYear, int year, int semester, int totalCredits, double sgpa) {
            this.academicYear = academicYear;
            this.year = year;
            this.semester = semester;
            this.totalCredits = totalCredits;
            this.sgpa = sgpa;
        }

        // Getters

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
        public int getYear() {
            return year;
        }

        /**
         *
         * @return
         */
        public int getSemester() {
            return semester;
        }

        /**
         *
         * @return
         */
        public int getTotalCredits() {
            return totalCredits;
        }

        /**
         *
         * @return
         */
        public double getSgpa() {
            return sgpa;
        }
    }
}
