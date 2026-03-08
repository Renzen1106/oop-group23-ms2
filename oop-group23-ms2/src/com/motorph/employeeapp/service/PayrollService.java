package com.motorph.employeeapp.service;

import com.motorph.employeeapp.model.Employee;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

/**
 * Handles payroll-related business logic such as
 * earnings, deductions, and net pay computation.
 */
public class PayrollService {

    /**
     * Computes the employee's gross monthly pay.
     * Formula: semi-monthly rate × 2 + allowances
     */
    public BigDecimal computeMonthlyPay(Employee emp, YearMonth ym) {
        if (emp == null) {
            throw new IllegalArgumentException("Employee must not be null");
        }

        BigDecimal grossMonthly = emp.getGrossSemiMonthlyRate()
                .multiply(BigDecimal.valueOf(2));

        return grossMonthly
                .add(emp.getRiceSubsidy())
                .add(emp.getPhoneAllowance())
                .add(emp.getClothingAllowance())
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Computes SSS deduction.
     */
    public BigDecimal computeSssDeduction(BigDecimal grossMonthly) {
        validateAmount(grossMonthly);
        return grossMonthly.multiply(new BigDecimal("0.04"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Computes PhilHealth deduction.
     */
    public BigDecimal computePhilHealthDeduction(BigDecimal grossMonthly) {
        validateAmount(grossMonthly);
        return grossMonthly.multiply(new BigDecimal("0.0275"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Computes Pag-IBIG deduction.
     */
    public BigDecimal computePagIbigDeduction(BigDecimal grossMonthly) {
        validateAmount(grossMonthly);
        return grossMonthly.multiply(new BigDecimal("0.02"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Computes withholding tax based on taxable income.
     */
    public BigDecimal computeWithholdingTax(
            BigDecimal earnings,
            BigDecimal sss,
            BigDecimal philHealth,
            BigDecimal pagIbig
    ) {
        validateAmount(earnings);
        validateAmount(sss);
        validateAmount(philHealth);
        validateAmount(pagIbig);

        BigDecimal taxableIncome = earnings
                .subtract(sss)
                .subtract(philHealth)
                .subtract(pagIbig);

        return taxableIncome.multiply(new BigDecimal("0.10"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Computes total mandatory deductions.
     */
    public BigDecimal computeTotalDeductions(Employee emp, YearMonth ym) {
        BigDecimal grossMonthly = computeMonthlyPay(emp, ym);

        BigDecimal sss = computeSssDeduction(grossMonthly);
        BigDecimal philHealth = computePhilHealthDeduction(grossMonthly);
        BigDecimal pagIbig = computePagIbigDeduction(grossMonthly);
        BigDecimal withholdingTax = computeWithholdingTax(grossMonthly, sss, philHealth, pagIbig);

        return sss.add(philHealth)
                .add(pagIbig)
                .add(withholdingTax)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Computes net monthly pay.
     */
    public BigDecimal computeNetMonthlyPay(Employee emp, YearMonth ym) {
        BigDecimal grossMonthly = computeMonthlyPay(emp, ym);
        BigDecimal totalDeductions = computeTotalDeductions(emp, ym);

        return grossMonthly.subtract(totalDeductions)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
    }
}