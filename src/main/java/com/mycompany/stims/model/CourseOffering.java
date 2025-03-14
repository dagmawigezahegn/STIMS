package com.mycompany.stims.model;

import java.sql.Timestamp;

/**
 * The CourseOffering class represents a course offering in the system. It
 * contains details such as the offering ID, course ID, course code, academic
 * year, year, semester, and timestamps for creation and updates.
 */
public class CourseOffering {

    private int offeringId;
    private int courseId;
    private String courseCode;
    private int academicYear;
    private int year;
    private int semester;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    /**
     * Constructs a CourseOffering object with the specified course ID, academic
     * year, year, and semester.
     *
     * @param courseId the ID of the course
     * @param academicYear the academic year of the offering
     * @param year the year of the offering
     * @param semester the semester of the offering
     */
    public CourseOffering(int courseId, int academicYear, int year, int semester) {
        this.courseId = courseId;
        this.academicYear = academicYear;
        this.year = year;
        this.semester = semester;
    }

    /**
     * Constructs a CourseOffering object with all fields.
     *
     * @param offeringId the ID of the course offering
     * @param courseId the ID of the course
     * @param courseCode the code of the course
     * @param academicYear the academic year of the offering
     * @param year the year of the offering
     * @param semester the semester of the offering
     * @param createdAt the timestamp when the offering was created
     * @param updatedAt the timestamp when the offering was last updated
     */
    public CourseOffering(int offeringId, int courseId, String courseCode, int academicYear, int year, int semester, Timestamp createdAt, Timestamp updatedAt) {
        this.offeringId = offeringId;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.academicYear = academicYear;
        this.year = year;
        this.semester = semester;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
     * Gets the course ID.
     *
     * @return the course ID
     */
    public int getCourseId() {
        return courseId;
    }

    /**
     * Sets the course ID.
     *
     * @param courseId the course ID to set
     */
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    /**
     * Gets the course code.
     *
     * @return the course code
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * Sets the course code.
     *
     * @param courseCode the course code to set
     */
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    /**
     * Gets the academic year.
     *
     * @return the academic year
     */
    public int getAcademicYear() {
        return academicYear;
    }

    /**
     * Sets the academic year.
     *
     * @param academicYear the academic year to set
     */
    public void setAcademicYear(int academicYear) {
        this.academicYear = academicYear;
    }

    /**
     * Gets the year.
     *
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * Sets the year.
     *
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Gets the semester.
     *
     * @return the semester
     */
    public int getSemester() {
        return semester;
    }

    /**
     * Sets the semester.
     *
     * @param semester the semester to set
     */
    public void setSemester(int semester) {
        this.semester = semester;
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
     * Returns a string representation of the CourseOffering object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "CourseOffering{"
                + "offeringId=" + offeringId
                + ", courseId=" + courseId
                + ", courseCode='" + courseCode + '\''
                + ", academicYear=" + academicYear
                + ", year=" + year
                + ", semester=" + semester
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
}
