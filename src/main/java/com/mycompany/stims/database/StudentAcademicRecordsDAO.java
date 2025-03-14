package com.mycompany.stims.database;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import com.mycompany.stims.model.StudentAcademicRecord;
import java.util.ArrayList;

/**
 * Data Access Object (DAO) class for managing student academic records in the
 * database. Provides methods to calculate Semester Grade Point Average (SGPA),
 * Cumulative Grade Point Average (CGPA), and update or retrieve academic
 * records for students.
 */
public class StudentAcademicRecordsDAO {

    private static final Logger logger = LoggerFactory.getLogger(StudentAcademicRecordsDAO.class);
    private final Connection connection;

    // Grade conversion table
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
     * Constructs a StudentAcademicRecordsDAO with a specified database
     * connection.
     *
     * @param connection the database connection to be used for operations
     * @throws IllegalArgumentException if the connection is null
     */
    public StudentAcademicRecordsDAO(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Database connection cannot be null.");
        }
        this.connection = connection;
    }

    /**
     * Calculates the Semester Grade Point Average (SGPA) for a student in a
     * specific academic year, year, and semester.
     *
     * @param studentId the ID of the student
     * @param academicYear the academic year
     * @param year the year of study (e.g., 1 for "Year 1")
     * @param semester the semester (e.g., 1 for "Semester 1")
     * @return the calculated SGPA, rounded to 2 decimal places
     * @throws SQLException if a database access error occurs
     * @throws IllegalArgumentException if the academic year or year is invalid
     */
    public double calculateSGPA(int studentId, int academicYear, int year, int semester) throws SQLException {
        validateAcademicYear(academicYear);
        validateYear(year);

        String sql = "SELECT c.credits, g.grade FROM studentcourse sc "
                + "LEFT JOIN grade g ON sc.student_course_id = g.student_course_id "
                + "JOIN courseoffering co ON sc.offering_id = co.offering_id "
                + "JOIN course c ON co.course_id = c.course_id "
                + "WHERE sc.student_id = ? AND co.academic_year = ? AND co.year = ? AND co.semester = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, academicYear);
            ps.setInt(3, year);
            ps.setInt(4, semester);

            ResultSet rs = ps.executeQuery();

            double totalGradePoints = 0.0;
            int totalCredits = 0;

            while (rs.next()) {
                int credits = rs.getInt("credits");
                String grade = rs.getString("grade");

                // Treat missing or invalid grades as 0.0
                double gradePoints = GRADE_POINTS.getOrDefault(grade, 0.0);
                totalGradePoints += gradePoints * credits;
                totalCredits += credits;
            }

            if (totalCredits == 0) {
                return 0.0;  // Avoid division by zero
            }

            // Calculate SGPA and round to 2 decimal places
            return roundToTwoDecimalPlaces(totalGradePoints / totalCredits);
        }
    }

    /**
     * Calculates the Cumulative Grade Point Average (CGPA) for a student.
     *
     * @param studentId the ID of the student
     * @return the calculated CGPA, rounded to 2 decimal places
     * @throws SQLException if a database access error occurs
     */
    public double calculateCGPA(int studentId) throws SQLException {
        String sql = "SELECT c.credits, g.grade FROM studentcourse sc "
                + "LEFT JOIN grade g ON sc.student_course_id = g.student_course_id "
                + "JOIN courseoffering co ON sc.offering_id = co.offering_id "
                + "JOIN course c ON co.course_id = c.course_id "
                + "WHERE sc.student_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();

            double totalGradePoints = 0.0;
            int totalCredits = 0;

            while (rs.next()) {
                int credits = rs.getInt("credits");
                String grade = rs.getString("grade");

                // Treat missing or invalid grades as 0.0
                double gradePoints = GRADE_POINTS.getOrDefault(grade, 0.0);
                totalGradePoints += gradePoints * credits;
                totalCredits += credits;
            }

            if (totalCredits == 0) {
                return 0.0;  // Avoid division by zero
            }

            // Calculate CGPA and round to 2 decimal places
            return roundToTwoDecimalPlaces(totalGradePoints / totalCredits);
        }
    }

    /**
     * Updates the student's academic record for a specific semester.
     *
     * @param studentId the ID of the student
     * @param academicYear the academic year
     * @param year the year of study (e.g., 1 for "Year 1")
     * @param semester the semester (e.g., 1 for "Semester 1")
     * @throws SQLException if a database access error occurs
     * @throws IllegalArgumentException if the academic year or year is invalid
     */
    public void updateStudentAcademicRecord(int studentId, int academicYear, int year, int semester) throws SQLException {
        validateAcademicYear(academicYear);
        validateYear(year);

        int totalCredits = getTotalCreditsForYearAndSemester(studentId, academicYear, year, semester);

        // Skip if there are no courses (totalCredits = 0)
        if (totalCredits == 0) {
            logger.debug("No courses found for studentId: {}, academicYear: {}, year: {}, semester: {}. Skipping record update.",
                    studentId, academicYear, year, semester);
            return;
        }

        double sgpa = calculateSGPA(studentId, academicYear, year, semester);
        double cgpa = calculateCGPA(studentId); // Calculate CGPA for the student

        // Check if a record already exists
        String checkSql = "SELECT COUNT(*) FROM studentacademicrecord WHERE student_id = ? AND academic_year = ? AND year = ? AND semester = ?";
        try (PreparedStatement psCheck = connection.prepareStatement(checkSql)) {
            psCheck.setInt(1, studentId);
            psCheck.setInt(2, academicYear);
            psCheck.setInt(3, year);
            psCheck.setInt(4, semester);

            ResultSet rs = psCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Record already exists, update it
                String updateSql = "UPDATE studentacademicrecord SET total_credits = ?, sgpa = ?, cgpa = ? "
                        + "WHERE student_id = ? AND academic_year = ? AND year = ? AND semester = ?";
                try (PreparedStatement psUpdate = connection.prepareStatement(updateSql)) {
                    psUpdate.setInt(1, totalCredits);
                    psUpdate.setDouble(2, sgpa);
                    psUpdate.setDouble(3, cgpa);
                    psUpdate.setInt(4, studentId);
                    psUpdate.setInt(5, academicYear);
                    psUpdate.setInt(6, year);
                    psUpdate.setInt(7, semester);

                    psUpdate.executeUpdate();
                    logger.info("Updated academic record for studentId: {}, academicYear: {}, year: {}, semester: {}",
                            studentId, academicYear, year, semester);
                }
            } else {
                // Record does not exist, insert a new one
                String insertSql = "INSERT INTO studentacademicrecord (student_id, academic_year, year, semester, total_credits, sgpa, cgpa) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement psInsert = connection.prepareStatement(insertSql)) {
                    psInsert.setInt(1, studentId);
                    psInsert.setInt(2, academicYear);
                    psInsert.setInt(3, year);
                    psInsert.setInt(4, semester);
                    psInsert.setInt(5, totalCredits);
                    psInsert.setDouble(6, sgpa);
                    psInsert.setDouble(7, cgpa); // Insert CGPA

                    psInsert.executeUpdate();
                    logger.info("Inserted new academic record for studentId: {}, academicYear: {}, year: {}, semester: {}",
                            studentId, academicYear, year, semester);
                }
            }
        } catch (SQLException e) {
            logger.error("Error updating academic record for studentId: {}", studentId, e);
            throw e;
        }

        // Update CGPA for all semesters of the student
        updateAllSemestersCGPA(studentId);
    }

    /**
     * Updates the CGPA for all semesters of a student.
     *
     * @param studentId the ID of the student
     * @throws SQLException if a database access error occurs
     */
    private void updateAllSemestersCGPA(int studentId) throws SQLException {
        double cgpa = calculateCGPA(studentId); // Calculate the latest CGPA

        // Update CGPA for all semesters of the student
        String updateSql = "UPDATE studentacademicrecord SET cgpa = ? WHERE student_id = ?";
        try (PreparedStatement psUpdate = connection.prepareStatement(updateSql)) {
            psUpdate.setDouble(1, cgpa);
            psUpdate.setInt(2, studentId);

            int rowsAffected = psUpdate.executeUpdate();
            logger.info("Updated CGPA for all semesters of studentId: {}. Rows affected: {}", studentId, rowsAffected);
        }
    }

    /**
     * Gets the total credits for a student in a specific academic year, year,
     * and semester.
     *
     * @param studentId the ID of the student
     * @param academicYear the academic year
     * @param year the year of study (e.g., 1 for "Year 1")
     * @param semester the semester (e.g., 1 for "Semester 1")
     * @return the total credits for the specified academic year, year, and
     * semester
     * @throws SQLException if a database access error occurs
     */
    private int getTotalCreditsForYearAndSemester(int studentId, int academicYear, int year, int semester) throws SQLException {
        String sql = "SELECT SUM(c.credits) AS total_credits FROM studentcourse sc "
                + "JOIN courseoffering co ON sc.offering_id = co.offering_id "
                + "JOIN course c ON co.course_id = c.course_id "
                + "WHERE sc.student_id = ? AND co.academic_year = ? AND co.year = ? AND co.semester = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, academicYear);
            ps.setInt(3, year);
            ps.setInt(4, semester);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_credits");
            } else {
                return 0;
            }
        }
    }

    /**
     * Retrieves the student ID by their student ID number.
     *
     * @param studentIdNo the student ID number
     * @return the student ID
     * @throws SQLException if no student is found with the given ID number or a
     * database access error occurs
     */
    public int getStudentIdByStudentIdNo(String studentIdNo) throws SQLException {
        String sql = "SELECT student_id FROM student WHERE studentId_No = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, studentIdNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("student_id");
            } else {
                throw new SQLException("No student found with Student ID No: " + studentIdNo);
            }
        }
    }

    /**
     * Retrieves all academic records for a student by their student ID.
     *
     * @param studentId the ID of the student
     * @return a list of StudentAcademicRecord objects representing the
     * student's academic records
     * @throws SQLException if a database access error occurs
     */
    public List<StudentAcademicRecord> getAcademicRecordsByStudentId(int studentId) throws SQLException {
        String sql = "SELECT * FROM studentacademicrecord WHERE student_id = ?";
        List<StudentAcademicRecord> records = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                StudentAcademicRecord record = new StudentAcademicRecord(
                        rs.getInt("student_id"),
                        rs.getInt("academic_year"),
                        rs.getInt("year"),
                        rs.getInt("semester"),
                        rs.getInt("total_credits"),
                        rs.getDouble("sgpa"),
                        rs.getDouble("cgpa")
                );
                records.add(record);
            }
        }

        return records;
    }

    /**
     * Validates the academic year.
     *
     * @param academicYear the academic year to validate
     * @throws IllegalArgumentException if the academic year is invalid
     */
    private void validateAcademicYear(int academicYear) {
        if (academicYear < 1900 || academicYear > 2100) {
            throw new IllegalArgumentException("Academic year must be between 1900 and 2100");
        }
    }

    /**
     * Validates the year of study.
     *
     * @param year the year of study to validate
     * @throws IllegalArgumentException if the year is invalid
     */
    private void validateYear(int year) {
        if (year < 1 || year > 9) { // Assuming a maximum of 10 years
            throw new IllegalArgumentException("Year must be between 1 and 9");
        }
    }

    /**
     * Rounds a double value to 2 decimal places.
     *
     * @param value the value to round
     * @return the rounded value
     */
    private double roundToTwoDecimalPlaces(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
