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

/**
 * Represents the total worked hours, overtime, and deductions for an employee within a single month.
 * This class aggregates multiple attendance records to compute the total work time.
 */
public class MonthlyTotals {
    private int normalHoursFull = 0;
    private int normalMinutesFull = 0;
    private int overtimeHoursFull = 0;
    private int overtimeMinutesFull = 0;

    private int normalHoursDeduction = 0;
    private int normalMinutesDeduction = 0;
    private int overtimeHoursDeduction = 0;
    private int overtimeMinutesDeduction = 0;

    private int lateMinutes = 0;
    private int undertimeMinutes = 0;

    /**
     * Adds a full attendance record for monthly aggregation.
     * Accumulates all worked hours, including normal and overtime hours.
     *
     * @param record The attendance record to process.
     */
    public void addFullAttendanceRecord(AttendanceRecord record) {
        DailySummary daily = new DailySummary(record);
        daily.addRecord(record);
        normalHoursFull += daily.getNormalHours();
        normalMinutesFull += daily.getNormalMinutes();
        overtimeHoursFull += daily.getOvertimeHours();
        overtimeMinutesFull += daily.getOvertimeMinutes();
        lateMinutes += daily.getLateMinutes();
        undertimeMinutes += daily.getUndertimeMinutes();
    }

    /**
     * Adds only deduction-related values for the given record.
     * Used for computing monthly payroll deductions.
     *
     * @param record The attendance record to process for deductions.
     */
    public void addDeductionRecord(AttendanceRecord record) {
        DailySummary daily = new DailySummary(record);
        daily.addRecord(record);
        normalHoursDeduction += daily.getNormalHours();
        normalMinutesDeduction += daily.getNormalMinutes();
        overtimeHoursDeduction += daily.getOvertimeHours();
        overtimeMinutesDeduction += daily.getOvertimeMinutes();
    }

    /**
     * Converts excess minutes into hours for all work categories.
     * Ensures accurate calculations for normal work, overtime, and deductions.
     */
    public void normalizeHours() {
        normalHoursFull += convertMinutesToHours(normalMinutesFull);
        normalMinutesFull %= 60;

        overtimeHoursFull += convertMinutesToHours(overtimeMinutesFull);
        overtimeMinutesFull %= 60;

        normalHoursDeduction += convertMinutesToHours(normalMinutesDeduction);
        normalMinutesDeduction %= 60;

        overtimeHoursDeduction += convertMinutesToHours(overtimeMinutesDeduction);
        overtimeMinutesDeduction %= 60;
    }

    /**
     * Helper method to convert minutes into equivalent hours.
     *
     * @param minutes The number of minutes to convert.
     * @return The number of whole hours derived from minutes.
     */
    private int convertMinutesToHours(int minutes) {
        return minutes / 60;
    }

    // Getters for accessing monthly totals

    /** @return The total normal full hours worked in the month. */
    public int getNormalHoursFull() {
        return normalHoursFull;
    }

    /** @return The total normal full minutes worked in the month. */
    public int getNormalMinutesFull() {
        return normalMinutesFull;
    }

    /** @return The total overtime full hours worked in the month. */
    public int getOvertimeHoursFull() {
        return overtimeHoursFull;
    }

    /** @return The total overtime full minutes worked in the month. */
    public int getOvertimeMinutesFull() {
        return overtimeMinutesFull;
    }

    /** @return The total normal hours considered for deductions in the month. */
    public int getNormalHoursDeduction() {
        return normalHoursDeduction;
    }

    /** @return The total normal minutes considered for deductions in the month. */
    public int getNormalMinutesDeduction() {
        return normalMinutesDeduction;
    }

    /** @return The total overtime hours considered for deductions in the month. */
    public int getOvertimeHoursDeduction() {
        return overtimeHoursDeduction;
    }

    /** @return The total overtime minutes considered for deductions in the month. */
    public int getOvertimeMinutesDeduction() {
        return overtimeMinutesDeduction;
    }

    /** @return The total late minutes recorded in the month. */
    public int getLateMinutes() {
        return lateMinutes;
    }

    /** @return The total undertime minutes recorded in the month. */
    public int getUndertimeMinutes() {
        return undertimeMinutes;
    }
}