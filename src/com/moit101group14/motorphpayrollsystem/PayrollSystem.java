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
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Main class
public class PayrollSystem {

    // File paths (update these paths as needed)
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
    private static final int RELEASE_MODE = 1; // Change to 2 for option 2

    // Map to accumulate computed weekly work values (gross pay from attendance) for each employee for the current month.
    private static final Map<String, Double> monthlyGrossMap = new HashMap<>();

    public static void main(String[] args) {
        try {
            loadAllData();
            processAttendanceRecords();
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            System.exit(1);
        }
    }

    // Load all data files
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

    // Process attendance records by grouping them by week.
    // Also, accumulate the weekly computed work values into monthlyGrossMap.
    private static void processAttendanceRecords() {
        Map<String, Map<LocalDate, List<AttendanceRecord>>> weeklyRecords = groupAttendanceByWeek();
        List<String> sortedWeekKeys = new ArrayList<>(weeklyRecords.keySet());
        sortedWeekKeys.sort(Comparator.comparing(s ->
                LocalDate.parse(s.split(" - ")[0], DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        ));

        String lastMonth = "";
        for (String weekKey : sortedWeekKeys) {
            LocalDate weekStart = LocalDate.parse(weekKey.split(" - ")[0], DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            String currentMonth = String.valueOf(weekStart.getMonthValue());
            // Reset accumulation when month changes
            if (!currentMonth.equals(lastMonth)) {
                monthlyGrossMap.clear();
                lastMonth = currentMonth;
            }
            Map<LocalDate, List<AttendanceRecord>> weekRecords = groupAttendanceByWeek().get(weekKey);
            Map<String, WeeklySummary> summaries = new TreeMap<>();
            // Build weekly summaries per employee
            for (List<AttendanceRecord> dailyRecords : weekRecords.values()) {
                for (AttendanceRecord record : dailyRecords) {
                    String empNumber = record.getEmployeeNumber();
                    EmployeeDetails emp = employees.get(empNumber);
                    if (emp != null) {
                        summaries.computeIfAbsent(empNumber, k -> new WeeklySummary(emp)).addRecord(record);
                    }
                }
            }
            // Accumulate weekly work value (computed gross) into monthlyGrossMap.
            for (Map.Entry<String, WeeklySummary> entry : summaries.entrySet()) {
                String empNumber = entry.getKey();
                WeeklySummary summary = entry.getValue();
                double hourlyRate = parseDouble(employees.get(empNumber).getHourlyRate());
                int totalNormalHours = summary.getTotalNormalHours();
                int totalNormalMinutes = summary.getTotalNormalMinutes();
                int totalOvertimeHours = summary.getTotalOvertimeHours();
                int totalOvertimeMinutes = summary.getTotalOvertimeMinutes();
                totalNormalHours += totalNormalMinutes / 60;
                totalNormalMinutes %= 60;
                totalOvertimeHours += totalOvertimeMinutes / 60;
                totalOvertimeMinutes %= 60;
                double weeklyNormalValue = (totalNormalHours + totalNormalMinutes / 60.0) * hourlyRate;
                double weeklyOvertimeValue = (totalOvertimeHours + totalOvertimeMinutes / 60.0) * hourlyRate * 1.25;
                double weeklyWorkValue = weeklyNormalValue + weeklyOvertimeValue;
                double accumulated = monthlyGrossMap.getOrDefault(empNumber, 0.0);
                monthlyGrossMap.put(empNumber, accumulated + weeklyWorkValue);
            }
            printWeeklySummary(weekKey, summaries);
        }
    }

    // Group attendance records by week using a week key (e.g., "MM/dd/yyyy - MM/dd/yyyy")
    private static Map<String, Map<LocalDate, List<AttendanceRecord>>> groupAttendanceByWeek() {
        Map<String, Map<LocalDate, List<AttendanceRecord>>> weeklyRecords = new HashMap<>();
        for (AttendanceRecord record : attendanceRecords) {
            LocalDate date = record.getDate();
            LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);
            LocalDate endOfWeek = date.with(DayOfWeek.FRIDAY);
            String weekKey = startOfWeek.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                    + " - " + endOfWeek.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            weeklyRecords
                .computeIfAbsent(weekKey, k -> new TreeMap<>())
                .computeIfAbsent(date, k -> new ArrayList<>())
                .add(record);
        }
        return weeklyRecords;
    }

    // Determine release week and fraction based on RELEASE_MODE.
    private static void printWeeklySummary(String weekKey, Map<String, WeeklySummary> summaries) {
        boolean releaseThisWeek = false;
        double fraction = 0.0;
        if (RELEASE_MODE == 1) {
            // Option 1: Release 100% only on the fourth week.
            if (isFourthWeekOfMonth(weekKey)) {
                releaseThisWeek = true;
                fraction = 1.0;
            }
        } else if (RELEASE_MODE == 2) {
            // Option 2: Release 50% on the second week and 50% on the fourth week.
            if (isSecondWeekOfMonth(weekKey) || isFourthWeekOfMonth(weekKey)) {
                releaseThisWeek = true;
                fraction = 0.5;
            }
        }
        
        for (WeeklySummary summary : summaries.values()) {
            EmployeeDetails emp = summary.getEmployee();
            // Compute "Gross Salary for the Month" as the accumulated computed work value over 4 weeks.
            double computedGross = monthlyGrossMap.getOrDefault(emp.getEmployeeNumber(), 0.0);
            // These deductions are computed based on the computedGross.
            double sssDeduction = calculateSSSDeduction(computedGross, sssBrackets);
            double philhealthDeduction = calculatePHICDeduction(computedGross, philhealthBrackets);
            double pagIBIGDeduction = calculatePagIBIGDeduction(computedGross, pagIBIGBrackets);
            double totalDeductionsBeforeTax = sssDeduction + philhealthDeduction + pagIBIGDeduction;
            // The taxable income is based on the CSV monthly basic salary minus the computed deductions.
            double monthlyBasicSalary = emp.getMonthlySalary();
            double taxableIncome = monthlyBasicSalary - totalDeductionsBeforeTax;
            double withholdingTax = calculateWithholdingTax(taxableIncome, taxBrackets);
            double fullDeductions = totalDeductionsBeforeTax + withholdingTax;
            
            double weeklySSSDeduction = releaseThisWeek ? sssDeduction * fraction : 0;
            double weeklyPhilhealthDeduction = releaseThisWeek ? philhealthDeduction * fraction : 0;
            double weeklyPagIBIGDeduction = releaseThisWeek ? pagIBIGDeduction * fraction : 0;
            double weeklyWithholdingTax = releaseThisWeek ? withholdingTax * fraction : 0;
            double weeklyTotalDeductions = weeklySSSDeduction + weeklyPhilhealthDeduction +
                    weeklyPagIBIGDeduction + weeklyWithholdingTax;

            System.out.println("Employee Number: " + emp.getEmployeeNumber());
            System.out.println("Employee Name: " + emp.getLastName() + ", " + emp.getFirstName());
            System.out.println("Birthday: " + emp.getBirthday());
            System.out.println("Week Covered: " + weekKey);
            System.out.println("\nDaily Breakdown:");
            for (Map.Entry<LocalDate, DailySummary> entry : summary.getDailySummaries().entrySet()) {
                LocalDate date = entry.getKey();
                DailySummary dailySummary = entry.getValue();
                System.out.printf("  Date: %s%n", date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                System.out.printf("    Log In: %s | Log Out: %s%n", dailySummary.getLogInTime(), dailySummary.getLogOutTime());
                System.out.printf("    Normal Hours Worked: %d hours and %d minutes%n",
                        dailySummary.getNormalHours(), dailySummary.getNormalMinutes());
                System.out.printf("    Overtime Hours Worked: %d hours and %d minutes%n",
                        dailySummary.getOvertimeHours(), dailySummary.getOvertimeMinutes());
                System.out.printf("    Late Minutes: %d minutes%n", dailySummary.getLateMinutes());
                System.out.printf("    Undertime Minutes: %d minutes%n", dailySummary.getUndertimeMinutes());
            }

            int totalNormalHours = summary.getTotalNormalHours();
            int totalNormalMinutes = summary.getTotalNormalMinutes();
            int totalOvertimeHours = summary.getTotalOvertimeHours();
            int totalOvertimeMinutes = summary.getTotalOvertimeMinutes();

            totalNormalHours += totalNormalMinutes / 60;
            totalNormalMinutes %= 60;
            totalOvertimeHours += totalOvertimeMinutes / 60;
            totalOvertimeMinutes %= 60;

            int totalHoursWorked = totalNormalHours + totalOvertimeHours;
            int totalMinutesWorked = totalNormalMinutes + totalOvertimeMinutes;
            totalHoursWorked += totalMinutesWorked / 60;
            totalMinutesWorked %= 60;

            System.out.printf("\nTotal Normal Hours Worked: %d hours and %d minutes%n", totalNormalHours, totalNormalMinutes);
            System.out.printf("Total Overtime Hours Worked: %d hours and %d minutes%n", totalOvertimeHours, totalOvertimeMinutes);
            System.out.printf("Total Hours Worked: %d hours and %d minutes%n", totalHoursWorked, totalMinutesWorked);
            System.out.printf("Total Late Minutes: %d minutes%n", summary.getTotalLateMinutes());
            System.out.printf("Total Undertime Minutes: %d minutes%n", summary.getTotalUndertimeMinutes());

            double hourlyRate = parseDouble(emp.getHourlyRate());
            System.out.println("\nHourly Rate: " + currencyFormatter.format(hourlyRate));

            int totalNormalHrs = summary.getTotalNormalHours();
            int totalNormalMins = summary.getTotalNormalMinutes();
            int totalOvertimeHrs = summary.getTotalOvertimeHours();
            int totalOvertimeMins = summary.getTotalOvertimeMinutes();
            totalNormalHrs += totalNormalMins / 60;
            totalNormalMins %= 60;
            totalOvertimeHrs += totalOvertimeMins / 60;
            totalOvertimeMins %= 60;
            double weeklyNormalValue = (totalNormalHrs + totalNormalMins / 60.0) * hourlyRate;
            double weeklyOvertimeValue = (totalOvertimeHrs + totalOvertimeMins / 60.0) * hourlyRate * 1.25;
            double weeklyWorkValue = weeklyNormalValue + weeklyOvertimeValue;

            String formattedNormalValue = currencyFormatter.format(weeklyNormalValue);
            String formattedOvertimeValue = currencyFormatter.format(weeklyOvertimeValue);
            String formattedWeeklyValue = currencyFormatter.format(weeklyWorkValue);

            System.out.println("\nWeekly Work Value:");
            System.out.println("  Normal Hours in Value: " + formattedNormalValue);
            System.out.println("  Overtime in Value: " + formattedOvertimeValue);
            System.out.println("  Total Work Value: " + formattedWeeklyValue);

            System.out.println("\nWeekly Deductions:");
            System.out.println("  SSS: " + currencyFormatter.format(weeklySSSDeduction));
            System.out.println("  PhilHealth: " + currencyFormatter.format(weeklyPhilhealthDeduction));
            System.out.println("  Pag-IBIG: " + currencyFormatter.format(weeklyPagIBIGDeduction));
            System.out.println("  Withholding Tax: " + currencyFormatter.format(weeklyWithholdingTax));
            System.out.println("  Total Deductions: " + currencyFormatter.format(weeklyTotalDeductions));

            double riceSubsidy = parseDouble(emp.getRiceSubsidy());
            double phoneAllowance = parseDouble(emp.getPhoneAllowance());
            double clothingAllowance = parseDouble(emp.getClothingAllowance());

            double weeklyRiceSubsidy = releaseThisWeek ? riceSubsidy * fraction : 0;
            double weeklyPhoneAllowance = releaseThisWeek ? phoneAllowance * fraction : 0;
            double weeklyClothingAllowance = releaseThisWeek ? clothingAllowance * fraction : 0;
            double weeklyTotalBenefits = weeklyRiceSubsidy + weeklyPhoneAllowance + weeklyClothingAllowance;

            System.out.println("\nBenefits:");
            System.out.println("  Rice Subsidy: " + currencyFormatter.format(weeklyRiceSubsidy));
            System.out.println("  Phone Allowance: " + currencyFormatter.format(weeklyPhoneAllowance));
            System.out.println("  Clothing Allowance: " + currencyFormatter.format(weeklyClothingAllowance));
            System.out.println("  Total Benefits: " + currencyFormatter.format(weeklyTotalBenefits));

            System.out.println("\nGross Salary for the Month (Computed over 4 weeks): " 
                    + currencyFormatter.format(monthlyGrossMap.get(emp.getEmployeeNumber())));
            System.out.println("Gross Salary for this Week (Work Value + Benefits): " 
                    + currencyFormatter.format(weeklyWorkValue + weeklyTotalBenefits));

            System.out.println("\nNet Pay (Weekly Work Value + Benefits - Weekly Deductions): " 
                    + currencyFormatter.format((weeklyWorkValue + weeklyTotalBenefits) - weeklyTotalDeductions));
            System.out.println("\n" + "=".repeat(50) + "\n");
        }
    }

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
        if (amount == null || amount.trim().isEmpty() || amount.equalsIgnoreCase("N/A"))
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

    private static double calculateSSSDeduction(double salary, List<SSSBracket> brackets) {
        return findBracketAndApply(salary, brackets, SSSBracket::getContribution, "SSS");
    }

    private static double calculatePHICDeduction(double salary, List<PHICBracket> brackets) {
        if (salary <= 10000)
            return 150.0;
        else if (salary >= 60000)
            return 900.0;
        return findBracketAndApply(salary, brackets, bracket -> salary * 0.015, "PHIC");
    }

    private static double calculatePagIBIGDeduction(double salary, List<PagIBIGBracket> brackets) {
        return findBracketAndApply(salary, brackets, bracket -> 100.0, "Pag-IBIG");
    }

    private static double calculateWithholdingTax(double taxableIncome, List<TaxBracket> brackets) {
        for (TaxBracket bracket : brackets) {
            if (taxableIncome >= bracket.salaryFrom && taxableIncome <= bracket.salaryTo) {
                double excess = taxableIncome - bracket.inExcessOf;
                double taxOnExcess = excess * bracket.taxRate;
                return taxOnExcess + bracket.taxValue;
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
            return salary >= sss.compensationFrom && salary <= sss.compensationTo;
        } else if (bracket instanceof PHICBracket) {
            PHICBracket phic = (PHICBracket) bracket;
            return salary >= phic.compensationFrom && salary <= phic.compensationTo;
        } else if (bracket instanceof PagIBIGBracket) {
            PagIBIGBracket pib = (PagIBIGBracket) bracket;
            return salary >= pib.salaryFrom && salary <= pib.salaryTo;
        }
        return false;
    }

    // Week helper methods
    private static boolean isFirstWeekOfMonth(String weekKey) {
        LocalDate startDate = LocalDate.parse(weekKey.split(" - ")[0], DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        return startDate.getDayOfMonth() <= 7;
    }

    private static boolean isSecondWeekOfMonth(String weekKey) {
        LocalDate startDate = LocalDate.parse(weekKey.split(" - ")[0], DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        int day = startDate.getDayOfMonth();
        return day >= 8 && day <= 14;
    }

    private static boolean isThirdWeekOfMonth(String weekKey) {
        LocalDate startDate = LocalDate.parse(weekKey.split(" - ")[0], DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        int day = startDate.getDayOfMonth();
        return day >= 15 && day <= 21;
    }
    
    private static boolean isFourthWeekOfMonth(String weekKey) {
        LocalDate startDate = LocalDate.parse(weekKey.split(" - ")[0], DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        int day = startDate.getDayOfMonth();
        return day >= 22 && day <= 28;
    }

    // Functional interfaces
    @FunctionalInterface
    private interface BracketCalculator<T> {
        double calculate(T bracket);
    }

    @FunctionalInterface
    private interface BracketLoader<T> {
        T load(String[] parts);
    }
}