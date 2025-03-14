package com.mycompany.stims.report;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.model.Course;
import com.mycompany.stims.utils.FilePathUtils;
import com.mycompany.stims.utils.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for generating course-related reports in PDF
 * format. It includes methods to generate individual course reports, all
 * courses reports, and course statistics reports.
 */
public class CourseReport {

    /**
     * Generates a PDF report for a specific course.
     *
     * @param courseCode The code of the course for which the report is
     * generated.
     * @throws IOException If an I/O error occurs during report generation.
     * @throws IllegalArgumentException If the course with the specified code is
     * not found.
     */
    public static void generateCourseReport(String courseCode) throws IOException {
        // Fetch course details from the database
        Course course = getCourseFromDb(courseCode);
        if (course == null) {
            throw new IllegalArgumentException("Course with code " + courseCode + " not found.");
        }

        // Generate file name based on course code and current date
        String currentDate = java.time.LocalDate.now().toString();  // Get current date in YYYY-MM-DD format

        // Use FilePathUtils to get the file path
        FilePathUtils.ensureDirectoryExists(Config.getCourseReportDirectory());
        String filePath = FilePathUtils.getCourseReportFilePath(courseCode, currentDate);

        // Calculate student statistics
        int studentCount = getEnrollmentCount(courseCode);
        double avgGrade = calculateAverageGrade(courseCode);

        // Generate the PDF report
        try (PdfWriter writer = new PdfWriter(filePath); PdfDocument pdfDoc = new PdfDocument(writer); Document document = new Document(pdfDoc)) {

            // Add a title with custom styling
            Paragraph title = new Paragraph("COURSE REPORT")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(24)
                    .setFontColor(ColorConstants.BLUE)
                    .setMarginBottom(20);
            document.add(title);

            // Add course details in a table
            Table courseDetailsTable = new Table(UnitValue.createPercentArray(new float[]{3, 7}))
                    .useAllAvailableWidth();

            // Add course details
            courseDetailsTable.addCell(new Cell().add(new Paragraph("Course Code").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            courseDetailsTable.addCell(new Cell().add(new Paragraph(course.getCourseCode())));

            courseDetailsTable.addCell(new Cell().add(new Paragraph("Course Name").setBold()));
            courseDetailsTable.addCell(new Cell().add(new Paragraph(course.getCourseName())));

            courseDetailsTable.addCell(new Cell().add(new Paragraph("Description").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            courseDetailsTable.addCell(new Cell().add(new Paragraph(course.getCourseDescription())));

            courseDetailsTable.addCell(new Cell().add(new Paragraph("Credits").setBold()));
            courseDetailsTable.addCell(new Cell().add(new Paragraph(String.valueOf(course.getCredits()))));

            courseDetailsTable.addCell(new Cell().add(new Paragraph("Course Level").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            courseDetailsTable.addCell(new Cell().add(new Paragraph(String.valueOf(course.getCourseLevel()))));

            courseDetailsTable.addCell(new Cell().add(new Paragraph("Year").setBold()));
            courseDetailsTable.addCell(new Cell().add(new Paragraph(String.valueOf(course.getYear()))));

            courseDetailsTable.addCell(new Cell().add(new Paragraph("Semester").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            courseDetailsTable.addCell(new Cell().add(new Paragraph(String.valueOf(course.getSemester()))));

            document.add(courseDetailsTable);

            document.add(new Paragraph(" ").setMarginBottom(20)); // Spacer

            // Add student statistics section
            Paragraph studentStatsTitle = new Paragraph("STUDENT STATISTICS")
                    .setBold()
                    .setFontSize(18)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginBottom(10);
            document.add(studentStatsTitle);

            // Add student statistics in a table
            Table studentStatsTable = new Table(UnitValue.createPercentArray(new float[]{5, 5}))
                    .useAllAvailableWidth();

            studentStatsTable.addCell(new Cell().add(new Paragraph("Number of Students Enrolled").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            studentStatsTable.addCell(new Cell().add(new Paragraph(String.valueOf(studentCount))));

            studentStatsTable.addCell(new Cell().add(new Paragraph("Average Grade").setBold()));
            studentStatsTable.addCell(new Cell().add(new Paragraph(avgGrade == 0 ? "N/A" : String.format("%.2f", avgGrade))));

            document.add(studentStatsTable);

            document.add(new Paragraph(" ").setMarginBottom(20)); // Spacer

            // Add a footer section
            Paragraph footer = new Paragraph("Report generated on " + java.time.LocalDate.now() + " by STIMS")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY)
                    .setMarginTop(20);
            document.add(footer);

            System.out.println("Report generated successfully: " + filePath);
        }
    }

    /**
     * Calculates the average grade for a specific course.
     *
     * @param courseCode The code of the course for which the average grade is
     * calculated.
     * @return The average grade as a double. Returns 0.0 if no grades are
     * found.
     */
    private static double calculateAverageGrade(String courseCode) {
        String query = "SELECT g.grade FROM grade g "
                + "JOIN studentcourse sc ON g.student_course_id = sc.student_course_id "
                + "JOIN courseoffering co ON sc.offering_id = co.offering_id "
                + "JOIN course c ON co.course_id = c.course_id "
                + "WHERE c.course_code = ?";
        Map<String, Double> gradePointMap = new HashMap<>();
        gradePointMap.put("A+", 4.0);
        gradePointMap.put("A", 4.0);
        gradePointMap.put("A-", 3.7);
        gradePointMap.put("B+", 3.5);
        gradePointMap.put("B", 3.0);
        gradePointMap.put("B-", 2.7);
        gradePointMap.put("C+", 2.5);
        gradePointMap.put("C", 2.0);
        gradePointMap.put("D", 1.0);
        gradePointMap.put("F", 0.0);

        double totalGradePoints = 0;
        int gradeCount = 0;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String grade = rs.getString("grade");
                    Double gradePoint = gradePointMap.get(grade);
                    if (gradePoint != null) {
                        totalGradePoints += gradePoint;
                        gradeCount++;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating average grade: " + e.getMessage());
        }

        return gradeCount > 0 ? totalGradePoints / gradeCount : 0.0; // Return the average or 0 if no grades
    }

    /**
     * Fetches course details from the database.
     *
     * @param courseCode The code of the course to fetch.
     * @return A Course object containing the course details, or null if the
     * course is not found.
     */
    private static Course getCourseFromDb(String courseCode) {
        String query = "SELECT c.course_code, c.course_name, c.course_description, c.credits, "
                + "c.course_level, c.year, c.semester, c.department_id, d.department_name "
                + "FROM course c "
                + "JOIN department d ON c.department_id = d.department_id "
                + "WHERE c.course_code = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Course course = new Course(
                            rs.getString("course_name"),
                            rs.getString("course_description"),
                            rs.getInt("credits"),
                            rs.getInt("course_level"),
                            rs.getInt("year"),
                            rs.getInt("semester"),
                            rs.getInt("department_id")
                    );
                    course.setCourseCode(rs.getString("course_code"));
                    return course;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching course details: " + e.getMessage());
        }
        return null;
    }

    /**
     * Generates a PDF report for all courses.
     */
    public static void generateAllCoursesReport() {
        List<Course> courses = getAllCoursesFromDb();
        // Generate file name based on course code and current date
        String currentDate = java.time.LocalDate.now().toString();  // Get current date in YYYY-MM-DD format

        // Use FilePathUtils to get the file path
        FilePathUtils.ensureDirectoryExists(Config.getAllCoursesDirectory());
        String filePath = FilePathUtils.getAllCoursesReportFilePath(currentDate);

        // Debug: Print the file path
        System.out.println("Attempting to save report to: " + filePath);

        try (PdfWriter writer = new PdfWriter(filePath); PdfDocument pdfDoc = new PdfDocument(writer); Document document = new Document(pdfDoc)) {

            // Adding report title
            document.add(new Paragraph("ALL COURSES REPORT")
                    .setBold()
                    .setFontSize(24)
                    .setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Create a table for course details
            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 4, 2, 2, 3})).useAllAvailableWidth();

            // Add table headers with styling
            table.addHeaderCell(new Cell().add(new Paragraph("Course Code").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Course Name").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Description").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Credits").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Course Level").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Department").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Add course data to the table
            for (Course course : courses) {
                table.addCell(new Cell().add(new Paragraph(course.getCourseCode())));
                table.addCell(new Cell().add(new Paragraph(course.getCourseName())));
                table.addCell(new Cell().add(new Paragraph(course.getCourseDescription())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(course.getCredits()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(course.getCourseLevel()))));
                table.addCell(new Cell().add(new Paragraph(getDepartmentName(course.getDepartmentId()))));
            }

            // Add the table to the document
            document.add(table);

            // Add a footer section
            Paragraph footer = new Paragraph("Report generated on " + java.time.LocalDate.now() + " by STIMS")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY)
                    .setMarginTop(20);
            document.add(footer);

            System.out.println("All courses report generated successfully: " + filePath);
        } catch (IOException e) {
            System.err.println("Error generating report: " + e.getMessage());
            e.printStackTrace(); // Print the full stack trace for debugging
        }
    }

    /**
     * Fetches all courses from the database.
     *
     * @return A list of Course objects containing details of all courses.
     */
    private static List<Course> getAllCoursesFromDb() {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT c.course_code, c.course_name, c.course_description, c.credits, "
                + "c.course_level, c.year, c.semester, c.department_id, d.department_name "
                + "FROM course c "
                + "JOIN department d ON c.department_id = d.department_id";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Course course = new Course(
                        rs.getString("course_name"),
                        rs.getString("course_description"),
                        rs.getInt("credits"),
                        rs.getInt("course_level"),
                        rs.getInt("year"),
                        rs.getInt("semester"),
                        rs.getInt("department_id")
                );
                course.setCourseCode(rs.getString("course_code"));
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all courses: " + e.getMessage());
        }
        return courses;
    }

    /**
     * Gets the number of students enrolled in a specific course.
     *
     * @param courseCode The code of the course.
     * @return The number of students enrolled in the course.
     */
    private static int getEnrollmentCount(String courseCode) {
        String query = "SELECT COUNT(*) FROM studentcourse sc "
                + "JOIN courseoffering co ON sc.offering_id = co.offering_id "
                + "JOIN course c ON co.course_id = c.course_id "
                + "WHERE c.course_code = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching enrollment count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Gets the number of teachers assigned to a specific course.
     *
     * @param courseCode The code of the course.
     * @return The number of teachers assigned to the course.
     */
    private static int getTeacherCount(String courseCode) {
        String query = "SELECT COUNT(*) FROM teachercourse tc "
                + "JOIN courseoffering co ON tc.offering_id = co.offering_id "
                + "JOIN course c ON co.course_id = c.course_id "
                + "WHERE c.course_code = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching teacher count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Gets the name of a department by its ID.
     *
     * @param departmentId The ID of the department.
     * @return The name of the department, or "N/A" if not found.
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
     * Generates a PDF report for course statistics.
     */
    public static void generateCourseStatistics() {
        // Calculate course statistics
        CourseStatistics stats = calculateCourseStatistics();

        // Generate file name based on current date
        String currentDate = java.time.LocalDate.now().toString();

        // Use FilePathUtils to get the file path
        FilePathUtils.ensureDirectoryExists(Config.getCourseStatisticsDirectory());
        String filePath = FilePathUtils.getCourseStatisticsReportFilePath(currentDate);

        try (PdfWriter writer = new PdfWriter(filePath); PdfDocument pdfDoc = new PdfDocument(writer); Document document = new Document(pdfDoc)) {

            // Add title
            document.add(new Paragraph("Course Statistics Report")
                    .setBold()
                    .setFontSize(24)
                    .setFontColor(ColorConstants.BLUE));
            document.add(new Paragraph(" "));

            // Add general statistics
            document.add(new Paragraph("Total Courses: " + stats.getTotalCourses()));
            document.add(new Paragraph("Average Credits: " + stats.getAverageCredits()));
            document.add(new Paragraph("Most Popular Course (By Enrollment): " + stats.getMostPopularCourseByEnrollment()));
            document.add(new Paragraph("Department with Most Courses: " + stats.getDepartmentWithMostCourses()));

            document.add(new Paragraph(" "));

            // Add department-wise distribution
            document.add(new Paragraph("Credits Distribution by Department:"));
            Table table = new Table(2);

            // Add header cells with background color
            table.addCell(new Cell().add(new Paragraph("Department"))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addCell(new Cell().add(new Paragraph("Number of Courses"))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Add data rows
            for (Map.Entry<String, Integer> entry : stats.getDepartmentCourseCount().entrySet()) {
                table.addCell(entry.getKey());
                table.addCell(entry.getValue().toString());
            }

            document.add(table);

            // Add course enrollments
            document.add(new Paragraph(" ")); // Space between sections
            document.add(new Paragraph("Course Enrollment Summary:"));
            Table enrollmentTable = new Table(3);

            // Add header cells with background color
            enrollmentTable.addCell(new Cell().add(new Paragraph("Course Code"))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            enrollmentTable.addCell(new Cell().add(new Paragraph("Course Name"))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            enrollmentTable.addCell(new Cell().add(new Paragraph("Enrollment Count"))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Add data rows
            for (Map.Entry<String, Integer> entry : stats.getCourseEnrollmentCount().entrySet()) {
                enrollmentTable.addCell(entry.getKey());
                enrollmentTable.addCell(stats.getCourseNames().get(entry.getKey()));
                enrollmentTable.addCell(entry.getValue().toString());
            }

            document.add(enrollmentTable);

            // Add footer
            Paragraph footer = new Paragraph("Report generated on " + java.time.LocalDate.now())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY);
            document.add(footer);

            System.out.println("Course statistics report generated successfully: " + filePath);
        } catch (IOException e) {
            System.err.println("Error generating statistics report: " + e.getMessage());
        }
    }

    /**
     * Calculates statistics for all courses.
     *
     * @return A CourseStatistics object containing the calculated statistics.
     */
    private static CourseStatistics calculateCourseStatistics() {
        List<Course> courses = getAllCoursesFromDb();
        int totalCourses = courses.size();
        double totalCredits = 0;
        Map<String, Integer> departmentCourseCount = new HashMap<>();
        Map<String, Integer> courseEnrollmentCount = new HashMap<>();
        Map<String, String> courseNames = new HashMap<>();
        Map<String, Integer> courseTeacherCount = new HashMap<>();

        String mostPopularCourseCode = "";
        int maxEnrollment = 0;
        String departmentWithMostCourses = "";
        int maxCoursesInDept = 0;

        for (Course course : courses) {
            totalCredits += course.getCredits();

            // Department-wise course count
            String departmentName = getDepartmentName(course.getDepartmentId());
            departmentCourseCount.put(departmentName, departmentCourseCount.getOrDefault(departmentName, 0) + 1);

            // Course names
            courseNames.put(course.getCourseCode(), course.getCourseName());

            // Enrollment count for each course
            int enrollmentCount = getEnrollmentCount(course.getCourseCode());
            courseEnrollmentCount.put(course.getCourseCode(), enrollmentCount);
            if (enrollmentCount > maxEnrollment) {
                mostPopularCourseCode = course.getCourseCode();
                maxEnrollment = enrollmentCount;
            }

            // Teacher count for each course
            int teacherCount = getTeacherCount(course.getCourseCode());
            courseTeacherCount.put(course.getCourseCode(), teacherCount);
        }

        // Determine department with most courses
        for (Map.Entry<String, Integer> entry : departmentCourseCount.entrySet()) {
            if (entry.getValue() > maxCoursesInDept) {
                departmentWithMostCourses = entry.getKey();
                maxCoursesInDept = entry.getValue();
            }
        }

        // Calculate average credits
        double averageCredits = totalCourses == 0 ? 0 : totalCredits / totalCourses;

        return new CourseStatistics(
                totalCourses, averageCredits, departmentCourseCount,
                courseEnrollmentCount, courseNames, courseTeacherCount,
                mostPopularCourseCode, departmentWithMostCourses
        );
    }

    /**
     * Inner class to hold course statistics data.
     */
    private static class CourseStatistics {

        private final int totalCourses;
        private final double averageCredits;
        private final Map<String, Integer> departmentCourseCount;
        private final Map<String, Integer> courseEnrollmentCount;
        private final Map<String, String> courseNames;
        private final Map<String, Integer> courseTeacherCount;
        private final String mostPopularCourseByEnrollment;
        private final String departmentWithMostCourses;

        public CourseStatistics(int totalCourses, double averageCredits, Map<String, Integer> departmentCourseCount,
                Map<String, Integer> courseEnrollmentCount, Map<String, String> courseNames,
                Map<String, Integer> courseTeacherCount, String mostPopularCourseByEnrollment,
                String departmentWithMostCourses) {
            this.totalCourses = totalCourses;
            this.averageCredits = averageCredits;
            this.departmentCourseCount = departmentCourseCount;
            this.courseEnrollmentCount = courseEnrollmentCount;
            this.courseNames = courseNames;
            this.courseTeacherCount = courseTeacherCount;
            this.mostPopularCourseByEnrollment = mostPopularCourseByEnrollment;
            this.departmentWithMostCourses = departmentWithMostCourses;
        }

        public int getTotalCourses() {
            return totalCourses;
        }

        public double getAverageCredits() {
            return averageCredits;
        }

        public Map<String, Integer> getDepartmentCourseCount() {
            return departmentCourseCount;
        }

        public Map<String, Integer> getCourseEnrollmentCount() {
            return courseEnrollmentCount;
        }

        public Map<String, String> getCourseNames() {
            return courseNames;
        }

        public Map<String, Integer> getCourseTeacherCount() {
            return courseTeacherCount;
        }

        public String getMostPopularCourseByEnrollment() {
            return mostPopularCourseByEnrollment;
        }

        public String getDepartmentWithMostCourses() {
            return departmentWithMostCourses;
        }
    }
}
