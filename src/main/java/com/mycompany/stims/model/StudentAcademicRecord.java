package com.mycompany.stims.model;

/**
 * Represents a student's academic record for a specific semester. This class
 * encapsulates details such as the student ID, academic year, year of study,
 * semester, total credits earned, semester GPA (SGPA), and cumulative GPA
 * (CGPA).
 */
public class StudentAcademicRecord {

    private int studentId;
    private int academicYear;      // Academic year (e.g., 2023, 2024)
    private int year;              // Year (e.g., 1, 2, 3)
    private int semester;          // Semester (1 or 2)
    private int totalCredits;      // Total credits earned in the semester
    private double sgpa;           // Semester Grade Point Average
    private double cgpa;           // Cumulative Grade Point Average

    /**
     * Default constructor for StudentAcademicRecord. Initializes an empty
     * instance of the class.
     */
    public StudentAcademicRecord() {
    }

    /**
     * Parameterized constructor for StudentAcademicRecord. Initializes an
     * instance with the provided details and performs validation.
     *
     * @param studentId the ID of the student
     * @param academicYear the academic year (e.g., 2023, 2024)
     * @param year the year of study (e.g., 1, 2, 3)
     * @param semester the semester (1 or 2)
     * @param totalCredits the total credits earned in the semester
     * @param sgpa the semester GPA (SGPA)
     * @param cgpa the cumulative GPA (CGPA)
     * @throws IllegalArgumentException if any of the provided values are
     * invalid
     */
    public StudentAcademicRecord(int studentId, int academicYear, int year, int semester, int totalCredits, double sgpa, double cgpa) {
        setStudentId(studentId);
        setAcademicYear(academicYear);
        setYear(year);
        setSemester(semester);
        setTotalCredits(totalCredits);
        setSgpa(sgpa);
        setCgpa(cgpa);
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
     * @throws IllegalArgumentException if the student ID is not a positive
     * integer
     */
    public void setStudentId(int studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("Student ID must be a positive integer.");
        }
        this.studentId = studentId;
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
     * @throws IllegalArgumentException if the academic year is not between 1900
     * and 2100
     */
    public void setAcademicYear(int academicYear) {
        if (academicYear < 1900 || academicYear > 2100) {
            throw new IllegalArgumentException("Academic year must be between 1900 and 2100.");
        }
        this.academicYear = academicYear;
    }

    /**
     * Gets the year of study.
     *
     * @return the year of study
     */
    public int getYear() {
        return year;
    }

    /**
     * Sets the year of study.
     *
     * @param year the year of study to set
     * @throws IllegalArgumentException if the year is less than 1
     */
    public void setYear(int year) {
        if (year < 1) {
            throw new IllegalArgumentException("Year must be at least 1.");
        }
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
     * @throws IllegalArgumentException if the semester is not 1 or 2
     */
    public void setSemester(int semester) {
        if (semester < 1 || semester > 2) {
            throw new IllegalArgumentException("Semester must be either 1 or 2.");
        }
        this.semester = semester;
    }

    /**
     * Gets the total credits earned in the semester.
     *
     * @return the total credits
     */
    public int getTotalCredits() {
        return totalCredits;
    }

    /**
     * Sets the total credits earned in the semester.
     *
     * @param totalCredits the total credits to set
     * @throws IllegalArgumentException if the total credits are negative
     */
    public void setTotalCredits(int totalCredits) {
        if (totalCredits < 0) {
            throw new IllegalArgumentException("Total credits cannot be negative.");
        }
        this.totalCredits = totalCredits;
    }

    /**
     * Gets the semester GPA (SGPA).
     *
     * @return the semester GPA
     */
    public double getSgpa() {
        return sgpa;
    }

    /**
     * Sets the semester GPA (SGPA).
     *
     * @param sgpa the semester GPA to set
     * @throws IllegalArgumentException if the SGPA is not between 0.0 and 4.0
     */
    public void setSgpa(double sgpa) {
        if (sgpa < 0.0 || sgpa > 4.0) {
            throw new IllegalArgumentException("SGPA must be between 0.0 and 4.0.");
        }
        this.sgpa = sgpa;
    }

    /**
     * Gets the cumulative GPA (CGPA).
     *
     * @return the cumulative GPA
     */
    public double getCgpa() {
        return cgpa;
    }

    /**
     * Sets the cumulative GPA (CGPA).
     *
     * @param cgpa the cumulative GPA to set
     * @throws IllegalArgumentException if the CGPA is not between 0.0 and 4.0
     */
    public void setCgpa(double cgpa) {
        if (cgpa < 0.0 || cgpa > 4.0) {
            throw new IllegalArgumentException("CGPA must be between 0.0 and 4.0.");
        }
        this.cgpa = cgpa;
    }

    /**
     * Returns a string representation of the StudentAcademicRecord object.
     *
     * @return a string containing the student's academic record details
     */
    @Override
    public String toString() {
        return "StudentAcademicRecord{"
                + "studentId=" + studentId
                + ", academicYear=" + academicYear
                + ", year=" + year
                + ", semester=" + semester
                + ", totalCredits=" + totalCredits
                + ", sgpa=" + sgpa
                + ", cgpa=" + cgpa
                + '}';
    }
}
