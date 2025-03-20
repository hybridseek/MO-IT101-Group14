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
 * Represents an employee's details, including personal information,
 * salary, and allowances.
 */
public class EmployeeDetails {
    private final String employeeNumber;
    private final String firstName;
    private final String lastName;
    private final String birthday;
    private final double hourlyRate;
    private final double monthlySalary;
    private final double riceSubsidy;
    private final double phoneAllowance;
    private final double clothingAllowance;

    /**
     * Constructs an EmployeeDetails object using an array of employee data.
     *
     * @param data An array containing employee details from a CSV file.
     */
    public EmployeeDetails(String[] data) {
        this.employeeNumber = data[0].trim();
        this.firstName = data[1].trim();
        this.lastName = data[2].trim();
        this.birthday = data[3].trim();
        this.monthlySalary = parseDouble(data[13]);
        this.riceSubsidy = parseDouble(data[14]);
        this.phoneAllowance = parseDouble(data[15]);
        this.clothingAllowance = parseDouble(data[16]);
        this.hourlyRate = parseDouble(data[18]);
    }

    /** @return The unique employee number. */
    public String getEmployeeNumber() {
        return employeeNumber;
    }

    /** @return The first name of the employee. */
    public String getFirstName() {
        return firstName;
    }

    /** @return The last name of the employee. */
    public String getLastName() {
        return lastName;
    }

    /** @return The employee's date of birth. */
    public String getBirthday() {
        return birthday;
    }

    /** @return The hourly rate of the employee. */
    public double getHourlyRate() {
        return hourlyRate;
    }

    /** @return The employee's monthly salary. */
    public double getMonthlySalary() {
        return monthlySalary;
    }

    /** @return The employee's rice subsidy allowance. */
    public double getRiceSubsidy() {
        return riceSubsidy;
    }

    /** @return The employee's phone allowance. */
    public double getPhoneAllowance() {
        return phoneAllowance;
    }

    /** @return The employee's clothing allowance. */
    public double getClothingAllowance() {
        return clothingAllowance;
    }

    /**
     * Parses a string to a double safely, handling empty or invalid values.
     *
     * @param value The string representation of a numeric value.
     * @return The parsed double value or 0.0 if invalid.
     */
    private double parseDouble(String value) {
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
     * Generates a formatted string representation of the employee details.
     *
     * @return A string describing the employee's key details.
     */
    @Override
    public String toString() {
        return String.format("Employee: %s, %s (ID: %s) | Salary: %.2f | Hourly Rate: %.2f",
                lastName, firstName, employeeNumber, monthlySalary, hourlyRate);
    }
}