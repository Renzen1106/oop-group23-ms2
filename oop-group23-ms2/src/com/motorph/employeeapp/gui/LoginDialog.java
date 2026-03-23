package com.motorph.employeeapp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.motorph.employeeapp.model.UserAccount;
import com.motorph.employeeapp.repository.UserAccountDAO;
import com.motorph.employeeapp.service.LoginService;

public class LoginDialog extends JDialog {

    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private boolean succeeded;

    private final LoginService loginService;
    private UserAccount authenticatedUser;

    public LoginDialog(Frame parent) {
        super(parent, "Login", true);

        String path = resolveAccountFilePath();
        UserAccountDAO dao = new UserAccountDAO(path);
        this.loginService = new LoginService(dao);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.insets = new Insets(5, 5, 5, 5); // spacing fix

        // USERNAME
        cs.gridx = 0;
        cs.gridy = 0;
        panel.add(new JLabel("Username:"), cs);

        tfUsername = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        // PASSWORD
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(new JLabel("Password:"), cs);

        pfPassword = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);

        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // BUTTONS
        JButton btnLogin = new JButton("Login");
        JButton btnCancel = new JButton("Cancel");

        btnLogin.addActionListener(e -> handleLogin());
        btnCancel.addActionListener(e -> {
            succeeded = false;
            dispose();
        });

        JPanel bp = new JPanel();
        bp.add(btnLogin);
        bp.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
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
                    "Login",
                    JOptionPane.ERROR_MESSAGE
            );

            tfUsername.setText("");
            pfPassword.setText("");
            succeeded = false;
        }
    }

    private String resolveAccountFilePath() {
        String[] paths = {
                "data/account.csv",
                "oop-group23-ms2/data/account.csv"
        };

        for (String p : paths) {
            File f = new File(p);
            if (f.exists()) return p;
        }

        return "data/account.csv";
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