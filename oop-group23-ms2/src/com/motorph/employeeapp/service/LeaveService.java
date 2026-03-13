package com.motorph.employeeapp.service;

import com.motorph.employeeapp.model.LeaveRequest;
import com.motorph.employeeapp.repository.LeaveRequestDAO;

import java.util.List;

public class LeaveService {

    private LeaveRequestDAO leaveDAO = new LeaveRequestDAO();

    public void submitLeave(int leaveId, int employeeId,
                            String type, String startDate, String endDate) {

        LeaveRequest leave = new LeaveRequest(
                leaveId,
                employeeId,
                type,
                startDate,
                endDate,
                "PENDING"
        );

        leaveDAO.saveLeave(leave);
    }

    public void approveLeave(int leaveId) {
        leaveDAO.updateLeaveStatus(leaveId, "APPROVED");
    }

    public void rejectLeave(int leaveId) {
        leaveDAO.updateLeaveStatus(leaveId, "REJECTED");
    }

    public List<LeaveRequest> viewLeaveHistory() {
        return leaveDAO.getAllLeaves();
    }
}