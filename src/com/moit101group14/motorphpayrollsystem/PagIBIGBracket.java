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

public class PagIBIGBracket {
    private double salaryFrom;
    private double salaryTo;
    private double contributionRate;
    
    public PagIBIGBracket(String[] parts) {
        this.salaryFrom = parseAmount(parts[1]);
        this.salaryTo = parseAmount(parts[2]);
        this.contributionRate = parseAmount(parts[3].replace("%", "")) / 100.0;
    }
    
    private double parseAmount(String amount) {
        if (amount == null || amount.isEmpty() || amount.equalsIgnoreCase("N/A")) {
            return 0.0;
        }
        try {
            return Double.parseDouble(amount.replace(",", "").replace("\"", ""));
        } catch (NumberFormatException e) {
            System.err.println("Error parsing amount in PagIBIGBracket: " + amount);
            return 0.0;
        }
    }
    
    public double getSalaryFrom() {
        return salaryFrom;
    }
    
    public double getSalaryTo() {
        return salaryTo;
    }
    
    public double getContributionRate() {
        return contributionRate;
    }
}
