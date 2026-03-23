package com.motorph.employeeapp.service;

import com.motorph.employeeapp.model.UserAccount;
import com.motorph.employeeapp.repository.UserAccountDAO;

import java.io.IOException;
import java.util.Map;

/**
 * Handles login validation and returns authenticated user.
 */
public class LoginService {

    private final UserAccountDAO userAccountDAO;

    public LoginService(UserAccountDAO userAccountDAO) {
        this.userAccountDAO = userAccountDAO;
    }

    public UserAccount authenticate(String username, String password) {

        try {
            Map<String, UserAccount> accounts = userAccountDAO.loadAccounts();

            UserAccount user = accounts.get(username);

            if (user != null && user.getPassword().equals(password)) {
                return user;
            }

        } catch (IOException e) {
            System.err.println("Error reading account data: " + e.getMessage());
        }

        return null;
    }
}