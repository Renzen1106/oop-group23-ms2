package com.motorph.employeeapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a probationary employee.
 * Probationary employees receive REDUCED allowances.
 */
public class ProbationaryEmployee extends Employee {

    private static final BigDecimal ALLOWANCE_RATE = new BigDecimal("0.50");

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
     * Probationary employees receive base pay + 50% allowances.
     */
    @Override
    public BigDecimal computeMonthlyPay() {
        BigDecimal reducedAllowances = computeStandardAllowances()
                .multiply(ALLOWANCE_RATE);

        return computeBaseMonthlyPay()
                .add(reducedAllowances);
    }
}