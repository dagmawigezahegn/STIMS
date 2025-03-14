package com.mycompany.stims.database;

import com.mycompany.stims.model.Student;
import com.mycompany.stims.utils.PasswordUtils;

import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dagim
 */
public class StudentDAO {

    private final Connection connection;

    /**
     *
     * @param connection
     */
    public StudentDAO(Connection connection) {
        this.connection = connection;
    }

    // Generate studentIdNo based on program type and year
    private String generateStudentIdNo(int programId) {
        String programName = getProgramNameById(programId);
        String prefix;

        if (programName.equalsIgnoreCase("Undergraduate")) {
            prefix = "ID/UGR/";
        } else if (programName.equalsIgnoreCase("Graduate")) {
            prefix = "ID/GRA/";
        } else {
            throw new IllegalArgumentException("Program must be either 'Undergraduate' or 'Graduate'.");
        }

        String yearSuffix = String.valueOf(LocalDate.now().getYear()).substring(2);
        String fullPrefix = prefix + yearSuffix + "/";
        int count = getCountOfStudentsWithProgram(fullPrefix);

        return fullPrefix + String.format("%03d", count + 1);
    }

    //Get program name from database

    /**
     *
     * @param programId
     * @return
     */
    public String getProgramNameById(int programId) {
        String sql = "SELECT program_name FROM Program WHERE program_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, programId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("program_name");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching program name: " + e.getMessage(), e);
        }
        throw new IllegalArgumentException("Invalid program ID: " + programId);
    }

    //Counts the number of Students in that Program
    private int getCountOfStudentsWithProgram(String prefix) {
        String sql = "SELECT COUNT(*) AS count FROM Student WHERE studentId_No LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, prefix + "%");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting students: " + e.getMessage(), e);
        }
        return 0;
    }

    // Generate a unique username
    private String generateUsername(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("First name and last name cannot be null.");
        }

        String baseUsername = firstName.toLowerCase() + "." + lastName.toLowerCase();
        String username = baseUsername;
        int count = 1;

        // Check if the username already exists
        while (usernameExists(username)) {
            username = baseUsername + count; // Append the counter
            count++;
        }
        return username;
    }

    // Check if a username already exists
    private boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) AS count FROM Student WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking username: " + e.getMessage(), e);
        }
        return false;
    }

    // Generate a temporary password
    private String generateTemporaryPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }

        return password.toString();
    }

    // Add a new student and return the temporary password

    /**
     *
     * @param student
     * @return
     */
    public String addStudent(Student student) {
        // Generate a username if not provided
        if (student.getUsername() == null || student.getUsername().isEmpty()) {
            String username = generateUsername(student.getFirstName(), student.getLastName());
            student.setUsername(username);
        }

        // Generate a temporary password
        String temporaryPassword = generateTemporaryPassword();
        student.setPasswordHash(temporaryPassword);

        // Hash the temporary password
        String hashedPassword = PasswordUtils.hashPassword(temporaryPassword);
        student.setPasswordHash(hashedPassword);

        // Generate a student ID No
        String studentIdNo = generateStudentIdNo(student.getProgramId());
        student.setStudentIdNo(studentIdNo);

        // Insert the student into the database
        String sql = "INSERT INTO Student (studentId_No, username, password_hash, first_name, middle_name, last_name, email, phone_number, "
                + "address, date_of_birth, program_id, department_id, sex, enrollment_date, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, student.getStudentIdNo());
            pstmt.setString(2, student.getUsername());
            pstmt.setString(3, student.getPasswordHash());
            pstmt.setString(4, student.getFirstName());
            pstmt.setString(5, student.getMiddleName());
            pstmt.setString(6, student.getLastName());
            pstmt.setString(7, student.getEmail());
            pstmt.setString(8, student.getPhoneNumber());
            pstmt.setString(9, student.getAddress());
            pstmt.setDate(10, Date.valueOf(student.getDateOfBirth()));
            pstmt.setInt(11, student.getProgramId());
            pstmt.setInt(12, student.getDepartmentId());
            pstmt.setString(13, student.getSex());
            pstmt.setDate(14, Date.valueOf(student.getEnrollmentDate()));

            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                student.setStudentId(keys.getInt(1)); // Set the auto-generated student_id
            }

            System.out.println("Student added successfully with StudentIdNo: " + studentIdNo);
            System.out.println("Temporary Password: " + temporaryPassword); // Log the temporary password

            return temporaryPassword; // Return the temporary password
        } catch (SQLException e) {
            throw new RuntimeException("Error adding student: " + e.getMessage(), e);
        }
    }

    // Retrieve a student by studentIdNo

    /**
     *
     * @param studentIdNo
     * @return
     */
    public Student getStudentByIdNo(String studentIdNo) {
        String sql = "SELECT * FROM Student WHERE studentId_No = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentIdNo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildStudentFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving student: " + e.getMessage(), e);
        }
        return null;
    }

    // Retrieve a student by EmailOrUsername

    /**
     *
     * @param emailOrUsername
     * @return
     */
    public Student getStudentByEmailOrUsername(String emailOrUsername) {
        String sql = "SELECT * FROM Student WHERE email = ? OR username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emailOrUsername);
            pstmt.setString(2, emailOrUsername);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildStudentFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching admin by email/username: " + e.getMessage());
        }
        return null;
    }

    // Retrieve all students

    /**
     *
     * @return
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM Student";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(buildStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving students: " + e.getMessage(), e);
        }
        return students;
    }

    // Retrieves Department Name by department_id

    /**
     *
     * @param departmentId
     * @return
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

    // Update student details

    /**
     *
     * @param student
     */
    public void updateStudent(Student student) {
        String sql = "UPDATE Student SET username = ?, "
                + "first_name = ?, middle_name = ?, last_name = ?, "
                + "email = ?, phone_number = ?, address = ?, date_of_birth = ?, "
                + "program_id = ?, department_id = ?, sex = ?, "
                + "updated_at = CURRENT_TIMESTAMP "
                + "WHERE studentId_No = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getUsername());
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getMiddleName());
            pstmt.setString(4, student.getLastName());
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getPhoneNumber());
            pstmt.setString(7, student.getAddress());
            pstmt.setDate(8, Date.valueOf(student.getDateOfBirth()));
            pstmt.setInt(9, student.getProgramId());
            pstmt.setInt(10, student.getDepartmentId());
            pstmt.setString(11, student.getSex());
            pstmt.setString(12, student.getStudentIdNo());

            pstmt.executeUpdate();
            System.out.println("Student updated successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating student: " + e.getMessage(), e);
        }
    }

    // Change password

    /**
     *
     * @param studentIdNo
     * @param newPassword
     */
    public void changePassword(String studentIdNo, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("New password cannot be null or empty.");
        }

        String sql = "UPDATE Student SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE studentId_No = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, studentIdNo);
            pstmt.executeUpdate();
            System.out.println("Password updated successfully for student: " + studentIdNo);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating password: " + e.getMessage(), e);
        }
    }

    // Delete a student by studentIdNo

    /**
     *
     * @param studentIdNo
     */
    public void deleteStudent(String studentIdNo) {
        String sql = "DELETE FROM Student WHERE studentId_No = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentIdNo);
            pstmt.executeUpdate();
            System.out.println("Student deleted successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting student: " + e.getMessage(), e);
        }
    }

    // Authenticate a student

    /**
     *
     * @param emailOrUsername
     * @param passwordInput
     * @return
     */
    public Student authenticateStudent(String emailOrUsername, String passwordInput) {
        String sql = "SELECT * FROM Student WHERE username = ? OR email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emailOrUsername);
            pstmt.setString(2, emailOrUsername);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPasswordHash = rs.getString("password_hash");
                if (PasswordUtils.checkPassword(passwordInput, storedPasswordHash)) {
                    return buildStudentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error authenticating student: " + e.getMessage(), e);
        }
        return null;
    }

    // Helper method to build Student object from ResultSet
    private Student buildStudentFromResultSet(ResultSet rs) throws SQLException {
        return new Student(
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
