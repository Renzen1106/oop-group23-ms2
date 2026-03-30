package com.motorph.employeeapp.gui;

import com.motorph.employeeapp.model.Employee;
import com.motorph.employeeapp.repository.EmployeeRepository;
import com.motorph.employeeapp.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
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
    private final JComboBox<String> statusField = new JComboBox<>(new String[]{"Regular", "Probationary"});
    private final JTextField positionField = new JTextField(15);
    private final JTextField supervisorField = new JTextField(15);
    private final JTextField basicSalaryField = new JTextField(10);
    private final JTextField riceSubsidyField = new JTextField(10);
    private final JTextField phoneAllowanceField = new JTextField(10);
    private final JTextField clothingAllowanceField = new JTextField(10);
    private final JTextField semiMonthlyRateField = new JTextField(10);
    private final JTextField hourlyRateField = new JTextField(10);

    public UpdateDialog(Frame owner, EmployeeRepository repo, Employee employee, Runnable onUpdate) {
        super(owner, "Update Employee", true);
        this.repo = repo;
        this.employee = employee;
        this.onUpdate = onUpdate;

        JSpinner.DateEditor de = new JSpinner.DateEditor(birthdaySpinner, "M/d/yyyy");
        birthdaySpinner.setEditor(de);

        buildForm();
        loadEmployee();

        pack();
        setLocationRelativeTo(owner);
    }

    private void buildForm() {
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));

        form.add(new JLabel("Employee #:")); form.add(idField);
        form.add(new JLabel("Last Name:")); form.add(lastNameField);
        form.add(new JLabel("First Name:")); form.add(firstNameField);
        form.add(new JLabel("Birthday:")); form.add(birthdaySpinner);
        form.add(new JLabel("Address:")); form.add(addressField);
        form.add(new JLabel("Phone:")); form.add(phoneField);
        form.add(new JLabel("SSS #:")); form.add(sssField);
        form.add(new JLabel("PhilHealth #:")); form.add(philHealthField);
        form.add(new JLabel("TIN #:")); form.add(tinField);
        form.add(new JLabel("Pag-IBIG #:")); form.add(pagIbigField);
        form.add(new JLabel("Status:")); form.add(statusField);
        form.add(new JLabel("Position:")); form.add(positionField);
        form.add(new JLabel("Supervisor:")); form.add(supervisorField);
        form.add(new JLabel("Basic Salary:")); form.add(basicSalaryField);
        form.add(new JLabel("Rice Subsidy:")); form.add(riceSubsidyField);
        form.add(new JLabel("Phone Allowance:")); form.add(phoneAllowanceField);
        form.add(new JLabel("Clothing Allowance:")); form.add(clothingAllowanceField);
        form.add(new JLabel("Semi-monthly Rate:")); form.add(semiMonthlyRateField);
        form.add(new JLabel("Hourly Rate:")); form.add(hourlyRateField);

        idField.setEditable(false);

        JButton saveBtn = new JButton("Update");
        JButton closeBtn = new JButton("Close");

        saveBtn.addActionListener(this::onSave);
        closeBtn.addActionListener(e -> dispose());

        JPanel buttons = new JPanel();
        buttons.add(saveBtn);
        buttons.add(closeBtn);

        getContentPane().add(new JScrollPane(form), BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void loadEmployee() {
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

    private void onSave(ActionEvent e) {
        try {
            validateInputs();

            LocalDate birthDate = convertDate((Date) birthdaySpinner.getValue());

            updateEmployeeFields(birthDate);

            List<Employee> all = repo.loadAll();
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getId().equals(employee.getId())) {
                    all.set(i, employee);
                    break;
                }
            }

            repo.saveAll(all);

            JOptionPane.showMessageDialog(this, "Employee updated successfully.");
            dispose();

            if (onUpdate != null) {
                onUpdate.run();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Input Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void validateInputs() {
        if (!ValidationUtil.isValidName(lastNameField.getText())) {
            throw new IllegalArgumentException("Invalid Last Name.");
        }

        if (!ValidationUtil.isValidName(firstNameField.getText())) {
            throw new IllegalArgumentException("Invalid First Name.");
        }

        if (!ValidationUtil.isValidAddress(addressField.getText())) {
            throw new IllegalArgumentException("Invalid Address.");
        }

        if (!ValidationUtil.isValidPhone(phoneField.getText())) {
            throw new IllegalArgumentException("Invalid Phone.");
        }

        if (!ValidationUtil.isValidSSS(sssField.getText())) {
            throw new IllegalArgumentException("Invalid SSS.");
        }

        if (!ValidationUtil.isValidPhilHealth(philHealthField.getText())) {
            throw new IllegalArgumentException("Invalid PhilHealth.");
        }

        if (!ValidationUtil.isValidTIN(tinField.getText())) {
            throw new IllegalArgumentException("Invalid TIN.");
        }

        if (!ValidationUtil.isValidPagibig(pagIbigField.getText())) {
            throw new IllegalArgumentException("Invalid Pag-IBIG.");
        }

        if (!ValidationUtil.isValidStatus((String) statusField.getSelectedItem())) {
            throw new IllegalArgumentException("Status must be exactly 'Regular' or 'Probationary'.");
        }

        if (!ValidationUtil.isValidPosition(positionField.getText())) {
            throw new IllegalArgumentException("Invalid Position.");
        }

        if (!ValidationUtil.isValidSupervisor(supervisorField.getText())) {
            throw new IllegalArgumentException("Invalid Supervisor.");
        }

        if (basicSalaryField.getText().isBlank() ||
            riceSubsidyField.getText().isBlank() ||
            phoneAllowanceField.getText().isBlank() ||
            clothingAllowanceField.getText().isBlank() ||
            semiMonthlyRateField.getText().isBlank() ||
            hourlyRateField.getText().isBlank()) {
            throw new IllegalArgumentException("Salary fields cannot be empty.");
        }

        if (!ValidationUtil.isNumeric(basicSalaryField.getText()) ||
            !ValidationUtil.isNumeric(riceSubsidyField.getText()) ||
            !ValidationUtil.isNumeric(phoneAllowanceField.getText()) ||
            !ValidationUtil.isNumeric(clothingAllowanceField.getText()) ||
            !ValidationUtil.isNumeric(semiMonthlyRateField.getText()) ||
            !ValidationUtil.isNumeric(hourlyRateField.getText())) {
            throw new IllegalArgumentException("Salary fields must be numeric.");
        }
    }

    private void updateEmployeeFields(LocalDate birthDate) {
        employee.setLastName(lastNameField.getText().trim());
        employee.setFirstName(firstNameField.getText().trim());
        employee.setBirthDate(birthDate);
        employee.setAddress(addressField.getText().trim());
        employee.setPhone(phoneField.getText().trim());
        employee.setSssNumber(sssField.getText().trim());
        employee.setPhilHealthNumber(philHealthField.getText().trim());
        employee.setTinNumber(tinField.getText().trim());
        employee.setPagIbigNumber(pagIbigField.getText().trim());
        employee.setStatus(statusField.getSelectedItem().toString());
        employee.setPosition(positionField.getText().trim());
        employee.setSupervisor(supervisorField.getText().trim());
        employee.setBasicSalary(parseDecimal(basicSalaryField.getText(), "Basic Salary"));
        employee.setRiceSubsidy(parseDecimal(riceSubsidyField.getText(), "Rice Subsidy"));
        employee.setPhoneAllowance(parseDecimal(phoneAllowanceField.getText(), "Phone Allowance"));
        employee.setClothingAllowance(parseDecimal(clothingAllowanceField.getText(), "Clothing Allowance"));
        employee.setGrossSemiMonthlyRate(parseDecimal(semiMonthlyRateField.getText(), "Semi-monthly Rate"));
        employee.setHourlyRate(parseDecimal(hourlyRateField.getText(), "Hourly Rate"));
    }

    private LocalDate convertDate(Date dt) {
        return Instant.ofEpochMilli(dt.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private BigDecimal parseDecimal(String txt, String fieldName) {
        if (txt == null || txt.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }

        BigDecimal val = new BigDecimal(txt.trim());

        if (val.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative.");
        }

        return val;
    }
}