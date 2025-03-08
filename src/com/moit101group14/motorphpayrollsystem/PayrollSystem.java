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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class PayrollSystem {

    // File paths (update as needed)
    private static final String EMPLOYEE_FILE = "/Users/mylife/NetBeansProjects/com.moit101group14.motorphpayrollsystem/assets/MotorPHEmployeeDetails.csv";
    private static final String ATTENDANCE_FILE = "/Users/mylife/NetBeansProjects/com.moit101group14.motorphpayrollsystem/assets/MotorPHAttendanceRecord.csv";
    private static final String TAX_BRACKET_FILE = "/Users/mylife/NetBeansProjects/com.moit101group14.motorphpayrollsystem/assets/MotorPHWithholdingTax.csv";
    private static final String SSS_CONTRIBUTION_FILE = "/Users/mylife/NetBeansProjects/com.moit101group14.motorphpayrollsystem/assets/MotorPHSSSContribution.csv";
    private static final String PHIC_CONTRIBUTION_FILE = "/Users/mylife/NetBeansProjects/com.moit101group14.motorphpayrollsystem/assets/MotorPHPhilhealthContribution.csv";
    private static final String HDMF_CONTRIBUTION_FILE = "/Users/mylife/NetBeansProjects/com.moit101group14.motorphpayrollsystem/assets/MotorPHPagIBIGContribution.csv";

    // Data storage
    private static final Map<String, EmployeeDetails> employees = new TreeMap<>();
    private static final List<AttendanceRecord> attendanceRecords = new ArrayList<>();
    private static final List<TaxBracket> taxBrackets = new ArrayList<>();
    private static final List<SSSBracket> sssBrackets = new ArrayList<>();
    private static final List<PHICBracket> philhealthBrackets = new ArrayList<>();
    private static final List<PagIBIGBracket> pagIBIGBrackets = new ArrayList<>();
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    // Release mode for benefits and deductions:
    // 1: Release 100% on the fourth week only.
    // 2: Release 50% on the second week and 50% on the fourth week.
    private static final int RELEASE_MODE = 1; // Change to 2 if needed

    public static void main(String[] args) {
        try {
            loadAllData();
            Map<String, Map<String, MonthlyTotals>> monthlyDeductionMap = getMonthlyDeductionTotals();
            Map<String, Map<String, WeeklyTotals>> weeklyData = processWeeklyRecords();
            generateWeeklyReportSortedByWeek(weeklyData, monthlyDeductionMap);
            processMonthlyRecords();
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            System.exit(1);
        }
    }

    //--------------------------------------------------------------------------
    // Data Loading Methods
    //--------------------------------------------------------------------------
    private static void loadAllData() throws IOException {
        loadEmployees();
        loadAttendanceRecords();
        loadTaxBrackets();
        loadSSSBrackets();
        loadPHICBrackets();
        loadPagIBIGBrackets();
    }
    
    private static void loadEmployees() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                EmployeeDetails emp = new EmployeeDetails(data);
                employees.put(emp.getEmployeeNumber(), emp);
            }
        }
    }
    
    private static void loadAttendanceRecords() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_FILE))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                attendanceRecords.add(new AttendanceRecord(data));
            }
        }
    }
    
    private static void loadTaxBrackets() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(TAX_BRACKET_FILE))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                double salaryFrom = parseAmount(parts[1]);
                double salaryTo = parseAmount(parts[2]);
                double inExcessOf = parseAmount(parts[3]);
                double taxValue = parseAmount(parts[4]);
                double taxRate = parsePercentage(parts[5]);
                taxBrackets.add(new TaxBracket(salaryFrom, salaryTo, inExcessOf, taxValue, taxRate));
            }
        }
    }
    
    private static void loadSSSBrackets() throws IOException {
        loadBrackets(SSS_CONTRIBUTION_FILE, sssBrackets, SSSBracket::new);
    }
    
    private static void loadPHICBrackets() throws IOException {
        loadBrackets(PHIC_CONTRIBUTION_FILE, philhealthBrackets, PHICBracket::new);
    }
    
    private static void loadPagIBIGBrackets() throws IOException {
        loadBrackets(HDMF_CONTRIBUTION_FILE, pagIBIGBrackets, PagIBIGBracket::new);
    }
    
    private static <T> void loadBrackets(String filePath, List<T> brackets, BracketLoader<T> loader) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                brackets.add(loader.load(parts));
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // Helper: Compute Last Working Day of Fourth Week
    //--------------------------------------------------------------------------
    private static LocalDate getLastWorkingDayOfFourthWeek(LocalDate anyDateInMonth) {
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
    
    //--------------------------------------------------------------------------
    // Pre-Aggregate Monthly Deduction Totals
    //--------------------------------------------------------------------------
    private static Map<String, Map<String, MonthlyTotals>> getMonthlyDeductionTotals() {
        Map<String, Map<String, MonthlyTotals>> monthlyMap = new TreeMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
        for (AttendanceRecord record : attendanceRecords) {
            LocalDate date = record.getDate();
            String monthKey = date.format(monthFormatter);
            String empNumber = record.getEmployeeNumber();
            Map<String, MonthlyTotals> empMonthly = monthlyMap.computeIfAbsent(empNumber, k -> new TreeMap<>());
            MonthlyTotals totals = empMonthly.computeIfAbsent(monthKey, k -> new MonthlyTotals());
            LocalDate monthStart = LocalDate.of(date.getYear(), date.getMonthValue(), 1);
            LocalDate cutoff = getLastWorkingDayOfFourthWeek(monthStart);
            if (!date.isAfter(cutoff)) {
                totals.addDeductionRecord(record);
            }
        }
        for (Map<String, MonthlyTotals> empMonthly : monthlyMap.values()) {
            for (MonthlyTotals totals : empMonthly.values()) {
                totals.normalize();
            }
        }
        return monthlyMap;
    }
    
    //--------------------------------------------------------------------------
    // Process Weekly Records (Grouping by Employee and Week)
    //--------------------------------------------------------------------------
    private static Map<String, Map<String, WeeklyTotals>> processWeeklyRecords() {
        Map<String, Map<String, WeeklyTotals>> weeklyData = new TreeMap<>();
        DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        
        for (AttendanceRecord record : attendanceRecords) {
            LocalDate date = record.getDate();
            LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
            String weekKey = startOfWeek.format(weekFormatter) + " - " + endOfWeek.format(weekFormatter);
            String empNumber = record.getEmployeeNumber();
            Map<String, WeeklyTotals> empWeekly = weeklyData.computeIfAbsent(empNumber, k -> new TreeMap<>());
            WeeklyTotals totals = empWeekly.computeIfAbsent(weekKey, k -> new WeeklyTotals());
            totals.addRecord(record);
        }
        return weeklyData;
    }
    
    //--------------------------------------------------------------------------
    // Generate Weekly Report Sorted by Week then Employee Number
    //--------------------------------------------------------------------------
    private static void generateWeeklyReportSortedByWeek(Map<String, Map<String, WeeklyTotals>> weeklyData,
                                                         Map<String, Map<String, MonthlyTotals>> monthlyDeductionMap) {
        DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        
        // Group entries by week start date.
        TreeMap<LocalDate, Map<String, WeeklyReportEntry>> weekMap = new TreeMap<>();
        
        for (String empNumber : weeklyData.keySet()) {
            EmployeeDetails emp = employees.get(empNumber);
            if (emp == null) continue;
            
            Map<String, WeeklyTotals> empWeekly = weeklyData.get(empNumber);
            for (String weekKey : empWeekly.keySet()) {
                // Parse week start and end dates from the weekKey (format: "MM/dd/yyyy - MM/dd/yyyy")
                String[] parts = weekKey.split(" - ");
                LocalDate weekStart = LocalDate.parse(parts[0], weekFormatter);
                LocalDate weekEnd = LocalDate.parse(parts[1], weekFormatter);
                WeeklyTotals totals = empWeekly.get(weekKey);
                totals.normalize();
                double hourlyRate = parseDouble(emp.getHourlyRate());
                
                double normalHours = totals.normalHours + totals.normalMinutes / 60.0;
                double overtimeHours = totals.overtimeHours + totals.overtimeMinutes / 60.0;
                double weeklyNormalValue = normalHours * hourlyRate;
                double weeklyOvertimeValue = overtimeHours * hourlyRate * 1.25;
                double weeklyWorkValue = weeklyNormalValue + weeklyOvertimeValue;
                
                LocalDate monthStart = weekStart.withDayOfMonth(1);
                LocalDate cutoff = getLastWorkingDayOfFourthWeek(monthStart);
                boolean isFourthWeek = weekEnd.equals(cutoff);
                
                // Benefit release logic
                double riceSubsidy, phoneAllowance, clothingAllowance, totalBenefits;
                if (RELEASE_MODE == 1) {
                    if (isFourthWeek) {
                        riceSubsidy = parseDouble(emp.getRiceSubsidy());
                        phoneAllowance = parseDouble(emp.getPhoneAllowance());
                        clothingAllowance = parseDouble(emp.getClothingAllowance());
                    } else {
                        riceSubsidy = 0.0;
                        phoneAllowance = 0.0;
                        clothingAllowance = 0.0;
                    }
                } else if (RELEASE_MODE == 2) {
                    int weekNumber = getWeekNumberInMonth(weekStart);
                    if (weekNumber == 2 || weekNumber == 4) {
                        double factor = 0.5;
                        riceSubsidy = parseDouble(emp.getRiceSubsidy()) * factor;
                        phoneAllowance = parseDouble(emp.getPhoneAllowance()) * factor;
                        clothingAllowance = parseDouble(emp.getClothingAllowance()) * factor;
                    } else {
                        riceSubsidy = 0.0;
                        phoneAllowance = 0.0;
                        clothingAllowance = 0.0;
                    }
                } else {
                    riceSubsidy = parseDouble(emp.getRiceSubsidy());
                    phoneAllowance = parseDouble(emp.getPhoneAllowance());
                    clothingAllowance = parseDouble(emp.getClothingAllowance());
                }
                totalBenefits = riceSubsidy + phoneAllowance + clothingAllowance;
                
                double sssDeduction = 0.0, philhealthDeduction = 0.0, pagibigDeduction = 0.0, withholdingTax = 0.0, totalDeductions = 0.0;
                if (isFourthWeek) {
                    DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
                    String monthKey = monthStart.format(monthFormatter);
                    MonthlyTotals deductionTotals = null;
                    if (monthlyDeductionMap.containsKey(empNumber)) {
                        deductionTotals = monthlyDeductionMap.get(empNumber).get(monthKey);
                    }
                    if (deductionTotals == null) {
                        deductionTotals = new MonthlyTotals();
                    }
                    double normalHoursDeduction = deductionTotals.normalHoursDeduction + deductionTotals.normalMinutesDeduction / 60.0;
                    double overtimeHoursDeduction = deductionTotals.overtimeHoursDeduction + deductionTotals.overtimeMinutesDeduction / 60.0;
                    double deductionBasisValue = (normalHoursDeduction * hourlyRate)
                            + (overtimeHoursDeduction * hourlyRate * 1.25);
                    
                    sssDeduction = calculateSSSDeduction(deductionBasisValue, sssBrackets);
                    philhealthDeduction = calculatePHICDeduction(deductionBasisValue, philhealthBrackets);
                    pagibigDeduction = calculatePagIBIGDeduction(deductionBasisValue, pagIBIGBrackets);
                    double totalDeductionsBeforeTax = sssDeduction + philhealthDeduction + pagibigDeduction;
                    double taxableIncome = emp.getMonthlySalary() - totalDeductionsBeforeTax;
                    withholdingTax = calculateWithholdingTax(taxableIncome, taxBrackets);
                    totalDeductions = totalDeductionsBeforeTax + withholdingTax;
                }
                
                double grossSalary = weeklyWorkValue;
                double netPay = grossSalary + totalBenefits - totalDeductions;
                
                // Create a WeeklyReportEntry with additional fields for normal and overtime hours.
                WeeklyReportEntry entry = new WeeklyReportEntry(emp, weekKey, weeklyWorkValue, totalBenefits,
                                                                sssDeduction, philhealthDeduction, pagibigDeduction,
                                                                withholdingTax, totalDeductions, grossSalary, netPay,
                                                                riceSubsidy, phoneAllowance, clothingAllowance,
                                                                normalHours, overtimeHours);
                
                weekMap.computeIfAbsent(weekStart, k -> new TreeMap<>((e1, e2) -> {
                    try {
                        return Integer.compare(Integer.parseInt(e1), Integer.parseInt(e2));
                    } catch (NumberFormatException nfe) {
                        return e1.compareTo(e2);
                    }
                })).put(empNumber, entry);
            }
        }
        
        // Print the weekly report.
        for (LocalDate weekStart : weekMap.keySet()) {
            Map<String, WeeklyReportEntry> entries = weekMap.get(weekStart);
            String weekKey = entries.values().iterator().next().getWeekKey();
            System.out.println("=== Weekly Report for Week: " + weekKey + " ===\n");
            for (String empNumber : entries.keySet()) {
                WeeklyReportEntry entry = entries.get(empNumber);
                EmployeeDetails emp = entry.getEmployee();
                
                System.out.println("Employee Number: " + emp.getEmployeeNumber());
                System.out.println("Employee Name: " + emp.getLastName() + ", " + emp.getFirstName());
                System.out.println("Birthday: " + emp.getBirthday());
                System.out.println("Week Covered: " + entry.getWeekKey());
                System.out.println();
                // Display total hours worked
                double totalHours = entry.getNormalHours() + entry.getOvertimeHours();
                System.out.println("Hours Worked: " + String.format("%.2f", totalHours) 
                        + " // Normal Hours: " + String.format("%.2f", entry.getNormalHours()) 
                        + ", Overtime Hours: " + String.format("%.2f", entry.getOvertimeHours()));
                System.out.println("Gross Salary: " + currencyFormatter.format(entry.getGrossSalary()));
                System.out.println();
                System.out.println("Benefits:");
                System.out.println("  Rice Subsidy: " + currencyFormatter.format(entry.getRiceSubsidy()));
                System.out.println("  Phone Allowance: " + currencyFormatter.format(entry.getPhoneAllowance()));
                System.out.println("  Clothing Allowance: " + currencyFormatter.format(entry.getClothingAllowance()));
                System.out.println("  Total Benefits: " + currencyFormatter.format(entry.getTotalBenefits()));
                System.out.println();
                System.out.println("Deductions:");
                System.out.println("  SSS: " + currencyFormatter.format(entry.getSssDeduction()));
                System.out.println("  PhilHealth: " + currencyFormatter.format(entry.getPhilhealthDeduction()));
                System.out.println("  Pag-IBIG: " + currencyFormatter.format(entry.getPagibigDeduction()));
                System.out.println("  Withholding Tax: " + currencyFormatter.format(entry.getWithholdingTax()));
                System.out.println("  Total Deductions: " + currencyFormatter.format(entry.getTotalDeductions()));
                System.out.println();
                System.out.println("Net Pay: " + currencyFormatter.format(entry.getNetPay()));
                System.out.println();
                System.out.println("=".repeat(50));
                System.out.println();
            }
        }
    }
    
    // Helper: Compute week number in month based on a given date.
    private static int getWeekNumberInMonth(LocalDate date) {
        return ((date.getDayOfMonth() - 1) / 7) + 1;
    }
    
    //--------------------------------------------------------------------------
    // Process Monthly Records and Print Monthly Report
    //--------------------------------------------------------------------------
    private static void processMonthlyRecords() {
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
        Map<String, Map<String, MonthlyTotals>> monthlyData = new TreeMap<>();
        
        for (AttendanceRecord record : attendanceRecords) {
            LocalDate date = record.getDate();
            String monthKey = date.format(monthFormatter);
            String empNumber = record.getEmployeeNumber();
            
            Map<String, MonthlyTotals> empMonthly = monthlyData.computeIfAbsent(empNumber, k -> new TreeMap<>());
            MonthlyTotals totals = empMonthly.computeIfAbsent(monthKey, k -> new MonthlyTotals());
            
            totals.addFullRecord(record);
            
            LocalDate monthStart = LocalDate.of(date.getYear(), date.getMonthValue(), 1);
            LocalDate lastDeductionDay = getLastWorkingDayOfFourthWeek(monthStart);
            if (!date.isAfter(lastDeductionDay)) {
                totals.addDeductionRecord(record);
            }
        }
        
        for (String empNumber : monthlyData.keySet()) {
            EmployeeDetails emp = employees.get(empNumber);
            if (emp == null) continue;
            double hourlyRate = parseDouble(emp.getHourlyRate());
            double riceSubsidy = parseDouble(emp.getRiceSubsidy());
            double phoneAllowance = parseDouble(emp.getPhoneAllowance());
            double clothingAllowance = parseDouble(emp.getClothingAllowance());
            double monthlySalary = emp.getMonthlySalary();
            
            Map<String, MonthlyTotals> empMonthly = monthlyData.get(empNumber);
            for (String monthKey : empMonthly.keySet()) {
                MonthlyTotals totals = empMonthly.get(monthKey);
                totals.normalize();
                
                double normalHoursFull = totals.normalHoursFull + totals.normalMinutesFull / 60.0;
                double overtimeHoursFull = totals.overtimeHoursFull + totals.overtimeMinutesFull / 60.0;
                double totalNormalValue = normalHoursFull * hourlyRate;
                double totalOvertimeValue = overtimeHoursFull * hourlyRate * 1.25;
                double fullAttendanceValue = totalNormalValue + totalOvertimeValue;
                
                double normalHoursDeduction = totals.normalHoursDeduction + totals.normalMinutesDeduction / 60.0;
                double overtimeHoursDeduction = totals.overtimeHoursDeduction + totals.overtimeMinutesDeduction / 60.0;
                double deductionBasisValue = (normalHoursDeduction * hourlyRate)
                        + (overtimeHoursDeduction * hourlyRate * 1.25);
                
                double sssDeduction = calculateSSSDeduction(deductionBasisValue, sssBrackets);
                double philhealthDeduction = calculatePHICDeduction(deductionBasisValue, philhealthBrackets);
                double pagIBIGDeduction = calculatePagIBIGDeduction(deductionBasisValue, pagIBIGBrackets);
                double totalDeductionsBeforeTax = sssDeduction + philhealthDeduction + pagIBIGDeduction;
                double taxableIncome = monthlySalary - totalDeductionsBeforeTax;
                double withholdingTax = calculateWithholdingTax(taxableIncome, taxBrackets);
                double fullDeductions = totalDeductionsBeforeTax + withholdingTax;
                
                System.out.println("Employee Number: " + emp.getEmployeeNumber());
                System.out.println("Employee Name: " + emp.getLastName() + ", " + emp.getFirstName());
                System.out.println("Month Covered: " + monthKey);
                System.out.println("\n--- Attendance Totals ---");
                System.out.printf("Full Month - Normal Hours: %.2f, Overtime Hours: %.2f%n", normalHoursFull, overtimeHoursFull);
                System.out.printf("Deduction Basis (up to end of fourth week) - Normal Hours: %.2f, Overtime Hours: %.2f%n",
                                  normalHoursDeduction, overtimeHoursDeduction);
                System.out.println("\nDeduction Basis Value: " + currencyFormatter.format(deductionBasisValue));
                
                System.out.println("\n--- Value Computation ---");
                System.out.println("Total Normal Value: " + currencyFormatter.format(totalNormalValue));
                System.out.println("Total Overtime Value: " + currencyFormatter.format(totalOvertimeValue));
                System.out.println("Full Attendance Value: " + currencyFormatter.format(fullAttendanceValue));
                double totalBenefits = riceSubsidy + phoneAllowance + clothingAllowance;
                System.out.println("Benefits: " + currencyFormatter.format(totalBenefits));
                double grossSalary = fullAttendanceValue + totalBenefits;
                System.out.println("Gross Salary: " + currencyFormatter.format(grossSalary));
                
                System.out.println("\n--- Deductions ---");
                System.out.println("SSS: " + currencyFormatter.format(sssDeduction));
                System.out.println("PhilHealth: " + currencyFormatter.format(philhealthDeduction));
                System.out.println("Pag-IBIG: " + currencyFormatter.format(pagIBIGDeduction));
                System.out.println("Withholding Tax: " + currencyFormatter.format(withholdingTax));
                System.out.println("Total Deductions: " + currencyFormatter.format(fullDeductions));
                
                double netPay = grossSalary - fullDeductions;
                System.out.println("\nNet Pay: " + currencyFormatter.format(netPay));
                System.out.println("\n" + "=".repeat(50) + "\n");
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // Utility Methods
    //--------------------------------------------------------------------------
    private static double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) return 0.0;
        try {
            String cleanedValue = value.replaceAll("[\",]", "").trim();
            return Double.parseDouble(cleanedValue);
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse value: " + value);
            return 0.0;
        }
    }
    
    private static double parseAmount(String amount) {
        if (amount == null || amount.isEmpty() || amount.equalsIgnoreCase("N/A"))
            return 0.0;
        try {
            return Double.parseDouble(amount.replace(",", "").replace("\"", ""));
        } catch (NumberFormatException e) {
            System.err.println("Error parsing amount: " + amount);
            return 0.0;
        }
    }
    
    private static double parsePercentage(String percentage) {
        try {
            NumberFormat percentFormat = NumberFormat.getPercentInstance();
            Number number = percentFormat.parse(percentage);
            return number.doubleValue();
        } catch (Exception e) {
            System.err.println("Error parsing percentage: " + percentage);
            return 0.0;
        }
    }
    
    //--------------------------------------------------------------------------
    // Deduction Calculation Methods
    //--------------------------------------------------------------------------
    private static double calculateSSSDeduction(double salary, List<SSSBracket> brackets) {
        return findBracketAndApply(salary, brackets, SSSBracket::getContribution, "SSS");
    }
    
    private static double calculatePHICDeduction(double salary, List<PHICBracket> brackets) {
        if (salary <= 10000) return 150.0;
        else if (salary >= 60000) return 900.0;
        return findBracketAndApply(salary, brackets, bracket -> salary * 0.015, "PHIC");
    }
    
    private static double calculatePagIBIGDeduction(double salary, List<PagIBIGBracket> brackets) {
        return findBracketAndApply(salary, brackets, bracket -> 100.0, "Pag-IBIG");
    }
    
    private static double calculateWithholdingTax(double taxableIncome, List<TaxBracket> brackets) {
        for (TaxBracket bracket : brackets) {
            if (taxableIncome >= bracket.getSalaryFrom() && taxableIncome <= bracket.getSalaryTo()) {
                double excess = taxableIncome - bracket.getInExcessOf();
                double taxOnExcess = excess * bracket.getTaxRate();
                return taxOnExcess + bracket.getTaxValue();
            }
        }
        return 0.0;
    }
    
    private static <T> double findBracketAndApply(double salary, List<T> brackets, BracketCalculator<T> calculator, String deductionType) {
        for (T bracket : brackets) {
            if (isSalaryInRange(salary, bracket)) {
                return calculator.calculate(bracket);
            }
        }
        return 0.0;
    }
    
    private static <T> boolean isSalaryInRange(double salary, T bracket) {
        if (bracket instanceof SSSBracket) {
            SSSBracket sss = (SSSBracket) bracket;
            return salary >= sss.getCompensationFrom() && salary <= sss.getCompensationTo();
        } else if (bracket instanceof PHICBracket) {
            PHICBracket phic = (PHICBracket) bracket;
            return salary >= phic.getCompensationFrom() && salary <= phic.getCompensationTo();
        } else if (bracket instanceof PagIBIGBracket) {
            PagIBIGBracket pib = (PagIBIGBracket) bracket;
            return salary >= pib.getSalaryFrom() && salary <= pib.getSalaryTo();
        }
        return false;
    }
    
    //--------------------------------------------------------------------------
    // Functional Interfaces
    //--------------------------------------------------------------------------
    @FunctionalInterface
    private interface BracketCalculator<T> {
        double calculate(T bracket);
    }
    
    @FunctionalInterface
    private interface BracketLoader<T> {
        T load(String[] parts);
    }
    
    //--------------------------------------------------------------------------
    // Inner Helper Classes for Aggregating Totals and Report Data
    //--------------------------------------------------------------------------
    private static class WeeklyTotals {
        int normalHours = 0;
        int normalMinutes = 0;
        int overtimeHours = 0;
        int overtimeMinutes = 0;
        int lateMinutes = 0;
        int undertimeMinutes = 0;
        
        void addRecord(AttendanceRecord record) {
            DailySummary daily = new DailySummary(record);
            daily.addRecord(record);
            normalHours += daily.getNormalHours();
            normalMinutes += daily.getNormalMinutes();
            overtimeHours += daily.getOvertimeHours();
            overtimeMinutes += daily.getOvertimeMinutes();
            lateMinutes += daily.getLateMinutes();
            undertimeMinutes += daily.getUndertimeMinutes();
        }
        
        void normalize() {
            normalHours += normalMinutes / 60;
            normalMinutes %= 60;
            overtimeHours += overtimeMinutes / 60;
            overtimeMinutes %= 60;
        }
    }
    
    private static class MonthlyTotals {
        int normalHoursFull = 0;
        int normalMinutesFull = 0;
        int overtimeHoursFull = 0;
        int overtimeMinutesFull = 0;
        
        int normalHoursDeduction = 0;
        int normalMinutesDeduction = 0;
        int overtimeHoursDeduction = 0;
        int overtimeMinutesDeduction = 0;
        
        int lateMinutes = 0;
        int undertimeMinutes = 0;
        
        void addFullRecord(AttendanceRecord record) {
            DailySummary daily = new DailySummary(record);
            daily.addRecord(record);
            normalHoursFull += daily.getNormalHours();
            normalMinutesFull += daily.getNormalMinutes();
            overtimeHoursFull += daily.getOvertimeHours();
            overtimeMinutesFull += daily.getOvertimeMinutes();
            lateMinutes += daily.getLateMinutes();
            undertimeMinutes += daily.getUndertimeMinutes();
        }
        
        void addDeductionRecord(AttendanceRecord record) {
            DailySummary daily = new DailySummary(record);
            daily.addRecord(record);
            normalHoursDeduction += daily.getNormalHours();
            normalMinutesDeduction += daily.getNormalMinutes();
            overtimeHoursDeduction += daily.getOvertimeHours();
            overtimeMinutesDeduction += daily.getOvertimeMinutes();
        }
        
        void normalize() {
            normalHoursFull += normalMinutesFull / 60;
            normalMinutesFull %= 60;
            overtimeHoursFull += overtimeMinutesFull / 60;
            overtimeMinutesFull %= 60;
            
            normalHoursDeduction += normalMinutesDeduction / 60;
            normalMinutesDeduction %= 60;
            overtimeHoursDeduction += overtimeMinutesDeduction / 60;
            overtimeMinutesDeduction %= 60;
        }
    }
    
    // Helper class to store computed weekly payroll values.
    // (Updated with new fields for normal and overtime hours)
    private static class WeeklyReportEntry {
        private final EmployeeDetails employee;
        private final String weekKey;
        private final double weeklyWorkValue;
        private final double totalBenefits;
        private final double sssDeduction;
        private final double philhealthDeduction;
        private final double pagibigDeduction;
        private final double withholdingTax;
        private final double totalDeductions;
        private final double grossSalary;
        private final double netPay;
        private final double riceSubsidy;
        private final double phoneAllowance;
        private final double clothingAllowance;
        // New fields for hours worked
        private final double normalHours;
        private final double overtimeHours;
        
        public WeeklyReportEntry(EmployeeDetails employee, String weekKey, double weeklyWorkValue, double totalBenefits,
                                 double sssDeduction, double philhealthDeduction, double pagibigDeduction,
                                 double withholdingTax, double totalDeductions, double grossSalary, double netPay,
                                 double riceSubsidy, double phoneAllowance, double clothingAllowance,
                                 double normalHours, double overtimeHours) {
            this.employee = employee;
            this.weekKey = weekKey;
            this.weeklyWorkValue = weeklyWorkValue;
            this.totalBenefits = totalBenefits;
            this.sssDeduction = sssDeduction;
            this.philhealthDeduction = philhealthDeduction;
            this.pagibigDeduction = pagibigDeduction;
            this.withholdingTax = withholdingTax;
            this.totalDeductions = totalDeductions;
            this.grossSalary = grossSalary;
            this.netPay = netPay;
            this.riceSubsidy = riceSubsidy;
            this.phoneAllowance = phoneAllowance;
            this.clothingAllowance = clothingAllowance;
            this.normalHours = normalHours;
            this.overtimeHours = overtimeHours;
        }
        
        public EmployeeDetails getEmployee() { return employee; }
        public String getWeekKey() { return weekKey; }
        public double getWeeklyWorkValue() { return weeklyWorkValue; }
        public double getTotalBenefits() { return totalBenefits; }
        public double getSssDeduction() { return sssDeduction; }
        public double getPhilhealthDeduction() { return philhealthDeduction; }
        public double getPagibigDeduction() { return pagibigDeduction; }
        public double getWithholdingTax() { return withholdingTax; }
        public double getTotalDeductions() { return totalDeductions; }
        public double getGrossSalary() { return grossSalary; }
        public double getNetPay() { return netPay; }
        public double getRiceSubsidy() { return riceSubsidy; }
        public double getPhoneAllowance() { return phoneAllowance; }
        public double getClothingAllowance() { return clothingAllowance; }
        public double getNormalHours() { return normalHours; }
        public double getOvertimeHours() { return overtimeHours; }
    }
}