package com.mycompany.stims.model;

/**
 * Represents a Course entity in the system. This class encapsulates the details
 * of a course, including its ID, code, name, description, credits, level, year,
 * semester, and department ID.
 */
public class Course {

    private int courseId;
    private String courseCode;
    private String courseName;
    private String courseDescription;
    private int credits;
    private int courseLevel;
    private int year;
    private int semester;
    private int departmentId;

    /**
     * Constructs a new Course with the specified details.
     *
     * @param courseName the name of the course
     * @param courseDescription the description of the course
     * @param credits the number of credits for the course
     * @param courseLevel the level of the course (e.g., undergraduate,
     * graduate)
     * @param year the academic year the course is offered
     * @param semester the semester the course is offered
     * @param departmentId the ID of the department offering the course
     */
    public Course(String courseName, String courseDescription, int credits, int courseLevel, int year, int semester, int departmentId) {
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.credits = credits;
        this.courseLevel = courseLevel;
        this.year = year;
        this.semester = semester;
        this.departmentId = departmentId;
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
     * Gets the course name.
     *
     * @return the course name
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Sets the course name.
     *
     * @param courseName the course name to set
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * Gets the course description.
     *
     * @return the course description
     */
    public String getCourseDescription() {
        return courseDescription;
    }

    /**
     * Sets the course description.
     *
     * @param courseDescription the course description to set
     */
    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    /**
     * Gets the number of credits for the course.
     *
     * @return the number of credits
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Sets the number of credits for the course.
     *
     * @param credits the number of credits to set
     */
    public void setCredits(int credits) {
        this.credits = credits;
    }

    /**
     * Gets the course level.
     *
     * @return the course level
     */
    public int getCourseLevel() {
        return courseLevel;
    }

    /**
     * Sets the course level.
     *
     * @param courseLevel the course level to set
     */
    public void setCourseLevel(int courseLevel) {
        this.courseLevel = courseLevel;
    }

    /**
     * Gets the academic year the course is offered.
     *
     * @return the academic year
     */
    public int getYear() {
        return year;
    }

    /**
     * Sets the academic year the course is offered.
     *
     * @param year the academic year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Gets the semester the course is offered.
     *
     * @return the semester
     */
    public int getSemester() {
        return semester;
    }

    /**
     * Sets the semester the course is offered.
     *
     * @param semester the semester to set
     */
    public void setSemester(int semester) {
        this.semester = semester;
    }

    /**
     * Gets the department ID offering the course.
     *
     * @return the department ID
     */
    public int getDepartmentId() {
        return departmentId;
    }

    /**
     * Sets the department ID offering the course.
     *
     * @param departmentId the department ID to set
     */
    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Returns a string representation of the Course object.
     *
     * @return a string containing the course details
     */
    @Override
    public String toString() {
        return "Course{"
                + "courseId=" + courseId
                + ", courseCode='" + courseCode + '\''
                + ", courseName='" + courseName + '\''
                + ", courseDescription='" + courseDescription + '\''
                + ", credits=" + credits
                + ", courseLevel=" + courseLevel
                + ", year=" + year
                + ", semester=" + semester
                + ", departmentId=" + departmentId
                + '}';
    }
}
