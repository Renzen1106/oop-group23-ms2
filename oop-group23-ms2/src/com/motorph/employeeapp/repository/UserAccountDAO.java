package com.motorph.employeeapp.repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * DAO responsible for reading login accounts from CSV.
 */
public class UserAccountDAO {

    private final String accountsFile;

    public UserAccountDAO(String accountsFile) {
        this.accountsFile = accountsFile;
    }

    public Map<String, String> loadAccounts() throws IOException {

        Map<String, String> accounts = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(accountsFile))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",");

                if (parts.length == 2) {
                    accounts.put(parts[0].trim(), parts[1].trim());
                }
            }
        }

        return accounts;
    }
}