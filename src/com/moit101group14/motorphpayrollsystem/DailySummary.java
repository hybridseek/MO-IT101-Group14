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

import java.time.Duration;
import java.time.LocalTime;

/**
 * DailySummary aggregates an employee's attendance data for a single day.
 * It computes normal hours, overtime, late minutes, and undertime based on login and logout times.
 */
public class DailySummary {
    private final LocalTime logInTime;
    private final LocalTime logOutTime;
    private int normalHours = 0;
    private int normalMinutes = 0;
    private int overtimeHours = 0;
    private int overtimeMinutes = 0;
    private int lateMinutes = 0;
    private int undertimeMinutes = 0;

    /**
     * Constructs a DailySummary from an AttendanceRecord.
     * If login or logout is missing, work hours are set to 0.
     *
     * @param record The attendance record for the day.
     */
    public DailySummary(AttendanceRecord record) {
        this.logInTime = record.getLogIn();
        this.logOutTime = record.getLogOut();
        
        if (logInTime == null || logOutTime == null) {
            this.normalHours = 0;
            this.overtimeHours = 0;
            return;
        }
        addRecord(record);
    }

    /**
     * Processes an attendance record to compute daily work details.
     * Calculates normal working hours (excluding lunch), overtime, late minutes, and undertime.
     *
     * @param record The attendance record for the day.
     */
    public void addRecord(AttendanceRecord record) {
        LocalTime officialStart = LocalTime.of(8, 0);
        LocalTime officialMorningEnd = LocalTime.of(12, 0);
        LocalTime lunchEnd = LocalTime.of(13, 0);
        LocalTime officialEnd = LocalTime.of(17, 0);
        LocalTime gracePeriod = LocalTime.of(8, 10);
        LocalTime lateReference = LocalTime.of(8, 1);
        
        LocalTime logIn = record.getLogIn();
        LocalTime logOut = record.getLogOut();
        
        if (logIn == null || logOut == null) {
            this.normalHours = 0;
            this.overtimeHours = 0;
            return;
        }

        LocalTime effectiveStart = (logIn.compareTo(gracePeriod) <= 0) ? officialStart : logIn;
        lateMinutes = (logIn.compareTo(gracePeriod) > 0) ? (int) Duration.between(lateReference, logIn).toMinutes() : 0;
        
        Duration morning = Duration.between(effectiveStart, officialMorningEnd).plusSeconds(1);
        Duration afternoon = Duration.between(lunchEnd, logOut.isAfter(officialEnd) ? officialEnd : logOut).plusSeconds(1);
        
        Duration totalNormal = morning.plus(afternoon);
        normalHours = totalNormal.toHoursPart();
        normalMinutes = totalNormal.toMinutesPart() % 60;
        
        if (logIn.compareTo(gracePeriod) <= 0 && logOut.isAfter(officialEnd)) {
            Duration overtime = Duration.between(officialEnd, logOut);
            overtimeHours = overtime.toHoursPart();
            overtimeMinutes = overtime.toMinutesPart() % 60;
        }
        
        undertimeMinutes = logOut.isBefore(officialEnd) ? (int) Duration.between(logOut, officialEnd).toMinutes() : 0;
    }

    // Getter methods

    /** @return The login time for the day. */
    public LocalTime getLogInTime() { return logInTime; }

    /** @return The logout time for the day. */
    public LocalTime getLogOutTime() { return logOutTime; }

    /** @return Total normal working hours. */
    public int getNormalHours() { return normalHours; }

    /** @return Remaining normal minutes (less than 60). */
    public int getNormalMinutes() { return normalMinutes; }

    /** @return Total overtime hours. */
    public int getOvertimeHours() { return overtimeHours; }

    /** @return Remaining overtime minutes (less than 60). */
    public int getOvertimeMinutes() { return overtimeMinutes; }

    /** @return Total late minutes accumulated. */
    public int getLateMinutes() { return lateMinutes; }

    /** @return Total undertime minutes accumulated. */
    public int getUndertimeMinutes() { return undertimeMinutes; }
}