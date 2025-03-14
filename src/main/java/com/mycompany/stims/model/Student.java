package com.mycompany.stims.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a student entity in the system. This class encapsulates the
 * details of a student, including personal information, academic details, and
 * timestamps for record creation and updates.
 */
public class Student {

    private int studentId;            // Primary key, auto-increment
    private String studentIdNo;       // Generated Id No 
    private String username;          // Unique username
    private String passwordHash;      // Hashed password
    private String firstName;         // First name
    private String middleName;        // Middle name (nullable)
    private String lastName;          // Last name
    private String email;             // Unique email (nullable)
    private String phoneNumber;       // Phone number (nullable)
    private String address;           // Address (nullable)
    private LocalDate dateOfBirth;    // Date of birth (nullable)
    private int programId;            // Foreign key to program table
    private int departmentId;         // Foreign key to department table
    private String sex;               // Gender: 'M' or 'F'
    private LocalDate enrollmentDate; // Enrollment date
    private LocalDateTime createdAt;  // Timestamp when the record was created
    private LocalDateTime updatedAt;  // Timestamp when the record was last updated

    /**
     * No-argument constructor for creating an empty Student object.
     */
    public Student() {
        // No-argument constructor
    }

    /**
     * Constructs a Student object with basic personal and academic details.
     *
     * @param firstName the first name of the student
     * @param middleName the middle name of the student (nullable)
     * @param lastName the last name of the student
     * @param email the email address of the student (nullable)
     * @param phoneNumber the phone number of the student (nullable)
     * @param address the address of the student (nullable)
     * @param dateOfBirth the date of birth of the student (nullable)
     * @param programId the ID of the program the student is enrolled in
     * @param departmentId the ID of the department the student belongs to
     * @param sex the gender of the student ('M' or 'F')
     * @param enrollmentDate the date the student enrolled in the program
     */
    public Student(String firstName, String middleName, String lastName,
            String email, String phoneNumber, String address, LocalDate dateOfBirth,
            int programId, int departmentId, String sex, LocalDate enrollmentDate) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.programId = programId;
        this.departmentId = departmentId;
        this.sex = sex;
        this.enrollmentDate = enrollmentDate;
    }

    /**
     * Constructs a Student object with all fields, including timestamps.
     *
     * @param studentId the unique ID of the student
     * @param studentIdNo the generated ID number of the student
     * @param username the unique username of the student
     * @param passwordHash the hashed password of the student
     * @param firstName the first name of the student
     * @param middleName the middle name of the student (nullable)
     * @param lastName the last name of the student
     * @param email the email address of the student (nullable)
     * @param phoneNumber the phone number of the student (nullable)
     * @param address the address of the student (nullable)
     * @param dateOfBirth the date of birth of the student (nullable)
     * @param programId the ID of the program the student is enrolled in
     * @param departmentId the ID of the department the student belongs to
     * @param sex the gender of the student ('M' or 'F')
     * @param enrollmentDate the date the student enrolled in the program
     * @param createdAt the timestamp when the student record was created
     * @param updatedAt the timestamp when the student record was last updated
     */
    public Student(int studentId, String studentIdNo, String username, String passwordHash, String firstName, String middleName, String lastName,
            String email, String phoneNumber, String address, LocalDate dateOfBirth,
            int programId, int departmentId, String sex, LocalDate enrollmentDate,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.studentId = studentId;
        this.studentIdNo = studentIdNo;
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.programId = programId;
        this.departmentId = departmentId;
        this.sex = sex;
        this.enrollmentDate = enrollmentDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    /**
     * Gets the student ID.
     *
     * @return the student ID
     */
    public int getStudentId() {
        return studentId;
    }

    /**
     * Sets the student ID.
     *
     * @param studentId the student ID to set
     */
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    /**
     * Gets the student ID number.
     *
     * @return the student ID number
     */
    public String getStudentIdNo() {
        return studentIdNo;
    }

    /**
     * Sets the student ID number.
     *
     * @param studentIdNo the student ID number to set
     */
    public void setStudentIdNo(String studentIdNo) {
        this.studentIdNo = studentIdNo;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the hashed password.
     *
     * @return the hashed password
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the hashed password.
     *
     * @param passwordHash the hashed password to set
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Gets the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the middle name.
     *
     * @return the middle name
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the middle name.
     *
     * @param middleName the middle name to set
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Gets the last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number.
     *
     * @param phoneNumber the phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address.
     *
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the date of birth.
     *
     * @return the date of birth
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the date of birth.
     *
     * @param dateOfBirth the date of birth to set
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Gets the program ID.
     *
     * @return the program ID
     */
    public int getProgramId() {
        return programId;
    }

    /**
     * Sets the program ID.
     *
     * @param programId the program ID to set
     */
    public void setProgramId(int programId) {
        this.programId = programId;
    }

    /**
     * Gets the department ID.
     *
     * @return the department ID
     */
    public int getDepartmentId() {
        return departmentId;
    }

    /**
     * Sets the department ID.
     *
     * @param departmentId the department ID to set
     */
    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Gets the gender.
     *
     * @return the gender ('M' or 'F')
     */
    public String getSex() {
        return sex;
    }

    /**
     * Sets the gender.
     *
     * @param sex the gender to set ('M' or 'F')
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * Gets the enrollment date.
     *
     * @return the enrollment date
     */
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    /**
     * Sets the enrollment date.
     *
     * @param enrollmentDate the enrollment date to set
     */
    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last update timestamp.
     *
     * @return the last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp.
     *
     * @param updatedAt the last update timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns a string representation of the Student object.
     *
     * @return a string representation of the Student object
     */
    @Override
    public String toString() {
        return "Student{"
                + "studentId=" + studentId
                + ", studentIdNo='" + studentIdNo + '\''
                + ", username='" + username + '\''
                + ", passwordHash='" + passwordHash + '\''
                + ", firstName='" + firstName + '\''
                + ", middleName='" + middleName + '\''
                + ", lastName='" + lastName + '\''
                + ", email='" + email + '\''
                + ", phoneNumber='" + phoneNumber + '\''
                + ", address='" + address + '\''
                + ", dateOfBirth=" + dateOfBirth
                + ", programId=" + programId
                + ", departmentId=" + departmentId
                + ", sex='" + sex + '\''
                + ", enrollmentDate=" + enrollmentDate
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
}
