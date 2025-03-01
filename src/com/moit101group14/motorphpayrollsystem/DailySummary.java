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

public class DailySummary {
    private LocalTime logInTime;
    private LocalTime logOutTime;
    private int normalHours = 0;
    private int normalMinutes = 0;
    private int overtimeHours = 0;
    private int overtimeMinutes = 0;
    private int lateMinutes = 0;
    private int undertimeMinutes = 0;
    
    public DailySummary(AttendanceRecord record) {
        this.logInTime = record.getLogIn();
        this.logOutTime = record.getLogOut();
    }
    
    public void addRecord(AttendanceRecord record) {
        // Define official boundaries
        LocalTime officialStart       = LocalTime.of(8, 0);
        LocalTime officialMorningEnd  = LocalTime.of(12, 0);
        LocalTime lunchEnd            = LocalTime.of(13, 0);
        LocalTime officialEnd         = LocalTime.of(17, 0);
        LocalTime gracePeriod         = LocalTime.of(8, 10);   // on-time if <= 8:10
        LocalTime lateReference       = LocalTime.of(8, 1);    // late minutes are counted from 8:01
        
        LocalTime logIn  = record.getLogIn();
        LocalTime logOut = record.getLogOut();
        
        // Determine effective start time for normal hours
        // If employee logs in on or before 8:10, we count as if they started at 8:00.
        // Otherwise, effective start is their actual logIn.
        LocalTime effectiveStart;
        if (logIn.compareTo(gracePeriod) <= 0) {
            effectiveStart = officialStart;
            lateMinutes = 0;
        } else {
            effectiveStart = logIn;
            // Late minutes are computed from 8:01 to actual logIn time.
            lateMinutes = (int) Duration.between(lateReference, logIn).toMinutes();
        }
        
        // Compute normal working hours (excluding lunch break)
        // Morning session: from effectiveStart (if before 12:00) until 12:00.
        Duration morningDuration = Duration.ZERO;
        if (effectiveStart.isBefore(officialMorningEnd)) {
            // If the effective start is after 12:00, morning duration remains zero.
            LocalTime morningEnd = officialMorningEnd;
            morningDuration = Duration.between(effectiveStart, morningEnd);
        }
        
        // Afternoon session: from 13:00 until the earlier of logOut and 17:00.
        Duration afternoonDuration = Duration.ZERO;
        if (logOut.isAfter(lunchEnd)) {
            LocalTime afternoonEnd = logOut.isAfter(officialEnd) ? officialEnd : logOut;
            afternoonDuration = Duration.between(lunchEnd, afternoonEnd);
        }
        
        Duration computedNormal = morningDuration.plus(afternoonDuration);
        normalHours = (int) computedNormal.toHours();
        normalMinutes = computedNormal.toMinutesPart();
        
        // Calculate undertime: if logOut is before officialEnd, the difference is undertime.
        if (logOut.isBefore(officialEnd)) {
            undertimeMinutes = (int) Duration.between(logOut, officialEnd).toMinutes();
        } else {
            undertimeMinutes = 0;
        }
        
        // Calculate overtime: Only if employee is on time (logIn ≤ 8:10) and logs out after 17:00.
        if (logIn.compareTo(gracePeriod) <= 0 && logOut.isAfter(officialEnd)) {
            Duration overtimeDuration = Duration.between(officialEnd, logOut);
            overtimeHours = (int) overtimeDuration.toHours();
            overtimeMinutes = overtimeDuration.toMinutesPart();
        } else {
            overtimeHours = 0;
            overtimeMinutes = 0;
        }
        overtimeHours += overtimeMinutes / 60;
        overtimeMinutes %= 60;
    }
    
    public LocalTime getLogInTime() {
        return logInTime;
    }
    
    public LocalTime getLogOutTime() {
        return logOutTime;
    }
    
    public int getNormalHours() {
        return normalHours;
    }
    
    public int getNormalMinutes() {
        return normalMinutes;
    }
    
    public int getOvertimeHours() {
        return overtimeHours;
    }
    
    public int getOvertimeMinutes() {
        return overtimeMinutes;
    }
    
    public int getLateMinutes() {
        return lateMinutes;
    }
    
    public int getUndertimeMinutes() {
        return undertimeMinutes;
    }
}
