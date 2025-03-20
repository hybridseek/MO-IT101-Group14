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
 * Represents an SSS (Social Security System) contribution bracket.
 * Each bracket defines the salary range and corresponding contribution amount.
 */
public class SSSBracket {
    private final double compensationFrom;
    private final double compensationTo;
    private final double contribution;

    /**
     * Constructs an SSSBracket object defining salary range and contribution details.
     *
     * @param compensationFrom The minimum salary for this bracket.
     * @param compensationTo   The maximum salary for this bracket.
     * @param contribution     The fixed SSS contribution for this salary range.
     */
    public SSSBracket(double compensationFrom, double compensationTo, double contribution) {
        this.compensationFrom = compensationFrom;
        this.compensationTo = compensationTo;
        this.contribution = contribution;
    }

    /** @return The lower bound of the salary range for this SSS bracket. */
    public double getCompensationFrom() {
        return compensationFrom;
    }

    /** @return The upper bound of the salary range for this SSS bracket. */
    public double getCompensationTo() {
        return compensationTo;
    }

    /** @return The fixed contribution amount for this bracket. */
    public double getContribution() {
        return contribution;
    }

    /**
     * Generates a formatted string representation of the SSS bracket.
     *
     * @return A string describing the SSS bracket details.
     */
    @Override
    public String toString() {
        return String.format("SSS Bracket: Salary %.2f - %.2f | Contribution: %.2f",
                compensationFrom, compensationTo, contribution);
    }
}