package com.motorph.employeeapp.repository;

import com.motorph.employeeapp.model.Employee;
import com.motorph.employeeapp.model.ProbationaryEmployee;
import com.motorph.employeeapp.model.RegularEmployee;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV-based Employee repository using standard Java I/O only.
 */
public class CsvEmployeeRepository implements EmployeeRepository {
    private final Path csvPath;
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("M/d/yyyy");

    public CsvEmployeeRepository(String path) {
        this.csvPath = Paths.get(path);
    }

    @Override
    public List<Employee> loadAll() throws IOException {
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String line;

            // Skip header
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",", -1);

                String id = getValue(parts, 0);
                String last = getValue(parts, 1);
                String first = getValue(parts, 2);
                LocalDate birth = parseDateOrNow(getValue(parts, 3));

                String status = getValue(parts, 10);

                BigDecimal basic = parseDecimalOrZero(getValue(parts, 13));
                BigDecimal rice = parseDecimalOrZero(getValue(parts, 14));
                BigDecimal phoneA = parseDecimalOrZero(getValue(parts, 15));
                BigDecimal clothA = parseDecimalOrZero(getValue(parts, 16));
                BigDecimal semi = parseDecimalOrZero(getValue(parts, 17));
                BigDecimal hour = parseDecimalOrZero(getValue(parts, 18));

                Employee e;

                if (status.equalsIgnoreCase("Probationary")) {
                    e = new ProbationaryEmployee(
                            id,
                            first,
                            last,
                            birth,
                            basic,
                            rice,
                            phoneA,
                            clothA,
                            semi,
                            hour
                    );
                    status = "Probationary";
                } else {
                    e = new RegularEmployee(
                            id,
                            first,
                            last,
                            birth,
                            basic,
                            rice,
                            phoneA,
                            clothA,
                            semi,
                            hour
                    );
                    status = "Regular";
                }

                e.setAddress(getValue(parts, 4));
                e.setPhone(getValue(parts, 5));
                e.setSssNumber(getValue(parts, 6));
                e.setPhilHealthNumber(getValue(parts, 7));
                e.setTinNumber(getValue(parts, 8));
                e.setPagIbigNumber(getValue(parts, 9));
                e.setStatus(status);
                e.setPosition(getValue(parts, 11));
                e.setSupervisor(getValue(parts, 12));

                employees.add(e);
            }
        }

        return employees;
    }

    @Override
    public void saveAll(List<Employee> employees) throws IOException {
        Path temp = csvPath.resolveSibling(csvPath.getFileName() + ".tmp");

        try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            writer.write(String.join(",",
                    "Employee", "Last Name", "First Name", "Birthday",
                    "Address", "Phone Number", "SSS Number", "PhilHealth Number",
                    "TIN Number", "Pag-IBIG Number", "Status", "Position",
                    "Supervisor", "Basic Salary", "Rice Subsidy",
                    "Phone Allowance", "Clothing Allowance",
                    "Semi-monthly Rate", "Hourly Rate"));
            writer.newLine();

            for (Employee e : employees) {
                String[] row = {
                        escapeCsv(e.getId()),
                        escapeCsv(e.getLastName()),
                        escapeCsv(e.getFirstName()),
                        escapeCsv(e.getBirthDate().format(DATE_FMT)),
                        escapeCsv(e.getAddress()),
                        escapeCsv(e.getPhone()),
                        escapeCsv(e.getSssNumber()),
                        escapeCsv(e.getPhilHealthNumber()),
                        escapeCsv(e.getTinNumber()),
                        escapeCsv(e.getPagIbigNumber()),
                        escapeCsv(e.getStatus()),
                        escapeCsv(e.getPosition()),
                        escapeCsv(e.getSupervisor()),
                        escapeCsv(e.getBasicSalary().toPlainString()),
                        escapeCsv(e.getRiceSubsidy().toPlainString()),
                        escapeCsv(e.getPhoneAllowance().toPlainString()),
                        escapeCsv(e.getClothingAllowance().toPlainString()),
                        escapeCsv(e.getGrossSemiMonthlyRate().toPlainString()),
                        escapeCsv(e.getHourlyRate().toPlainString())
                };

                writer.write(String.join(",", row));
                writer.newLine();
            }
        }

        Files.deleteIfExists(csvPath);
        Files.move(temp, csvPath);
    }

    private String getValue(String[] parts, int index) {
        if (index >= parts.length || parts[index] == null) {
            return "";
        }
        return unescapeCsv(parts[index].trim());
    }

    private LocalDate parseDateOrNow(String txt) {
        try {
            return LocalDate.parse(txt, DATE_FMT);
        } catch (Exception ex) {
            return LocalDate.now();
        }
    }

    private BigDecimal parseDecimalOrZero(String txt) {
        try {
            return new BigDecimal(txt);
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    private String unescapeCsv(String value) {
        if (value == null) {
            return "";
        }

        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            value = value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }

        return value;
    }
}