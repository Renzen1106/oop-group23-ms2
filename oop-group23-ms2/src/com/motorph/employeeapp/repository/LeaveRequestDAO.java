package com.motorph.employeeapp.repository;

import com.motorph.employeeapp.model.LeaveRequest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestDAO {

    private static final String HEADER = "leaveId,employeeId,leaveType,startDate,endDate,status";

    private Path resolveFilePath() {
        String[] possiblePaths = {
                "data/leave_requests.csv",
                "oop-group23-ms2/data/leave_requests.csv"
        };

        for (String p : possiblePaths) {
            Path path = Paths.get(p);
            if (Files.exists(path)) {
                return path;
            }
        }

        return Paths.get("data/leave_requests.csv");
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

    public void saveLeave(LeaveRequest leave) {
        try {
            ensureFileExists();
            Path path = resolveFilePath();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), true))) {
                writer.write(
                        leave.getLeaveId() + "," +
                        leave.getEmployeeId() + "," +
                        leave.getLeaveType() + "," +
                        leave.getStartDate() + "," +
                        leave.getEndDate() + "," +
                        leave.getStatus()
                );
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to save leave request: " + e.getMessage(), e);
        }
    }

    public List<LeaveRequest> getAllLeaves() {
        List<LeaveRequest> leaves = new ArrayList<>();

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

                    if (line.equalsIgnoreCase(HEADER) || line.startsWith("leaveId,")) {
                        continue;
                    }

                    String[] data = line.split(",", -1);
                    if (data.length < 6) {
                        continue;
                    }

                    LeaveRequest leave = new LeaveRequest(
                            Integer.parseInt(data[0].trim()),
                            Integer.parseInt(data[1].trim()),
                            data[2].trim(),
                            data[3].trim(),
                            data[4].trim(),
                            data[5].trim()
                    );

                    leaves.add(leave);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load leave requests: " + e.getMessage(), e);
        }

        return leaves;
    }

    public void updateLeaveStatus(int leaveId, String status) {
        List<LeaveRequest> leaves = getAllLeaves();
        boolean found = false;

        for (LeaveRequest leave : leaves) {
            if (leave.getLeaveId() == leaveId) {
                leave.setStatus(status);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Leave request not found.");
        }

        try {
            ensureFileExists();
            Path path = resolveFilePath();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
                writer.write(HEADER);
                writer.newLine();

                for (LeaveRequest leave : leaves) {
                    writer.write(
                            leave.getLeaveId() + "," +
                            leave.getEmployeeId() + "," +
                            leave.getLeaveType() + "," +
                            leave.getStartDate() + "," +
                            leave.getEndDate() + "," +
                            leave.getStatus()
                    );
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to update leave status: " + e.getMessage(), e);
        }
    }
}