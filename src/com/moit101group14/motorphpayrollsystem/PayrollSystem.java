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

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PayrollSystem serves as the entry point for the payroll application.
 * It loads data from CSV files, processes attendance records, computes deductions,
 * and generates the weekly payroll report.
 */
public class PayrollSystem {
    private static final Logger logger = Logger.getLogger(PayrollSystem.class.getName());
    
    // Benefit release mode: 1 = 100% on the fourth week only; 2 = 50% on the second and fourth weeks.
    private static final int RELEASE_MODE = 1;
    
    /**
     * Main method that initiates the payroll processing workflow.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Set root logger level to ALL to display all messages.
        Logger.getLogger("").setLevel(Level.ALL);
        logger.info("Payroll System started");
        try {
            // Load data from CSV files.
            DataLoader dataLoader = new DataLoader();
            dataLoader.loadAllData();
            Map<String, EmployeeDetails> employees = dataLoader.getEmployees();
            var attendanceRecords = dataLoader.getAttendanceRecords();
            logger.info("Data successfully loaded");

            // Set financial brackets in PayrollSystemHelper.
            PayrollSystemHelper.setSssBrackets(dataLoader.getSssBrackets());
            PayrollSystemHelper.setPhilHealthBrackets(dataLoader.getPhilHealthBrackets());
            PayrollSystemHelper.setPagIbigBrackets(dataLoader.getPagIbigBrackets());
            PayrollSystemHelper.setTaxBrackets(dataLoader.getTaxBrackets());

            // Calculate monthly deduction totals per employee.
            Map<String, Map<String, MonthlyTotals>> monthlyDeductionMap = 
                    MonthlyDeductionCalculator.calculateMonthlyDeductions(attendanceRecords);
            logger.info("Monthly deduction totals calculated");

            // Process attendance records into weekly totals.
            Map<String, Map<String, WeeklyTotals>> weeklyData = 
                    WeeklyRecordsProcessor.processWeeklyRecords(attendanceRecords);
            logger.info("Weekly records processed");

            // Generate and print the weekly payroll report.
            PayrollReportGenerator.generateWeeklyReport(weeklyData, monthlyDeductionMap, employees, RELEASE_MODE);
            logger.info("Weekly report generated");

        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred during data loading or processing", e);
            System.exit(1);
        }
    }
}