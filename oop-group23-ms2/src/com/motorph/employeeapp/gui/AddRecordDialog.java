package com.motorph.employeeapp.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.swing.*;

import com.motorph.employeeapp.model.Employee;
import com.motorph.employeeapp.model.ProbationaryEmployee;
import com.motorph.employeeapp.model.RegularEmployee;
import com.motorph.employeeapp.repository.EmployeeRepository;
import com.motorph.employeeapp.util.ValidationUtil;

public class AddRecordDialog extends JDialog {

    private final EmployeeRepository repo;
    private final Runnable onSave;

    private final JTextField idField = new JTextField(6);
    private final JTextField lastNameField = new JTextField(15);
    private final JTextField firstNameField = new JTextField(15);
    private final JSpinner birthdaySpinner = new JSpinner(new SpinnerDateModel());
    private final JTextField addressField = new JTextField(20);
    private final JTextField phoneField = new JTextField(12);
    private final JTextField sssField = new JTextField(12);
    private final JTextField philhealthField = new JTextField(12);
    private final JTextField tinField = new JTextField(12);
    private final JTextField pagibigField = new JTextField(12);
    private final JTextField statusField = new JTextField(10);
    private final JTextField positionField = new JTextField(15);
    private final JTextField supervisorField = new JTextField(15);
    private final JTextField basicSalaryField = new JTextField(10);
    private final JTextField riceSubsidyField = new JTextField(10);
    private final JTextField phoneAllowanceField = new JTextField(10);
    private final JTextField clothingAllowanceField = new JTextField(10);
    private final JTextField semiMonthlyRateField = new JTextField(10);
    private final JTextField hourlyRateField = new JTextField(10);

    public AddRecordDialog(Frame owner, EmployeeRepository repo, Runnable onSave) {
        super(owner, "Add Employee", true);
        this.repo = repo;
        this.onSave = onSave;

        birthdaySpinner.setEditor(new JSpinner.DateEditor(birthdaySpinner, "MM/dd/yyyy"));

        buildForm();
        setSize(400, 600);
        setLocationRelativeTo(owner);
    }

    private void buildForm() {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        panel.add(new JLabel("Employee ID")); panel.add(idField);
        panel.add(new JLabel("Last Name")); panel.add(lastNameField);
        panel.add(new JLabel("First Name")); panel.add(firstNameField);
        panel.add(new JLabel("Birthday")); panel.add(birthdaySpinner);
        panel.add(new JLabel("Address")); panel.add(addressField);
        panel.add(new JLabel("Phone")); panel.add(phoneField);
        panel.add(new JLabel("SSS")); panel.add(sssField);
        panel.add(new JLabel("PhilHealth")); panel.add(philhealthField);
        panel.add(new JLabel("TIN")); panel.add(tinField);
        panel.add(new JLabel("Pag-IBIG")); panel.add(pagibigField);
        panel.add(new JLabel("Status")); panel.add(statusField);
        panel.add(new JLabel("Position")); panel.add(positionField);
        panel.add(new JLabel("Supervisor")); panel.add(supervisorField);
        panel.add(new JLabel("Basic Salary")); panel.add(basicSalaryField);
        panel.add(new JLabel("Rice Subsidy")); panel.add(riceSubsidyField);
        panel.add(new JLabel("Phone Allowance")); panel.add(phoneAllowanceField);
        panel.add(new JLabel("Clothing Allowance")); panel.add(clothingAllowanceField);
        panel.add(new JLabel("Semi Monthly Rate")); panel.add(semiMonthlyRateField);
        panel.add(new JLabel("Hourly Rate")); panel.add(hourlyRateField);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(this::onSave);

        add(new JScrollPane(panel), BorderLayout.CENTER);
        add(saveBtn, BorderLayout.SOUTH);
    }

    private void onSave(ActionEvent e) {
        try {
            validateInputs();

            LocalDate birthDate = convertDate((Date) birthdaySpinner.getValue());

            Employee employee = createEmployee(birthDate);

            List<Employee> all = repo.loadAll();
            all.add(employee);
            repo.saveAll(all);

            JOptionPane.showMessageDialog(this, "Employee added successfully.");
            dispose();

            if (onSave != null) onSave.run();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validateInputs() {
        if (!ValidationUtil.isValidName(firstNameField.getText()) ||
            !ValidationUtil.isValidName(lastNameField.getText()))
            throw new IllegalArgumentException("Invalid name format.");

        if (!ValidationUtil.isValidPhone(phoneField.getText()))
            throw new IllegalArgumentException("Invalid phone number.");

        if (!ValidationUtil.isValidSSS(sssField.getText()))
            throw new IllegalArgumentException("Invalid SSS.");

        if (!ValidationUtil.isValidPhilHealth(philhealthField.getText()))
            throw new IllegalArgumentException("Invalid PhilHealth.");

        if (!ValidationUtil.isValidTIN(tinField.getText()))
            throw new IllegalArgumentException("Invalid TIN.");

        if (!ValidationUtil.isValidPagibig(pagibigField.getText()))
            throw new IllegalArgumentException("Invalid Pag-IBIG.");

        if (!ValidationUtil.isValidStatus(statusField.getText()))
            throw new IllegalArgumentException("Status must be 'Regular' or 'Probationary'.");

        if (basicSalaryField.getText().isBlank() ||
            riceSubsidyField.getText().isBlank() ||
            phoneAllowanceField.getText().isBlank() ||
            clothingAllowanceField.getText().isBlank() ||
            semiMonthlyRateField.getText().isBlank() ||
            hourlyRateField.getText().isBlank())
            throw new IllegalArgumentException("Salary fields cannot be empty.");

        if (!ValidationUtil.isNumeric(basicSalaryField.getText()) ||
            !ValidationUtil.isNumeric(riceSubsidyField.getText()) ||
            !ValidationUtil.isNumeric(phoneAllowanceField.getText()) ||
            !ValidationUtil.isNumeric(clothingAllowanceField.getText()) ||
            !ValidationUtil.isNumeric(semiMonthlyRateField.getText()) ||
            !ValidationUtil.isNumeric(hourlyRateField.getText()))
            throw new IllegalArgumentException("Salary fields must be numeric.");
    }

    private Employee createEmployee(LocalDate birthDate) {
        String status = statusField.getText().trim();

        if (status.equalsIgnoreCase("Probationary")) {
            return new ProbationaryEmployee(
                idField.getText(), firstNameField.getText(), lastNameField.getText(),
                birthDate, parseDecimal(basicSalaryField.getText(), "Basic Salary"),
                parseDecimal(riceSubsidyField.getText(), "Rice Subsidy"),
                parseDecimal(phoneAllowanceField.getText(), "Phone Allowance"),
                parseDecimal(clothingAllowanceField.getText(), "Clothing Allowance"),
                parseDecimal(semiMonthlyRateField.getText(), "Semi Monthly Rate"),
                parseDecimal(hourlyRateField.getText(), "Hourly Rate")
            );
        }

        return new RegularEmployee(
            idField.getText(), firstNameField.getText(), lastNameField.getText(),
            birthDate, parseDecimal(basicSalaryField.getText(), "Basic Salary"),
            parseDecimal(riceSubsidyField.getText(), "Rice Subsidy"),
            parseDecimal(phoneAllowanceField.getText(), "Phone Allowance"),
            parseDecimal(clothingAllowanceField.getText(), "Clothing Allowance"),
            parseDecimal(semiMonthlyRateField.getText(), "Semi Monthly Rate"),
            parseDecimal(hourlyRateField.getText(), "Hourly Rate")
        );
    }

    private LocalDate convertDate(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private BigDecimal parseDecimal(String text, String fieldName) {
        if (text == null || text.isBlank())
            throw new IllegalArgumentException(fieldName + " cannot be empty.");

        BigDecimal value = new BigDecimal(text);

        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException(fieldName + " cannot be negative.");

        return value;
    }
}