package com.mycompany.stims.database;

import com.mycompany.stims.model.Teacher;
import com.mycompany.stims.utils.PasswordUtils;

import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The TeacherDAO class provides methods to interact with the Teacher table in
 * the database. It supports adding, retrieving, updating, and deleting
 * teachers, as well as generating usernames, temporary passwords, and
 * authenticating teachers.
 */
public class TeacherDAO {

    private Connection connection;

    /**
     * Constructs a TeacherDAO object with a specified database connection.
     *
     * @param connection the database connection to be used for teacher
     * operations
     */
    public TeacherDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Generates a unique username based on the teacher's first and last name.
     * If the base username already exists, appends a number to make it unique.
     *
     * @param firstName the first name of the teacher
     * @param lastName the last name of the teacher
     * @return a unique username
     */
    private String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName.toLowerCase() + "." + lastName.toLowerCase();
        String username = baseUsername;
        int count = 1;
        // Check if the username already exists
        while (usernameExists(username)) {
            username = baseUsername + count;
            count++;
        }
        return username;
    }

    /**
     * Checks if a username already exists in the Teacher table.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    private boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) AS count FROM Teacher WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        return false;
    }

    /**
     * Generates a temporary password consisting of random characters.
     *
     * @return a randomly generated temporary password
     */
    private String generateTemporaryPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

    /**
     * Adds a new teacher to the Teacher table in the database. Generates a
     * username and temporary password if not provided.
     *
     * @param teacher the Teacher object containing the teacher's details
     * @return the temporary password generated for the teacher, or null if an
     * error occurs
     */
    public String addTeacher(Teacher teacher) {
        // Generate a username if not provided
        if (teacher.getUsername() == null || teacher.getUsername().isEmpty()) {
            String username = generateUsername(teacher.getFirstName(), teacher.getLastName());
            teacher.setUsername(username);
        }

        // Generate a temporary password
        String temporaryPassword = generateTemporaryPassword();

        // Hash the temporary password
        String hashedPassword = PasswordUtils.hashPassword(temporaryPassword);
        teacher.setPasswordHash(hashedPassword);

        // Insert the teacher into the database
        String sql = "INSERT INTO Teacher (username, password_hash, first_name, middle_name, last_name, sex, date_of_birth, email, phone_number, address, department_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, teacher.getUsername());
            pstmt.setString(2, teacher.getPasswordHash());
            pstmt.setString(3, teacher.getFirstName());
            pstmt.setString(4, teacher.getMiddleName());
            pstmt.setString(5, teacher.getLastName());
            pstmt.setString(6, teacher.getSex());
            pstmt.setDate(7, new java.sql.Date(teacher.getDateOfBirth().getTime())); // Ensure date is converted to java.sql.Date
            pstmt.setString(8, teacher.getEmail());
            pstmt.setString(9, teacher.getPhoneNumber());
            pstmt.setString(10, teacher.getAddress());
            pstmt.setInt(11, teacher.getDepartmentId());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    teacher.setTeacherId(generatedKeys.getInt(1));
                }
            }

            System.out.println("Teacher added successfully with ID: " + teacher.getTeacherId());
            System.out.println("Temporary Password: " + temporaryPassword); // Log the temporary password
            return temporaryPassword; // Return the temporary password
        } catch (SQLException e) {
            System.err.println("Error adding teacher: " + e.getMessage());
            return null; // Return null in case of an error
        }
    }

    /**
     * Authenticates a teacher using either their email or username and a
     * plain-text password.
     *
     * @param emailOrUsername the email or username of the teacher
     * @param plainPassword the plain-text password to authenticate
     * @return the authenticated Teacher object, or null if authentication fails
     */
    public Teacher authenticateTeacher(String emailOrUsername, String plainPassword) {
        String sql = "SELECT * FROM Teacher WHERE username = ? OR email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emailOrUsername); // Check for username
            pstmt.setString(2, emailOrUsername); // Check for email
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password_hash");
                    if (PasswordUtils.checkPassword(plainPassword, storedHashedPassword)) {
                        return mapRowToTeacher(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating teacher: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves the department name by its ID.
     *
     * @param departmentId the ID of the department
     * @return the name of the department, or null if not found
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
     * Retrieves a teacher's details by their ID.
     *
     * @param teacherId the ID of the teacher
     * @return the Teacher object corresponding to the given ID, or null if not
     * found
     */
    public Teacher getTeacher(int teacherId) {
        String sql = "SELECT * FROM Teacher WHERE teacher_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTeacher(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving teacher with ID " + teacherId + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a teacher by their email or username.
     *
     * @param emailOrUsername the email or username of the teacher
     * @return the Teacher object corresponding to the given email or username,
     * or null if not found
     */
    public Teacher getTeacherByEmailOrUsername(String emailOrUsername) {
        String sql = "SELECT * FROM Teacher WHERE email = ? OR username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emailOrUsername);
            pstmt.setString(2, emailOrUsername);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToTeacher(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching admin by email/username: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all teachers from the database.
     *
     * @return a list of all Teacher objects in the database
     */
    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT * FROM Teacher";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                teachers.add(mapRowToTeacher(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all teachers: " + e.getMessage());
        }
        return teachers;
    }

    /**
     * Updates the details of a teacher in the database.
     *
     * @param teacher the Teacher object containing the updated details
     */
    public void updateTeacher(Teacher teacher) {
        String sql = "UPDATE Teacher SET username = ?, first_name = ?, middle_name = ?, last_name = ?, "
                + "sex = ?, date_of_birth = ?, email = ?, phone_number = ?, address = ?, department_id = ? "
                + "WHERE teacher_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, teacher.getUsername());
            pstmt.setString(2, teacher.getFirstName());
            pstmt.setString(3, teacher.getMiddleName());
            pstmt.setString(4, teacher.getLastName());
            pstmt.setString(5, teacher.getSex());
            pstmt.setDate(6, new java.sql.Date(teacher.getDateOfBirth().getTime())); // Convert date correctly
            pstmt.setString(7, teacher.getEmail());
            pstmt.setString(8, teacher.getPhoneNumber());
            pstmt.setString(9, teacher.getAddress());
            pstmt.setInt(10, teacher.getDepartmentId());
            pstmt.setInt(11, teacher.getTeacherId());

            pstmt.executeUpdate();
            System.out.println("Teacher updated successfully: " + teacher.getTeacherId());
        } catch (SQLException e) {
            System.err.println("Error updating teacher: " + e.getMessage());
        }
    }

    /**
     * Changes the password of a teacher.
     *
     * @param teacherId the ID of the teacher
     * @param newPassword the new password to set
     */
    public void changePassword(int teacherId, String newPassword) {
        String hashedPassword = PasswordUtils.hashPassword(newPassword);
        String sql = "UPDATE Teacher SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE teacher_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, teacherId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
        }
    }

    /**
     * Deletes a teacher from the database by their ID.
     *
     * @param teacherId the ID of the teacher to delete
     */
    public void deleteTeacher(int teacherId) {
        String sql = "DELETE FROM Teacher WHERE teacher_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            pstmt.executeUpdate();
            System.out.println("Teacher deleted successfully with ID: " + teacherId);
        } catch (SQLException e) {
            System.err.println("Error deleting teacher with ID " + teacherId + ": " + e.getMessage());
        }
    }

    /**
     * Maps a ResultSet row to a Teacher object.
     *
     * @param rs the ResultSet containing the teacher data
     * @return a Teacher object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private Teacher mapRowToTeacher(ResultSet rs) throws SQLException {
        return new Teacher(
                rs.getInt("teacher_id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getString("last_name"),
                rs.getString("sex"),
                rs.getDate("date_of_birth"), // java.sql.Date
                rs.getString("email"),
                rs.getString("phone_number"),
                rs.getString("address"),
                rs.getInt("department_id"),
                rs.getTimestamp("last_login"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
    }
}
