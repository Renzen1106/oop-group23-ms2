package com.motorph.employeeapp.model;

/**
 * Represents a system user with role-based access.
 */
public class UserAccount {

    private final String username;
    private final String password;
    private final Role role;

    public UserAccount(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}