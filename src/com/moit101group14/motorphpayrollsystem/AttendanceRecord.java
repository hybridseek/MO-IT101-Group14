/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.moit101group14.motorphpayrollsystem;

/**
 *
 * @author MO-IT101-Group14 | Codes | S1101 | Arellano, L., Castillo, D., Castillo, K.M., Ranay, D.
 * 
 **/

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AttendanceRecord represents an employee's attendance entry.
 * It contains the employee's identifier, date, login time, and logout time.
 */
public class AttendanceRecord {
    private static final Logger logger = Logger.getLogger(AttendanceRecord.class.getName());
    
    private final String employeeNumber;
    private final LocalDate date;
    private final LocalTime logIn;
    private final LocalTime logOut;

    /**
     * Constructs an AttendanceRecord from CSV data.
     *
     * @param data Array of strings representing attendance data.
     */
    public AttendanceRecord(String[] data) {
        this.employeeNumber = data[0].trim();
        this.date = LocalDate.parse(data[3].trim(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        this.logIn = parseTime(data[4].trim());
        this.logOut = parseTime(data[5].trim());
        
        if (logIn == null) {
            logger.log(Level.WARNING, "Missing login for employee {0} on {1}", new Object[]{employeeNumber, date});
        }
        if (logOut == null) {
            logger.log(Level.WARNING, "Missing logout for employee {0} on {1}", new Object[]{employeeNumber, date});
        }
    }

    /**
     * Parses a time string into a LocalTime object.
     *
     * @param timeStr Time string in "H:mm" format.
     * @return A LocalTime object or null if parsing fails.
     */
    private LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return null;
        }
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("H:mm"));
    }

    // Getter methods

    /** @return The employee's unique identifier. */
    public String getEmployeeNumber() {
        return employeeNumber;
    }

    /** @return The date of the attendance record. */
    public LocalDate getDate() {
        return date;
    }

    /** @return The login time (may be null). */
    public LocalTime getLogIn() {
        return logIn;
    }

    /** @return The logout time (may be null). */
    public LocalTime getLogOut() {
        return logOut;
    }

    /**
     * Checks if the record is valid (both login and logout exist).
     *
     * @return true if valid; false otherwise.
     */
    public boolean isValid() {
        return (logIn != null && logOut != null);
    }
}