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
 * Utility class for managing system-wide payroll settings and financial brackets.
 * Provides helper methods for parsing financial data and retrieving contribution brackets.
 */
public class PayrollSystemHelper {

    private static List<SSSBracket> sssBrackets;
    private static List<PHICBracket> philHealthBrackets;
    private static List<PagIBIGBracket> pagIbigBrackets;
    private static List<TaxBracket> taxBrackets;

    /**
     * Safely parses a string value into a double.
     * Handles cases where the string may be empty or incorrectly formatted.
     *
     * @param value The string to be parsed.
     * @return The parsed double value, or 0.0 if parsing fails.
     */
    public static double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.replaceAll("[\",]", "").trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Retrieves the list of SSS brackets. Ensures null safety.
     *
     * @return List of SSSBracket objects, or an empty list if uninitialized.
     */
    public static List<SSSBracket> getSssBrackets() {
        return sssBrackets != null ? sssBrackets : List.of();
    }

    /**
     * Sets the SSS contribution brackets.
     *
     * @param brackets The list of SSS brackets to set.
     */
    public static void setSssBrackets(List<SSSBracket> brackets) {
        sssBrackets = brackets;
    }

    /**
     * Retrieves the list of PhilHealth brackets. Ensures null safety.
     *
     * @return List of PHICBracket objects, or an empty list if uninitialized.
     */
    public static List<PHICBracket> getPhilHealthBrackets() {
        return philHealthBrackets != null ? philHealthBrackets : List.of();
    }

    /**
     * Sets the PhilHealth contribution brackets.
     *
     * @param brackets The list of PhilHealth brackets to set.
     */
    public static void setPhilHealthBrackets(List<PHICBracket> brackets) {
        philHealthBrackets = brackets;
    }

    /**
     * Retrieves the list of Pag-IBIG brackets. Ensures null safety.
     *
     * @return List of PagIBIGBracket objects, or an empty list if uninitialized.
     */
    public static List<PagIBIGBracket> getPagIbigBrackets() {
        return pagIbigBrackets != null ? pagIbigBrackets : List.of();
    }

    /**
     * Sets the Pag-IBIG contribution brackets.
     *
     * @param brackets The list of Pag-IBIG brackets to set.
     */
    public static void setPagIbigBrackets(List<PagIBIGBracket> brackets) {
        pagIbigBrackets = brackets;
    }

    /**
     * Retrieves the list of Tax brackets. Ensures null safety.
     *
     * @return List of TaxBracket objects, or an empty list if uninitialized.
     */
    public static List<TaxBracket> getTaxBrackets() {
        return taxBrackets != null ? taxBrackets : List.of();
    }

    /**
     * Sets the tax brackets for computing withholding tax.
     *
     * @param brackets The list of TaxBracket objects to set.
     */
    public static void setTaxBrackets(List<TaxBracket> brackets) {
        taxBrackets = brackets;
    }
}
