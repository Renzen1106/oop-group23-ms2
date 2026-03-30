package com.motorph.employeeapp.gui;

import com.motorph.employeeapp.model.AttendanceRecord;
import com.motorph.employeeapp.service.AttendanceService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AttendanceDialog extends JDialog {

    private final AttendanceService attendanceService = new AttendanceService();

    private final JTextField employeeIdField = new JTextField(12);
    private final JTextField dateField = new JTextField(12);
    private final JTextField timeInField = new JTextField(12);
    private final JTextField timeOutField = new JTextField(12);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Employee ID", "Date", "Time In", "Time Out"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    public AttendanceDialog(Frame owner) {
        super(owner, "Attendance", true);

        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Employee ID:"));
        form.add(employeeIdField);
        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);
        form.add(new JLabel("Time In (HH:MM):"));
        form.add(timeInField);
        form.add(new JLabel("Time Out (HH:MM):"));
        form.add(timeOutField);

        JButton timeInBtn = new JButton("Time In");
        JButton timeOutBtn = new JButton("Time Out");
        JButton refreshBtn = new JButton("Refresh");
        JButton clearBtn = new JButton("Clear");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(timeInBtn);
        buttonPanel.add(timeOutBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(clearBtn);

        JScrollPane scrollPane = new JScrollPane(table);

        add(form, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        timeInBtn.addActionListener(e -> handleTimeIn());
        timeOutBtn.addActionListener(e -> handleTimeOut());
        refreshBtn.addActionListener(e -> loadAttendance());
        clearBtn.addActionListener(e -> clearInputs());

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> populateFieldsFromSelectedRow());

        setSize(700, 450);
        setLocationRelativeTo(owner);

        loadAttendance();
    }

    private void handleTimeIn() {
        try {
            attendanceService.timeIn(
                    employeeIdField.getText().trim(),
                    dateField.getText().trim(),
                    timeInField.getText().trim()
            );

            JOptionPane.showMessageDialog(this, "Time in recorded successfully.");
            clearInputs();
            loadAttendance();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Attendance Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleTimeOut() {
        try {
            attendanceService.timeOut(
                    employeeIdField.getText().trim(),
                    dateField.getText().trim(),
                    timeOutField.getText().trim()
            );

            JOptionPane.showMessageDialog(this, "Time out recorded successfully.");
            clearInputs();
            loadAttendance();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Attendance Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadAttendance() {
        model.setRowCount(0);

        List<AttendanceRecord> records = attendanceService.getAttendanceHistory();
        for (AttendanceRecord record : records) {
            model.addRow(new Object[]{
                    record.getEmployeeId(),
                    record.getDate(),
                    record.getTimeIn(),
                    record.getTimeOut()
            });
        }
    }

    private void populateFieldsFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }

        employeeIdField.setText(String.valueOf(model.getValueAt(row, 0)));
        dateField.setText(String.valueOf(model.getValueAt(row, 1)));
        timeInField.setText(String.valueOf(model.getValueAt(row, 2)));

        Object timeOutValue = model.getValueAt(row, 3);
        timeOutField.setText(timeOutValue == null ? "" : String.valueOf(timeOutValue));
    }

    private void clearInputs() {
        employeeIdField.setText("");
        dateField.setText("");
        timeInField.setText("");
        timeOutField.setText("");
        table.clearSelection();
    }
}