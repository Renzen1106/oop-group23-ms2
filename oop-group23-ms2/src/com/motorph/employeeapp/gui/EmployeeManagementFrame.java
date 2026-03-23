package com.motorph.employeeapp.gui;

import com.motorph.employeeapp.model.Employee;
import com.motorph.employeeapp.model.Role;
import com.motorph.employeeapp.model.UserAccount;
import com.motorph.employeeapp.repository.CsvEmployeeRepository;
import com.motorph.employeeapp.repository.EmployeeRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
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

    public EmployeeManagementFrame(EmployeeRepository repo, UserAccount loggedInUser) {
        super("Employee Management");
        this.repo = repo;
        this.loggedInUser = loggedInUser;

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {
        }

        Color PRIMARY = new Color(45, 137, 239);
        Color DARK_PRIMARY = new Color(30, 100, 180);
        Color ACCENT = new Color(245, 245, 245);
        Color BG_WHITE = Color.WHITE;
        Color ADD_GREEN = new Color(76, 175, 80);
        Color DEL_RED = new Color(244, 67, 54);
        Color UPD_ORANGE = new Color(255, 152, 0);

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
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowHeight(28);
        table.setFont(table.getFont().deriveFont(14f));
        table.setSelectionBackground(PRIMARY);
        table.setSelectionForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);

        JTableHeader hdr = table.getTableHeader();
        hdr.setOpaque(true);
        hdr.setBackground(DARK_PRIMARY);
        hdr.setForeground(Color.WHITE);
        hdr.setFont(hdr.getFont().deriveFont(Font.BOLD, 14f));
        hdr.setPreferredSize(new Dimension(hdr.getPreferredSize().width, 32));
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY));
        hdr.setReorderingAllowed(false);

        hdr.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col
            ) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        tbl, value, isSelected, hasFocus, row, col
                );
                lbl.setBackground(DARK_PRIMARY);
                lbl.setForeground(Color.WHITE);
                lbl.setHorizontalAlignment(CENTER);
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, PRIMARY));
                return lbl;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean focus, int row, int col
            ) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);

                if (sel) {
                    setBackground(PRIMARY);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? BG_WHITE : ACCENT);
                    setForeground(Color.DARK_GRAY);
                }

                if (!sel && (col == 4 || col == 6) && v != null) {
                    try {
                        BigDecimal bd = new BigDecimal(v.toString());
                        setText(bd.toPlainString());
                    } catch (Exception ex) {
                        setText(v.toString());
                    }
                }

                return this;
            }
        });

        addBtn = new JButton("Add Employee");
        updateBtn = new JButton("Update Employee");
        deleteBtn = new JButton("Delete Employee");
        viewBtn = new JButton("View Employee");

        List<JButton> btnsList = List.of(addBtn, updateBtn, deleteBtn, viewBtn);
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

        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        viewBtn.setEnabled(false);

        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> updateButtonStates());

        addBtn.addActionListener(e -> {
            if (!canManageEmployees()) {
                showAccessDenied("add employee records");
                return;
            }

            new AddRecordDialog(
                    EmployeeManagementFrame.this,
                    repo,
                    EmployeeManagementFrame.this::loadTable
            ).setVisible(true);
        });

        updateBtn.addActionListener(e -> {
            if (!canManageEmployees()) {
                showAccessDenied("update employee records");
                return;
            }

            int r = table.getSelectedRow();
            if (r < 0) {
                return;
            }

            String id = (String) model.getValueAt(r, 0);

            try {
                for (Employee emp : repo.loadAll()) {
                    if (emp.getId().equals(id)) {
                        new UpdateDialog(
                                EmployeeManagementFrame.this,
                                repo,
                                emp,
                                EmployeeManagementFrame.this::loadTable
                        ).setVisible(true);
                        return;
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        EmployeeManagementFrame.this,
                        "Cannot edit: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        deleteBtn.addActionListener(e -> {
            if (!canManageEmployees()) {
                showAccessDenied("delete employee records");
                return;
            }

            int r = table.getSelectedRow();
            if (r < 0) {
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    EmployeeManagementFrame.this,
                    "Delete this employee?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            String id = (String) model.getValueAt(r, 0);

            try {
                List<Employee> updatedList = new ArrayList<>();
                for (Employee emp : repo.loadAll()) {
                    if (!emp.getId().equals(id)) {
                        updatedList.add(emp);
                    }
                }

                repo.saveAll(updatedList);
                loadTable();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        EmployeeManagementFrame.this,
                        "Delete failed: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        viewBtn.addActionListener(e -> {
            if (!canViewPayslip()) {
                showAccessDenied("view payroll and payslip information");
                return;
            }

            int r = table.getSelectedRow();
            if (r < 0) {
                return;
            }

            String id = (String) model.getValueAt(r, 0);

            try {
                for (Employee emp : repo.loadAll()) {
                    if (emp.getId().equals(id)) {
                        new PayslipSplitDialog(EmployeeManagementFrame.this, emp).setVisible(true);
                        return;
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        EmployeeManagementFrame.this,
                        "Cannot open: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(BG_WHITE);
        side.setBorder(new EmptyBorder(10, 10, 10, 10));
        side.add(addBtn);
        side.add(Box.createVerticalStrut(8));
        side.add(updateBtn);
        side.add(Box.createVerticalStrut(8));
        side.add(deleteBtn);
        side.add(Box.createVerticalStrut(8));
        side.add(viewBtn);

        JLabel userInfoLabel = new JLabel(
                "Logged in as: " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")"
        );
        userInfoLabel.setBorder(new EmptyBorder(8, 12, 8, 12));
        userInfoLabel.setFont(userInfoLabel.getFont().deriveFont(Font.BOLD, 13f));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_WHITE);
        topPanel.add(userInfoLabel, BorderLayout.WEST);

        getContentPane().setBackground(BG_WHITE);
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(side, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);

        loadTable();
        applyRolePermissions();
    }

    private void loadTable() {
        model.setRowCount(0);

        try {
            List<Employee> employees = repo.loadAll();

            if (employees.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No employee records found.\n\nChecked path:\n"
                                + new File(EMPLOYEE_CSV_PATH).getAbsolutePath(),
                        "No Data",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            for (Employee emp : employees) {
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
            JOptionPane.showMessageDialog(
                    this,
                    "Load failed: " + e.getMessage() + "\n\nChecked path:\n"
                            + new File(EMPLOYEE_CSV_PATH).getAbsolutePath(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void applyRolePermissions() {
        Role role = loggedInUser.getRole();

        switch (role) {
            case HR:
                addBtn.setEnabled(true);
                break;
            case IT:
                addBtn.setEnabled(true);
                break;
            case FINANCE:
                addBtn.setEnabled(false);
                break;
            default:
                addBtn.setEnabled(false);
                break;
        }

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

    private void showAccessDenied(String action) {
        JOptionPane.showMessageDialog(
                this,
                "Access denied. Your role does not have permission to " + action + ".",
                "Unauthorized Access",
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