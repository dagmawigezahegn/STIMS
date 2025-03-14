package com.mycompany.stims.model;

import java.util.Date;

/**
 * The Admin class represents an administrator in the system. It contains
 * details such as personal information, login credentials, and timestamps for
 * record-keeping.
 */
public class Admin {

    private int adminId;                // Primary key, auto-increment
    private String adminIdNo;           // Generated Id No 
    private String username;            // Unique username
    private String passwordHash;        // Hashed password
    private String firstName;           // First name
    private String middleName;          // Middle name (nullable)
    private String lastName;            // Last name
    private String sex;                 // Gender: 'M' or 'F'
    private Date dateOfBirth;           // Date of birth 
    private String email;               // Unique email (nullable)
    private String phoneNumber;         // Phone number 
    private String address;             // Address 
    private int roleId;                 // Foreign key to role table
    private Date lastLogin;             // Timestamp of lastLogin (nullable)
    private Date createdAt;             // Timestamp when the record was created
    private Date updatedAt;             // Timestamp when the record was last updated

    /**
     * Default constructor for the Admin class.
     */
    public Admin() {
        // No-argument constructor
    }

    /**
     * Constructs an Admin object with basic personal and role information.
     *
     * @param firstName the first name of the admin
     * @param middleName the middle name of the admin (nullable)
     * @param lastName the last name of the admin
     * @param sex the gender of the admin ('M' or 'F')
     * @param dateOfBirth the date of birth of the admin
     * @param email the email address of the admin (nullable)
     * @param phoneNumber the phone number of the admin
     * @param address the address of the admin
     * @param roleId the role ID associated with the admin
     */
    public Admin(String firstName, String middleName, String lastName, String sex, Date dateOfBirth,
            String email, String phoneNumber, String address, int roleId) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.roleId = roleId;
    }

    /**
     * Constructs an Admin object with all fields, typically used when
     * retrieving an admin from the database.
     *
     * @param adminId the unique ID of the admin
     * @param adminIdNo the generated ID number of the admin
     * @param username the unique username of the admin
     * @param passwordHash the hashed password of the admin
     * @param firstName the first name of the admin
     * @param middleName the middle name of the admin (nullable)
     * @param lastName the last name of the admin
     * @param sex the gender of the admin ('M' or 'F')
     * @param dateOfBirth the date of birth of the admin
     * @param email the email address of the admin (nullable)
     * @param phoneNumber the phone number of the admin
     * @param address the address of the admin
     * @param roleId the role ID associated with the admin
     * @param lastLogin the timestamp of the last login (nullable)
     * @param createdAt the timestamp when the admin record was created
     * @param updatedAt the timestamp when the admin record was last updated
     */
    public Admin(int adminId, String adminIdNo, String username, String passwordHash, String firstName, String middleName,
            String lastName, String sex, Date dateOfBirth, String email, String phoneNumber, String address, int roleId,
            Date lastLogin, Date createdAt, Date updatedAt) {
        this.adminId = adminId;
        this.adminIdNo = adminIdNo;
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
        this.roleId = roleId;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    /**
     * Gets the admin ID.
     *
     * @return the admin ID
     */
    public int getAdminId() {
        return adminId;
    }

    /**
     * Sets the admin ID.
     *
     * @param adminId the admin ID to set
     */
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    /**
     * Gets the admin ID number.
     *
     * @return the admin ID number
     */
    public String getAdminIdNo() {
        return adminIdNo;
    }

    /**
     * Sets the admin ID number.
     *
     * @param adminIdNo the admin ID number to set
     */
    public void setAdminIdNo(String adminIdNo) {
        this.adminIdNo = adminIdNo;
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
     * Gets the role ID.
     *
     * @return the role ID
     */
    public int getRoleId() {
        return roleId;
    }

    /**
     * Sets the role ID.
     *
     * @param roleId the role ID to set
     */
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    /**
     * Gets the timestamp of the last login.
     *
     * @return the timestamp of the last login
     */
    public Date getLastLogin() {
        return lastLogin;
    }

    /**
     * Sets the timestamp of the last login.
     *
     * @param lastLogin the timestamp of the last login to set
     */
    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * Gets the timestamp when the admin record was created.
     *
     * @return the creation timestamp
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the admin record was created.
     *
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp when the admin record was last updated.
     *
     * @return the last update timestamp
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when the admin record was last updated.
     *
     * @param updatedAt the last update timestamp to set
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns a string representation of the Admin object.
     *
     * @return a string containing all fields of the Admin object
     */
    @Override
    public String toString() {
        return "Admin{"
                + "adminId=" + adminId
                + ", adminIdNo='" + adminIdNo + '\''
                + ", username='" + username + '\''
                + ", firstName='" + firstName + '\''
                + ", middleName='" + middleName + '\''
                + ", lastName='" + lastName + '\''
                + ", sex='" + sex + '\''
                + ", dateOfBirth=" + dateOfBirth
                + ", email='" + email + '\''
                + ", phoneNumber='" + phoneNumber + '\''
                + ", address='" + address + '\''
                + ", roleId=" + roleId
                + ", lastLogin=" + lastLogin
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
}
