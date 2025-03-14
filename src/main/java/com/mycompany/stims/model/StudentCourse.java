package com.mycompany.stims.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * The StudentCourse class represents the relationship between a student and a
 * course offering. It contains details such as the student-course ID, student
 * ID, offering ID, enrollment date, and timestamps for creation and updates.
 */
public class StudentCourse {

    private int studentCourseId;
    private int studentId;
    private int offeringId;
    private Date enrollmentDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    /**
     * Constructs a StudentCourse object with the specified student ID, offering
     * ID, and enrollment date.
     *
     * @param studentId the ID of the student
     * @param offeringId the ID of the course offering
     * @param enrollmentDate the date of enrollment
     */
    public StudentCourse(int studentId, int offeringId, Date enrollmentDate) {
        this.studentId = studentId;
        this.offeringId = offeringId;
        this.enrollmentDate = enrollmentDate;
    }

    /**
     * Constructs a StudentCourse object with all fields.
     *
     * @param studentCourseId the ID of the student-course relationship
     * @param studentId the ID of the student
     * @param offeringId the ID of the course offering
     * @param enrollmentDate the date of enrollment
     * @param createdAt the timestamp when the relationship was created
     * @param updatedAt the timestamp when the relationship was last updated
     */
    public StudentCourse(int studentCourseId, int studentId, int offeringId, Date enrollmentDate, Timestamp createdAt, Timestamp updatedAt) {
        this.studentCourseId = studentCourseId;
        this.studentId = studentId;
        this.offeringId = offeringId;
        this.enrollmentDate = enrollmentDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the student-course ID.
     *
     * @return the student-course ID
     */
    public int getStudentCourseId() {
        return studentCourseId;
    }

    /**
     * Sets the student-course ID.
     *
     * @param studentCourseId the student-course ID to set
     */
    public void setStudentCourseId(int studentCourseId) {
        this.studentCourseId = studentCourseId;
    }

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
     * Gets the offering ID.
     *
     * @return the offering ID
     */
    public int getOfferingId() {
        return offeringId;
    }

    /**
     * Sets the offering ID.
     *
     * @param offeringId the offering ID to set
     */
    public void setOfferingId(int offeringId) {
        this.offeringId = offeringId;
    }

    /**
     * Gets the enrollment date.
     *
     * @return the enrollment date
     */
    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    /**
     * Sets the enrollment date.
     *
     * @param enrollmentDate the enrollment date to set
     */
    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last update timestamp.
     *
     * @return the last update timestamp
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp.
     *
     * @param updatedAt the last update timestamp to set
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns a string representation of the StudentCourse object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "StudentCourse{"
                + "studentCourseId=" + studentCourseId
                + ", studentId=" + studentId
                + ", offeringId=" + offeringId
                + ", enrollmentDate=" + enrollmentDate
                + ", createdAt=" + createdAt
                + '}';
    }
}
