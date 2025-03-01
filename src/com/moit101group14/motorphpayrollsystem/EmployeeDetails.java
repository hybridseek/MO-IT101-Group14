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

public class EmployeeDetails {
    private final String employeeNumber;
    private final String lastName;
    private final String firstName;
    private final String birthday;
    private final double monthlySalary;
    
    // Dynamic benefit values read from CSV
    private final String riceSubsidy;
    private final String phoneAllowance;
    private final String clothingAllowance;
    private final String hourlyRate;
    
    public EmployeeDetails(String[] data) {
        this.employeeNumber = data[0].trim();
        this.lastName = data[1].trim();
        this.firstName = data[2].trim();
        this.birthday = data[3].trim();
        this.monthlySalary = parseAmount(data[13].trim());
        // Mapping dynamic fields according to indices:
        // Rice Subsidy from data[14], Phone Allowance from data[15],
        // Clothing Allowance from data[16], Hourly Rate from data[18]
        this.riceSubsidy = data.length > 14 ? data[14].trim() : "0";
        this.phoneAllowance = data.length > 15 ? data[15].trim() : "0";
        this.clothingAllowance = data.length > 16 ? data[16].trim() : "0";
        this.hourlyRate = data.length > 18 ? data[18].trim() : "0";
    }
    
    private double parseAmount(String amount) {
        if (amount == null || amount.isEmpty() || amount.equalsIgnoreCase("N/A")) {
            return 0.0;
        }
        try {
            return Double.parseDouble(amount.replace(",", "").replace("\"", ""));
        } catch (NumberFormatException e) {
            System.err.println("Error parsing amount: " + amount);
            return 0.0;
        }
    }
    
    public String getEmployeeNumber() {
        return employeeNumber;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getBirthday() {
        return birthday;
    }
    
    public double getMonthlySalary() {
        return monthlySalary;
    }
    
    public String getRiceSubsidy() {
        return riceSubsidy;
    }
    
    public String getPhoneAllowance() {
        return phoneAllowance;
    }
    
    public String getClothingAllowance() {
        return clothingAllowance;
    }
    
    public String getHourlyRate() {
        return hourlyRate;
    }
}
