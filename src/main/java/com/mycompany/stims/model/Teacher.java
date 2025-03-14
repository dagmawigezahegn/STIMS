package com.mycompany.stims.model;

import java.sql.Timestamp;
import java.util.Date;

/**
 * The Teacher class represents a teacher in the system. It contains details
 * such as personal information, login credentials, and timestamps for
 * record-keeping.
 */
public class Teacher {

    private int teacherId;          // Primary key, auto-increment
    private String username;        // Unique username
    private String passwordHash;    // Hashed password
    private String firstName;       // First name
    private String middleName;      // Middle name (nullable)
    private String lastName;        // Last name
    private String sex;             // Gender: 'M' or 'F'
    private Date dateOfBirth;       // Date of birth (nullable)
    private String email;           // Unique email (nullable)
    private String phoneNumber;     // Phone number (nullable)
    private String address;         // Address (nullable)
    private int departmentId;       // Foreign key to department table
    private Timestamp lastLogin;    // Timestamp of lastLogin (nullable)
    private Date createdAt;         // Timestamp when the record was created
    private Date updatedAt;         // Timestamp when the record was last updated

    /**
     * Default constructor for the Teacher class.
     */
    public Teacher() {
    }

    /**
     * Constructs a Teacher object with basic personal and department
     * information.
     *
     * @param firstName the first name of the teacher
     * @param middleName the middle name of the teacher (nullable)
     * @param lastName the last name of the teacher
     * @param sex the gender of the teacher ('M' or 'F')
     * @param dateOfBirth the date of birth of the teacher (nullable)
     * @param email the email address of the teacher (nullable)
     * @param phoneNumber the phone number of the teacher (nullable)
     * @param address the address of the teacher (nullable)
     * @param departmentId the department ID associated with the teacher
     */
    public Teacher(String firstName, String middleName, String lastName, String sex, Date dateOfBirth,
            String email, String phoneNumber, String address, int departmentId) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.departmentId = departmentId;
    }

    /**
     * Constructs a Teacher object with all fields, typically used when
     * retrieving a teacher from the database.
     *
     * @param teacherId the unique ID of the teacher
     * @param username the unique username of the teacher
     * @param passwordHash the hashed password of the teacher
     * @param firstName the first name of the teacher
     * @param middleName the middle name of the teacher (nullable)
     * @param lastName the last name of the teacher
     * @param sex the gender of the teacher ('M' or 'F')
     * @param dateOfBirth the date of birth of the teacher (nullable)
     * @param email the email address of the teacher (nullable)
     * @param phoneNumber the phone number of the teacher (nullable)
     * @param address the address of the teacher (nullable)
     * @param departmentId the department ID associated with the teacher
     * @param lastLogin the timestamp of the last login (nullable)
     * @param createdAt the timestamp when the teacher record was created
     * @param updatedAt the timestamp when the teacher record was last updated
     */
    public Teacher(int teacherId, String username, String passwordHash, String firstName, String middleName,
            String lastName, String sex, Date dateOfBirth, String email, String phoneNumber, String address, int departmentId,
            Timestamp lastLogin, Date createdAt, Date updatedAt) {
        this.teacherId = teacherId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.departmentId = departmentId;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    /**
     * Gets the teacher ID.
     *
     * @return the teacher ID
     */
    public int getTeacherId() {
        return teacherId;
    }

    /**
     * Sets the teacher ID.
     *
     * @param teacherId the teacher ID to set
     */
    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
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
     * Gets the date of birth.
     *
     * @return the date of birth
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the date of birth.
     *
     * @param dateOfBirth the date of birth to set
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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
     * Gets the timestamp of the last login.
     *
     * @return the timestamp of the last login
     */
    public Timestamp getLastLogin() {
        return lastLogin;
    }

    /**
     * Sets the timestamp of the last login.
     *
     * @param lastLogin the timestamp of the last login to set
     */
    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * Gets the timestamp when the teacher record was created.
     *
     * @return the creation timestamp
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the teacher record was created.
     *
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp when the teacher record was last updated.
     *
     * @return the last update timestamp
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when the teacher record was last updated.
     *
     * @param updatedAt the last update timestamp to set
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns a string representation of the Teacher object.
     *
     * @return a string containing all fields of the Teacher object
     */
    @Override
    public String toString() {
        return "Teacher{"
                + "teacherId=" + teacherId
                + ", username='" + username + '\''
                + ", passwordHash='" + passwordHash + '\''
                + ", firstName='" + firstName + '\''
                + ", middleName='" + middleName + '\''
                + ", lastName='" + lastName + '\''
                + ", sex='" + sex + '\''
                + ", dateOfBirth=" + dateOfBirth
                + ", email='" + email + '\''
                + ", phoneNumber='" + phoneNumber + '\''
                + ", address='" + address + '\''
                + ", departmentId=" + departmentId
                + ", lastLogin=" + lastLogin
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
}
