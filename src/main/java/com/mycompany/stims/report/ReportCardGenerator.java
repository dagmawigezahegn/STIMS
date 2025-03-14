package com.mycompany.stims.report;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.utils.FilePathUtils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * The ReportCardGenerator class is responsible for generating report cards for
 * students. It fetches student data and course details from the database and
 * populates them into an Excel template to create a report card.
 */
public class ReportCardGenerator {

    /**
     * Fetches student data from the database for a specific semester.
     *
     * @param studentIdNo the student ID number
     * @param year the academic year
     * @param semester the semester
     * @return a map containing student details such as name, ID, program,
     * department, year, semester, and SGPA
     * @throws SQLException if a database access error occurs or no data is
     * found
     */
    public static Map<String, Object> fetchStudentData(String studentIdNo, int year, int semester) throws SQLException {
        Map<String, Object> studentData = new HashMap<>();
        String studentQuery = "SELECT s.first_name, s.middle_name, s.last_name, s.studentId_No, p.program_name, "
                + "d.department_name, sar.year, sar.semester, sar.sgpa "
                + "FROM student s "
                + "JOIN studentacademicrecord sar ON s.student_id = sar.student_id "
                + "JOIN program p ON s.program_id = p.program_id "
                + "JOIN department d ON s.department_id = d.department_id "
                + "WHERE s.studentId_No = ? AND sar.year = ? AND sar.semester = ? "
                + "LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(studentQuery)) {

            pstmt.setString(1, studentIdNo);
            pstmt.setInt(2, year);
            pstmt.setInt(3, semester);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    studentData.put("name", rs.getString("first_name") + " " + rs.getString("middle_name") + " " + rs.getString("last_name"));
                    studentData.put("studentId_No", rs.getString("studentId_No"));
                    studentData.put("program", rs.getString("program_name"));
                    studentData.put("department", rs.getString("department_name"));
                    studentData.put("year", rs.getInt("year"));
                    studentData.put("semester", "Semester " + rs.getInt("semester"));
                    studentData.put("sgpa", rs.getDouble("sgpa"));
                } else {
                    throw new SQLException("No student data found for studentId_No: " + studentIdNo
                            + ", year: " + year + ", semester: " + semester);
                }
            }
        }
        return studentData;
    }

    /**
     * Fetches a student's courses and grades for a specific semester.
     *
     * @param studentIdNo the student ID number
     * @param year the academic year
     * @param semester the semester
     * @return a list of arrays containing course details such as course code,
     * course name, credits, and grade
     * @throws SQLException if a database access error occurs
     */
    public static List<Object[]> fetchStudentCourses(String studentIdNo, int year, int semester) throws SQLException {
        List<Object[]> courses = new ArrayList<>();
        String courseQuery = "SELECT c.course_code, c.course_name, c.credits, g.grade "
                + "FROM studentcourse sc "
                + "JOIN courseoffering co ON sc.offering_id = co.offering_id "
                + "JOIN course c ON co.course_id = c.course_id "
                + "JOIN grade g ON sc.student_course_id = g.student_course_id "
                + "WHERE sc.student_id = (SELECT student_id FROM student WHERE studentId_No = ?) "
                + "AND co.year = ? AND co.semester = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(courseQuery)) {

            pstmt.setString(1, studentIdNo);
            pstmt.setInt(2, year);
            pstmt.setInt(3, semester);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] courseData = {
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("credits"),
                        rs.getString("grade")
                    };
                    courses.add(courseData);
                }
            }
        }
        return courses;
    }

    /**
     * Generates a report card for a specific semester.
     *
     * @param studentIdNo the student ID number
     * @param year the academic year
     * @param semester the semester
     * @return true if the report card is generated successfully, false
     * otherwise
     */
    public static boolean generateReportCard(String studentIdNo, int year, int semester) {
        String outputPath = FilePathUtils.getReportCardFilePath(studentIdNo, year, semester);
        String templatePath = FilePathUtils.getReportCardTemplatePath();

        try (FileInputStream file = new FileInputStream(templatePath); Workbook workbook = new XSSFWorkbook(file)) {

            // Access the first sheet
            Sheet sheet = workbook.getSheetAt(0);

            // Fetch student data and populate the sheet
            Map<String, Object> studentData = fetchStudentData(studentIdNo, year, semester);
            if (studentData == null || studentData.isEmpty()) {
                throw new SQLException("No student data found for the provided details.");
            }
            populateStudentDetails(sheet, studentData);

            // Fetch and populate courses and grades for the specific semester
            List<Object[]> courses = fetchStudentCourses(studentIdNo, year, semester);
            if (courses == null || courses.isEmpty()) {
                throw new SQLException("No courses found for the provided details.");
            }
            populateCourses(sheet, courses);

            // Save the updated Excel report card
            try (FileOutputStream outFile = new FileOutputStream(outputPath)) {
                workbook.write(outFile);
            }

            System.out.println("Excel Report Card generated successfully!");
            return true;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("File I/O error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Populates student details into the Excel sheet.
     *
     * @param sheet the Excel sheet to populate
     * @param studentData a map containing student details
     */
    private static void populateStudentDetails(Sheet sheet, Map<String, Object> studentData) {
        Row nameRow = sheet.getRow(2);
        if (nameRow == null) {
            nameRow = sheet.createRow(2);
        }
        nameRow.createCell(1).setCellValue((String) studentData.getOrDefault("name", "N/A"));

        Row studentIdRow = sheet.getRow(3);
        if (studentIdRow == null) {
            studentIdRow = sheet.createRow(3);
        }
        studentIdRow.createCell(1).setCellValue((String) studentData.getOrDefault("studentId_No", "N/A"));

        Row programRow = sheet.getRow(4);
        if (programRow == null) {
            programRow = sheet.createRow(4);
        }
        programRow.createCell(1).setCellValue((String) studentData.getOrDefault("program", "N/A"));

        Row departmentRow = sheet.getRow(5);
        if (departmentRow == null) {
            departmentRow = sheet.createRow(5);
        }
        departmentRow.createCell(1).setCellValue((String) studentData.getOrDefault("department", "N/A"));

        Row yearRow = sheet.getRow(6);
        if (yearRow == null) {
            yearRow = sheet.createRow(6);
        }
        yearRow.createCell(1).setCellValue(studentData.getOrDefault("year", "N/A").toString());

        Row semesterRow = sheet.getRow(7);
        if (semesterRow == null) {
            semesterRow = sheet.createRow(7);
        }
        semesterRow.createCell(1).setCellValue((String) studentData.getOrDefault("semester", "N/A"));

        Row sgpaRow = sheet.getRow(8);
        if (sgpaRow == null) {
            sgpaRow = sheet.createRow(8);
        }
        sgpaRow.createCell(1).setCellValue(studentData.getOrDefault("sgpa", "N/A").toString());
    }

    /**
     * Populates course details into the Excel sheet.
     *
     * @param sheet the Excel sheet to populate
     * @param courses a list of arrays containing course details
     */
    private static void populateCourses(Sheet sheet, List<Object[]> courses) {
        int startingRow = 10; // Starting row for courses

        for (int i = 0; i < courses.size(); i++) {
            Row courseRow = sheet.getRow(startingRow + i);
            if (courseRow == null) {
                courseRow = sheet.createRow(startingRow + i);
            }

            courseRow.createCell(0).setCellValue((String) courses.get(i)[0]); // Course Code
            courseRow.createCell(1).setCellValue((String) courses.get(i)[1]); // Course Title
            courseRow.createCell(2).setCellValue((Integer) courses.get(i)[2]); // Credit Hours
            courseRow.createCell(3).setCellValue((String) courses.get(i)[3]); // Grade

            double gradePoint = gradeToPoint((String) courses.get(i)[3]);
            courseRow.createCell(4).setCellValue(gradePoint); // Grade Point
        }
    }

    /**
     * Maps a grade to its corresponding grade point.
     *
     * @param grade the grade to map
     * @return the corresponding grade point
     */
    public static double gradeToPoint(String grade) {
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
}
