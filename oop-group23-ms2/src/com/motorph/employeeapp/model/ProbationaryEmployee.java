package com.motorph.employeeapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a probationary employee.
 * Payroll logic can differ slightly from regular employees.
 */
public class ProbationaryEmployee extends Employee {

    public ProbationaryEmployee(
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
     * Gross pay for probationary employees.
     */
    @Override
    public BigDecimal calculateGrossPay() {
        return getBasicSalary()
                .add(getRiceSubsidy())
                .add(getPhoneAllowance())
                .add(getClothingAllowance());
    }

    /**
     * Allowances.
     */
    @Override
    public BigDecimal calculateAllowances() {
        return getRiceSubsidy()
                .add(getPhoneAllowance())
                .add(getClothingAllowance());
    }

    /**
     * Probationary employees may have slightly different deductions.
     * For now we keep it simple.
     */
    @Override
    public BigDecimal calculateDeductions() {
        return BigDecimal.ZERO;
    }

    /**
     * Net pay calculation.
     */
    @Override
    public BigDecimal calculateNetPay() {
        return calculateGrossPay().subtract(calculateDeductions());
    }
}