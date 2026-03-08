package com.motorph.employeeapp.gui;

import com.motorph.employeeapp.model.Employee;
import com.motorph.employeeapp.service.PayrollService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class PayslipSplitDialog extends JDialog {
    private final Employee emp;
    private final PayrollService payrollService = new PayrollService();

    // Left fields
    private final JTextField idF = new JTextField(15);
    private final JTextField lnF = new JTextField(15);
    private final JTextField fnF = new JTextField(15);
    private final JTextField bdF = new JTextField(15);
    private final JTextArea addr = new JTextArea(3, 20);
    private final JTextField phF = new JTextField(15);
    private final JTextField sssF = new JTextField(15);
    private final JTextField philF = new JTextField(15);
    private final JTextField tinF = new JTextField(15);
    private final JTextField pagF = new JTextField(15);
    private final JTextField stF = new JTextField(15);
    private final JTextField posF = new JTextField(15);
    private final JTextField supF = new JTextField(15);

    // Right controls / output fields
    private final JComboBox<String> monthCB = new JComboBox<>();
    private final JTextField periodF = new JTextField(25);
    private final JTextField earnedF = new JTextField(25);
    private final JTextField riceF = new JTextField(25);
    private final JTextField phAllF = new JTextField(25);
    private final JTextField clAllF = new JTextField(25);
    private final JTextField grossF = new JTextField(25);
    private final JTextField sssDedF = new JTextField(25);
    private final JTextField philDedF = new JTextField(25);
    private final JTextField pagDedF = new JTextField(25);
    private final JTextField taxDedF = new JTextField(25);
    private final JTextField totDedF = new JTextField(25);
    private final JTextField netF = new JTextField(25);

    public PayslipSplitDialog(Frame owner, Employee e) {
        super(owner, "Employee Payslip", true);
        this.emp = e;

        populateEmployeeDetails();
        setupMonthComboBox();
        setupOutputFields();

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                buildDetailPane(),
                buildCalculatorPane()
        );
        split.setResizeWeight(0.5);
        split.setDividerLocation(350);

        getContentPane().add(split);
        pack();
        setLocationRelativeTo(owner);
    }

    private void populateEmployeeDetails() {
        idF.setText(emp.getId());
        lnF.setText(emp.getLastName());
        fnF.setText(emp.getFirstName());
        bdF.setText(emp.getBirthDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        addr.setText(emp.getAddress());
        phF.setText(emp.getPhone());
        sssF.setText(emp.getSssNumber());
        philF.setText(emp.getPhilHealthNumber());
        tinF.setText(emp.getTinNumber());
        pagF.setText(emp.getPagIbigNumber());
        stF.setText(emp.getStatus());
        posF.setText(emp.getPosition());
        supF.setText(emp.getSupervisor());

        addr.setLineWrap(true);
        addr.setWrapStyleWord(true);
        addr.setEditable(false);
        addr.setBackground(UIManager.getColor("TextField.background"));
        addr.setBorder(UIManager.getBorder("TextField.border"));

        for (JComponent f : new JComponent[]{
                idF, lnF, fnF, bdF, phF, sssF, philF, tinF, pagF, stF, posF, supF
        }) {
            if (f instanceof JTextField textField) {
                textField.setEditable(false);
            }
        }
    }

    private void setupMonthComboBox() {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        for (String month : months) {
            monthCB.addItem(month);
        }

        monthCB.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
    }

    private void setupOutputFields() {
        for (JTextField f : new JTextField[]{
                periodF, earnedF, riceF, phAllF, clAllF, grossF,
                sssDedF, philDedF, pagDedF, taxDedF, totDedF, netF
        }) {
            f.setEditable(false);
            f.setPreferredSize(new Dimension(200, 24));
        }
    }

    private JPanel buildDetailPane() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(12, 12, 12, 12));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.LINE_END;

        String[] labels = {
                "Employee No.:", "Last Name:", "First Name:", "Birthday:",
                "Address:", "Phone:", "SSS #:", "PhilHealth #:", "TIN #:", "Pag-IBIG #:",
                "Status:", "Position:", "Supervisor:"
        };

        Component[] fields = {
                idF, lnF, fnF, bdF,
                new JScrollPane(addr),
                phF, sssF, philF, tinF, pagF,
                stF, posF, supF
        };

        for (int i = 0; i < labels.length; i++) {
            c.gridy = i;
            c.gridx = 0;
            p.add(new JLabel(labels[i]), c);

            c.gridx = 1;
            p.add(fields[i], c);
        }

        return p;
    }

    private JPanel buildCalculatorPane() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(12, 12, 12, 12));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridy = 0;
        c.gridx = 0;
        p.add(new JLabel("Month:"), c);

        c.gridx = 1;
        p.add(monthCB, c);

        c.gridx = 2;
        JButton compute = new JButton("Compute");
        compute.setBackground(new Color(45, 137, 239));
        compute.setForeground(Color.WHITE);
        compute.setFocusPainted(false);
        compute.setBorder(new EmptyBorder(8, 16, 8, 16));
        compute.addActionListener(e -> doCompute());
        p.add(compute, c);

        String[] labels = {
                "Salary Period:", "Salary Earned:", "Rice Allowance:",
                "Phone Allowance:", "Clothing Allowance:", "Gross:",
                "SSS Deduction:", "PhilHealth Deduction:", "Pag-IBIG Deduction:",
                "Withholding Tax:", "Total Deductions:", "Net Salary:"
        };

        JTextField[] fields = {
                periodF, earnedF, riceF, phAllF, clAllF, grossF,
                sssDedF, philDedF, pagDedF, taxDedF, totDedF, netF
        };

        for (int i = 0; i < labels.length; i++) {
            c.gridy = i + 1;
            c.gridx = 0;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.NONE;
            p.add(new JLabel(labels[i]), c);

            c.gridx = 1;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            p.add(fields[i], c);

            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
        }

        return p;
    }

    private void doCompute() {
        try {
            int selectedMonth = monthCB.getSelectedIndex() + 1;
            YearMonth yearMonth = YearMonth.of(LocalDate.now().getYear(), selectedMonth);

            // All computations are delegated to the service layer
            BigDecimal earned = payrollService.computeMonthlyPay(emp, yearMonth);
            BigDecimal sssDeduction = payrollService.computeSssDeduction(earned);
            BigDecimal philHealthDeduction = payrollService.computePhilHealthDeduction(earned);
            BigDecimal pagIbigDeduction = payrollService.computePagIbigDeduction(earned);
            BigDecimal withholdingTax = payrollService.computeWithholdingTax(
                    earned,
                    sssDeduction,
                    philHealthDeduction,
                    pagIbigDeduction
            );
            BigDecimal totalDeductions = payrollService.computeTotalDeductions(emp, yearMonth);
            BigDecimal netSalary = payrollService.computeNetMonthlyPay(emp, yearMonth);

            // Display values only
            periodF.setText(formatSalaryPeriod(yearMonth));
            earnedF.setText(formatAmount(earned));
            riceF.setText(formatAmount(emp.getRiceSubsidy()));
            phAllF.setText(formatAmount(emp.getPhoneAllowance()));
            clAllF.setText(formatAmount(emp.getClothingAllowance()));
            grossF.setText(formatAmount(earned));
            sssDedF.setText(formatAmount(sssDeduction));
            philDedF.setText(formatAmount(philHealthDeduction));
            pagDedF.setText(formatAmount(pagIbigDeduction));
            taxDedF.setText(formatAmount(withholdingTax));
            totDedF.setText(formatAmount(totalDeductions));
            netF.setText(formatAmount(netSalary));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to compute payslip: " + ex.getMessage(),
                    "Computation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String formatSalaryPeriod(YearMonth yearMonth) {
        String monthName = yearMonth.getMonth().name().substring(0, 1)
                + yearMonth.getMonth().name().substring(1).toLowerCase();
        return monthName + " " + yearMonth.getYear();
    }

    private String formatAmount(BigDecimal amount) {
        return amount == null ? "" : amount.toPlainString();
    }
}