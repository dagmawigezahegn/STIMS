package com.mycompany.stims.database;

import com.mycompany.stims.model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The RoleDAO class provides methods to interact with the Role table in the
 * database. It supports adding, retrieving, and listing roles.
 */
public class RoleDAO {

    private Connection connection;

    /**
     * Constructs a RoleDAO object with a specified database connection.
     *
     * @param connection the database connection to be used for role operations
     */
    public RoleDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a new role to the Role table in the database.
     *
     * @param role the Role object containing the role details to be added
     */
    public void addRole(Role role) {
        String sql = "INSERT INTO Role (role_id, role_name) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, role.getRoleId());
            pstmt.setString(2, role.getRoleName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding role: " + e.getMessage());
        }
    }

    /**
     * Retrieves a role from the Role table by its ID.
     *
     * @param roleId the ID of the role to retrieve
     * @return the Role object corresponding to the given ID, or null if not
     * found
     */
    public Role getRoleById(int roleId) {
        String sql = "SELECT * FROM Role WHERE role_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, roleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Role(rs.getInt("role_id"), rs.getString("role_name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving role: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all roles from the Role table.
     *
     * @return a list of Role objects representing all roles in the database
     */
    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM Role";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                roles.add(new Role(rs.getInt("role_id"), rs.getString("role_name")));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving roles: " + e.getMessage());
        }
        return roles;
    }
}
