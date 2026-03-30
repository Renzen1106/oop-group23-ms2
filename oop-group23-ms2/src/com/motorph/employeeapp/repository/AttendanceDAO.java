package com.motorph.employeeapp.repository;

import com.motorph.employeeapp.model.AttendanceRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    private static final String HEADER = "employeeId,date,timeIn,timeOut";

    private Path resolveFilePath() {
        String[] possiblePaths = {
                "data/attendance.csv",
                "oop-group23-ms2/data/attendance.csv"
        };

        for (String p : possiblePaths) {
            Path path = Paths.get(p);
            if (Files.exists(path)) {
                return path;
            }
        }

        return Paths.get("data/attendance.csv");
    }

    private void ensureFileExists() throws IOException {
        Path path = resolveFilePath();

        if (path.getParent() != null && Files.notExists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        if (Files.notExists(path) || Files.size(path) == 0) {
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write(HEADER);
                writer.newLine();
            }
        }
    }

    public void saveAttendance(AttendanceRecord record) {
        try {
            ensureFileExists();
            Path path = resolveFilePath();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), true))) {
                writer.write(record.toCSV());
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to save attendance record: " + e.getMessage(), e);
        }
    }

    public List<AttendanceRecord> getAllAttendance() {
        List<AttendanceRecord> records = new ArrayList<>();

        try {
            ensureFileExists();
            Path path = resolveFilePath();

            try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    if (line.isEmpty()) {
                        continue;
                    }

                    if (line.equalsIgnoreCase(HEADER) || line.startsWith("employeeId,")) {
                        continue;
                    }

                    String[] data = line.split(",", -1);
                    if (data.length < 4) {
                        continue;
                    }

                    AttendanceRecord record = new AttendanceRecord(
                            data[0].trim(),
                            data[1].trim(),
                            data[2].trim(),
                            data[3].trim()
                    );

                    records.add(record);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load attendance records: " + e.getMessage(), e);
        }

        return records;
    }

    public void overwriteAttendance(List<AttendanceRecord> records) {
        try {
            ensureFileExists();
            Path path = resolveFilePath();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
                writer.write(HEADER);
                writer.newLine();

                for (AttendanceRecord record : records) {
                    writer.write(record.toCSV());
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to update attendance records: " + e.getMessage(), e);
        }
    }
}