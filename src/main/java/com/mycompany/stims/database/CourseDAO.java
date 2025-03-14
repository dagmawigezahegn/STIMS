package com.mycompany.stims.database;

import com.mycompany.stims.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class for managing Course entities in the database.
 * Provides methods to perform CRUD operations on the Course table.
 */
public class CourseDAO {

    private Connection connection;

    /**
     * Constructs a CourseDAO with the specified database connection.
     *
     * @param connection the database connection to be used for operations
     */
    public CourseDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a new course to the database.
     *
     * @param course the Course object containing the course details to be added
     */
    public void addCourse(Course course) {
        String sql = "INSERT INTO course (course_name, course_description, credits, course_level, year, semester, department_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, course.getCourseName());       // course_name
            pstmt.setString(2, course.getCourseDescription()); // course_description
            pstmt.setInt(3, course.getCredits());             // credits
            pstmt.setInt(4, course.getCourseLevel());         // course_level
            pstmt.setInt(5, course.getYear());                // year
            pstmt.setInt(6, course.getSemester());            // semester
            pstmt.setInt(7, course.getDepartmentId());        // department_id

            pstmt.executeUpdate();

            // Retrieve the auto-generated courseId
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int courseId = generatedKeys.getInt(1);
                course.setCourseId(courseId);
                System.out.println("Course added successfully with courseId: " + courseId);
            } else {
                System.err.println("Failed to retrieve auto-generated courseId.");
            }
        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
        }
    }

    /**
     * Retrieves a course from the database by its course code.
     *
     * @param courseCode the course code of the course to retrieve
     * @return the Course object if found, or null if no course matches the code
     */
    public Course getCourseByCode(String courseCode) {
        String sql = "SELECT * FROM course WHERE course_code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, courseCode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCourse(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving course: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all courses associated with a specific department.
     *
     * @param departmentId the ID of the department to retrieve courses for
     * @return a list of Course objects belonging to the specified department
     */
    public List<Course> getCoursesByDepartment(int departmentId) {
        String sql = "SELECT * FROM course WHERE department_id = ?";
        List<Course> courses = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, departmentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving courses by department: " + e.getMessage());
        }
        return courses;
    }

    /**
     * Retrieves the name of a department by its ID.
     *
     * @param departmentId the ID of the department to retrieve the name for
     * @return the name of the department, or null if no department matches the
     * ID
     * @throws RuntimeException if an error occurs while fetching the department
     * name
     */
    public String getDepartmentNameById(int departmentId) {
        String sql = "SELECT department_name FROM Department WHERE department_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, departmentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("department_name");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching department name: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Retrieves all courses from the database.
     *
     * @return a list of all Course objects in the database
     */
    public List<Course> getAllCourses() {
        String sql = "SELECT * FROM course";
        List<Course> courses = new ArrayList<>();

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving courses: " + e.getMessage());
        }
        return courses;
    }

    /**
     * Updates an existing course in the database.
     *
     * @param courseId the ID of the course to update
     * @param updatedCourse the Course object containing the updated details
     */
    public void updateCourse(int courseId, Course updatedCourse) {
        String sql = "UPDATE course SET course_name = ?, course_description = ?, credits = ?, course_level = ?, year = ?, semester = ?, department_id = ? "
                + "WHERE course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, updatedCourse.getCourseName());
            pstmt.setString(2, updatedCourse.getCourseDescription());
            pstmt.setInt(3, updatedCourse.getCredits());
            pstmt.setInt(4, updatedCourse.getCourseLevel());
            pstmt.setInt(5, updatedCourse.getYear());
            pstmt.setInt(6, updatedCourse.getSemester());
            pstmt.setInt(7, updatedCourse.getDepartmentId());
            pstmt.setInt(8, courseId);

            pstmt.executeUpdate();
            System.out.println("Course updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating course: " + e.getMessage());
        }
    }

    /**
     * Deletes a course from the database by its ID.
     *
     * @param courseId the ID of the course to delete
     */
    public void deleteCourse(int courseId) {
        String sql = "DELETE FROM course WHERE course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);

            pstmt.executeUpdate();
            System.out.println("Course deleted successfully.");
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
        }
    }

    /**
     * Helper method to map a ResultSet row to a Course object.
     *
     * @param rs the ResultSet containing the course data
     * @return a Course object populated with data from the ResultSet
     * @throws SQLException if an error occurs while accessing the ResultSet
     */
    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course(
                rs.getString("course_name"),
                rs.getString("course_description"),
                rs.getInt("credits"),
                rs.getInt("course_level"),
                rs.getInt("year"),
                rs.getInt("semester"),
                rs.getInt("department_id")
        );
        course.setCourseId(rs.getInt("course_id")); // Set courseId from the database
        course.setCourseCode(rs.getString("course_code")); // Set courseCode from the database
        return course;
    }
}
