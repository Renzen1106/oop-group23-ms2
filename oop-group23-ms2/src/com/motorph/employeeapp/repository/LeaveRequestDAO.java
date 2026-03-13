package com.motorph.employeeapp.repository;

import com.motorph.employeeapp.model.LeaveRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestDAO {

    private static final String FILE_PATH = "data/leave_requests.csv";

    public void saveLeave(LeaveRequest leave) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {

            writer.write(
                    leave.getLeaveId() + "," +
                    leave.getEmployeeId() + "," +
                    leave.getLeaveType() + "," +
                    leave.getStartDate() + "," +
                    leave.getEndDate() + "," +
                    leave.getStatus()
            );

            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<LeaveRequest> getAllLeaves() {

        List<LeaveRequest> leaves = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                LeaveRequest leave = new LeaveRequest(
                        Integer.parseInt(data[0]),
                        Integer.parseInt(data[1]),
                        data[2],
                        data[3],
                        data[4],
                        data[5]
                );

                leaves.add(leave);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return leaves;
    }

    public void updateLeaveStatus(int leaveId, String status) {

        List<LeaveRequest> leaves = getAllLeaves();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {

            for (LeaveRequest leave : leaves) {

                if (leave.getLeaveId() == leaveId) {
                    leave.setStatus(status);
                }

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
            e.printStackTrace();
        }
    }
}