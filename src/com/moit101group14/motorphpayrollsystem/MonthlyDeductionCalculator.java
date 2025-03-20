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
import java.util.Map;
import java.util.TreeMap;

/**
 * MonthlyDeductionCalculator calculates monthly deduction totals per employee.
 * It groups attendance records by employee and month (MM/yyyy) and aggregates deduction-related totals
 * for records up to the cutoff (last working day of the fourth week).
 */
public class MonthlyDeductionCalculator {

    /**
     * Computes monthly deduction totals for each employee.
     *
     * @param attendanceRecords Iterable collection of attendance records.
     * @return A nested map where the outer key is the employee number, the inner key is the month (MM/yyyy),
     *         and the value is a MonthlyTotals object containing aggregated deduction data.
     */
    public static Map<String, Map<String, MonthlyTotals>> calculateMonthlyDeductions(
            Iterable<AttendanceRecord> attendanceRecords) {
        
        Map<String, Map<String, MonthlyTotals>> monthlyMap = new TreeMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
        
        for (AttendanceRecord record : attendanceRecords) {
            LocalDate date = record.getDate();
            String monthKey = date.format(monthFormatter);
            String empNumber = record.getEmployeeNumber();
            
            Map<String, MonthlyTotals> empMonthly = monthlyMap.computeIfAbsent(empNumber, k -> new TreeMap<>());
            MonthlyTotals totals = empMonthly.computeIfAbsent(monthKey, k -> new MonthlyTotals());
            
            LocalDate monthStart = LocalDate.of(date.getYear(), date.getMonthValue(), 1);
            LocalDate cutoff = PayrollDateUtils.getLastWorkingDayOfFourthWeek(monthStart);
            
            if (!date.isAfter(cutoff)) {
                totals.addDeductionRecord(record);
            }
        }
        
        for (Map<String, MonthlyTotals> empMonthly : monthlyMap.values()) {
            for (MonthlyTotals totals : empMonthly.values()) {
                totals.normalizeHours();
            }
        }
        return monthlyMap;
    }
}