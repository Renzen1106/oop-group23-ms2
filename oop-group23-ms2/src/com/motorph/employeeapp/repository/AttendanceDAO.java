package com.motorph.employeeapp.repository;

import com.motorph.employeeapp.model.AttendanceRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    private static final String FILE_PATH = "data/attendance.csv";

    // Save a new attendance record (Time In)
    public void saveAttendance(AttendanceRecord record) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {

            writer.write(record.toCSV());
            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read all attendance records
    public List<AttendanceRecord> getAllAttendance() {

        List<AttendanceRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {

            String line;

            // Skip header row
            reader.readLine();

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                AttendanceRecord record = new AttendanceRecord(
                        data[0],
                        data[1],
                        data[2],
                        data[3]);

                records.add(record);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }

    // Overwrite attendance CSV (used when updating timeout)
    public void overwriteAttendance(List<AttendanceRecord> records) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {

            // Write header again
            writer.write("employeeId,date,timeIn,timeOut");
            writer.newLine();

            for (AttendanceRecord record : records) {
                writer.write(record.toCSV());
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}