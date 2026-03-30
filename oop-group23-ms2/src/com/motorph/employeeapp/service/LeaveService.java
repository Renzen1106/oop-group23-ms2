package com.motorph.employeeapp.service;

import com.motorph.employeeapp.model.LeaveRequest;
import com.motorph.employeeapp.repository.LeaveRequestDAO;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class LeaveService {

    private final LeaveRequestDAO leaveDAO;

    public LeaveService() {
        this.leaveDAO = new LeaveRequestDAO();
    }

    public void submitLeave(int leaveId, int employeeId, String leaveType,
                            String startDate, String endDate) {

        if (leaveId <= 0) {
            throw new IllegalArgumentException("Leave ID must be greater than zero.");
        }

        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be greater than zero.");
        }

        validateRequired(leaveType, "Leave Type");
        validateDate(startDate, "Start Date");
        validateDate(endDate, "End Date");

        LocalDate start = LocalDate.parse(startDate.trim());
        LocalDate end = LocalDate.parse(endDate.trim());

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End Date cannot be earlier than Start Date.");
        }

        for (LeaveRequest existing : leaveDAO.getAllLeaves()) {
            if (existing.getLeaveId() == leaveId) {
                throw new IllegalArgumentException("Leave ID already exists.");
            }
        }

        LeaveRequest leave = new LeaveRequest(
                leaveId,
                employeeId,
                leaveType.trim(),
                startDate.trim(),
                endDate.trim(),
                "Pending"
        );

        leaveDAO.saveLeave(leave);
    }

    public void approveLeave(int leaveId) {
        if (leaveId <= 0) {
            throw new IllegalArgumentException("Invalid Leave ID.");
        }
        leaveDAO.updateLeaveStatus(leaveId, "Approved");
    }

    public void rejectLeave(int leaveId) {
        if (leaveId <= 0) {
            throw new IllegalArgumentException("Invalid Leave ID.");
        }
        leaveDAO.updateLeaveStatus(leaveId, "Rejected");
    }

    public List<LeaveRequest> getAllLeaves() {
        return leaveDAO.getAllLeaves();
    }

    public List<LeaveRequest> getLeavesByEmployee(int employeeId) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Invalid Employee ID.");
        }

        List<LeaveRequest> allLeaves = leaveDAO.getAllLeaves();
        List<LeaveRequest> filtered = new ArrayList<>();

        for (LeaveRequest leave : allLeaves) {
            if (leave.getEmployeeId() == employeeId) {
                filtered.add(leave);
            }
        }

        return filtered;
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }

    private void validateDate(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        try {
            LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(fieldName + " must follow YYYY-MM-DD format.");
        }
    }
}