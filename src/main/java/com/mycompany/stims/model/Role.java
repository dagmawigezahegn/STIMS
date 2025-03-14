package com.mycompany.stims.model;

/**
 * The `Role` class represents a role entity in the system. It contains
 * information about a role's ID and name, along with methods to access and
 * modify these attributes.
 */
public class Role {

    private int roleId;
    private String roleName;

    /**
     * Constructs a new `Role` object with the specified role ID and role name.
     *
     * @param roleId The unique identifier for the role.
     * @param roleName The name of the role.
     */
    public Role(int roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    /**
     * Returns the role ID of this role.
     *
     * @return The role ID.
     */
    public int getRoleId() {
        return roleId;
    }

    /**
     * Sets the role ID of this role.
     *
     * @param roleId The new role ID to set.
     */
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    /**
     * Returns the role name of this role.
     *
     * @return The role name.
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Sets the role name of this role.
     *
     * @param roleName The new role name to set.
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
