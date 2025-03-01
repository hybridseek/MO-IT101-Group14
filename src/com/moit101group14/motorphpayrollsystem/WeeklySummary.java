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
import java.util.Map;
import java.util.TreeMap;

public class WeeklySummary {
    private final EmployeeDetails employee;
    private final Map<LocalDate, DailySummary> dailySummaries = new TreeMap<>();
    
    public WeeklySummary(EmployeeDetails employee) {
        this.employee = employee;
    }
    
    public void addRecord(AttendanceRecord record) {
        LocalDate date = record.getDate();
        dailySummaries.computeIfAbsent(date, k -> new DailySummary(record)).addRecord(record);
    }
    
    public EmployeeDetails getEmployee() {
        return employee;
    }
    
    public Map<LocalDate, DailySummary> getDailySummaries() {
        return dailySummaries;
    }
    
    public int getTotalNormalHours() {
        return dailySummaries.values().stream().mapToInt(DailySummary::getNormalHours).sum();
    }
    
    public int getTotalNormalMinutes() {
        return dailySummaries.values().stream().mapToInt(DailySummary::getNormalMinutes).sum();
    }
    
    public int getTotalOvertimeHours() {
        return dailySummaries.values().stream().mapToInt(DailySummary::getOvertimeHours).sum();
    }
    
    public int getTotalOvertimeMinutes() {
        return dailySummaries.values().stream().mapToInt(DailySummary::getOvertimeMinutes).sum();
    }
    
    public int getTotalLateMinutes() {
        return dailySummaries.values().stream().mapToInt(DailySummary::getLateMinutes).sum();
    }
    
    public int getTotalUndertimeMinutes() {
        return dailySummaries.values().stream().mapToInt(DailySummary::getUndertimeMinutes).sum();
    }
}
