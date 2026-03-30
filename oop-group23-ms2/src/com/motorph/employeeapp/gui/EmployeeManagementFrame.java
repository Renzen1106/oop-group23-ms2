package com.motorph.employeeapp.gui;

import com.motorph.employeeapp.model.Employee;
import com.motorph.employeeapp.model.Role;
import com.motorph.employeeapp.model.UserAccount;
import com.motorph.employeeapp.repository.CsvEmployeeRepository;
import com.motorph.employeeapp.repository.EmployeeRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManagementFrame extends JFrame {

    private static final String EMPLOYEE_CSV_PATH = resolveEmployeeCsvPath();

    private final EmployeeRepository repo;
    private final UserAccount loggedInUser;
    private final JTable table;
    private final DefaultTableModel model;

    private JButton addBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton viewBtn;
    private JButton attendanceBtn;
    private JButton leaveBtn;

    public EmployeeManagementFrame(EmployeeRepository repo, UserAccount loggedInUser) {
        super("Employee Management");
        this.repo = repo;
        this.loggedInUser = loggedInUser;

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        Color PRIMARY = new Color(45, 137, 239);
        Color ADD_GREEN = new Color(76, 175, 80);
        Color DEL_RED = new Color(244, 67, 54);
        Color UPD_ORANGE = new Color(255, 152, 0);
        Color ATTENDANCE_PURPLE = new Color(123, 104, 238);
        Color LEAVE_TEAL = new Color(0, 150, 136);

        String[] cols = {
                "Employee #", "Last Name", "First Name",
                "SSS #", "PhilHealth #", "TIN #", "Pag-IBIG #"
        };

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(table);

        // Buttons
        addBtn = new JButton("Add Employee");
        updateBtn = new JButton("Update Employee");
        deleteBtn = new JButton("Delete Employee");
        viewBtn = new JButton("View Employee");
        attendanceBtn = new JButton("Attendance");
        leaveBtn = new JButton("Leave");

        List<JButton> btnsList = List.of(addBtn, updateBtn, deleteBtn, viewBtn, attendanceBtn, leaveBtn);
        for (JButton b : btnsList) {
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(new EmptyBorder(8, 16, 8, 16));
        }

        addBtn.setBackground(ADD_GREEN);
        updateBtn.setBackground(UPD_ORANGE);
        deleteBtn.setBackground(DEL_RED);
        viewBtn.setBackground(PRIMARY);
        attendanceBtn.setBackground(ATTENDANCE_PURPLE);
        leaveBtn.setBackground(LEAVE_TEAL);

        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        viewBtn.setEnabled(false);

        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> updateButtonStates());

        // ACTIONS

        addBtn.addActionListener(e -> {
            if (!canManageEmployees()) {
                showAccessDenied("add employee records");
                return;
            }

            new AddRecordDialog(this, repo, this::loadTable).setVisible(true);
        });

        updateBtn.addActionListener(e -> {
            if (!canManageEmployees()) {
                showAccessDenied("update employee records");
                return;
            }

            int r = table.getSelectedRow();
            if (r < 0) return;

            String id = (String) model.getValueAt(r, 0);

            try {
                for (Employee emp : repo.loadAll()) {
                    if (emp.getId().equals(id)) {
                        new UpdateDialog(this, repo, emp, this::loadTable).setVisible(true);
                        return;
                    }
                }
            } catch (IOException ex) {
                showError("Cannot edit employee", ex);
            }
        });

        deleteBtn.addActionListener(e -> {
            if (!canManageEmployees()) {
                showAccessDenied("delete employee records");
                return;
            }

            int r = table.getSelectedRow();
            if (r < 0) return;

            String id = (String) model.getValueAt(r, 0);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete Employee ID: " + id + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            try {
                List<Employee> updatedList = new ArrayList<>();
                for (Employee emp : repo.loadAll()) {
                    if (!emp.getId().equals(id)) {
                        updatedList.add(emp);
                    }
                }

                repo.saveAll(updatedList);
                loadTable();

                JOptionPane.showMessageDialog(this, "Employee deleted successfully.");

            } catch (IOException ex) {
                showError("Delete failed", ex);
            }
        });

        viewBtn.addActionListener(e -> {
            if (!canViewPayslip()) {
                showAccessDenied("view payroll and payslip");
                return;
            }

            int r = table.getSelectedRow();
            if (r < 0) return;

            String id = (String) model.getValueAt(r, 0);

            try {
                for (Employee emp : repo.loadAll()) {
                    if (emp.getId().equals(id)) {
                        new PayslipSplitDialog(this, emp).setVisible(true);
                        return;
                    }
                }
            } catch (IOException ex) {
                showError("Cannot open payslip", ex);
            }
        });

        attendanceBtn.addActionListener(e -> {
            if (!canAccessAttendance()) {
                showAccessDenied("access attendance");
                return;
            }
            new AttendanceDialog(this).setVisible(true);
        });

        leaveBtn.addActionListener(e -> {
            if (!canAccessLeave()) {
                showAccessDenied("access leave requests");
                return;
            }
            new LeaveDialog(this, loggedInUser).setVisible(true);
        });

        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(10, 10, 10, 10));

        side.add(addBtn);
        side.add(Box.createVerticalStrut(8));
        side.add(updateBtn);
        side.add(Box.createVerticalStrut(8));
        side.add(deleteBtn);
        side.add(Box.createVerticalStrut(8));
        side.add(viewBtn);
        side.add(Box.createVerticalStrut(8));
        side.add(attendanceBtn);
        side.add(Box.createVerticalStrut(8));
        side.add(leaveBtn);

        JLabel userInfoLabel = new JLabel(
                "Logged in as: " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")"
        );

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(userInfoLabel, BorderLayout.WEST);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(side, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 580);
        setLocationRelativeTo(null);

        loadTable();
        applyRolePermissions();
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                message + ": " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void loadTable() {
        model.setRowCount(0);

        try {
            for (Employee emp : repo.loadAll()) {
                model.addRow(new Object[]{
                        emp.getId(),
                        emp.getLastName(),
                        emp.getFirstName(),
                        emp.getSssNumber(),
                        emp.getPhilHealthNumber(),
                        emp.getTinNumber(),
                        emp.getPagIbigNumber()
                });
            }
        } catch (IOException e) {
            showError("Load failed", e);
        }
    }

    private void applyRolePermissions() {
        Role role = loggedInUser.getRole();

        addBtn.setVisible(role == Role.HR || role == Role.IT);
        updateBtn.setVisible(role == Role.HR || role == Role.IT);
        deleteBtn.setVisible(role == Role.HR || role == Role.IT);
        viewBtn.setVisible(role == Role.HR || role == Role.FINANCE);
        attendanceBtn.setVisible(role == Role.HR || role == Role.IT);
        leaveBtn.setVisible(true);

        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean selected = table.getSelectedRow() >= 0;

        updateBtn.setEnabled(selected && canManageEmployees());
        deleteBtn.setEnabled(selected && canManageEmployees());
        viewBtn.setEnabled(selected && canViewPayslip());
    }

    private boolean canManageEmployees() {
        Role role = loggedInUser.getRole();
        return role == Role.HR || role == Role.IT;
    }

    private boolean canViewPayslip() {
        Role role = loggedInUser.getRole();
        return role == Role.HR || role == Role.FINANCE;
    }

    private boolean canAccessAttendance() {
        Role role = loggedInUser.getRole();
        return role == Role.HR || role == Role.IT;
    }

    private boolean canAccessLeave() {
        return true;
    }

    private void showAccessDenied(String action) {
        JOptionPane.showMessageDialog(
                this,
                "Access denied. You do not have permission to " + action + ".",
                "Unauthorized",
                JOptionPane.WARNING_MESSAGE
        );
    }

   private static String resolveEmployeeCsvPath() {
    String[] possiblePaths = {
            "data/MotorPH Employee Record.csv",
            "oop-group23-ms2/data/MotorPH Employee Record.csv"
    };

    for (String path : possiblePaths) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return path;
        }
    }

    return "data/MotorPH Employee Record.csv";
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);

            if (!loginDialog.isSucceeded()) {
                System.exit(0);
            }

            UserAccount user = loginDialog.getAuthenticatedUser();
            EmployeeRepository repo = new CsvEmployeeRepository(EMPLOYEE_CSV_PATH);
            new EmployeeManagementFrame(repo, user).setVisible(true);
        });
    }
}