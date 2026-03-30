package com.motorph.employeeapp.service;

import com.motorph.employeeapp.model.AttendanceRecord;
import com.motorph.employeeapp.repository.AttendanceDAO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceService {

    private final AttendanceDAO attendanceDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
    }

    public void timeIn(String employeeId, String date, String timeIn) {
        validateEmployeeId(employeeId);
        validateDate(date);
        validateTime(timeIn, "Time In");

        List<AttendanceRecord> records = attendanceDAO.getAllAttendance();

        for (AttendanceRecord record : records) {
            boolean sameEmployee = record.getEmployeeId().equals(employeeId);
            boolean sameDate = record.getDate().equals(date);
            boolean noTimeOutYet = record.getTimeOut() == null || record.getTimeOut().isBlank();

            if (sameEmployee && sameDate && noTimeOutYet) {
                throw new IllegalArgumentException("Employee already has an active time-in record for this date.");
            }
        }

        AttendanceRecord newRecord = new AttendanceRecord(employeeId, date, timeIn, "");
        attendanceDAO.saveAttendance(newRecord);
    }

    public void timeOut(String employeeId, String date, String timeOut) {
        validateEmployeeId(employeeId);
        validateDate(date);
        validateTime(timeOut, "Time Out");

        List<AttendanceRecord> records = attendanceDAO.getAllAttendance();
        boolean updated = false;

        for (AttendanceRecord record : records) {
            boolean sameEmployee = record.getEmployeeId().equals(employeeId);
            boolean sameDate = record.getDate().equals(date);
            boolean noTimeOutYet = record.getTimeOut() == null || record.getTimeOut().isBlank();

            if (sameEmployee && sameDate && noTimeOutYet) {
                LocalTime in = LocalTime.parse(record.getTimeIn().trim());
                LocalTime out = LocalTime.parse(timeOut.trim());

                if (out.isBefore(in) || out.equals(in)) {
                    throw new IllegalArgumentException("Time Out must be later than Time In.");
                }

                record.setTimeOut(timeOut);
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new IllegalArgumentException("No active time-in record found for this employee on the given date.");
        }

        attendanceDAO.overwriteAttendance(records);
    }

    public List<AttendanceRecord> getAttendanceHistory() {
        return attendanceDAO.getAllAttendance();
    }

    public List<AttendanceRecord> getAttendanceByEmployee(String employeeId) {
        validateEmployeeId(employeeId);

        List<AttendanceRecord> allRecords = attendanceDAO.getAllAttendance();
        List<AttendanceRecord> filteredRecords = new ArrayList<>();

        for (AttendanceRecord record : allRecords) {
            if (record.getEmployeeId().equals(employeeId)) {
                filteredRecords.add(record);
            }
        }

        return filteredRecords;
    }

    private void validateEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID is required.");
        }

        if (!employeeId.trim().matches("\\d+")) {
            throw new IllegalArgumentException("Employee ID must contain digits only.");
        }
    }

    private void validateDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date is required.");
        }

        try {
            LocalDate.parse(date.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must follow YYYY-MM-DD format.");
        }
    }

    private void validateTime(String time, String fieldName) {
        if (time == null || time.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        if (!time.trim().matches("^\\d{2}:\\d{2}$")) {
            throw new IllegalArgumentException(fieldName + " must follow HH:MM format.");
        }

        try {
            LocalTime.parse(time.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid time.");
        }
    }
}