package com.mycompany.stims.report;

import com.mycompany.stims.database.DatabaseConnection;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.mycompany.stims.utils.FilePathUtils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for generating student transcripts in Excel format. This class
 * provides methods to insert student details, semester records, and grades into
 * a predefined Excel template and save the updated transcript as a new file.
 */
public class Transcript_Generator {

    /**
     * Inserts basic student details into the specified sheet of the Excel
     * workbook.
     *
     * @param sheet the Excel sheet to insert the details into
     * @param studentId the ID of the student whose details are to be inserted
     * @throws IllegalArgumentException if the student with the given ID is not
     * found
     * @throws RuntimeException if a database error occurs
     */
    private static void insertBasicDetails(Sheet sheet, String studentId) {
        String query = "SELECT s.first_name, s.middle_name, s.last_name, s.date_of_birth, s.sex, s.studentId_No, "
                + "p.program_name, d.department_name, s.enrollment_date, s.graduation_date "
                + "FROM student s "
                + "JOIN program p ON s.program_id = p.program_id "
                + "JOIN department d ON s.department_id = d.department_id "
                + "WHERE s.studentId_No = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Fetch details from the result set
                    String[] studentDetails = {
                        rs.getString("first_name") + " " + rs.getString("middle_name") + " " + rs.getString("last_name"),
                        rs.getString("date_of_birth"),
                        rs.getString("sex"),
                        rs.getString("studentId_No"),
                        rs.getString("program_name"),
                        rs.getString("department_name"),
                        rs.getString("enrollment_date"),
                        rs.getString("graduation_date") // Fetch graduation date from the database
                    };

                    // Row indices for the respective fields in Sheet1
                    int[] rowPositions = {8, 9, 10, 11, 12, 13, 14, 15};

                    // Insert the fetched student details into the Excel sheet
                    for (int i = 0; i < studentDetails.length; i++) {
                        setCellValue(sheet, rowPositions[i] - 1, 2, studentDetails[i]);
                    }
                } else {
                    throw new IllegalArgumentException("Student with ID " + studentId + " not found.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Inserts semester records (courses and grades) into the specified sheet of
     * the Excel workbook.
     *
     * @param sheet the Excel sheet to insert the records into
     * @param studentId the ID of the student whose records are to be inserted
     * @param startRow the starting row index for inserting records
     * @throws RuntimeException if a database error occurs
     */
    private static void insertSemesterRecords(Sheet sheet, String studentId, int startRow) {
        String courseQuery = "SELECT c.course_code, c.course_name, c.credits, g.grade, co.academic_year "
                + "FROM studentcourse sc "
                + "JOIN courseoffering co ON sc.offering_id = co.offering_id "
                + "JOIN course c ON co.course_id = c.course_id "
                + "JOIN grade g ON sc.student_course_id = g.student_course_id "
                + "WHERE sc.student_id = (SELECT student_id FROM student WHERE studentId_No = ?) "
                + "AND co.year = ? AND co.semester = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement courseStmt = conn.prepareStatement(courseQuery)) {

            // Initialize CGPA
            double cgpa = fetchCgpa(conn, studentId);

            // Iterate through years and semesters in order
            int maxYears = 4; // Assuming a maximum of 4 years (adjust as needed)
            for (int year = 1; year <= maxYears; year++) {
                for (int semester = 1; semester <= 2; semester++) { // Semesters 1 and 2
                    // Check if records exist for this semester
                    boolean hasRecords = checkSemesterRecords(conn, studentId, year, semester);

                    if (hasRecords) {
                        // Insert semester label
                        String semesterName = "Year " + year + " - Sem " + semester;
                        insertSemesterLabel(sheet, semesterName, startRow);

                        // Insert the course headers after semester label
                        insertCourseHeaders(sheet, startRow + 1); // Place headers in the row after the label

                        // Fetch course records for this semester
                        courseStmt.setString(1, studentId);
                        courseStmt.setInt(2, year);
                        courseStmt.setString(3, String.valueOf(semester));

                        try (ResultSet courseRs = courseStmt.executeQuery()) {
                            int courseRowStart = startRow + 2;  // Start two rows below the semester label
                            while (courseRs.next()) {
                                String[] courseDetails = {
                                    courseRs.getString("course_code"),
                                    courseRs.getString("course_name"),
                                    courseRs.getString("credits"),
                                    courseRs.getString("grade"),
                                    courseRs.getString("academic_year").substring(0, 4) // Extract year part
                                };

                                // Insert the course record
                                insertCourseRecord(sheet, courseDetails, courseRowStart++);
                            }

                            // Insert semester summary (SGPA)
                            double sgpa = fetchSgpa(conn, studentId, year, semester);
                            insertSemesterSummary(sheet, sgpa, courseRowStart);

                            // Update startRow for the next semester
                            startRow = courseRowStart + 3; // Leave two rows blank after the summary
                        }
                    }
                }
            }

            // Insert CGPA at the end
            insertCgpaPlaceholder(sheet, startRow, cgpa);

        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving semester records: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches the cumulative GPA (CGPA) for the specified student.
     *
     * @param conn the database connection
     * @param studentId the ID of the student
     * @return the CGPA of the student
     * @throws SQLException if a database error occurs
     */
    private static double fetchCgpa(Connection conn, String studentId) throws SQLException {
        String cgpaQuery = "SELECT cgpa FROM studentacademicrecord "
                + "WHERE student_id = (SELECT student_id FROM student WHERE studentId_No = ?) "
                + "ORDER BY academic_year DESC, semester DESC LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(cgpaQuery)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("cgpa");
                }
            }
        }
        return 0.0; // Default CGPA if not found
    }

    /**
     * Fetches the semester GPA (SGPA) for the specified student, year, and
     * semester.
     *
     * @param conn the database connection
     * @param studentId the ID of the student
     * @param year the academic year
     * @param semester the semester
     * @return the SGPA of the student for the specified semester
     * @throws SQLException if a database error occurs
     */
    private static double fetchSgpa(Connection conn, String studentId, int year, int semester) throws SQLException {
        String sgpaQuery = "SELECT sgpa FROM studentacademicrecord "
                + "WHERE student_id = (SELECT student_id FROM student WHERE studentId_No = ?) "
                + "AND year = ? AND semester = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sgpaQuery)) {
            pstmt.setString(1, studentId);
            pstmt.setInt(2, year);
            pstmt.setInt(3, semester);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("sgpa");
                }
            }
        }
        return 0.0; // Default SGPA if not found
    }

    /**
     * Checks if records exist for the specified student, year, and semester.
     *
     * @param conn the database connection
     * @param studentId the ID of the student
     * @param year the academic year
     * @param semester the semester
     * @return true if records exist, false otherwise
     * @throws SQLException if a database error occurs
     */
    private static boolean checkSemesterRecords(Connection conn, String studentId, int year, int semester) throws SQLException {
        String checkQuery = "SELECT 1 FROM studentacademicrecord "
                + "WHERE student_id = (SELECT student_id FROM student WHERE studentId_No = ?) "
                + "AND year = ? AND semester = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
            pstmt.setString(1, studentId);
            pstmt.setInt(2, year);
            pstmt.setInt(3, semester);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if records exist
            }
        }
    }

    /**
     * Sets the value of a cell in the specified sheet at the given row and
     * column indices.
     *
     * @param sheet the Excel sheet
     * @param rowIndex the row index
     * @param colIndex the column index
     * @param value the value to set in the cell
     */
    private static void setCellValue(Sheet sheet, int rowIndex, int colIndex, String value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        cell.setCellValue(value);
    }

    /**
     * Inserts a semester label into the specified sheet at the given row index.
     *
     * @param sheet the Excel sheet
     * @param semesterName the name of the semester (e.g., "Year 1 - Sem 1")
     * @param rowIndex the row index to insert the label
     */
    private static void insertSemesterLabel(Sheet sheet, String semesterName, int rowIndex) {
        setCellValue(sheet, rowIndex, 0, "Semester");
        setCellValue(sheet, rowIndex, 1, semesterName);
    }

    /**
     * Inserts a course record into the specified sheet at the given row index.
     *
     * @param sheet the Excel sheet
     * @param courseDetails the details of the course (code, name, credits,
     * grade, academic year)
     * @param rowIndex the row index to insert the record
     */
    private static void insertCourseRecord(Sheet sheet, String[] courseDetails, int rowIndex) {
        for (int col = 0; col < courseDetails.length - 1; col++) {
            setCellValue(sheet, rowIndex, col, courseDetails[col]);
        }
        String grade = courseDetails[3];
        double gradePoint = gradeToPoint(grade);
        setCellValue(sheet, rowIndex, 4, String.valueOf(gradePoint));
        setCellValue(sheet, rowIndex, 5, courseDetails[4]);
    }

    /**
     * Inserts a semester summary (SGPA) into the specified sheet at the given
     * row index.
     *
     * @param sheet the Excel sheet
     * @param sgpa the semester GPA
     * @param rowIndex the row index to insert the summary
     */
    private static void insertSemesterSummary(Sheet sheet, double sgpa, int rowIndex) {
        setCellValue(sheet, rowIndex, 3, "SGPA");
        setCellValue(sheet, rowIndex, 4, String.format("%.2f", sgpa));
    }

    /**
     * Inserts a CGPA placeholder into the specified sheet at the given row
     * index.
     *
     * @param sheet the Excel sheet
     * @param rowIndex the row index to insert the CGPA
     * @param cgpa the cumulative GPA
     */
    private static void insertCgpaPlaceholder(Sheet sheet, int rowIndex, double cgpa) {
        setCellValue(sheet, rowIndex, 0, "CGPA");
        setCellValue(sheet, rowIndex, 1, String.format("%.2f", cgpa));
    }

    /**
     * Inserts course headers into the specified sheet at the given row index.
     *
     * @param sheet the Excel sheet
     * @param rowIndex the row index to insert the headers
     */
    private static void insertCourseHeaders(Sheet sheet, int rowIndex) {
        String[] headers = {"Course Code", "Course Title", "Credit Hrs", "Grade", "Grade Point", "Academic Year"};
        for (int col = 0; col < headers.length; col++) {
            setCellValue(sheet, rowIndex, col, headers[col]);
        }
    }

    /**
     * Converts a letter grade to a grade point.
     *
     * @param grade the letter grade (e.g., "A+", "B-")
     * @return the corresponding grade point
     */
    private static double gradeToPoint(String grade) {
        Map<String, Double> gradeMap = new HashMap<>();
        gradeMap.put("A+", 4.0);
        gradeMap.put("A", 4.0);
        gradeMap.put("A-", 3.7);
        gradeMap.put("B+", 3.5);
        gradeMap.put("B", 3.0);
        gradeMap.put("B-", 2.7);
        gradeMap.put("C+", 2.5);
        gradeMap.put("C", 2.0);
        gradeMap.put("D", 1.0);
        gradeMap.put("F", 0.0);

        return gradeMap.getOrDefault(grade, 0.0);
    }

    /**
     * Generates a transcript for the specified student and saves it as an Excel
     * file.
     *
     * @param studentId the ID of the student
     * @throws IOException if an I/O error occurs
     * @throws IllegalArgumentException if the student ID is invalid or the
     * student is not found
     */
    public static void generateTranscript(String studentId) throws IOException, IllegalArgumentException {
        // Use FilePathUtils to get the dynamic file paths
        String outputFilePath = FilePathUtils.getTranscriptFilePath(studentId); // Dynamic output path
        String inputFilePath = FilePathUtils.getTranscriptTemplatePath(); // Dynamic template path

        try (FileInputStream file = new FileInputStream(inputFilePath); Workbook workbook = new XSSFWorkbook(file)) {

            // Insert basic student details into the first sheet
            insertBasicDetails(workbook.getSheetAt(0), studentId);

            // Insert semester records into the second sheet
            insertSemesterRecords(workbook.getSheetAt(1), studentId, 4);

            // Save the updated workbook with the dynamically generated file name
            try (FileOutputStream outFile = new FileOutputStream(outputFilePath)) {
                workbook.write(outFile);
                System.out.println("Transcript successfully updated and saved as " + outputFilePath);
            }
        }
    }
}
