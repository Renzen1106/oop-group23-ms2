package com.motorph.employeeapp.gui;

import com.motorph.employeeapp.model.Employee;
import com.motorph.employeeapp.model.ProbationaryEmployee;
import com.motorph.employeeapp.model.RegularEmployee;
import com.motorph.employeeapp.repository.EmployeeRepository;
import com.motorph.employeeapp.util.ValidationUtil;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.swing.*;


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
    private final JTextField philHealthField = new JTextField(12);
    private final JTextField tinField = new JTextField(12);
    private final JTextField pagIbigField = new JTextField(12);
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
        super(owner, "Add New Employee", true);
        this.repo = repo;
        this.onSave = onSave;

        JSpinner.DateEditor de = new JSpinner.DateEditor(birthdaySpinner, "M/d/yyyy");
        birthdaySpinner.setEditor(de);

        buildForm();
        pack();
        setLocationRelativeTo(owner);

        idField.setText(nextId());
        idField.setEditable(false);
    }

    private String nextId() {
        try {
            List<Employee> all = repo.loadAll();
            return all.stream()
                    .map(Employee::getId)
                    .map(id -> {
                        try { return Integer.parseInt(id); }
                        catch (Exception e) { return 0; }
                    })
                    .max(Comparator.naturalOrder())
                    .map(n -> n + 1)
                    .orElse(10001)
                    .toString();
        } catch (IOException ex) {
            return "10001";
        }
    }

    private void buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;

        String[] labels = {
            "Employee #:", "Last Name:", "First Name:", "Birthday:",
            "Address:", "Phone:", "SSS #:", "PhilHealth #:", "TIN #:",
            "Pag-IBIG #:", "Status:", "Position:", "Supervisor:",
            "Basic Salary:", "Rice Subsidy:", "Phone Allowance:",
            "Clothing Allowance:", "Semi-monthly Rate:", "Hourly Rate:"
        };

        JComponent[] fields = {
            idField, lastNameField, firstNameField, birthdaySpinner,
            addressField, phoneField, sssField, philHealthField,
            tinField, pagIbigField, statusField, positionField,
            supervisorField, basicSalaryField, riceSubsidyField,
            phoneAllowanceField, clothingAllowanceField,
            semiMonthlyRateField, hourlyRateField
        };

        for (int i = 0; i < labels.length; i++) {
            c.gridx = 0;
            c.gridy = i;
            form.add(new JLabel(labels[i]), c);
            c.gridx = 1;
            form.add(fields[i], c);
        }

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton closeBtn = new JButton("Close");

        saveBtn.addActionListener(this::onSave);
        closeBtn.addActionListener(e -> dispose());

        buttons.add(saveBtn);
        buttons.add(closeBtn);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(form), BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void onSave(ActionEvent ev) {
        try {
            if (!ValidationUtil.isValidName(lastNameField.getText()))
                throw new IllegalArgumentException("Last Name invalid: letters, spaces, hyphens only.");
            if (!ValidationUtil.isValidName(firstNameField.getText()))
                throw new IllegalArgumentException("First Name invalid: letters, spaces, hyphens only.");
            if (!ValidationUtil.isValidAddress(addressField.getText()))
                throw new IllegalArgumentException("Address invalid.");
            if (!ValidationUtil.isValidPhone(phoneField.getText()))
                throw new IllegalArgumentException("Phone must be 9-digit Philippine landline.");
            if (!ValidationUtil.isValidSSS(sssField.getText()))
                throw new IllegalArgumentException("Invalid SSS format. Example: 12-3456789-0");
            if (!ValidationUtil.isValidPhilHealth(philHealthField.getText()))
                throw new IllegalArgumentException("Invalid PhilHealth format. Example: 123456789123");
            if (!ValidationUtil.isValidTIN(tinField.getText()))
                throw new IllegalArgumentException("Invalid TIN format. Example: 123-456-789-000");
            if (!ValidationUtil.isValidPagibig(pagIbigField.getText()))
                throw new IllegalArgumentException("Invalid Pag-IBIG format. Example: 123456789123");
            if (!ValidationUtil.isValidStatus(statusField.getText()))
                throw new IllegalArgumentException("Status must be 'Regular' or 'Probationary'.");
            if (!ValidationUtil.isValidPosition(positionField.getText()))
                throw new IllegalArgumentException("Position invalid: letters and spaces only.");
            if (!ValidationUtil.isValidSupervisor(supervisorField.getText()))
                throw new IllegalArgumentException("Supervisor invalid: letters and spaces only.");
            if (!ValidationUtil.isNumeric(basicSalaryField.getText()) ||
                !ValidationUtil.isNumeric(riceSubsidyField.getText()) ||
                !ValidationUtil.isNumeric(phoneAllowanceField.getText()) ||
                !ValidationUtil.isNumeric(clothingAllowanceField.getText()) ||
                !ValidationUtil.isNumeric(semiMonthlyRateField.getText()) ||
                !ValidationUtil.isNumeric(hourlyRateField.getText()))
                throw new IllegalArgumentException("Salary and allowances must be numeric.");

            Date dt = (Date) birthdaySpinner.getValue();
            LocalDate bday = Instant.ofEpochMilli(dt.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            String status = statusField.getText().trim();
            Employee e;
            if (status.equalsIgnoreCase("Probationary")) {
                e = new ProbationaryEmployee(
                        idField.getText().trim(),
                        lastNameField.getText().trim(),
                        firstNameField.getText().trim(),
                        bday,
                        parseDecimal(basicSalaryField.getText().trim()),
                        parseDecimal(riceSubsidyField.getText().trim()),
                        parseDecimal(phoneAllowanceField.getText().trim()),
                        parseDecimal(clothingAllowanceField.getText().trim()),
                        parseDecimal(semiMonthlyRateField.getText().trim()),
                        parseDecimal(hourlyRateField.getText().trim())
                );
                status = "Probationary";
            } else {
                e = new RegularEmployee(
                        idField.getText().trim(),
                        lastNameField.getText().trim(),
                        firstNameField.getText().trim(),
                        bday,
                        parseDecimal(basicSalaryField.getText().trim()),
                        parseDecimal(riceSubsidyField.getText().trim()),
                        parseDecimal(phoneAllowanceField.getText().trim()),
                        parseDecimal(clothingAllowanceField.getText().trim()),
                        parseDecimal(semiMonthlyRateField.getText().trim()),
                        parseDecimal(hourlyRateField.getText().trim())
                );
                status = "Regular";
            }

            e.setAddress(addressField.getText().trim());
            e.setPhone(phoneField.getText().trim());
            e.setSssNumber(sssField.getText().trim());
            e.setPhilHealthNumber(philHealthField.getText().trim());
            e.setTinNumber(tinField.getText().trim());
            e.setPagIbigNumber(pagIbigField.getText().trim());
            e.setStatus(status);
            e.setPosition(positionField.getText().trim());
            e.setSupervisor(supervisorField.getText().trim());

            List<Employee> all = repo.loadAll();
            all.add(e);
            repo.saveAll(all);

            JOptionPane.showMessageDialog(this, "Employee Record is saved.");
            dispose();
            if(onSave != null) onSave.run();

        } catch(Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid input: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private BigDecimal parseDecimal(String txt) {
        if(txt == null || txt.isBlank()) return BigDecimal.ZERO;
        return new BigDecimal(txt);
    }
}