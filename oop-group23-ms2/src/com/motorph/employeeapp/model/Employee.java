package com.motorph.employeeapp.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Abstract parent class representing a MotorPH employee.
 * Contains shared personal, employment, and salary-related attributes.
 *
 * Polymorphism is implemented through computeMonthlyPay(), where
 * each employee subtype can override how gross monthly pay is computed.
 */
public abstract class Employee {
    private final String id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    // contact & government IDs
    private String address = "";
    private String phone = "";
    private String sssNumber = "";
    private String philHealthNumber = "";
    private String tinNumber = "";
    private String pagIbigNumber = "";

    // employment info
    private String status = "";
    private String position = "";
    private String supervisor = "";

    // salary fields
    private BigDecimal basicSalary;
    private BigDecimal riceSubsidy;
    private BigDecimal phoneAllowance;
    private BigDecimal clothingAllowance;
    private BigDecimal grossSemiMonthlyRate;
    private BigDecimal hourlyRate;

    /**
     * Main constructor for complete employee data.
     */
    public Employee(
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
        this.id = requireNonBlank(id, "ID must not be blank");
        setFirstName(firstName);
        setLastName(lastName);
        setBirthDate(birthDate);
        setBasicSalary(basicSalary);
        setRiceSubsidy(riceSubsidy);
        setPhoneAllowance(phoneAllowance);
        setClothingAllowance(clothingAllowance);
        setGrossSemiMonthlyRate(grossSemiMonthlyRate);
        setHourlyRate(hourlyRate);
    }

    /**
     * Convenience constructor when only basic personal data is known.
     */
    public Employee(String id, String firstName, String lastName, LocalDate birthDate) {
        this(
                id,
                firstName,
                lastName,
                birthDate,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    // =========================
    // Basic identity
    // =========================
    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = requireNonBlank(firstName, "First name cannot be empty");
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = requireNonBlank(lastName, "Last name cannot be empty");
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date must not be null");
    }

    // =========================
    // Contact & government IDs
    // =========================
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = normalizeNullableText(address);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = normalizeNullableText(phone);
    }

    public String getSssNumber() {
        return sssNumber;
    }

    public void setSssNumber(String sssNumber) {
        this.sssNumber = normalizeNullableText(sssNumber);
    }

    public String getPhilHealthNumber() {
        return philHealthNumber;
    }

    public void setPhilHealthNumber(String philHealthNumber) {
        this.philHealthNumber = normalizeNullableText(philHealthNumber);
    }

    public String getTinNumber() {
        return tinNumber;
    }

    public void setTinNumber(String tinNumber) {
        this.tinNumber = normalizeNullableText(tinNumber);
    }

    public String getPagIbigNumber() {
        return pagIbigNumber;
    }

    public void setPagIbigNumber(String pagIbigNumber) {
        this.pagIbigNumber = normalizeNullableText(pagIbigNumber);
    }

    // =========================
    // Employment info
    // =========================
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = normalizeNullableText(status);
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = normalizeNullableText(position);
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = normalizeNullableText(supervisor);
    }

    // =========================
    // Salary fields
    // =========================
    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = validateMoney(basicSalary, "Basic salary must not be null");
    }

    public BigDecimal getRiceSubsidy() {
        return riceSubsidy;
    }

    public void setRiceSubsidy(BigDecimal riceSubsidy) {
        this.riceSubsidy = validateMoney(riceSubsidy, "Rice subsidy must not be null");
    }

    public BigDecimal getPhoneAllowance() {
        return phoneAllowance;
    }

    public void setPhoneAllowance(BigDecimal phoneAllowance) {
        this.phoneAllowance = validateMoney(phoneAllowance, "Phone allowance must not be null");
    }

    public BigDecimal getClothingAllowance() {
        return clothingAllowance;
    }

    public void setClothingAllowance(BigDecimal clothingAllowance) {
        this.clothingAllowance = validateMoney(clothingAllowance, "Clothing allowance must not be null");
    }

    public BigDecimal getGrossSemiMonthlyRate() {
        return grossSemiMonthlyRate;
    }

    public void setGrossSemiMonthlyRate(BigDecimal grossSemiMonthlyRate) {
        this.grossSemiMonthlyRate = validateMoney(grossSemiMonthlyRate, "Gross semi-monthly rate must not be null");
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = validateMoney(hourlyRate, "Hourly rate must not be null");
    }

    // =========================
    // Shared payroll helpers
    // =========================
    public BigDecimal computeBaseMonthlyPay() {
        return grossSemiMonthlyRate
                .multiply(BigDecimal.valueOf(2))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal computeStandardAllowances() {
        return riceSubsidy
                .add(phoneAllowance)
                .add(clothingAllowance)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Polymorphic payroll behavior.
     * Each employee subtype must define how monthly pay is computed.
     */
    public abstract BigDecimal computeMonthlyPay();

    @Override
    public String toString() {
        return String.format(
                "Employee[id=%s, fullName=%s, position=%s, status=%s]",
                id, getFullName(), position, status
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee)) {
            return false;
        }
        Employee employee = (Employee) o;
        return id.equals(employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private String normalizeNullableText(String value) {
        return value == null ? "" : value.trim();
    }

    private BigDecimal validateMoney(BigDecimal value, String nullMessage) {
        Objects.requireNonNull(value, nullMessage);

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money values cannot be negative");
        }

        return value.setScale(2, RoundingMode.HALF_UP);
    }
}