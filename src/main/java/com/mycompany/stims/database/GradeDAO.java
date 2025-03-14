package com.mycompany.stims.database;

import com.mycompany.stims.model.Grade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class for managing Grade entities in the database.
 * Provides methods to perform CRUD operations on the Grade table.
 */
public class GradeDAO {

    private Connection connection;

    /**
     * Constructs a GradeDAO with a specified database connection.
     *
     * @param connection the database connection to be used for operations
     */
    public GradeDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a new grade to the database.
     *
     * @param grade the Grade object containing the details to be added
     */
    public void addGrade(Grade grade) {
        String sql = "INSERT INTO Grade (student_course_id, grade) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, grade.getStudentCourseId());
            pstmt.setString(2, grade.getGrade());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding grade: " + e.getMessage());
        }
    }

    /**
     * Retrieves a grade from the database by its ID.
     *
     * @param gradeId the ID of the grade to retrieve
     * @return the Grade object corresponding to the given ID, or null if not
     * found
     */
    public Grade getGradeById(int gradeId) {
        String sql = "SELECT * FROM Grade WHERE grade_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, gradeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Grade(
                            rs.getInt("grade_id"),
                            rs.getInt("student_course_id"),
                            rs.getString("grade"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving grade: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a grade from the database by the student-course ID.
     *
     * @param studentCourseId the ID of the student-course association
     * @return the Grade object corresponding to the given student-course ID, or
     * null if not found
     */
    public Grade getGradeByStudentCourseId(int studentCourseId) {
        String sql = "SELECT * FROM Grade WHERE student_course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentCourseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Grade(
                            rs.getInt("grade_id"),
                            rs.getInt("student_course_id"),
                            rs.getString("grade"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving grade: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all grades from the database.
     *
     * @return a list of Grade objects representing all grades in the database
     */
    public List<Grade> getAllGrades() {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM Grade";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                grades.add(new Grade(
                        rs.getInt("grade_id"),
                        rs.getInt("student_course_id"),
                        rs.getString("grade"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving grades: " + e.getMessage());
        }
        return grades;
    }

    /**
     * Updates an existing grade in the database.
     *
     * @param grade the Grade object containing the updated details
     */
    public void updateGrade(Grade grade) {
        String sql = "UPDATE Grade SET grade = ? WHERE student_course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, grade.getGrade());
            pstmt.setInt(2, grade.getStudentCourseId());
            pstmt.executeUpdate();
            System.out.println("Grade updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating grade: " + e.getMessage());
        }
    }

    /**
     * Deletes a grade from the database by its ID.
     *
     * @param gradeId the ID of the grade to delete
     */
    public void deleteGradeById(int gradeId) {
        String sql = "DELETE FROM Grade WHERE grade_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, gradeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting grade: " + e.getMessage());
        }
    }
}
