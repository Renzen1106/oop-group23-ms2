package com.motorph.employeeapp.gui;

import com.motorph.employeeapp.model.Employee;
import com.motorph.employeeapp.model.ProbationaryEmployee;
import com.motorph.employeeapp.model.RegularEmployee;
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
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
            return repo.loadAll().stream()
                    .map(Employee::getId)
                    .map(id -> {
                        try { return Integer.parseInt(id); }
                        catch (Exception e) { return 0; }
                    })
                    .max(Integer::compareTo)
                    .map(n -> n + 1)
                    .orElse(10001)
                    .toString();
        } catch (IOException e) {
            return "10001";
        }
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
        form.add(new JLabel("Status (Regular/Probationary):")); form.add(statusField);
        form.add(new JLabel("Position:")); form.add(positionField);
        form.add(new JLabel("Supervisor:")); form.add(supervisorField);
        form.add(new JLabel("Basic Salary:")); form.add(basicSalaryField);
        form.add(new JLabel("Rice Subsidy:")); form.add(riceSubsidyField);
        form.add(new JLabel("Phone Allowance:")); form.add(phoneAllowanceField);
        form.add(new JLabel("Clothing Allowance:")); form.add(clothingAllowanceField);
        form.add(new JLabel("Semi-monthly Rate:")); form.add(semiMonthlyRateField);
        form.add(new JLabel("Hourly Rate:")); form.add(hourlyRateField);

        JButton saveBtn = new JButton("Save");
        JButton closeBtn = new JButton("Close");

        saveBtn.addActionListener(this::onSave);
        closeBtn.addActionListener(e -> dispose());

        JPanel buttons = new JPanel();
        buttons.add(saveBtn);
        buttons.add(closeBtn);

        getContentPane().add(new JScrollPane(form), BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void onSave(ActionEvent ev) {
        try {
            validateInputs();

            LocalDate bday = convertDate((Date) birthdaySpinner.getValue());

            Employee employee = createEmployee(bday);

            setAdditionalFields(employee);

            List<Employee> all = repo.loadAll();
            all.add(employee);
            repo.saveAll(all);

            JOptionPane.showMessageDialog(this, "Employee saved successfully.");
            dispose();

            if (onSave != null) onSave.run();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void validateInputs() {

        if (!ValidationUtil.isValidName(lastNameField.getText()))
            throw new IllegalArgumentException("Invalid Last Name.");

        if (!ValidationUtil.isValidName(firstNameField.getText()))
            throw new IllegalArgumentException("Invalid First Name.");

        if (!ValidationUtil.isValidAddress(addressField.getText()))
            throw new IllegalArgumentException("Invalid Address.");

        if (!ValidationUtil.isValidPhone(phoneField.getText()))
            throw new IllegalArgumentException("Invalid Phone.");

        if (!ValidationUtil.isValidSSS(sssField.getText()))
            throw new IllegalArgumentException("Invalid SSS.");

        if (!ValidationUtil.isValidPhilHealth(philHealthField.getText()))
            throw new IllegalArgumentException("Invalid PhilHealth.");

        if (!ValidationUtil.isValidTIN(tinField.getText()))
            throw new IllegalArgumentException("Invalid TIN.");

        if (!ValidationUtil.isValidPagibig(pagIbigField.getText()))
            throw new IllegalArgumentException("Invalid Pag-IBIG.");

        if (!ValidationUtil.isValidStatus(statusField.getText()))
            throw new IllegalArgumentException("Status must be Regular or Probationary.");

        if (!ValidationUtil.isValidPosition(positionField.getText()))
            throw new IllegalArgumentException("Invalid Position.");

        if (!ValidationUtil.isValidSupervisor(supervisorField.getText()))
            throw new IllegalArgumentException("Invalid Supervisor.");

        if (!ValidationUtil.isNumeric(basicSalaryField.getText()) ||
            !ValidationUtil.isNumeric(riceSubsidyField.getText()) ||
            !ValidationUtil.isNumeric(phoneAllowanceField.getText()) ||
            !ValidationUtil.isNumeric(clothingAllowanceField.getText()) ||
            !ValidationUtil.isNumeric(semiMonthlyRateField.getText()) ||
            !ValidationUtil.isNumeric(hourlyRateField.getText()))
            throw new IllegalArgumentException("Salary fields must be numeric.");
    }

    private Employee createEmployee(LocalDate bday) {

        String status = statusField.getText().trim();

        if (status.equalsIgnoreCase("Probationary")) {
            return new ProbationaryEmployee(
                    idField.getText().trim(),
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    bday,
                    parseDecimal(basicSalaryField.getText()),
                    parseDecimal(riceSubsidyField.getText()),
                    parseDecimal(phoneAllowanceField.getText()),
                    parseDecimal(clothingAllowanceField.getText()),
                    parseDecimal(semiMonthlyRateField.getText()),
                    parseDecimal(hourlyRateField.getText())
            );
        }

        return new RegularEmployee(
                idField.getText().trim(),
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                bday,
                parseDecimal(basicSalaryField.getText()),
                parseDecimal(riceSubsidyField.getText()),
                parseDecimal(phoneAllowanceField.getText()),
                parseDecimal(clothingAllowanceField.getText()),
                parseDecimal(semiMonthlyRateField.getText()),
                parseDecimal(hourlyRateField.getText())
        );
    }

    private void setAdditionalFields(Employee e) {
        e.setAddress(addressField.getText().trim());
        e.setPhone(phoneField.getText().trim());
        e.setSssNumber(sssField.getText().trim());
        e.setPhilHealthNumber(philHealthField.getText().trim());
        e.setTinNumber(tinField.getText().trim());
        e.setPagIbigNumber(pagIbigField.getText().trim());
        e.setStatus(statusField.getText().trim());
        e.setPosition(positionField.getText().trim());
        e.setSupervisor(supervisorField.getText().trim());
    }

    private LocalDate convertDate(Date dt) {
        return Instant.ofEpochMilli(dt.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private BigDecimal parseDecimal(String txt) {
        if (txt == null || txt.isBlank()) return BigDecimal.ZERO;

        BigDecimal val = new BigDecimal(txt);

        if (val.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Values cannot be negative.");

        return val;
    }
}