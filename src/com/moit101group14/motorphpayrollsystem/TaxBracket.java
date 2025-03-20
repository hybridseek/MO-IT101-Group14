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

/**
 * Represents a tax bracket for withholding tax calculations.
 * Each bracket specifies a salary range and the corresponding tax rate and fixed tax value.
 */
public class TaxBracket {
    private final double compensationFrom;
    private final double compensationTo;
    private final double inExcessOf;
    private final double fixedTaxAmount;
    private final double taxRate;

    /**
     * Constructs a TaxBracket object with salary ranges and tax details.
     *
     * @param compensationFrom     The lower bound of the salary range.
     * @param compensationTo       The upper bound of the salary range.
     * @param inExcessOf     The portion of the salary that is taxable.
     * @param fixedTaxAmount The fixed tax amount applied to the salary range.
     * @param taxRate        The tax rate applied to the excess amount beyond `inExcessOf`.
     */
    public TaxBracket(double compensationFrom, double compensationTo, double inExcessOf, 
                      double fixedTaxAmount, double taxRate) {
        this.compensationFrom = compensationFrom;
        this.compensationTo = compensationTo;
        this.inExcessOf = inExcessOf;
        this.fixedTaxAmount = fixedTaxAmount;
        this.taxRate = taxRate;
    }

    /** @return The lower bound of the salary range. */
    public double getCompensationFrom() {
        return compensationFrom;
    }

    /** @return The upper bound of the salary range. */
    public double getCompensationTo() {
        return compensationTo;
    }

    /** @return The portion of the salary that is taxable. */
    public double getInExcessOf() {
        return inExcessOf;
    }

    /** @return The fixed tax amount applied within the bracket. */
    public double getFixedTaxAmount() {
        return fixedTaxAmount;
    }

    /** @return The tax rate applicable to the excess salary beyond the bracket's limit. */
    public double getTaxRate() {
        return taxRate;
    }

    /**
     * Generates a formatted string representation of the tax bracket.
     *
     * @return A string describing the tax bracket details.
     */
    @Override
    public String toString() {
        return String.format("Tax Bracket: Salary %.2f - %.2f | Fixed Tax: %.2f | Rate: %.2f%% | Excess: %.2f",
                compensationFrom, compensationTo, fixedTaxAmount, taxRate * 100, inExcessOf);
    }
}