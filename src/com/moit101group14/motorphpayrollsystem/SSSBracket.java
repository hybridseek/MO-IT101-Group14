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

public class SSSBracket {
    public double compensationFrom;
    public double compensationTo;
    public double contribution;
    
    public SSSBracket(String[] parts) {
        this.compensationFrom = parseAmount(parts[1]);
        this.compensationTo = parseAmount(parts[2]);
        this.contribution = parseAmount(parts[3]);
    }
    
    private double parseAmount(String amount) {
        if (amount == null || amount.isEmpty() || amount.equalsIgnoreCase("N/A")) {
            return 0.0;
        }
        try {
            return Double.parseDouble(amount.replace(",", "").replace("\"", ""));
        } catch (NumberFormatException e) {
            System.err.println("Error parsing amount in SSSBracket: " + amount);
            return 0.0;
        }
    }
    
    public double getContribution() {
        return contribution;
    }
}
