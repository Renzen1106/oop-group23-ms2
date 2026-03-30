package com.motorph.employeeapp.gui;

import com.motorph.employeeapp.model.LeaveRequest;
import com.motorph.employeeapp.model.Role;
import com.motorph.employeeapp.model.UserAccount;
import com.motorph.employeeapp.service.LeaveService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LeaveDialog extends JDialog {

    private final LeaveService leaveService = new LeaveService();
    private final UserAccount loggedInUser;

    private final JTextField leaveIdField = new JTextField(10);
    private final JTextField employeeIdField = new JTextField(10);
    private final JTextField leaveTypeField = new JTextField(12);
    private final JTextField startDateField = new JTextField(12);
    private final JTextField endDateField = new JTextField(12);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Leave ID", "Employee ID", "Leave Type", "Start Date", "End Date", "Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    public LeaveDialog(Frame owner, UserAccount loggedInUser) {
        super(owner, "Leave Request", true);

        this.loggedInUser = loggedInUser;

        setTitle("Leave Request - " + loggedInUser.getRole());
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Leave ID:"));
        form.add(leaveIdField);
        form.add(new JLabel("Employee ID:"));
        form.add(employeeIdField);
        form.add(new JLabel("Leave Type:"));
        form.add(leaveTypeField);
        form.add(new JLabel("Start Date (YYYY-MM-DD):"));
        form.add(startDateField);
        form.add(new JLabel("End Date (YYYY-MM-DD):"));
        form.add(endDateField);

        JButton submitBtn = new JButton("Submit Leave");
        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn = new JButton("Reject");
        JButton refreshBtn = new JButton("Refresh");
        JButton clearBtn = new JButton("Clear");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitBtn);
        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(clearBtn);

        JScrollPane scrollPane = new JScrollPane(table);

        add(form, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        submitBtn.addActionListener(e -> handleSubmitLeave());
        approveBtn.addActionListener(e -> handleApprove());
        rejectBtn.addActionListener(e -> handleReject());
        refreshBtn.addActionListener(e -> loadLeaves());
        clearBtn.addActionListener(e -> clearInputs());

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> populateFieldsFromSelectedRow());

        applyRolePermissions(submitBtn, approveBtn, rejectBtn);

        setSize(800, 450);
        setLocationRelativeTo(owner);

        loadLeaves();
    }

    private void applyRolePermissions(JButton submitBtn, JButton approveBtn, JButton rejectBtn) {
        Role role = loggedInUser.getRole();

        switch (role) {
            case HR:
                submitBtn.setEnabled(true);
                approveBtn.setVisible(true);
                rejectBtn.setVisible(true);
                break;

            case IT:
                submitBtn.setEnabled(true);
                approveBtn.setVisible(false);
                rejectBtn.setVisible(false);
                break;

            case FINANCE:
                submitBtn.setEnabled(false);
                approveBtn.setVisible(false);
                rejectBtn.setVisible(false);
                break;

            default:
                submitBtn.setEnabled(false);
                approveBtn.setVisible(false);
                rejectBtn.setVisible(false);
                break;
        }
    }

    private void handleSubmitLeave() {
        try {
            leaveService.submitLeave(
                    Integer.parseInt(leaveIdField.getText().trim()),
                    Integer.parseInt(employeeIdField.getText().trim()),
                    leaveTypeField.getText().trim(),
                    startDateField.getText().trim(),
                    endDateField.getText().trim()
            );

            JOptionPane.showMessageDialog(this, "Leave request submitted successfully.");
            clearInputs();
            loadLeaves();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Leave Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleApprove() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a leave request first.");
            return;
        }

        try {
            int leaveId = Integer.parseInt(model.getValueAt(row, 0).toString());
            leaveService.approveLeave(leaveId);
            JOptionPane.showMessageDialog(this, "Leave approved successfully.");
            loadLeaves();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Leave Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleReject() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a leave request first.");
            return;
        }

        try {
            int leaveId = Integer.parseInt(model.getValueAt(row, 0).toString());
            leaveService.rejectLeave(leaveId);
            JOptionPane.showMessageDialog(this, "Leave rejected successfully.");
            loadLeaves();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Leave Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadLeaves() {
        model.setRowCount(0);

        List<LeaveRequest> leaves = leaveService.getAllLeaves();
        for (LeaveRequest leave : leaves) {
            model.addRow(new Object[]{
                    leave.getLeaveId(),
                    leave.getEmployeeId(),
                    leave.getLeaveType(),
                    leave.getStartDate(),
                    leave.getEndDate(),
                    leave.getStatus()
            });
        }
    }

    private void populateFieldsFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }

        leaveIdField.setText(String.valueOf(model.getValueAt(row, 0)));
        employeeIdField.setText(String.valueOf(model.getValueAt(row, 1)));
        leaveTypeField.setText(String.valueOf(model.getValueAt(row, 2)));
        startDateField.setText(String.valueOf(model.getValueAt(row, 3)));
        endDateField.setText(String.valueOf(model.getValueAt(row, 4)));
    }

    private void clearInputs() {
        leaveIdField.setText("");
        employeeIdField.setText("");
        leaveTypeField.setText("");
        startDateField.setText("");
        endDateField.setText("");
        table.clearSelection();
    }
}