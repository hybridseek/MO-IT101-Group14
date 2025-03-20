/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.moit101group14.motorphpayrollsystem;

/**
 *
 * @author MO-IT101-Group14 | Codes | S1101 | Arellano, L., Castillo, D., Castillo, K.M., Ranay, D.
 * 
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.TreeMap;

/**
 * WeeklyRecordsProcessor processes attendance records and groups them by employee and week.
 * It determines the week range (Monday to Friday) for each record and aggregates data into WeeklyTotals.
 */
public class WeeklyRecordsProcessor {

    /**
     * Processes attendance records into weekly totals per employee.
     *
     * @param attendanceRecords Iterable collection of attendance records.
     * @return A nested map where the outer key is the employee number and the inner key is the week range
     *         (formatted as "MM/dd/yyyy - MM/dd/yyyy") mapped to a WeeklyTotals object.
     */
    public static Map<String, Map<String, WeeklyTotals>> processWeeklyRecords(
            Iterable<AttendanceRecord> attendanceRecords) {
        
        Map<String, Map<String, WeeklyTotals>> weeklyData = new TreeMap<>();
        DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        
        for (AttendanceRecord record : attendanceRecords) {
            LocalDate date = record.getDate();
            LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.FRIDAY));
            String weekKey = startOfWeek.format(weekFormatter) + " - " + endOfWeek.format(weekFormatter);
            String empNumber = record.getEmployeeNumber();
            
            Map<String, WeeklyTotals> empWeekly = weeklyData.computeIfAbsent(empNumber, k -> new TreeMap<>());
            WeeklyTotals totals = empWeekly.computeIfAbsent(weekKey, k -> new WeeklyTotals());
            totals.addAttendanceRecord(record);
        }
        
        return weeklyData;
    }
}