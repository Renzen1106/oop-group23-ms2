package com.motorph.employeeapp.service;

import com.motorph.employeeapp.model.AttendanceRecord;
import com.motorph.employeeapp.repository.AttendanceDAO;

import java.util.ArrayList;
import java.util.List;

public class AttendanceService {

    private final AttendanceDAO attendanceDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
    }

    public void timeIn(String employeeId, String date, String timeIn) {
        validateRequired(employeeId, "Employee ID");
        validateRequired(date, "Date");
        validateRequired(timeIn, "Time In");

        List<AttendanceRecord> records = attendanceDAO.getAllAttendance();

        for (AttendanceRecord record : records) {
            if (record.getEmployeeId().equals(employeeId)
                    && record.getDate().equals(date)
                    && record.getTimeOut() != null
                    && !record.getTimeOut().isBlank()) {
                throw new IllegalArgumentException(
                        "Attendance already completed for this employee on this date."
                );
            }

            if (record.getEmployeeId().equals(employeeId)
                    && record.getDate().equals(date)
                    && (record.getTimeOut() == null || record.getTimeOut().isBlank())) {
                throw new IllegalArgumentException(
                        "Employee already timed in for this date and is still active."
                );
            }
        }

        AttendanceRecord newRecord = new AttendanceRecord(employeeId, date, timeIn, "");
        attendanceDAO.saveAttendance(newRecord);
    }

    public void timeOut(String employeeId, String date, String timeOut) {
        validateRequired(employeeId, "Employee ID");
        validateRequired(date, "Date");
        validateRequired(timeOut, "Time Out");

        List<AttendanceRecord> records = attendanceDAO.getAllAttendance();
        boolean updated = false;

        for (AttendanceRecord record : records) {
            boolean sameEmployee = record.getEmployeeId().equals(employeeId);
            boolean sameDate = record.getDate().equals(date);
            boolean noTimeOutYet = record.getTimeOut() == null || record.getTimeOut().isBlank();

            if (sameEmployee && sameDate && noTimeOutYet) {
                record.setTimeOut(timeOut);
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new IllegalArgumentException(
                    "No active time-in record found for this employee on the given date."
            );
        }

        attendanceDAO.overwriteAttendance(records);
    }

    public List<AttendanceRecord> getAttendanceHistory() {
        return attendanceDAO.getAllAttendance();
    }

    public List<AttendanceRecord> getAttendanceByEmployee(String employeeId) {
        validateRequired(employeeId, "Employee ID");

        List<AttendanceRecord> allRecords = attendanceDAO.getAllAttendance();
        List<AttendanceRecord> filteredRecords = new ArrayList<>();

        for (AttendanceRecord record : allRecords) {
            if (record.getEmployeeId().equals(employeeId)) {
                filteredRecords.add(record);
            }
        }

        return filteredRecords;
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }
}