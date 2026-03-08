package com.motorph.employeeapp.gui;

import com.motorph.employeeapp.model.Employee;
import com.motorph.employeeapp.repository.EmployeeRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class UpdateDialog extends JDialog {
    private final EmployeeRepository repo;
    private final Employee employee;
    private final Runnable onUpdate;

    // form fields
    private final JTextField idField = new JTextField(10);
    private final JTextField lastNameField = new JTextField(15);
    private final JTextField firstNameField = new JTextField(15);
    private final JSpinner birthdaySpinner = new JSpinner(new SpinnerDateModel());
    private final JTextField addressField = new JTextField(30);
    private final JTextField phoneField = new JTextField(12);
    private final JTextField sssField = new JTextField(12);
    private final JTextField philHealthField = new JTextField(12);
    private final JTextField tinField = new JTextField(12);
    private final JTextField pagIbigField = new JTextField(12);
    private final JComboBox<String> statusField =
            new JComboBox<>(new String[]{"Regular", "Probationary"});
    private final JTextField positionField = new JTextField(15);
    private final JTextField supervisorField = new JTextField(15);
    private final JTextField basicSalaryField = new JTextField(10);
    private final JTextField riceSubsidyField = new JTextField(10);
    private final JTextField phoneAllowanceField = new JTextField(10);
    private final JTextField clothingAllowanceField = new JTextField(10);
    private final JTextField semiMonthlyRateField = new JTextField(10);
    private final JTextField hourlyRateField = new JTextField(10);

    public UpdateDialog(Frame owner,
                        EmployeeRepository repo,
                        Employee employee,
                        Runnable onUpdate) {
        super(owner, "Edit Employee", true);
        this.repo = repo;
        this.employee = employee;
        this.onUpdate = onUpdate;

        JSpinner.DateEditor de = new JSpinner.DateEditor(birthdaySpinner, "M/d/yyyy");
        birthdaySpinner.setEditor(de);

        buildForm();
        loadEmployeeIntoFields();

        pack();
        setLocationRelativeTo(owner);
    }

    private void buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;

        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;

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
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            form.add(new JLabel(labels[i]), c);

            c.gridx = 1;
            c.weightx = 1.0;
            c.fill = GridBagConstraints.HORIZONTAL;
            form.add(fields[i], c);
        }

        idField.setEditable(false);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Update");
        JButton closeBtn = new JButton("Close");
        saveBtn.addActionListener(this::onSave);
        closeBtn.addActionListener(e -> dispose());
        buttons.add(saveBtn);
        buttons.add(closeBtn);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(form), BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void loadEmployeeIntoFields() {
        idField.setText(employee.getId());
        lastNameField.setText(employee.getLastName());
        firstNameField.setText(employee.getFirstName());
        birthdaySpinner.setValue(java.sql.Date.valueOf(employee.getBirthDate()));
        addressField.setText(employee.getAddress());
        phoneField.setText(employee.getPhone());
        sssField.setText(employee.getSssNumber());
        philHealthField.setText(employee.getPhilHealthNumber());
        tinField.setText(employee.getTinNumber());
        pagIbigField.setText(employee.getPagIbigNumber());
        statusField.setSelectedItem(employee.getStatus());
        positionField.setText(employee.getPosition());
        supervisorField.setText(employee.getSupervisor());
        basicSalaryField.setText(employee.getBasicSalary().toPlainString());
        riceSubsidyField.setText(employee.getRiceSubsidy().toPlainString());
        phoneAllowanceField.setText(employee.getPhoneAllowance().toPlainString());
        clothingAllowanceField.setText(employee.getClothingAllowance().toPlainString());
        semiMonthlyRateField.setText(employee.getGrossSemiMonthlyRate().toPlainString());
        hourlyRateField.setText(employee.getHourlyRate().toPlainString());
    }

    private void onSave(ActionEvent ev) {
        try {
            if (lastNameField.getText().trim().isEmpty()
                    || firstNameField.getText().trim().isEmpty()
                    || birthdaySpinner.getValue() == null
                    || sssField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Last Name, First Name, Birthday, and SSS # are required.");
            }

            Date dt = (Date) birthdaySpinner.getValue();
            LocalDate bday = Instant.ofEpochMilli(dt.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            employee.setLastName(lastNameField.getText().trim());
            employee.setFirstName(firstNameField.getText().trim());
            employee.setBirthDate(bday);
            employee.setAddress(addressField.getText().trim());
            employee.setPhone(phoneField.getText().trim());
            employee.setSssNumber(sssField.getText().trim());
            employee.setPhilHealthNumber(philHealthField.getText().trim());
            employee.setTinNumber(tinField.getText().trim());
            employee.setPagIbigNumber(pagIbigField.getText().trim());
            employee.setStatus(statusField.getSelectedItem().toString());
            employee.setPosition(positionField.getText().trim());
            employee.setSupervisor(supervisorField.getText().trim());
            employee.setBasicSalary(parseDecimal(basicSalaryField.getText().trim()));
            employee.setRiceSubsidy(parseDecimal(riceSubsidyField.getText().trim()));
            employee.setPhoneAllowance(parseDecimal(phoneAllowanceField.getText().trim()));
            employee.setClothingAllowance(parseDecimal(clothingAllowanceField.getText().trim()));
            employee.setGrossSemiMonthlyRate(parseDecimal(semiMonthlyRateField.getText().trim()));
            employee.setHourlyRate(parseDecimal(hourlyRateField.getText().trim()));

            List<Employee> all = repo.loadAll();
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getId().equals(employee.getId())) {
                    all.set(i, employee);
                    break;
                }
            }
            repo.saveAll(all);

            JOptionPane.showMessageDialog(this, "Record Updated!");
            dispose();
            if (onUpdate != null) {
                onUpdate.run();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid input: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private BigDecimal parseDecimal(String txt) {
        if (txt == null || txt.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(txt);
    }
}