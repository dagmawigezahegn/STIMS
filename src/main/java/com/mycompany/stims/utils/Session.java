package com.mycompany.stims.utils;

/**
 * The `Session` class manages user session information for the system. It
 * provides static methods to set, retrieve, and clear session data for
 * administrators, students, and teachers. This class is used to track the
 * currently logged-in users.
 */
public class Session {

    private static String loggedInAdminEmailOrUsername;
    private static String loggedInStudentEmailOrUsername;
    private static String loggedInTeacherEmailOrUsername;

    /**
     * Returns the email or username of the currently logged-in administrator.
     *
     * @return The email or username of the logged-in administrator, or `null`
     * if no administrator is logged in.
     */
    public static String getLoggedInAdminEmailOrUsername() {
        return loggedInAdminEmailOrUsername;
    }

    /**
     * Sets the email or username of the currently logged-in administrator.
     *
     * @param emailOrUsername The email or username of the administrator.
     */
    public static void setLoggedInAdminEmailOrUsername(String emailOrUsername) {
        loggedInAdminEmailOrUsername = emailOrUsername;
    }

    /**
     * Returns the email or username of the currently logged-in student.
     *
     * @return The email or username of the logged-in student, or `null` if no
     * student is logged in.
     */
    public static String getLoggedInStudentEmailOrUsername() {
        return loggedInStudentEmailOrUsername;
    }

    /**
     * Sets the email or username of the currently logged-in student.
     *
     * @param emailOrUsername The email or username of the student.
     */
    public static void setLoggedInStudentEmailOrUsername(String emailOrUsername) {
        loggedInStudentEmailOrUsername = emailOrUsername;
    }

    /**
     * Returns the email or username of the currently logged-in teacher.
     *
     * @return The email or username of the logged-in teacher, or `null` if no
     * teacher is logged in.
     */
    public static String getLoggedInTeacherEmailOrUsername() {
        return loggedInTeacherEmailOrUsername;
    }

    /**
     * Sets the email or username of the currently logged-in teacher.
     *
     * @param emailOrUsername The email or username of the teacher.
     */
    public static void setLoggedInTeacherEmailOrUsername(String emailOrUsername) {
        loggedInTeacherEmailOrUsername = emailOrUsername;
    }

    /**
     * Clears all session data, effectively logging out all users
     * (administrators, students, and teachers). This method sets all session
     * fields to `null`.
     */
    public static void clear() {
        loggedInTeacherEmailOrUsername = null;
        loggedInAdminEmailOrUsername = null;
        loggedInStudentEmailOrUsername = null;
    }
}
