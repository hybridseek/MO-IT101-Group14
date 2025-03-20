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

import java.util.List;

/**
 * DeductionsCalculator calculates various payroll deductions (SSS, PhilHealth,
 * Pag-IBIG, and Withholding Tax) based on salary brackets.
 */
public class DeductionsCalculator {
    
    /**
     * Calculates the SSS (Social Security System) deduction based on salary.
     *
     * @param salary   The employee's salary.
     * @param brackets List of SSS brackets.
     * @return The calculated SSS deduction.
     */
    public static double calculateSssDeduction(double salary, List<SSSBracket> brackets) {
        return findBracketAndApply(salary, brackets, SSSBracket::getContribution);
    }
    
    /**
     * Calculates the PhilHealth (PHIC) deduction based on salary.
     * Uses fixed amounts for very low and very high salaries.
     *
     * @param salary   The employee's salary.
     * @param brackets List of PHIC brackets.
     * @return The calculated PhilHealth deduction.
     */
    public static double calculatePhicDeduction(double salary, List<PHICBracket> brackets) {
        if (salary <= 10000) return 150.0;
        else if (salary >= 60000) return 900.0;
        return findBracketAndApply(salary, brackets, bracket -> salary * 0.015);
    }
    
    /**
     * Calculates the Pag-IBIG (HDMF) deduction based on salary.
     *
     * @param salary   The employee's salary.
     * @param brackets List of Pag-IBIG brackets.
     * @return The fixed Pag-IBIG deduction amount.
     */
    public static double calculatePagIbigDeduction(double salary, List<PagIBIGBracket> brackets) {
        return findBracketAndApply(salary, brackets, bracket -> 100.0);
    }
    
    /**
     * Calculates the Withholding Tax based on taxable income and tax brackets.
     *
     * @param taxableIncome The taxable income.
     * @param brackets      List of tax brackets.
     * @return The calculated withholding tax amount.
     */
    public static double calculateWithholdingTax(double taxableIncome, List<TaxBracket> brackets) {
        for (TaxBracket bracket : brackets) {
            if (taxableIncome >= bracket.getCompensationFrom() && taxableIncome <= bracket.getCompensationTo()) {
                double excess = taxableIncome - bracket.getInExcessOf();
                double taxOnExcess = excess * bracket.getTaxRate();
                return taxOnExcess + bracket.getFixedTaxAmount();
            }
        }
        return 0.0;
    }

    /**
     * Generic method to find the applicable bracket and apply a calculation.
     *
     * @param salary     The employee's salary.
     * @param brackets   List of brackets.
     * @param calculator Functional interface to compute the deduction.
     * @param <T>        The type of bracket.
     * @return The calculated deduction, or 0.0 if no bracket matches.
     */
    private static <T> double findBracketAndApply(double salary, List<T> brackets, BracketCalculator<T> calculator) {
        for (T bracket : brackets) {
            if (isSalaryInRange(salary, bracket)) {
                return calculator.calculate(bracket);
            }
        }
        return 0.0;
    }
    
    /**
     * Determines if a given salary falls within the bracket's range.
     * Uses a switch expression with pattern matching (Java 16+).
     *
     * @param salary  The employee's salary.
     * @param bracket The bracket to check.
     * @return true if salary is within the bracket; false otherwise.
     */
    private static boolean isSalaryInRange(double salary, Object bracket) {
        return switch (bracket) {
            case SSSBracket sss -> salary >= sss.getCompensationFrom() && salary <= sss.getCompensationTo();
            case PHICBracket phic -> salary >= phic.getCompensationFrom() && salary <= phic.getCompensationTo();
            case PagIBIGBracket pib -> salary >= pib.getCompensationFrom() && salary <= pib.getCompensationTo();
            default -> false;
        };
    }
    
    /**
     * Functional interface for applying bracket calculations.
     *
     * @param <T> The type of bracket.
     */
    @FunctionalInterface
    private interface BracketCalculator<T> {
        double calculate(T bracket);
    }
}