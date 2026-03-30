package com.motorph.employeeapp.util;

public class ValidationUtil {

    public static boolean isNumeric(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        try {
            Double.parseDouble(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidSSS(String sss) {
        if (sss == null) {
            return false;
        }
        return sss.trim().matches("\\d{2}-\\d{7}-\\d");
    }

    public static boolean isValidPhilHealth(String philhealth) {
        if (philhealth == null) {
            return false;
        }
        return philhealth.trim().matches("\\d{12}");
    }

    public static boolean isValidTIN(String tin) {
        if (tin == null) {
            return false;
        }
        return tin.trim().matches("\\d{3}-\\d{3}-\\d{3}-\\d{3}");
    }

    public static boolean isValidPagibig(String pagibig) {
        if (pagibig == null) {
            return false;
        }
        return pagibig.trim().matches("\\d{12}");
    }

    public static boolean isValidName(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }
        return name.trim().matches("[a-zA-Z\\-\\s]+");
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return false;
        }
        return phone.trim().matches("^(09\\d{9}|\\+639\\d{9})$");
    }

    public static boolean isValidAddress(String address) {
        if (address == null || address.isBlank()) {
            return false;
        }
        return address.trim().matches("[a-zA-Z0-9\\s,\\.]+");
    }

    public static boolean isValidPosition(String position) {
        if (position == null || position.isBlank()) {
            return false;
        }
        return position.trim().matches("[a-zA-Z\\s]+");
    }

    public static boolean isValidSupervisor(String supervisor) {
        if (supervisor == null || supervisor.isBlank()) {
            return false;
        }
        return supervisor.trim().matches("[a-zA-Z\\s]+");
    }

    public static boolean isValidStatus(String status) {
        if (status == null || status.isBlank()) {
            return false;
        }

        String s = status.trim();
        return s.equalsIgnoreCase("Regular") || s.equalsIgnoreCase("Probationary");
    }
}