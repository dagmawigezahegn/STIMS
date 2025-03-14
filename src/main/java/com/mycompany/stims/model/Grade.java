package com.mycompany.stims.model;

import java.sql.Timestamp;

/**
 * The Grade class represents a grade assigned to a student for a specific
 * course. It includes details such as the grade value, timestamps for creation
 * and updates, and a reference to the student-course relationship.
 */
public class Grade {

    private int gradeId;                // Unique identifier for the grade
    private int studentCourseId;       // Reference to the student-course relationship
    private String grade;              // The grade value (e.g., "A", "B", "C")
    private Timestamp createdAt;       // Timestamp when the grade was created
    private Timestamp updatedAt;       // Timestamp when the grade was last updated

    /**
     * Constructs a Grade object for creating a new grade.
     *
     * @param studentCourseId the ID of the student-course relationship
     * @param grade the grade value (e.g., "A", "B", "C")
     */
    public Grade(int studentCourseId, String grade) {
        this.studentCourseId = studentCourseId;
        this.grade = grade;
    }

    /**
     * Constructs a Grade object for retrieving a grade from the database.
     *
     * @param gradeId the unique identifier for the grade
     * @param studentCourseId the ID of the student-course relationship
     * @param grade the grade value (e.g., "A", "B", "C")
     * @param createdAt the timestamp when the grade was created
     * @param updatedAt the timestamp when the grade was last updated
     */
    public Grade(int gradeId, int studentCourseId, String grade, Timestamp createdAt, Timestamp updatedAt) {
        this.gradeId = gradeId;
        this.studentCourseId = studentCourseId;
        this.grade = grade;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    /**
     * Gets the grade ID.
     *
     * @return the grade ID
     */
    public int getGradeId() {
        return gradeId;
    }

    /**
     * Sets the grade ID.
     *
     * @param gradeId the grade ID to set
     */
    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
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
     * Gets the grade value.
     *
     * @return the grade value (e.g., "A", "B", "C")
     */
    public String getGrade() {
        return grade;
    }

    /**
     * Sets the grade value.
     *
     * @param grade the grade value to set (e.g., "A", "B", "C")
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * Gets the timestamp when the grade was created.
     *
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the grade was created.
     *
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp when the grade was last updated.
     *
     * @return the last update timestamp
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when the grade was last updated.
     *
     * @param updatedAt the last update timestamp to set
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns a string representation of the Grade object.
     *
     * @return a string containing all fields of the Grade object
     */
    @Override
    public String toString() {
        return "Grade{"
                + "gradeId=" + gradeId
                + ", studentCourseId=" + studentCourseId
                + ", grade='" + grade + '\''
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
}
