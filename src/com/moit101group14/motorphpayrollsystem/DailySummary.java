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
        LocalTime lateReference       = LocalTime.of(8, 1);    // late minutes counted from 8:01
        
        LocalTime logIn  = record.getLogIn();
        LocalTime logOut = record.getLogOut();
        
        // Effective start time for normal hours
        LocalTime effectiveStart;
        if (logIn.compareTo(gracePeriod) <= 0) {
            effectiveStart = officialStart;
            lateMinutes = 0;
        } else {
            effectiveStart = logIn;
            lateMinutes = (int) Duration.between(lateReference, logIn).toMinutes();
        }
        
        // Compute normal working hours (excluding lunch)
        Duration morningDuration = Duration.ZERO;
        if (effectiveStart.isBefore(officialMorningEnd)) {
            morningDuration = Duration.between(effectiveStart, officialMorningEnd);
        }
        
        Duration afternoonDuration = Duration.ZERO;
        if (logOut.isAfter(lunchEnd)) {
            LocalTime afternoonEnd = logOut.isAfter(officialEnd) ? officialEnd : logOut;
            afternoonDuration = Duration.between(lunchEnd, afternoonEnd);
        }
        
        Duration computedNormal = morningDuration.plus(afternoonDuration);
        normalHours = (int) computedNormal.toHours();
        normalMinutes = computedNormal.toMinutesPart();
        
        // Undertime: if logOut is before officialEnd
        if (logOut.isBefore(officialEnd)) {
            undertimeMinutes = (int) Duration.between(logOut, officialEnd).toMinutes();
        } else {
            undertimeMinutes = 0;
        }
        
        // Overtime: if on time and logs out after officialEnd
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
