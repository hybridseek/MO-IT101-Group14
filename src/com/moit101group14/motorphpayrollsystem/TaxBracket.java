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

public class TaxBracket {
    private double salaryFrom;
    private double salaryTo;
    private double inExcessOf;
    private double taxValue;
    private double taxRate;
    
    public TaxBracket(double salaryFrom, double salaryTo, double inExcessOf, double taxValue, double taxRate) {
        this.salaryFrom = salaryFrom;
        this.salaryTo = salaryTo;
        this.inExcessOf = inExcessOf;
        this.taxValue = taxValue;
        this.taxRate = taxRate;
    }
    
    public double getSalaryFrom() {
        return salaryFrom;
    }
    
    public double getSalaryTo() {
        return salaryTo;
    }
    
    public double getInExcessOf() {
        return inExcessOf;
    }
    
    public double getTaxValue() {
        return taxValue;
    }
    
    public double getTaxRate() {
        return taxRate;
    }
}