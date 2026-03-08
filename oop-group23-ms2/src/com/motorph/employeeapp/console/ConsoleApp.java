package com.motorph.employeeapp.console;

import com.motorph.employeeapp.model.Employee;
import com.motorph.employeeapp.model.ProbationaryEmployee;
import com.motorph.employeeapp.model.RegularEmployee;
import com.motorph.employeeapp.repository.CsvEmployeeRepository;
import com.motorph.employeeapp.repository.EmployeeRepository;
import com.motorph.employeeapp.service.PayrollService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleApp {
    public static void main(String[] args) {
        EmployeeRepository repo = new CsvEmployeeRepository("data/MotorPH Employee Record.csv");
        PayrollService payrollService = new PayrollService();

        List<Employee> employees;
        try {
            employees = repo.loadAll();
        } catch (IOException ex) {
            System.err.println("Failed to load employees: " + ex.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("=== Employee Management Console ===");
            System.out.println("1) List all employees");
            System.out.println("2) View a specific employee");
            System.out.println("3) Create new employee");
            System.out.println("4) Update an employee");
            System.out.println("5) Delete an employee");
            System.out.println("6) Compute employee salary");
            System.out.println("7) Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1":
                    employees.forEach(System.out::println);
                    System.out.println();
                    break;

                case "2":
                    System.out.print("Enter Employee #: ");
                    String viewId = scanner.nextLine().trim();
                    System.out.println();

                    Optional<Employee> opt = employees.stream()
                            .filter(emp -> emp.getId().equals(viewId))
                            .findFirst();

                    opt.ifPresentOrElse(
                            System.out::println,
                            () -> System.out.println("Employee not found.")
                    );
                    System.out.println();
                    break;

                case "3":
                    System.out.print("Enter new Employee #: ");
                    String newId = scanner.nextLine().trim();

                    System.out.print("First name: ");
                    String newFirst = scanner.nextLine().trim();

                    System.out.print("Last name: ");
                    String newLast = scanner.nextLine().trim();

                    System.out.print("Birthday (yyyy-MM-dd): ");
                    LocalDate newBday = LocalDate.parse(scanner.nextLine().trim());

                    System.out.print("Status (Regular/Probationary): ");
                    String newStatus = scanner.nextLine().trim();

                    System.out.print("Basic Salary: ");
                    BigDecimal newBasic = new BigDecimal(scanner.nextLine().trim());

                    System.out.print("Rice Subsidy: ");
                    BigDecimal newRice = new BigDecimal(scanner.nextLine().trim());

                    System.out.print("Phone Allowance: ");
                    BigDecimal newPhone = new BigDecimal(scanner.nextLine().trim());

                    System.out.print("Clothing Allowance: ");
                    BigDecimal newClothing = new BigDecimal(scanner.nextLine().trim());

                    System.out.print("Gross Semi-monthly Rate: ");
                    BigDecimal newGrossSemi = new BigDecimal(scanner.nextLine().trim());

                    System.out.print("Hourly Rate: ");
                    BigDecimal newHourly = new BigDecimal(scanner.nextLine().trim());

                    Employee newEmp;

                    if (newStatus.equalsIgnoreCase("Probationary")) {
                        newEmp = new ProbationaryEmployee(
                                newId,
                                newFirst,
                                newLast,
                                newBday,
                                newBasic,
                                newRice,
                                newPhone,
                                newClothing,
                                newGrossSemi,
                                newHourly
                        );
                    } else {
                        newEmp = new RegularEmployee(
                                newId,
                                newFirst,
                                newLast,
                                newBday,
                                newBasic,
                                newRice,
                                newPhone,
                                newClothing,
                                newGrossSemi,
                                newHourly
                        );
                        newStatus = "Regular";
                    }

                    newEmp.setStatus(newStatus);
                    employees.add(newEmp);

                    try {
                        repo.saveAll(employees);
                        System.out.println("Employee created.\n");
                    } catch (IOException saveEx) {
                        System.err.println("Failed to save employees: " + saveEx.getMessage());
                    }
                    break;

                case "4":
                    System.out.print("Enter Employee # to update: ");
                    String updId = scanner.nextLine().trim();
                    System.out.println();

                    Optional<Employee> toUpdate = employees.stream()
                            .filter(emp -> emp.getId().equals(updId))
                            .findFirst();

                    toUpdate.ifPresentOrElse(emp -> {
                        System.out.print("New first name (blank to keep): ");
                        String f = scanner.nextLine().trim();
                        if (!f.isBlank()) {
                            emp.setFirstName(f);
                        }

                        System.out.print("New last name (blank to keep): ");
                        String l = scanner.nextLine().trim();
                        if (!l.isBlank()) {
                            emp.setLastName(l);
                        }

                        try {
                            repo.saveAll(employees);
                            System.out.println("Employee updated.\n");
                        } catch (IOException updEx) {
                            System.err.println("Failed to save employees: " + updEx.getMessage());
                        }
                    }, () -> System.out.println("Employee not found.\n"));
                    break;

                case "5":
                    System.out.print("Enter Employee # to delete: ");
                    String delId = scanner.nextLine().trim();
                    System.out.println();

                    boolean removed = employees.removeIf(emp -> emp.getId().equals(delId));

                    if (removed) {
                        try {
                            repo.saveAll(employees);
                            System.out.println("Employee deleted.\n");
                        } catch (IOException delEx) {
                            System.err.println("Failed to save employees: " + delEx.getMessage());
                        }
                    } else {
                        System.out.println("Employee not found.\n");
                    }
                    break;

                case "6":
                    System.out.print("Enter Employee #: ");
                    String payId = scanner.nextLine().trim();
                    System.out.println();

                    Optional<Employee> payEmpOpt = employees.stream()
                            .filter(emp -> emp.getId().equals(payId))
                            .findFirst();

                    if (payEmpOpt.isEmpty()) {
                        System.out.println("Employee not found.\n");
                        break;
                    }

                    Employee payEmp = payEmpOpt.get();

                    System.out.print("Enter month to compute (YYYY-MM): ");
                    YearMonth ym = YearMonth.parse(scanner.nextLine().trim());

                    BigDecimal grossPay = payrollService.computeMonthlyPay(payEmp, ym);
                    BigDecimal totalDeductions = payrollService.computeTotalDeductions(payEmp, ym);
                    BigDecimal netPay = payrollService.computeNetMonthlyPay(payEmp, ym);

                    System.out.println();
                    System.out.println("=== Pay Details for " + ym + " ===");
                    System.out.println("Name: " + payEmp.getFullName());
                    System.out.println("Status: " + payEmp.getStatus());
                    System.out.println("Gross Monthly Pay: " + grossPay);
                    System.out.println("Total Deductions: " + totalDeductions);
                    System.out.println("Net Monthly Pay: " + netPay);
                    System.out.println();
                    break;

                case "7":
                    System.out.println("Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please enter 1-7.\n");
            }
        }

        scanner.close();
    }
}