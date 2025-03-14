package com.mycompany.stims.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for loading and accessing configuration properties from a
 * properties file. This class provides methods to retrieve various directory
 * paths and template paths defined in the `config.properties` file.
 */
public class Config {

    private static Properties properties;

    // Static block to load the properties file during class initialization
    static {
        properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    /**
     * Retrieves the base directory for all reports.
     *
     * @return the base report directory path, with placeholders replaced by
     * system properties
     */
    public static String getReportDirectory() {
        String directory = properties.getProperty("report.directory");
        return directory.replace("${user.home}", System.getProperty("user.home"));
    }

    /**
     * Retrieves the directory for course reports.
     *
     * @return the course report directory path, with placeholders replaced by
     * system properties
     */
    public static String getCourseReportDirectory() {
        String directory = properties.getProperty("report.course_report_directory");
        return directory.replace("${user.home}", System.getProperty("user.home"))
                .replace("${report.directory}", getReportDirectory());
    }

    /**
     * Retrieves the directory for all courses reports.
     *
     * @return the all courses report directory path, with placeholders replaced
     * by system properties
     */
    public static String getAllCoursesDirectory() {
        String directory = properties.getProperty("report.all_courses_directory");
        return directory.replace("${user.home}", System.getProperty("user.home"))
                .replace("${report.course_report_directory}", getCourseReportDirectory());
    }

    /**
     * Retrieves the directory for course statistics reports.
     *
     * @return the course statistics report directory path, with placeholders
     * replaced by system properties
     */
    public static String getCourseStatisticsDirectory() {
        String directory = properties.getProperty("report.course_statistics_directory");
        return directory.replace("${user.home}", System.getProperty("user.home"))
                .replace("${report.course_report_directory}", getCourseReportDirectory());
    }

    /**
     * Retrieves the directory for report cards.
     *
     * @return the report card directory path, with placeholders replaced by
     * system properties
     */
    public static String getReportCardDirectory() {
        String directory = properties.getProperty("report.report_card_directory");
        return directory.replace("${user.home}", System.getProperty("user.home"))
                .replace("${report.directory}", getReportDirectory());
    }

    /**
     * Retrieves the directory for student reports.
     *
     * @return the student report directory path, with placeholders replaced by
     * system properties
     */
    public static String getStudentReportDirectory() {
        String directory = properties.getProperty("report.student_report_directory");
        return directory.replace("${user.home}", System.getProperty("user.home"))
                .replace("${report.directory}", getReportDirectory());
    }

    /**
     * Retrieves the directory for all students reports.
     *
     * @return the all students report directory path, with placeholders
     * replaced by system properties
     */
    public static String getAllStudentsReportDirectory() {
        String directory = properties.getProperty("report.all_students_report_directory");
        return directory.replace("${user.home}", System.getProperty("user.home"))
                .replace("${report.directory}", getReportDirectory());
    }

    /**
     * Retrieves the directory for transcripts.
     *
     * @return the transcript directory path, with placeholders replaced by
     * system properties
     */
    public static String getTranscriptDirectory() {
        String directory = properties.getProperty("report.transcript_directory");
        return directory.replace("${user.home}", System.getProperty("user.home"))
                .replace("${report.directory}", getReportDirectory());
    }

    /**
     * Retrieves the file path for the report card template.
     *
     * @return the report card template path, with placeholders replaced by
     * system properties
     */
    public static String getReportCardTemplatePath() {
        String templatePath = properties.getProperty("template.report_card");
        return templatePath.replace("${user.home}", System.getProperty("user.home"));
    }

    /**
     * Retrieves the file path for the transcript template.
     *
     * @return the transcript template path, with placeholders replaced by
     * system properties
     */
    public static String getTranscriptTemplatePath() {
        String templatePath = properties.getProperty("template.transcript");
        return templatePath.replace("${user.home}", System.getProperty("user.home"));
    }
}
