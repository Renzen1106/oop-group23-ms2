package com.motorph.employeeapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a regular full-time employee.
 * Regular employees receive FULL allowances.
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
     * Regular employees receive full base pay + full allowances.
     */
    @Override
    public BigDecimal computeMonthlyPay() {
        return computeBaseMonthlyPay()
                .add(computeStandardAllowances());
    }
}