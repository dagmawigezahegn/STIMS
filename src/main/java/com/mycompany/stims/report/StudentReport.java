package com.mycompany.stims.report;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.model.Student;
import com.mycompany.stims.utils.FilePathUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The `StudentReport` class provides functionality to generate and save student
 * reports in PDF format. It includes methods to fetch student details, classify
 * courses, and generate reports for individual students or all students in the
 * database.
 */
public class StudentReport {

    /**
     * Generates a PDF report for a specific student based on their student ID
     * number. The report includes student details, enrolled courses, and a
     * footer with the generation date.
     *
     * @param studentIdNo The student ID number for which the report is
     * generated.
     * @throws IOException If an I/O error occurs during PDF generation.
     * @throws IllegalArgumentException If the student with the given ID is not
     * found.
     */
    public static void generateStudentReport(String studentIdNo) throws IOException {
        Student student = getStudentFromDb(studentIdNo);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + studentIdNo + " not found.");
        }
        String currentDate = LocalDate.now().toString();  // Get current date in YYYY-MM-DD format

        // Use FilePathUtils to get the dynamic file path
        String filePath = FilePathUtils.getStudentReportFilePath(studentIdNo, currentDate);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        // Create a ByteArrayOutputStream to hold the PDF content
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); PdfWriter writer = new PdfWriter(outputStream); PdfDocument pdfDoc = new PdfDocument(writer); Document document = new Document(pdfDoc)) {

            // Adding report header
            document.add(new Paragraph("STUDENT REPORT")
                    .setBold()
                    .setFontSize(24)
                    .setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Add student details in a table
            addStudentDetails(document, student);

            // Fetch courses classified by academic_year, year, and semester
            Map<String, List<Map<String, String>>> classifiedCourses = getClassifiedCoursesForStudent(studentIdNo);

            if (!classifiedCourses.isEmpty()) {
                document.add(new Paragraph("Courses Enrolled:")
                        .setBold()
                        .setFontSize(18)
                        .setFontColor(ColorConstants.DARK_GRAY)
                        .setMarginTop(20)
                        .setMarginBottom(10));

                for (Map.Entry<String, List<Map<String, String>>> entry : classifiedCourses.entrySet()) {
                    String classification = entry.getKey();
                    List<Map<String, String>> courses = entry.getValue();

                    // Add classification header (e.g., "Academic Year: 2023, Year: 2, Semester: 1")
                    document.add(new Paragraph(classification)
                            .setBold()
                            .setFontSize(16)
                            .setFontColor(ColorConstants.BLACK)
                            .setMarginTop(15)
                            .setMarginBottom(10));

                    // Create a table for courses under this classification
                    Table courseTable = new Table(UnitValue.createPercentArray(new float[]{4, 3, 3})).useAllAvailableWidth();
                    courseTable.addCell(new Cell().add(new Paragraph("Course Name").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                    courseTable.addCell(new Cell().add(new Paragraph("Enrollment Date").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                    courseTable.addCell(new Cell().add(new Paragraph("Status").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));

                    for (Map<String, String> course : courses) {
                        courseTable.addCell(new Cell().add(new Paragraph(course.get("course_name"))));
                        courseTable.addCell(new Cell().add(new Paragraph(course.get("enrollment_date"))));
                        courseTable.addCell(new Cell().add(new Paragraph(course.get("status"))));
                    }

                    document.add(courseTable);
                    document.add(new Paragraph(" ").setMarginBottom(10)); // Add space between classifications
                }
            }

            // Add a footer section
            Paragraph footer = new Paragraph("Report generated on " + LocalDate.now() + " by STIMS")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY)
                    .setMarginTop(20);
            document.add(footer);

            // Finalize the PDF document
            document.close();

            // Convert the PDF content to a byte array
            byte[] pdfContent = outputStream.toByteArray();

            // Save the report to the database
            saveReportToDatabase(student.getStudentId(), "Student_Report_" + LocalDate.now() + ".pdf", pdfContent);

            // Save the report to a file
            try (PdfWriter fileWriter = new PdfWriter(filePath); PdfDocument filePdfDoc = new PdfDocument(fileWriter); Document fileDocument = new Document(filePdfDoc)) {

                // Add report content (header, student details, courses, footer)
                fileDocument.add(new Paragraph("STUDENT REPORT")
                        .setBold()
                        .setFontSize(24)
                        .setFontColor(ColorConstants.BLUE)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20));

                addStudentDetails(fileDocument, student);

                // Add courses and other content
                if (!classifiedCourses.isEmpty()) {
                    fileDocument.add(new Paragraph("Courses Enrolled:")
                            .setBold()
                            .setFontSize(18)
                            .setFontColor(ColorConstants.DARK_GRAY)
                            .setMarginTop(20)
                            .setMarginBottom(10));

                    for (Map.Entry<String, List<Map<String, String>>> entry : classifiedCourses.entrySet()) {
                        String classification = entry.getKey();
                        List<Map<String, String>> courses = entry.getValue();

                        fileDocument.add(new Paragraph(classification)
                                .setBold()
                                .setFontSize(16)
                                .setFontColor(ColorConstants.BLACK)
                                .setMarginTop(15)
                                .setMarginBottom(10));

                        Table courseTable = new Table(UnitValue.createPercentArray(new float[]{4, 3, 3})).useAllAvailableWidth();
                        courseTable.addCell(new Cell().add(new Paragraph("Course Name").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                        courseTable.addCell(new Cell().add(new Paragraph("Enrollment Date").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                        courseTable.addCell(new Cell().add(new Paragraph("Status").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));

                        for (Map<String, String> course : courses) {
                            courseTable.addCell(new Cell().add(new Paragraph(course.get("course_name"))));
                            courseTable.addCell(new Cell().add(new Paragraph(course.get("enrollment_date"))));
                            courseTable.addCell(new Cell().add(new Paragraph(course.get("status"))));
                        }

                        fileDocument.add(courseTable);
                        fileDocument.add(new Paragraph(" ").setMarginBottom(10)); // Add space between classifications
                    }
                }

                // Add a footer section
                Paragraph fileFooter = new Paragraph("Report generated on " + LocalDate.now() + " by STIMS")
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setFontSize(10)
                        .setFontColor(ColorConstants.GRAY)
                        .setMarginTop(20);
                fileDocument.add(fileFooter);
            }

            System.out.println("Report generated and saved to the database and file successfully.");
        } catch (IOException | SQLException e) {
            System.err.println("Error generating or saving report: " + e.getMessage());
        }
    }

    /**
     * Saves the PDF report to the database.
     *
     * @param studentId The ID of the student.
     * @param reportName The name of the report.
     * @param pdfContent The PDF content as a byte array.
     * @throws SQLException If there is an error executing the SQL query.
     */
    private static void saveReportToDatabase(int studentId, String reportName, byte[] pdfContent) throws SQLException {
        String query = "INSERT INTO student_reports (student_id, report_name, report_content) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            stmt.setString(2, reportName);
            stmt.setBytes(3, pdfContent);

            stmt.executeUpdate();
        }
    }

    /**
     * Fetches courses for a specific student, classified by academic year,
     * year, and semester.
     *
     * @param studentIdNo The student ID number.
     * @return A map where the key is a classification string (e.g., "Academic
     * Year: 2023, Year: 2, Semester: 1") and the value is a list of course
     * details (course name, enrollment date, and status).
     */
    private static Map<String, List<Map<String, String>>> getClassifiedCoursesForStudent(String studentIdNo) {
        Map<String, List<Map<String, String>>> classifiedCourses = new HashMap<>();
        String query = "SELECT c.course_name, sc.enrollment_date, co.academic_year, co.year, co.semester "
                + "FROM studentcourse sc "
                + "JOIN courseoffering co ON sc.offering_id = co.offering_id "
                + "JOIN course c ON co.course_id = c.course_id "
                + "JOIN student s ON sc.student_id = s.student_id "
                + "WHERE s.studentId_No = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, studentIdNo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String academicYear = rs.getString("academic_year");
                    int year = rs.getInt("year");
                    int semester = rs.getInt("semester");
                    String classification = "Academic Year: " + academicYear + ", Year: " + year + ", Semester: " + semester;

                    Map<String, String> courseInfo = new HashMap<>();
                    courseInfo.put("course_name", rs.getString("course_name"));
                    courseInfo.put("enrollment_date", rs.getDate("enrollment_date").toString());
                    courseInfo.put("status", "Enrolled"); // Default status

                    classifiedCourses.computeIfAbsent(classification, k -> new ArrayList<>()).add(courseInfo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching classified courses for student: " + e.getMessage());
            e.printStackTrace();
        }
        return classifiedCourses;
    }

    /**
     * Fetches a student's details from the database using their student ID
     * number.
     *
     * @param studentIdNo The student ID number.
     * @return A `Student` object containing the student's details, or `null` if
     * the student is not found.
     */
    private static Student getStudentFromDb(String studentIdNo) {
        String query = "SELECT s.*, p.program_name, d.department_name "
                + "FROM student s "
                + "LEFT JOIN program p ON s.program_id = p.program_id "
                + "LEFT JOIN department d ON s.department_id = d.department_id "
                + "WHERE s.studentId_No = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, studentIdNo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student( // Return the Student object
                            rs.getInt("student_id"),
                            rs.getString("studentId_No"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("first_name"),
                            rs.getString("middle_name"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            rs.getString("phone_number"),
                            rs.getString("address"),
                            rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null,
                            rs.getInt("program_id"),
                            rs.getInt("department_id"),
                            rs.getString("sex"),
                            rs.getDate("enrollment_date").toLocalDate(),
                            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                            rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student details: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Return null if the student is not found
    }

    /**
     * Adds student details to the PDF document in a tabular format.
     *
     * @param document The PDF document to which the details are added.
     * @param student The `Student` object containing the details to be added.
     */
    private static void addStudentDetails(Document document, Student student) {
        Table studentTable = new Table(UnitValue.createPercentArray(new float[]{3, 7})).useAllAvailableWidth();
        studentTable.addCell(new Cell().add(new Paragraph("Student ID-No").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        studentTable.addCell(new Cell().add(new Paragraph(student.getStudentIdNo())));
        studentTable.addCell(new Cell().add(new Paragraph("Name").setBold()));
        studentTable.addCell(new Cell().add(new Paragraph(student.getFirstName() + " " + (student.getMiddleName() != null ? student.getMiddleName() + " " : "") + student.getLastName())));
        studentTable.addCell(new Cell().add(new Paragraph("Username").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        studentTable.addCell(new Cell().add(new Paragraph(student.getUsername())));
        studentTable.addCell(new Cell().add(new Paragraph("Email").setBold()));
        studentTable.addCell(new Cell().add(new Paragraph(student.getEmail() != null ? student.getEmail() : "N/A")));
        studentTable.addCell(new Cell().add(new Paragraph("Phone Number").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        studentTable.addCell(new Cell().add(new Paragraph(student.getPhoneNumber() != null ? student.getPhoneNumber() : "N/A")));
        studentTable.addCell(new Cell().add(new Paragraph("Address").setBold()));
        studentTable.addCell(new Cell().add(new Paragraph(student.getAddress() != null ? student.getAddress() : "N/A")));
        studentTable.addCell(new Cell().add(new Paragraph("Date of Birth").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        studentTable.addCell(new Cell().add(new Paragraph(student.getDateOfBirth() != null ? student.getDateOfBirth().toString() : "N/A")));
        studentTable.addCell(new Cell().add(new Paragraph("Program").setBold()));
        studentTable.addCell(new Cell().add(new Paragraph(getProgramName(student.getProgramId()))));
        studentTable.addCell(new Cell().add(new Paragraph("Department").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        studentTable.addCell(new Cell().add(new Paragraph(getDepartmentName(student.getDepartmentId()))));
        studentTable.addCell(new Cell().add(new Paragraph("Sex").setBold()));
        studentTable.addCell(new Cell().add(new Paragraph(student.getSex())));
        studentTable.addCell(new Cell().add(new Paragraph("Enrollment Date").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        studentTable.addCell(new Cell().add(new Paragraph(student.getEnrollmentDate().toString())));

        document.add(studentTable);
        document.add(new Paragraph(" ").setMarginBottom(20)); // Add space after the table
    }

    /**
     * Fetches the program name for a given program ID.
     *
     * @param programId The program ID.
     * @return The program name, or "N/A" if the program is not found.
     */
    private static String getProgramName(int programId) {
        String query = "SELECT program_name FROM program WHERE program_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, programId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("program_name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching program name: " + e.getMessage());
        }
        return "N/A";
    }

    /**
     * Fetches the department name for a given department ID.
     *
     * @param departmentId The department ID.
     * @return The department name, or "N/A" if the department is not found.
     */
    private static String getDepartmentName(int departmentId) {
        String query = "SELECT department_name FROM department WHERE department_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("department_name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching department name: " + e.getMessage());
        }
        return "N/A";
    }

    /**
     * Generates a PDF report for all students in the database. The report
     * includes a table with student ID, name, sex, program, and department.
     */
    public static void generateAllStudentsReport() {
        List<Student> students = getAllStudentsFromDb();
        String currentDate = LocalDate.now().toString();  // Get current date in YYYY-MM-DD format

        // Use FilePathUtils to get the dynamic file path
        String filePath = FilePathUtils.getAllStudentsReportFilePath(currentDate);

        // Create a PDF document
        try (PdfWriter writer = new PdfWriter(filePath); PdfDocument pdfDoc = new PdfDocument(writer); Document document = new Document(pdfDoc)) {

            // Adding report title
            document.add(new Paragraph("ALL STUDENTS REPORT")
                    .setBold()
                    .setFontSize(24)
                    .setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Adding table header
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 4, 2, 3, 3})).useAllAvailableWidth();
            table.addCell(new Cell().add(new Paragraph("Student ID").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addCell(new Cell().add(new Paragraph("Name").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addCell(new Cell().add(new Paragraph("Sex").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addCell(new Cell().add(new Paragraph("Program").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addCell(new Cell().add(new Paragraph("Department").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Adding each student's details to the table
            for (Student student : students) {
                table.addCell(new Cell().add(new Paragraph(student.getStudentIdNo())));
                table.addCell(new Cell().add(new Paragraph(student.getFirstName() + " " + (student.getMiddleName() != null ? student.getMiddleName() + " " : "") + student.getLastName())));
                table.addCell(new Cell().add(new Paragraph(student.getSex())));
                table.addCell(new Cell().add(new Paragraph(getProgramName(student.getProgramId()))));
                table.addCell(new Cell().add(new Paragraph(getDepartmentName(student.getDepartmentId()))));
            }

            document.add(table);

            // Add a footer section
            Paragraph footer = new Paragraph("Report generated on " + LocalDate.now() + " by STIMS")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY)
                    .setMarginTop(20);
            document.add(footer);

            System.out.println("All students report generated successfully: " + filePath);
        } catch (IOException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }

    /**
     * Fetches all students from the database.
     *
     * @return A list of `Student` objects containing details of all students.
     */
    private static List<Student> getAllStudentsFromDb() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM student"; // Adjust table name if necessary

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Student student = new Student(
                        rs.getInt("student_id"),
                        rs.getString("studentId_No"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null,
                        rs.getInt("program_id"),
                        rs.getInt("department_id"),
                        rs.getString("sex"),
                        rs.getDate("enrollment_date").toLocalDate(),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                        rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null
                );

                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all students: " + e.getMessage());
        }
        return students; // Return the list of students
    }
}
