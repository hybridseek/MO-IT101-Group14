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
 * Represents the total worked hours, overtime, and deductions for an employee within a single week.
 * This class aggregates multiple attendance records to compute the total work time.
 */
public class WeeklyTotals {
    private int normalHours = 0;
    private int normalMinutes = 0;
    private int overtimeHours = 0;
    private int overtimeMinutes = 0;
    private int lateMinutes = 0;
    private int undertimeMinutes = 0;

    /**
     * Adds an attendance record's data into this weekly total.
     * Accumulates normal hours, overtime hours, late minutes, and undertime minutes.
     *
     * @param record The attendance record to process.
     */
    public void addAttendanceRecord(AttendanceRecord record) {
        DailySummary daily = new DailySummary(record);
        daily.addRecord(record);
        normalHours += daily.getNormalHours();
        normalMinutes += daily.getNormalMinutes();
        overtimeHours += daily.getOvertimeHours();
        overtimeMinutes += daily.getOvertimeMinutes();
        lateMinutes += daily.getLateMinutes();
        undertimeMinutes += daily.getUndertimeMinutes();
    }

    /**
     * Converts excess minutes into hours for normal and overtime work.
     * Ensures that the time format remains correct (e.g., 90 minutes -> 1 hour 30 minutes).
     */
    public void normalizeHours() {
        normalHours += convertMinutesToHours(normalMinutes);
        normalMinutes %= 60;

        overtimeHours += convertMinutesToHours(overtimeMinutes);
        overtimeMinutes %= 60;
    }

    /**
     * Helper method to convert excess minutes into equivalent hours.
     *
     * @param minutes The number of minutes to convert.
     * @return The number of whole hours derived from minutes.
     */
    private int convertMinutesToHours(int minutes) {
        return minutes / 60;
    }

    // Getters for accessing weekly totals

    /** @return The total normal hours worked in the week. */
    public int getNormalHours() {
        return normalHours;
    }

    /** @return The total normal minutes worked in the week. */
    public int getNormalMinutes() {
        return normalMinutes;
    }

    /** @return The total overtime hours worked in the week. */
    public int getOvertimeHours() {
        return overtimeHours;
    }

    /** @return The total overtime minutes worked in the week. */
    public int getOvertimeMinutes() {
        return overtimeMinutes;
    }

    /** @return The total late minutes accumulated in the week. */
    public int getLateMinutes() {
        return lateMinutes;
    }

    /** @return The total undertime minutes accumulated in the week. */
    public int getUndertimeMinutes() {
        return undertimeMinutes;
    }
}