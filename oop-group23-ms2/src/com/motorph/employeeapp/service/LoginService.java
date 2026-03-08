package com.motorph.employeeapp.service;

import com.motorph.employeeapp.repository.UserAccountDAO;

import java.io.IOException;
import java.util.Map;

/**
 * Handles login validation logic.
 */
public class LoginService {

    private final UserAccountDAO userAccountDAO;

    public LoginService(UserAccountDAO userAccountDAO) {
        this.userAccountDAO = userAccountDAO;
    }

    public boolean validateLogin(String username, String password) {

        try {
            Map<String, String> accounts = userAccountDAO.loadAccounts();
            return password.equals(accounts.get(username));

        } catch (IOException e) {

            System.err.println("Error reading account data: " + e.getMessage());
            return false;
        }
    }
}