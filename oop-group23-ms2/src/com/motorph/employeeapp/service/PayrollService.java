package com.motorph.employeeapp.service;

import com.motorph.employeeapp.model.Employee;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

/**
 * Handles payroll-related business logic such as
 * deductions and net pay computation.
 *
 * Polymorphism is implemented through Employee.computeMonthlyPay(),
 * where each employee subtype can define its own pay computation.
 */
public class PayrollService {

    /**
     * Computes the employee's gross monthly pay using polymorphism.
     * The actual computation is delegated to the employee subtype.
     */
    public BigDecimal computeMonthlyPay(Employee emp, YearMonth ym) {
        validateEmployee(emp);

        BigDecimal grossMonthly = emp.computeMonthlyPay();

        return normalize(grossMonthly);
    }

    /**
     * Computes SSS deduction.
     */
    public BigDecimal computeSssDeduction(BigDecimal grossMonthly) {
        validateAmount(grossMonthly);

        return normalize(grossMonthly.multiply(new BigDecimal("0.04")));
    }

    /**
     * Computes PhilHealth deduction.
     */
    public BigDecimal computePhilHealthDeduction(BigDecimal grossMonthly) {
        validateAmount(grossMonthly);

        return normalize(grossMonthly.multiply(new BigDecimal("0.0275")));
    }

    /**
     * Computes Pag-IBIG deduction.
     */
    public BigDecimal computePagIbigDeduction(BigDecimal grossMonthly) {
        validateAmount(grossMonthly);

        return normalize(grossMonthly.multiply(new BigDecimal("0.02")));
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

        if (taxableIncome.compareTo(BigDecimal.ZERO) < 0) {
            taxableIncome = BigDecimal.ZERO;
        }

        return normalize(taxableIncome.multiply(new BigDecimal("0.10")));
    }

    /**
     * Computes total mandatory deductions.
     */
    public BigDecimal computeTotalDeductions(Employee emp, YearMonth ym) {
        validateEmployee(emp);

        BigDecimal grossMonthly = computeMonthlyPay(emp, ym);
        BigDecimal sss = computeSssDeduction(grossMonthly);
        BigDecimal philHealth = computePhilHealthDeduction(grossMonthly);
        BigDecimal pagIbig = computePagIbigDeduction(grossMonthly);
        BigDecimal withholdingTax = computeWithholdingTax(grossMonthly, sss, philHealth, pagIbig);

        return normalize(
                sss.add(philHealth)
                   .add(pagIbig)
                   .add(withholdingTax)
        );
    }

    /**
     * Computes net monthly pay.
     */
    public BigDecimal computeNetMonthlyPay(Employee emp, YearMonth ym) {
        validateEmployee(emp);

        BigDecimal grossMonthly = computeMonthlyPay(emp, ym);
        BigDecimal totalDeductions = computeTotalDeductions(emp, ym);

        return normalize(grossMonthly.subtract(totalDeductions));
    }

    private void validateEmployee(Employee emp) {
        if (emp == null) {
            throw new IllegalArgumentException("Employee must not be null");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must not be negative");
        }
    }

    private BigDecimal normalize(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}