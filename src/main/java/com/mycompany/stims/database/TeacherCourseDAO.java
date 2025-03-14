package com.mycompany.stims.database;

import com.mycompany.stims.model.TeacherCourse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class for managing TeacherCourse entities in the
 * database. Provides methods to perform CRUD operations on the TeacherCourse
 * table.
 */
public class TeacherCourseDAO {

    private Connection connection;

    /**
     * Constructs a TeacherCourseDAO with the specified database connection.
     *
     * @param connection the database connection to be used for operations
     */
    public TeacherCourseDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a new teacher-course record to the database.
     *
     * @param teacherCourse the TeacherCourse object containing the details to
     * be added
     */
    public void addTeacherCourse(TeacherCourse teacherCourse) {
        String sql = "INSERT INTO TeacherCourse (teacher_course_id, teacher_id, offering_id, assigned_date) "
                + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, teacherCourse.getTeacherCourseId());
            pstmt.setInt(2, teacherCourse.getTeacherId());
            pstmt.setInt(3, teacherCourse.getOfferingId());
            pstmt.setDate(4, teacherCourse.getAssignedDate());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding teacher course: " + e.getMessage());
        }
    }

    /**
     * Retrieves a teacher-course record from the database by its ID.
     *
     * @param teacherCourseId the ID of the teacher-course record to retrieve
     * @return the TeacherCourse object if found, or null if no record matches
     * the ID
     */
    public TeacherCourse getTeacherCourseById(int teacherCourseId) {
        String sql = "SELECT * FROM TeacherCourse WHERE teacher_course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, teacherCourseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new TeacherCourse(
                            rs.getInt("teacher_course_id"),
                            rs.getInt("teacher_id"),
                            rs.getInt("offering_id"),
                            rs.getDate("assigned_date")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving teacher course: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all teacher-course records from the database.
     *
     * @return a list of all TeacherCourse objects in the database
     */
    public List<TeacherCourse> getAllTeacherCourses() {
        List<TeacherCourse> teacherCourses = new ArrayList<>();
        String sql = "SELECT * FROM TeacherCourse";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                teacherCourses.add(new TeacherCourse(
                        rs.getInt("teacher_course_id"),
                        rs.getInt("teacher_id"),
                        rs.getInt("offering_id"),
                        rs.getDate("assigned_date")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving teacher courses: " + e.getMessage());
        }
        return teacherCourses;
    }

    /**
     * Updates an existing teacher-course record in the database.
     *
     * @param teacherCourse the TeacherCourse object containing the updated
     * details
     */
    public void updateTeacherCourse(TeacherCourse teacherCourse) {
        String sql = "UPDATE TeacherCourse SET teacher_id = ?, offering_id = ?, assigned_date = ? "
                + "WHERE teacher_course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, teacherCourse.getTeacherId());
            pstmt.setInt(2, teacherCourse.getOfferingId());
            pstmt.setDate(3, teacherCourse.getAssignedDate());
            pstmt.setInt(4, teacherCourse.getTeacherCourseId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating teacher course: " + e.getMessage());
        }
    }

    /**
     * Deletes a teacher-course record from the database by its ID.
     *
     * @param teacherCourseId the ID of the teacher-course record to delete
     */
    public void deleteTeacherCourse(int teacherCourseId) {
        String sql = "DELETE FROM TeacherCourse WHERE teacher_course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, teacherCourseId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting teacher course: " + e.getMessage());
        }
    }
}
