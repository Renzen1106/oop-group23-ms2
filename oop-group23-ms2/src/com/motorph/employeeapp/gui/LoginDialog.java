package com.motorph.employeeapp.gui;

import com.motorph.employeeapp.model.UserAccount;
import com.motorph.employeeapp.repository.UserAccountDAO;
import com.motorph.employeeapp.service.LoginService;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {

    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private boolean succeeded;

    private final LoginService loginService;
    private UserAccount authenticatedUser;

    public LoginDialog(Frame parent) {
        super(parent, "Login", true);

        UserAccountDAO userAccountDAO = new UserAccountDAO("data/account.csv");
        this.loginService = new LoginService(userAccountDAO);

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));

        panel.add(new JLabel("Username:"));
        tfUsername = new JTextField();
        panel.add(tfUsername);

        panel.add(new JLabel("Password:"));
        pfPassword = new JPasswordField();
        panel.add(pfPassword);

        JButton btnLogin = new JButton("Login");
        JButton btnCancel = new JButton("Cancel");

        btnLogin.addActionListener(e -> handleLogin());
        btnCancel.addActionListener(e -> {
            succeeded = false;
            dispose();
        });

        JPanel buttons = new JPanel();
        buttons.add(btnLogin);
        buttons.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    private void handleLogin() {
        String username = getUsername();
        String password = getPassword();

        UserAccount user = loginService.authenticate(username, password);

        if (user != null) {
            authenticatedUser = user;
            succeeded = true;

            JOptionPane.showMessageDialog(
                    this,
                    "Welcome " + user.getUsername() + " (" + user.getRole() + ")"
            );

            dispose();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );

            tfUsername.setText("");
            pfPassword.setText("");
            succeeded = false;
        }
    }

    public String getUsername() {
        return tfUsername.getText().trim();
    }

    public String getPassword() {
        return new String(pfPassword.getPassword());
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public UserAccount getAuthenticatedUser() {
        return authenticatedUser;
    }
}