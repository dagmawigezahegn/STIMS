package com.mycompany.stims.utils;

import java.io.File;

/**
 * The FilePathUtils class provides utility methods for managing file paths and
 * directories used in the application. It ensures that directories exist and
 * provides methods to generate file paths for various reports, templates, and
 * other files.
 */
public class FilePathUtils {

    private static final String REPORT_DIRECTORY = Config.getReportDirectory();
    private static final String COURSE_REPORT_DIRECTORY = Config.getCourseReportDirectory();
    private static final String ALL_COURSES_DIRECTORY = Config.getAllCoursesDirectory();
    private static final String COURSE_STATISTICS_DIRECTORY = Config.getCourseStatisticsDirectory();
    private static final String REPORT_CARD_DIRECTORY = Config.getReportCardDirectory();
    private static final String STUDENT_REPORT_DIRECTORY = Config.getStudentReportDirectory();
    private static final String ALL_STUDENTS_REPORT_DIRECTORY = Config.getAllStudentsReportDirectory();
    private static final String TRANSCRIPT_DIRECTORY = Config.getTranscriptDirectory();

    /**
     * Ensures that the specified directory exists. If the directory does not
     * exist, it attempts to create it. Throws a RuntimeException if the
     * directory cannot be created or if there are no write permissions.
     *
     * @param directoryPath the path of the directory to check or create
     * @throws RuntimeException if the directory cannot be created or lacks
     * write permissions
     */
    public static void ensureDirectoryExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + directoryPath);
            }
        }
        if (!directory.canWrite()) {
            throw new RuntimeException("No write permissions for directory: " + directoryPath);
        }
    }

    /**
     * Generates the file path for a Course Statistics Report.
     *
     * @param currentDate the current date to include in the file name
     * @return the file path for the Course Statistics Report
     */
    public static String getCourseStatisticsReportFilePath(String currentDate) {
        ensureDirectoryExists(COURSE_STATISTICS_DIRECTORY);
        return COURSE_STATISTICS_DIRECTORY + "Course Statistics_" + currentDate + ".pdf";
    }

    /**
     * Generates the file path for an All Courses Report.
     *
     * @param currentDate the current date to include in the file name
     * @return the file path for the All Courses Report
     */
    public static String getAllCoursesReportFilePath(String currentDate) {
        ensureDirectoryExists(ALL_COURSES_DIRECTORY);
        return ALL_COURSES_DIRECTORY + "All Courses Report_" + currentDate + ".pdf";
    }

    /**
     * Generates the file path for an Individual Course Report.
     *
     * @param courseCode the course code to include in the file name
     * @param currentDate the current date to include in the file name
     * @return the file path for the Individual Course Report
     */
    public static String getCourseReportFilePath(String courseCode, String currentDate) {
        ensureDirectoryExists(COURSE_REPORT_DIRECTORY);
        return COURSE_REPORT_DIRECTORY + courseCode + "_Report_" + currentDate + ".pdf";
    }

    /**
     * Generates the file path for a Report Card.
     *
     * @param studentIdNo the student ID number to include in the file name
     * @param year the academic year to include in the file name
     * @param semester the semester to include in the file name
     * @return the file path for the Report Card
     */
    public static String getReportCardFilePath(String studentIdNo, int year, int semester) {
        ensureDirectoryExists(REPORT_CARD_DIRECTORY);
        String sanitizedStudentId = studentIdNo.replace("/", "_");
        return REPORT_CARD_DIRECTORY + sanitizedStudentId + "_Year-" + year + "_SEM-" + semester + ".xlsx";
    }

    /**
     * Generates the file path for a Student Report.
     *
     * @param studentIdNo the student ID number to include in the file name
     * @param currentDate the current date to include in the file name
     * @return the file path for the Student Report
     */
    public static String getStudentReportFilePath(String studentIdNo, String currentDate) {
        ensureDirectoryExists(STUDENT_REPORT_DIRECTORY);
        String sanitizedStudentId = studentIdNo.replace("/", "_");
        return STUDENT_REPORT_DIRECTORY + sanitizedStudentId + "_Report_" + currentDate + ".pdf";
    }

    /**
     * Generates the file path for an All Students Report.
     *
     * @param currentDate the current date to include in the file name
     * @return the file path for the All Students Report
     */
    public static String getAllStudentsReportFilePath(String currentDate) {
        ensureDirectoryExists(ALL_STUDENTS_REPORT_DIRECTORY);
        return ALL_STUDENTS_REPORT_DIRECTORY + "All Students Report_" + currentDate + ".pdf";
    }

    /**
     * Generates the file path for a Transcript.
     *
     * @param studentIdNo the student ID number to include in the file name
     * @return the file path for the Transcript
     */
    public static String getTranscriptFilePath(String studentIdNo) {
        ensureDirectoryExists(TRANSCRIPT_DIRECTORY);
        String sanitizedStudentId = studentIdNo.replace("/", "_");
        return TRANSCRIPT_DIRECTORY + sanitizedStudentId + "_Transcript.xlsx";
    }

    /**
     * Retrieves the file path for the Report Card template.
     *
     * @return the file path for the Report Card template
     */
    public static String getReportCardTemplatePath() {
        return Config.getReportCardTemplatePath();
    }

    /**
     * Retrieves the file path for the Transcript template.
     *
     * @return the file path for the Transcript template
     */
    public static String getTranscriptTemplatePath() {
        return Config.getTranscriptTemplatePath();
    }
}
