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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * DataLoader loads employee details, attendance records, and financial brackets
 * from CSV files. It parses the CSV data and stores it in structured collections.
 */
public class DataLoader {
    private static final Logger logger = Logger.getLogger(DataLoader.class.getName());

    // CSV file paths (update as needed)
    private final String EMPLOYEE_FILE = "/Users/mylife/NetBeansProjects/com.moit101group14.motorphpayrollsystem/assets/MotorPHEmployeeDetails.csv";
    private final String ATTENDANCE_FILE = "/Users/mylife/NetBeansProjects/com.moit101group14.motorphpayrollsystem/assets/MotorPHAttendanceRecord.csv";
    private final String TAX_BRACKET_FILE = "/Users/mylife/NetBeansProjects/com.moit101group14.motorphpayrollsystem/assets/MotorPHWithholdingTax.csv";
    private final String SSS_CONTRIBUTION_FILE = "/Users/mylife/NetBeansProjects/com.moit101group14.motorphpayrollsystem/assets/MotorPHSSSContribution.csv";
    private final String PHIC_CONTRIBUTION_FILE = "/Users/mylife/NetBeansProjects/com.moit101group14.motorphpayrollsystem/assets/MotorPHPhilhealthContribution.csv";
    private final String HDMF_CONTRIBUTION_FILE = "/Users/mylife/NetBeansProjects/com.moit101group14.motorphpayrollsystem/assets/MotorPHPagIBIGContribution.csv";

    // Data collections
    private final Map<String, EmployeeDetails> employees = new TreeMap<>();
    private final List<AttendanceRecord> attendanceRecords = new ArrayList<>();
    private final List<TaxBracket> taxBrackets = new ArrayList<>();
    private final List<SSSBracket> sssBrackets = new ArrayList<>();
    private final List<PHICBracket> philHealthBrackets = new ArrayList<>();
    private final List<PagIBIGBracket> pagIbigBrackets = new ArrayList<>();
    
    /**
     * Loads all required data from the CSV files.
     *
     * @throws IOException if any file cannot be read.
     */
    public void loadAllData() throws IOException {
        loadEmployees();
        loadAttendanceRecords();
        loadTaxBrackets();
        loadSssBrackets();
        loadPhicBrackets();
        loadPagIbigBrackets();
    }
    
    /**
     * Loads employee details from the EMPLOYEE_FILE.
     *
     * @throws IOException if the file cannot be read.
     */
    private void loadEmployees() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                EmployeeDetails emp = new EmployeeDetails(data);
                employees.put(emp.getEmployeeNumber(), emp);
            }
        }
    }
    
    /**
     * Loads attendance records from the ATTENDANCE_FILE.
     * Skips records with missing login or logout and logs a warning.
     *
     * @throws IOException if the file cannot be read.
     */
    private void loadAttendanceRecords() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_FILE))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                AttendanceRecord record = new AttendanceRecord(data);
                if (record.isValid()) {
                    attendanceRecords.add(record);
                } else {
                    logger.log(Level.WARNING, "Skipping incomplete record for employee {0} on {1}",
                            new Object[]{record.getEmployeeNumber(), record.getDate()});
                }
            }
        }
    }
    
    /**
     * Loads tax bracket data from the TAX_BRACKET_FILE.
     *
     * @throws IOException if the file cannot be read.
     */
    private void loadTaxBrackets() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(TAX_BRACKET_FILE))) {
            br.readLine(); // Skip header
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
    
    /**
     * Loads SSS contribution brackets using a lambda expression.
     *
     * @throws IOException if the file cannot be read.
     */
    private void loadSssBrackets() throws IOException {
        loadBrackets(SSS_CONTRIBUTION_FILE, sssBrackets, parts -> 
            new SSSBracket(
                parseAmount(parts[1]),
                parseAmount(parts[2]),
                parseAmount(parts[3])
            )
        );
    }
    
    /**
     * Loads PhilHealth contribution brackets using a lambda expression.
     *
     * @throws IOException if the file cannot be read.
     */
    private void loadPhicBrackets() throws IOException {
        loadBrackets(PHIC_CONTRIBUTION_FILE, philHealthBrackets, parts ->
            new PHICBracket(
                parseAmount(parts[1]),
                parseAmount(parts[2]),
                0.0  // Adjust this if a valid contribution rate is available
            )
        );
    }
    
    /**
     * Loads Pag-IBIG contribution brackets using a lambda expression.
     *
     * @throws IOException if the file cannot be read.
     */
    private void loadPagIbigBrackets() throws IOException {
        loadBrackets(HDMF_CONTRIBUTION_FILE, pagIbigBrackets, parts ->
            new PagIBIGBracket(
                parseAmount(parts[1]),
                parseAmount(parts[2]),
                parseAmount(parts[3].replace("%", "")) / 100.0
            )
        );
    }

    /**
     * Generic method to load bracket data from a CSV file.
     *
     * @param filePath The path to the CSV file.
     * @param brackets The list in which to store parsed bracket objects.
     * @param loader   Functional interface to create a bracket from CSV parts.
     * @param <T>      The type of bracket.
     * @throws IOException if the file cannot be read.
     */
    private <T> void loadBrackets(String filePath, List<T> brackets, BracketLoader<T> loader) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                brackets.add(loader.load(parts));
            }
        }
    }
    
    /**
     * Parses a monetary amount from a string.
     *
     * @param amount The string representation of the amount.
     * @return The parsed double value, or 0.0 if invalid.
     */
    private double parseAmount(String amount) {
        if (amount == null || amount.isEmpty() || amount.equalsIgnoreCase("N/A"))
            return 0.0;
        try {
            return Double.parseDouble(amount.replace(",", "").replace("\"", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Parses a percentage from a string and converts it to a decimal.
     *
     * @param percentage The percentage string.
     * @return The decimal value of the percentage.
     */
    private double parsePercentage(String percentage) {
        try {
            return Double.parseDouble(percentage.replace("%", "").trim()) / 100.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // Getters for loaded data

    /** @return Map of employee details keyed by employee number. */
    public Map<String, EmployeeDetails> getEmployees() {
        return employees;
    }

    /** @return List of attendance records. */
    public List<AttendanceRecord> getAttendanceRecords() {
        return attendanceRecords;
    }

    /** @return List of tax brackets. */
    public List<TaxBracket> getTaxBrackets() {
        return taxBrackets;
    }

    /** @return List of SSS brackets. */
    public List<SSSBracket> getSssBrackets() {
        return sssBrackets;
    }

    /** @return List of PhilHealth brackets. */
    public List<PHICBracket> getPhilHealthBrackets() {
        return philHealthBrackets;
    }

    /** @return List of Pag-IBIG brackets. */
    public List<PagIBIGBracket> getPagIbigBrackets() {
        return pagIbigBrackets;
    }

    /**
     * Functional interface for constructing bracket objects from CSV data.
     *
     * @param <T> The type of bracket.
     */
    @FunctionalInterface
    private interface BracketLoader<T> {
        T load(String[] parts);
    }
}