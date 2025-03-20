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

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * PayrollReportGenerator generates and prints the weekly payroll report.
 * It calculates earnings, benefits, deductions, and net pay for each employee,
 * then prints a formatted report.
 */
public class PayrollReportGenerator {
    // Formatter for currency values in Philippine Peso format.
    private static final NumberFormat currencyFormatter = 
            NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    /**
     * Generates and prints the weekly payroll report based on the provided data.
     *
     * @param weeklyData         Map of weekly totals per employee.
     * @param monthlyDeductionMap Map of monthly deduction totals per employee.
     * @param employees          Map of employee details.
     * @param releaseMode        Benefit release mode:
     *                           1 = 100% on the fourth week only,
     *                           2 = 50% on the second and fourth weeks,
     *                           default = full benefits every week.
     */
    public static void generateWeeklyReport(Map<String, Map<String, WeeklyTotals>> weeklyData,
                                              Map<String, Map<String, MonthlyTotals>> monthlyDeductionMap,
                                              Map<String, EmployeeDetails> employees,
                                              int releaseMode) {
        // Formatter for the week date range.
        DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        // TreeMap to sort weeks by their start date.
        TreeMap<LocalDate, Map<String, WeeklyReportEntry>> weekMap = new TreeMap<>();

        // Process payroll for each employee.
        for (String empNumber : weeklyData.keySet()) {
            EmployeeDetails emp = employees.get(empNumber);
            if (emp == null) continue; // Skip if employee details are missing

            Map<String, WeeklyTotals> empWeekly = weeklyData.get(empNumber);
            for (String weekKey : empWeekly.keySet()) {
                // Parse the week key into start and end dates.
                String[] parts = weekKey.split(" - ");
                LocalDate weekStart = LocalDate.parse(parts[0], weekFormatter);
                LocalDate weekEnd = LocalDate.parse(parts[1], weekFormatter);
                WeeklyTotals totals = empWeekly.get(weekKey);
                // Normalize minutes to hours.
                totals.normalizeHours();

                // Since the getters now return double values, use them directly.
                double hourlyRate = emp.getHourlyRate();
                double normalHours = totals.getNormalHours() + Math.floor(totals.getNormalMinutes() / 60.0 * 100) / 100;
                double overtimeHours = totals.getOvertimeHours() + Math.floor(totals.getOvertimeMinutes() / 60.0 * 100) / 100;
                double weeklyNormalValue = normalHours * hourlyRate;
                double weeklyOvertimeValue = overtimeHours * hourlyRate * 1.25;
                double weeklyWorkValue = weeklyNormalValue + weeklyOvertimeValue;

                // Determine if this week is the fourth week of the month.
                LocalDate monthStart = weekStart.withDayOfMonth(1);
                LocalDate cutoff = PayrollDateUtils.getLastWorkingDayOfFourthWeek(monthStart);
                boolean isFourthWeek = weekEnd.equals(cutoff);

                // Determine benefits based on release mode using a switch expression.
                double riceSubsidy = 0.0, phoneAllowance = 0.0, clothingAllowance = 0.0, totalBenefits = 0.0;
                switch (releaseMode) {
                    case 1 -> { // Full benefits released only on the fourth week.
                        if (isFourthWeek) {
                            riceSubsidy = emp.getRiceSubsidy();
                            phoneAllowance = emp.getPhoneAllowance();
                            clothingAllowance = emp.getClothingAllowance();
                        }
                    }
                    case 2 -> { // 50% benefits released on the second and fourth weeks.
                        int weekNumber = PayrollDateUtils.getWeekNumberInMonth(weekStart);
                        if (weekNumber == 2 || weekNumber == 4) {
                            double factor = 0.5;
                            riceSubsidy = emp.getRiceSubsidy() * factor;
                            phoneAllowance = emp.getPhoneAllowance() * factor;
                            clothingAllowance = emp.getClothingAllowance() * factor;
                        }
                    }
                    default -> { // Full benefits released every week.
                        riceSubsidy = emp.getRiceSubsidy();
                        phoneAllowance = emp.getPhoneAllowance();
                        clothingAllowance = emp.getClothingAllowance();
                    }
                }
                totalBenefits = riceSubsidy + phoneAllowance + clothingAllowance;

                // Calculate deductions only if this is the fourth week.
                double sssDeduction = 0.0, philHealthDeduction = 0.0, pagIbigDeduction = 0.0, withholdingTax = 0.0, totalDeductions = 0.0;
                if (isFourthWeek) {
                    DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
                    String monthKey = monthStart.format(monthFormatter);
                    MonthlyTotals deductionTotals = monthlyDeductionMap.getOrDefault(empNumber, new TreeMap<>())
                                                                        .getOrDefault(monthKey, new MonthlyTotals());
                    double normalHoursDeduction = deductionTotals.getNormalHoursDeduction() +
                            Math.floor(deductionTotals.getNormalMinutesDeduction() / 60.0 * 100) / 100;
                    double overtimeHoursDeduction = deductionTotals.getOvertimeHoursDeduction() +
                            Math.floor(deductionTotals.getOvertimeMinutesDeduction() / 60.0 * 100) / 100;
                    double deductionBasisValue = (normalHoursDeduction * hourlyRate)
                            + (overtimeHoursDeduction * hourlyRate * 1.25);

                    sssDeduction = DeductionsCalculator.calculateSssDeduction(deductionBasisValue, PayrollSystemHelper.getSssBrackets());
                    philHealthDeduction = DeductionsCalculator.calculatePhicDeduction(deductionBasisValue, PayrollSystemHelper.getPhilHealthBrackets());
                    pagIbigDeduction = DeductionsCalculator.calculatePagIbigDeduction(deductionBasisValue, PayrollSystemHelper.getPagIbigBrackets());
                    double totalDeductionsBeforeTax = sssDeduction + philHealthDeduction + pagIbigDeduction;
                    double taxableIncome = emp.getMonthlySalary() - totalDeductionsBeforeTax;
                    withholdingTax = DeductionsCalculator.calculateWithholdingTax(taxableIncome, PayrollSystemHelper.getTaxBrackets());
                    totalDeductions = totalDeductionsBeforeTax + withholdingTax;
                }
                
                double grossSalary = weeklyWorkValue;
                double netPay = grossSalary + totalBenefits - totalDeductions;
                
                WeeklyReportEntry entry = new WeeklyReportEntry(emp, weekKey, weeklyWorkValue, totalBenefits,
                        sssDeduction, philHealthDeduction, pagIbigDeduction, withholdingTax, totalDeductions,
                        grossSalary, netPay, riceSubsidy, phoneAllowance, clothingAllowance, normalHours, overtimeHours);
                
                // Use natural string ordering for employee numbers
                weekMap.computeIfAbsent(weekStart, k -> new TreeMap<>()).put(empNumber, entry);
            }
        }
        
        // Print the weekly payroll report
        for (LocalDate weekStart : weekMap.keySet()) {
            Map<String, WeeklyReportEntry> entries = weekMap.get(weekStart);
            String weekKey = entries.values().iterator().next().getWeekKey();
            System.out.println("=== Weekly Report for Week: " + weekKey + " ===\n");
            
            for (WeeklyReportEntry entry : entries.values()) {
                EmployeeDetails emp = entry.getEmployee();
                System.out.println("Employee Number      : " + emp.getEmployeeNumber());
                System.out.println("Employee Name        : " + emp.getLastName() + ", " + emp.getFirstName());
                System.out.println("Birthday             : " + emp.getBirthday());
                System.out.println("Week Covered         : " + entry.getWeekKey());
                System.out.println("-".repeat(65));
                
                double totalHours = entry.getNormalHours() + entry.getOvertimeHours();
                System.out.printf("Hours Worked         : %4.2f (Normal: %.2f | Overtime: %.2f)%n",
                    totalHours, entry.getNormalHours(), entry.getOvertimeHours());
                
                System.out.println("\nEarnings:");
                System.out.printf("  %-19s: %10s%n", "Gross Salary", currencyFormatter.format(entry.getGrossSalary()));
                
                System.out.println("\nBenefits:");
                System.out.printf("  %-19s: %10s%n", "Rice Subsidy", currencyFormatter.format(entry.getRiceSubsidy()));
                System.out.printf("  %-19s: %10s%n", "Phone Allowance", currencyFormatter.format(entry.getPhoneAllowance()));
                System.out.printf("  %-19s: %10s%n", "Clothing Allowance", currencyFormatter.format(entry.getClothingAllowance()));
                System.out.printf("  %-19s: %10s%n", "Total Benefits", currencyFormatter.format(entry.getTotalBenefits()));
                
                System.out.println("\nDeductions:");
                System.out.printf("  %-19s: %10s%n", "SSS", currencyFormatter.format(entry.getSssDeduction()));
                System.out.printf("  %-19s: %10s%n", "PhilHealth", currencyFormatter.format(entry.getPhilhealthDeduction()));
                System.out.printf("  %-19s: %10s%n", "Pag-IBIG", currencyFormatter.format(entry.getPagibigDeduction()));
                System.out.printf("  %-19s: %10s%n", "Withholding Tax", currencyFormatter.format(entry.getWithholdingTax()));
                System.out.printf("  %-19s: %10s%n", "Total Deductions", currencyFormatter.format(entry.getTotalDeductions()));
                
                System.out.printf("\n%-21s: %10s%n", "Net Pay", currencyFormatter.format(entry.getNetPay()));
                System.out.println("=".repeat(65));
            }
        }
    }
}