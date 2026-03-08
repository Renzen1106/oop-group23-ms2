package com.motorph.employeeapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a regular full-time employee.
 * Implements payroll calculations for regular employees.
 */
public class RegularEmployee extends Employee {

    public RegularEmployee(
            String id,
            String firstName,
            String lastName,
            LocalDate birthDate,
            BigDecimal basicSalary,
            BigDecimal riceSubsidy,
            BigDecimal phoneAllowance,
            BigDecimal clothingAllowance,
            BigDecimal grossSemiMonthlyRate,
            BigDecimal hourlyRate
    ) {
        super(
                id,
                firstName,
                lastName,
                birthDate,
                basicSalary,
                riceSubsidy,
                phoneAllowance,
                clothingAllowance,
                grossSemiMonthlyRate,
                hourlyRate
        );
    }

    /**
     * Gross pay for regular employees.
     */
    @Override
    public BigDecimal calculateGrossPay() {
        return getBasicSalary()
                .add(getRiceSubsidy())
                .add(getPhoneAllowance())
                .add(getClothingAllowance());
    }

    /**
     * Total allowances.
     */
    @Override
    public BigDecimal calculateAllowances() {
        return getRiceSubsidy()
                .add(getPhoneAllowance())
                .add(getClothingAllowance());
    }

    /**
     * Placeholder deduction logic.
     * You can later integrate SSS, PhilHealth, Pag-IBIG, tax, etc.
     */
    @Override
    public BigDecimal calculateDeductions() {
        return BigDecimal.ZERO;
    }

    /**
     * Net pay = gross pay − deductions.
     */
    @Override
    public BigDecimal calculateNetPay() {
        return calculateGrossPay().subtract(calculateDeductions());
    }
}