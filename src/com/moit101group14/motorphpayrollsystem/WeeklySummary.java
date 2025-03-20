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

/**
 * Represents a summary of an employee's weekly work details, including hours worked,
 * overtime, deductions, and other relevant payroll data.
 */
public class WeeklySummary {
    private final String employeeNumber;
    private final String employeeName;
    private final String weekKey;
    private final double totalHoursWorked;
    private final double totalOvertimeHours;
    private final double grossSalary;
    private final double totalBenefits;
    private final double totalDeductions;
    private final double netPay;

    /**
     * Constructs a WeeklySummary object containing the weekly payroll details for an employee.
     *
     * @param employeeNumber        The unique identifier of the employee.
     * @param employeeName          The full name of the employee.
     * @param weekKey               The key representing the week (e.g., "MM/DD/YYYY - MM/DD/YYYY").
     * @param totalHoursWorked      The total normal hours worked in the week.
     * @param totalOvertimeHours    The total overtime hours worked in the week.
     * @param grossSalary           The total earnings before deductions.
     * @param totalBenefits         The total benefits received.
     * @param totalDeductions       The total amount deducted.
     * @param netPay                The final net pay after deductions.
     */
    public WeeklySummary(String employeeNumber, String employeeName, String weekKey, 
                         double totalHoursWorked, double totalOvertimeHours, 
                         double grossSalary, double totalBenefits, 
                         double totalDeductions, double netPay) {
        this.employeeNumber = employeeNumber;
        this.employeeName = employeeName;
        this.weekKey = weekKey;
        this.totalHoursWorked = totalHoursWorked;
        this.totalOvertimeHours = totalOvertimeHours;
        this.grossSalary = grossSalary;
        this.totalBenefits = totalBenefits;
        this.totalDeductions = totalDeductions;
        this.netPay = netPay;
    }

    /** @return The unique identifier of the employee. */
    public String getEmployeeNumber() {
        return employeeNumber;
    }

    /** @return The full name of the employee. */
    public String getEmployeeName() {
        return employeeName;
    }

    /** @return The key representing the week (e.g., "MM/DD/YYYY - MM/DD/YYYY"). */
    public String getWeekKey() {
        return weekKey;
    }

    /** @return The total normal hours worked in the week. */
    public double getTotalHoursWorked() {
        return totalHoursWorked;
    }

    /** @return The total overtime hours worked in the week. */
    public double getTotalOvertimeHours() {
        return totalOvertimeHours;
    }

    /** @return The total earnings before deductions. */
    public double getGrossSalary() {
        return grossSalary;
    }

    /** @return The total benefits received. */
    public double getTotalBenefits() {
        return totalBenefits;
    }

    /** @return The total amount deducted. */
    public double getTotalDeductions() {
        return totalDeductions;
    }

    /** @return The final net pay after deductions. */
    public double getNetPay() {
        return netPay;
    }

    /**
     * Generates a formatted summary of the weekly payroll details for an employee.
     *
     * @return A string representation of the weekly payroll details.
     */
    @Override
    public String toString() {
        return String.format(
            "Weekly Summary for %s (%s) - Week: %s%n" +
            "Total Hours Worked : %.2f%n" +
            "Overtime Hours     : %.2f%n" +
            "Gross Salary       : %.2f%n" +
            "Total Benefits     : %.2f%n" +
            "Total Deductions   : %.2f%n" +
            "Net Pay            : %.2f%n",
            employeeName, employeeNumber, weekKey, 
            totalHoursWorked, totalOvertimeHours, 
            grossSalary, totalBenefits, totalDeductions, netPay
        );
    }
}
