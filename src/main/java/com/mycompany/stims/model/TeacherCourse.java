package com.mycompany.stims.model;

import java.sql.Date;

/**
 * The `TeacherCourse` class represents the association between a teacher and a
 * course offering. It contains information about the teacher-course
 * relationship, including the teacher ID, offering ID, and the date the teacher
 * was assigned to the course.
 */
public class TeacherCourse {

    private int teacherCourseId;
    private int teacherId;
    private int offeringId;
    private Date assignedDate;

    /**
     * Constructs a new `TeacherCourse` object with the specified teacher ID,
     * offering ID, and assigned date. This constructor is typically used when
     * creating a new record without a predefined teacher-course ID.
     *
     * @param teacherId The ID of the teacher.
     * @param offeringId The ID of the course offering.
     * @param assignedDate The date the teacher was assigned to the course.
     */
    public TeacherCourse(int teacherId, int offeringId, Date assignedDate) {
        this.teacherId = teacherId;
        this.offeringId = offeringId;
        this.assignedDate = assignedDate;
    }

    /**
     * Constructs a new `TeacherCourse` object with the specified teacher-course
     * ID, teacher ID, offering ID, and assigned date. This constructor is
     * typically used when retrieving an existing record from the database.
     *
     * @param teacherCourseId The unique identifier for the teacher-course
     * relationship.
     * @param teacherId The ID of the teacher.
     * @param offeringId The ID of the course offering.
     * @param assignedDate The date the teacher was assigned to the course.
     */
    public TeacherCourse(int teacherCourseId, int teacherId, int offeringId, Date assignedDate) {
        this.teacherCourseId = teacherCourseId;
        this.teacherId = teacherId;
        this.offeringId = offeringId;
        this.assignedDate = assignedDate;
    }

    /**
     * Returns the teacher-course ID of this relationship.
     *
     * @return The teacher-course ID.
     */
    public int getTeacherCourseId() {
        return teacherCourseId;
    }

    /**
     * Sets the teacher-course ID of this relationship.
     *
     * @param teacherCourseId The new teacher-course ID to set.
     */
    public void setTeacherCourseId(int teacherCourseId) {
        this.teacherCourseId = teacherCourseId;
    }

    /**
     * Returns the teacher ID of this relationship.
     *
     * @return The teacher ID.
     */
    public int getTeacherId() {
        return teacherId;
    }

    /**
     * Sets the teacher ID of this relationship.
     *
     * @param teacherId The new teacher ID to set.
     */
    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    /**
     * Returns the offering ID of this relationship.
     *
     * @return The offering ID.
     */
    public int getOfferingId() {
        return offeringId;
    }

    /**
     * Sets the offering ID of this relationship.
     *
     * @param offeringId The new offering ID to set.
     */
    public void setOfferingId(int offeringId) {
        this.offeringId = offeringId;
    }

    /**
     * Returns the assigned date of this relationship.
     *
     * @return The assigned date.
     */
    public Date getAssignedDate() {
        return assignedDate;
    }

    /**
     * Sets the assigned date of this relationship.
     *
     * @param assignedDate The new assigned date to set.
     */
    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }

    /**
     * Returns a string representation of the `TeacherCourse` object. The string
     * includes the teacher-course ID, teacher ID, offering ID, and assigned
     * date.
     *
     * @return A string representation of the `TeacherCourse` object.
     */
    @Override
    public String toString() {
        return "TeacherCourse{"
                + "teacherCourseId=" + teacherCourseId
                + ", teacherId=" + teacherId
                + ", offeringId=" + offeringId
                + ", assignedDate=" + assignedDate
                + '}';
    }
}
