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
 * Represents a PhilHealth (PHIC) contribution bracket.
 * Each bracket defines the salary range and corresponding contribution amount.
 */
public class PHICBracket {
    private final double compensationFrom;
    private final double compensationTo;
    private final double contributionRate;

    /**
     * Constructs a PHICBracket object defining salary range and contribution details.
     *
     * @param compensationFrom The minimum salary for this bracket.
     * @param compensationTo   The maximum salary for this bracket.
     * @param contributionRate The PhilHealth contribution rate for this salary range.
     */
    public PHICBracket(double compensationFrom, double compensationTo, double contributionRate) {
        this.compensationFrom = compensationFrom;
        this.compensationTo = compensationTo;
        this.contributionRate = contributionRate;
    }

    /** @return The lower bound of the salary range for this PhilHealth bracket. */
    public double getCompensationFrom() {
        return compensationFrom;
    }

    /** @return The upper bound of the salary range for this PhilHealth bracket. */
    public double getCompensationTo() {
        return compensationTo;
    }

    /** @return The contribution rate for this bracket. */
    public double getContributionRate() {
        return contributionRate;
    }

    /**
     * Generates a formatted string representation of the PhilHealth bracket.
     *
     * @return A string describing the PhilHealth bracket details.
     */
    @Override
    public String toString() {
        return String.format("PhilHealth Bracket: Salary %.2f - %.2f | Contribution Rate: %.2f%%",
                compensationFrom, compensationTo, contributionRate * 100);
    }
}
