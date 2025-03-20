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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * PayrollDateUtils provides utility methods for payroll-related date computations.
 * It includes methods to determine the last working day of the fourth week and
 * to calculate the week number within a month.
 */
public class PayrollDateUtils {

    /**
     * Determines the last working day (Friday) of the fourth week in the given month.
     * If the computed Friday falls outside the month, returns the last Friday of the month.
     *
     * @param anyDateInMonth A date within the target month.
     * @return The last working Friday of the fourth week or the last Friday of the month.
     */
    public static LocalDate getLastWorkingDayOfFourthWeek(LocalDate anyDateInMonth) {
        LocalDate firstDay = anyDateInMonth.withDayOfMonth(1);
        LocalDate firstMonday = firstDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        LocalDate fourthMonday = firstMonday.plusWeeks(3);
        LocalDate fourthWeekFriday = fourthMonday.plusDays(4);
        if (fourthWeekFriday.getMonthValue() != anyDateInMonth.getMonthValue()) {
            LocalDate lastDayOfMonth = anyDateInMonth.with(TemporalAdjusters.lastDayOfMonth());
            while (lastDayOfMonth.getDayOfWeek() != DayOfWeek.FRIDAY) {
                lastDayOfMonth = lastDayOfMonth.minusDays(1);
            }
            return lastDayOfMonth;
        }
        return fourthWeekFriday;
    }

    /**
     * Calculates the week number (1-based) of a given date within its month.
     *
     * @param date The date for which to determine the week number.
     * @return The week number within the month.
     */
    public static int getWeekNumberInMonth(LocalDate date) {
        return ((date.getDayOfMonth() - 1) / 7) + 1;
    }
}