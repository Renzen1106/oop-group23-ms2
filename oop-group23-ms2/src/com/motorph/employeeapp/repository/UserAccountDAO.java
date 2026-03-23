package com.motorph.employeeapp.repository;

import com.motorph.employeeapp.model.Role;
import com.motorph.employeeapp.model.UserAccount;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * DAO responsible for reading login accounts from CSV.
 * Format: username,password,role
 */
public class UserAccountDAO {

    private final String accountsFile;

    public UserAccountDAO(String accountsFile) {
        this.accountsFile = accountsFile;
    }

    public Map<String, UserAccount> loadAccounts() throws IOException {

        Map<String, UserAccount> accounts = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(accountsFile))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",");

                if (parts.length == 3) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    Role role = Role.valueOf(parts[2].trim().toUpperCase());

                    accounts.put(username, new UserAccount(username, password, role));
                }
            }
        }

        return accounts;
    }
}