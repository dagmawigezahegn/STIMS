package com.mycompany.stims.database;

import com.mycompany.stims.model.CourseOffering;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The CourseOfferingDAO class provides methods to interact with the
 * CourseOffering table in the database. It supports operations such as adding,
 * retrieving, updating, and deleting course offerings.
 */
public class CourseOfferingDAO {

    private Connection connection;

    /**
     * Constructs a CourseOfferingDAO object with a specified database
     * connection.
     *
     * @param connection the database connection to be used for operations
     */
    public CourseOfferingDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a new course offering to the database. The method inserts the course
     * offering details into the CourseOffering table and retrieves the
     * auto-generated offering_id.
     *
     * @param courseOffering the CourseOffering object containing the details to
     * be added
     */
    public void addCourseOffering(CourseOffering courseOffering) {
        String sql = "INSERT INTO CourseOffering (course_id, academic_year, year, semester, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, courseOffering.getCourseId());
            pstmt.setInt(2, courseOffering.getAcademicYear());
            pstmt.setInt(3, courseOffering.getYear());
            pstmt.setInt(4, courseOffering.getSemester());
            pstmt.setTimestamp(5, courseOffering.getCreatedAt());
            pstmt.setTimestamp(6, courseOffering.getUpdatedAt());
            pstmt.executeUpdate();

            // Retrieve the auto-generated offering_id
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int offeringId = generatedKeys.getInt(1);
                courseOffering.setOfferingId(offeringId);
                System.out.println("Added course offering with offering_id: " + offeringId);
            }
        } catch (SQLException e) {
            System.err.println("Error adding course offering: " + e.getMessage());
        }
    }

    /**
     * Retrieves a course offering from the database by its offering_id. The
     * method fetches the course offering details along with the associated
     * course code.
     *
     * @param offeringId the ID of the course offering to retrieve
     * @return the CourseOffering object if found, otherwise null
     */
    public CourseOffering getCourseOfferingById(int offeringId) {
        String sql = "SELECT co.offering_id, co.course_id, c.course_code, co.academic_year, co.year, co.semester, co.created_at, co.updated_at "
                + "FROM CourseOffering co "
                + "JOIN Course c ON co.course_id = c.course_id "
                + "WHERE co.offering_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, offeringId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new CourseOffering(
                            rs.getInt("offering_id"),
                            rs.getInt("course_id"),
                            rs.getString("course_code"), // Fetch course_code
                            rs.getInt("academic_year"),
                            rs.getInt("year"),
                            rs.getInt("semester"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving course offering: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all course offerings from the database. The method fetches the
     * course offering details along with the associated course codes.
     *
     * @return a list of CourseOffering objects representing all course
     * offerings
     */
    public List<CourseOffering> getAllCourseOfferings() {
        List<CourseOffering> offerings = new ArrayList<>();
        String sql = "SELECT co.offering_id, co.course_id, c.course_code, co.academic_year, co.year, co.semester, co.created_at, co.updated_at "
                + "FROM CourseOffering co "
                + "JOIN Course c ON co.course_id = c.course_id"; // Join with Course table to fetch course_code
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                offerings.add(new CourseOffering(
                        rs.getInt("offering_id"),
                        rs.getInt("course_id"),
                        rs.getString("course_code"), // Fetch course_code
                        rs.getInt("academic_year"),
                        rs.getInt("year"),
                        rs.getInt("semester"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving course offerings: " + e.getMessage());
        }
        return offerings;
    }

    /**
     * Updates an existing course offering in the database. The method modifies
     * the course offering details based on the provided CourseOffering object.
     *
     * @param courseOffering the CourseOffering object containing the updated
     * details
     */
    public void updateCourseOffering(CourseOffering courseOffering) {
        String sql = "UPDATE CourseOffering SET course_id = ?, academic_year = ?, year = ?, semester = ?, "
                + "created_at = ?, updated_at = ? WHERE offering_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseOffering.getCourseId());
            pstmt.setInt(2, courseOffering.getAcademicYear());
            pstmt.setInt(3, courseOffering.getYear());
            pstmt.setInt(4, courseOffering.getSemester());
            pstmt.setTimestamp(5, courseOffering.getCreatedAt());
            pstmt.setTimestamp(6, courseOffering.getUpdatedAt());
            pstmt.setInt(7, courseOffering.getOfferingId());
            pstmt.executeUpdate();
            System.out.println("Updated course offering with offering_id: " + courseOffering.getOfferingId());
        } catch (SQLException e) {
            System.err.println("Error updating course offering: " + e.getMessage());
        }
    }

    /**
     * Deletes a course offering from the database by its offering_id. The
     * method checks if the course offering is taken by students or teachers
     * before deletion. If the course offering is not taken, it is deleted from
     * the database.
     *
     * @param offeringId the ID of the course offering to delete
     * @return true if the deletion is successful, false if the course offering
     * is taken by students or teachers
     */
    public boolean deleteCourseOffering(int offeringId) {
        // Check if the course offering is taken
        String checkStudentCourseSql = "SELECT COUNT(*) FROM studentcourse WHERE offering_id = ?";
        String checkTeacherCourseSql = "SELECT COUNT(*) FROM teachercourse WHERE offering_id = ?";
        String deleteCourseOfferingSql = "DELETE FROM courseoffering WHERE offering_id = ?";

        try (PreparedStatement checkStudentCourseStmt = connection.prepareStatement(checkStudentCourseSql); PreparedStatement checkTeacherCourseStmt = connection.prepareStatement(checkTeacherCourseSql); PreparedStatement deleteCourseOfferingStmt = connection.prepareStatement(deleteCourseOfferingSql)) {

            // Check studentcourse table
            checkStudentCourseStmt.setInt(1, offeringId);
            ResultSet studentCourseResult = checkStudentCourseStmt.executeQuery();
            if (studentCourseResult.next() && studentCourseResult.getInt(1) > 0) {
                // Course offering is taken by students
                return false;
            }

            // Check teachercourse table
            checkTeacherCourseStmt.setInt(1, offeringId);
            ResultSet teacherCourseResult = checkTeacherCourseStmt.executeQuery();
            if (teacherCourseResult.next() && teacherCourseResult.getInt(1) > 0) {
                // Course offering is taken by teachers
                return false;
            }

            // If not taken, proceed with deletion
            deleteCourseOfferingStmt.setInt(1, offeringId);
            deleteCourseOfferingStmt.executeUpdate();
            return true; // Deletion successful
        } catch (SQLException e) {
            System.err.println("Error deleting course offering: " + e.getMessage());
            return false;
        }
    }
}
