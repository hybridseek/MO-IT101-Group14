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
    public double salaryFrom;
    public double salaryTo;
    public double inExcessOf;
    public double taxValue;
    public double taxRate;
    
    public TaxBracket(double salaryFrom, double salaryTo, double inExcessOf, double taxValue, double taxRate) {
        this.salaryFrom = salaryFrom;
        this.salaryTo = salaryTo;
        this.inExcessOf = inExcessOf;
        this.taxValue = taxValue;
        this.taxRate = taxRate;
    }
}
