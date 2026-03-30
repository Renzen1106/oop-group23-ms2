package com.motorph.employeeapp.service;

import com.motorph.employeeapp.model.LeaveRequest;
import com.motorph.employeeapp.repository.LeaveRequestDAO;

import java.util.ArrayList;
import java.util.List;

public class LeaveService {

    private final LeaveRequestDAO leaveDAO;

    public LeaveService() {
        this.leaveDAO = new LeaveRequestDAO();
    }

    // Submit new leave request
    public void submitLeave(int leaveId, int employeeId, String leaveType,
                           String startDate, String endDate) {

        validateRequired(leaveType, "Leave Type");
        validateRequired(startDate, "Start Date");
        validateRequired(endDate, "End Date");

        LeaveRequest leave = new LeaveRequest(
                leaveId,
                employeeId,
                leaveType,
                startDate,
                endDate,
                "Pending"
        );

        leaveDAO.saveLeave(leave);
    }

    // Approve leave
    public void approveLeave(int leaveId) {
        leaveDAO.updateLeaveStatus(leaveId, "Approved");
    }

    // Reject leave
    public void rejectLeave(int leaveId) {
        leaveDAO.updateLeaveStatus(leaveId, "Rejected");
    }

    // Get all leaves
    public List<LeaveRequest> getAllLeaves() {
        return leaveDAO.getAllLeaves();
    }

    // Get leaves by employee
    public List<LeaveRequest> getLeavesByEmployee(int employeeId) {

        List<LeaveRequest> allLeaves = leaveDAO.getAllLeaves();
        List<LeaveRequest> filtered = new ArrayList<>();

        for (LeaveRequest leave : allLeaves) {
            if (leave.getEmployeeId() == employeeId) {
                filtered.add(leave);
            }
        }

        return filtered;
    }

    // Simple validation
    private void validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }
}