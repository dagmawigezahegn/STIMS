package com.mycompany.stims.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * The PasswordUtils class provides utility methods for hashing and verifying
 * passwords using the BCrypt hashing algorithm.
 */
public class PasswordUtils {

    /**
     * Hashes a plain text password using BCrypt.
     *
     * @param plainPassword the plain text password to hash
     * @return the hashed password
     */
    public static String hashPassword(String plainPassword) {
        // Generate the salt and hash the password
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Verifies a plain text password against a stored hashed password.
     *
     * @param plainPassword the plain text password to verify
     * @param hashedPassword the stored hashed password
     * @return true if the plain password matches the hashed password, otherwise
     * false
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        // Compare the plain password with the stored hash
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
