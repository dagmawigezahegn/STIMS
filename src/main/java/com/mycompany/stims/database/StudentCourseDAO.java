package com.mycompany.stims.database;

import com.mycompany.stims.model.StudentCourse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The `StudentCourseDAO` class provides data access operations for the
 * `StudentCourse` entity. It allows for enrolling students in courses,
 * retrieving student-course records, updating enrollments, and deleting
 * enrollments. This class interacts with the database using a provided
 * `Connection` object.
 */
public class StudentCourseDAO {

    private Connection connection;

    /**
     * Constructs a `StudentCourseDAO` object with the specified database
     * connection.
     *
     * @param connection The database connection to be used for data access
     * operations.
     */
    public StudentCourseDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Enrolls a student in a course offering by adding a new `StudentCourse`
     * record to the database.
     *
     * @param studentCourse The `StudentCourse` object containing the student
     * ID, offering ID, and enrollment date.
     */
    public void addStudentCourse(StudentCourse studentCourse) {
        String sql = "INSERT INTO StudentCourse (student_id, offering_id, enrollment_date) "
                + "VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentCourse.getStudentId());
            pstmt.setInt(2, studentCourse.getOfferingId());
            pstmt.setDate(3, studentCourse.getEnrollmentDate());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding student course: " + e.getMessage());
        }
    }

    /**
     * Retrieves a `StudentCourse` record from the database by its ID.
     *
     * @param studentCourseId The ID of the student-course record to retrieve.
     * @return A `StudentCourse` object representing the retrieved record, or
     * `null` if no record is found.
     */
    public StudentCourse getStudentCourseById(int studentCourseId) {
        String sql = "SELECT * FROM StudentCourse WHERE student_course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentCourseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new StudentCourse(
                            rs.getInt("student_course_id"),
                            rs.getInt("student_id"),
                            rs.getInt("offering_id"),
                            rs.getDate("enrollment_date"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student course: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all `StudentCourse` records from the database.
     *
     * @return A list of `StudentCourse` objects representing all student-course
     * records, or an empty list if no records are found.
     */
    public List<StudentCourse> getAllStudentCourses() {
        List<StudentCourse> studentCourses = new ArrayList<>();
        String sql = "SELECT * FROM StudentCourse";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                studentCourses.add(new StudentCourse(
                        rs.getInt("student_course_id"),
                        rs.getInt("student_id"),
                        rs.getInt("offering_id"),
                        rs.getDate("enrollment_date"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student courses: " + e.getMessage());
        }
        return studentCourses;
    }

    /**
     * Updates an existing `StudentCourse` record in the database.
     *
     * @param studentCourse The `StudentCourse` object containing the updated
     * data.
     */
    public void updateStudentCourse(StudentCourse studentCourse) {
        String sql = "UPDATE StudentCourse SET student_id = ?, offering_id = ?, enrollment_date = ? "
                + "WHERE student_course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentCourse.getStudentId());
            pstmt.setInt(2, studentCourse.getOfferingId());
            pstmt.setDate(3, studentCourse.getEnrollmentDate());
            pstmt.setInt(4, studentCourse.getStudentCourseId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating student course: " + e.getMessage());
        }
    }

    /**
     * Deletes a `StudentCourse` record from the database by its ID. If the
     * record has an associated grade, the deletion is not allowed.
     *
     * @param studentCourseId The ID of the student-course record to delete.
     * @return `true` if the deletion was successful, `false` if the record has
     * an associated grade or if the deletion fails.
     */
    public boolean deleteStudentCourse(int studentCourseId) {
        // Check if the student-course record has an associated grade
        String checkGradeSql = "SELECT COUNT(*) FROM grade WHERE student_course_id = ?";
        String deleteStudentCourseSql = "DELETE FROM studentcourse WHERE student_course_id = ?";

        try (PreparedStatement checkGradeStmt = connection.prepareStatement(checkGradeSql); PreparedStatement deleteStudentCourseStmt = connection.prepareStatement(deleteStudentCourseSql)) {

            // Step 1: Check if the student-course record has an associated grade
            checkGradeStmt.setInt(1, studentCourseId);
            ResultSet gradeResult = checkGradeStmt.executeQuery();
            if (gradeResult.next() && gradeResult.getInt(1) > 0) {
                // Student-course record has an associated grade, so deletion is not allowed
                return false;
            }

            // Step 2: If no associated grade, proceed with deletion
            deleteStudentCourseStmt.setInt(1, studentCourseId);
            int rowsDeleted = deleteStudentCourseStmt.executeUpdate();

            // Return true if the deletion was successful
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting student course: " + e.getMessage());
            return false;
        }
    }
}
