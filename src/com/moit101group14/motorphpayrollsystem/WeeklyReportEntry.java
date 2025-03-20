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

/**
 * Represents a payroll report entry for a single employee within a given week.
 * Stores calculated earnings, benefits, deductions, and net pay.
 */
public class WeeklyReportEntry {
    private final EmployeeDetails employee;
    private final String weekKey;
    private final double weeklyWorkValue;
    private final double totalBenefits;
    private final double sssDeduction;
    private final double philhealthDeduction;
    private final double pagibigDeduction;
    private final double withholdingTax;
    private final double totalDeductions;
    private final double grossSalary;
    private final double netPay;
    private final double riceSubsidy;
    private final double phoneAllowance;
    private final double clothingAllowance;
    private final double normalHours;
    private final double overtimeHours;

    /**
     * Constructs a WeeklyReportEntry with detailed payroll data.
     *
     * @param employee              Employee details associated with this entry.
     * @param weekKey               The week identifier in "MM/dd/yyyy - MM/dd/yyyy" format.
     * @param weeklyWorkValue       Total computed earnings from hours worked.
     * @param totalBenefits         Total employee benefits received in the week.
     * @param sssDeduction          Social Security System (SSS) deduction.
     * @param philhealthDeduction   PhilHealth contribution deduction.
     * @param pagibigDeduction      Pag-IBIG contribution deduction.
     * @param withholdingTax        Withholding tax deducted from gross salary.
     * @param totalDeductions       Total deductions applied in this payroll period.
     * @param grossSalary           Gross salary before deductions.
     * @param netPay                Net salary after deductions.
     * @param riceSubsidy           Rice subsidy benefit.
     * @param phoneAllowance        Phone allowance benefit.
     * @param clothingAllowance     Clothing allowance benefit.
     * @param normalHours           Total normal hours worked in the week.
     * @param overtimeHours         Total overtime hours worked in the week.
     */
    public WeeklyReportEntry(EmployeeDetails employee, String weekKey, double weeklyWorkValue, double totalBenefits,
                             double sssDeduction, double philhealthDeduction, double pagibigDeduction,
                             double withholdingTax, double totalDeductions, double grossSalary, double netPay,
                             double riceSubsidy, double phoneAllowance, double clothingAllowance,
                             double normalHours, double overtimeHours) {
        this.employee = employee;
        this.weekKey = weekKey;
        this.weeklyWorkValue = weeklyWorkValue;
        this.totalBenefits = totalBenefits;
        this.sssDeduction = sssDeduction;
        this.philhealthDeduction = philhealthDeduction;
        this.pagibigDeduction = pagibigDeduction;
        this.withholdingTax = withholdingTax;
        this.totalDeductions = totalDeductions;
        this.grossSalary = grossSalary;
        this.netPay = netPay;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.normalHours = normalHours;
        this.overtimeHours = overtimeHours;
    }

    /** @return Employee details */
    public EmployeeDetails getEmployee() { return employee; }

    /** @return The week key in "MM/dd/yyyy - MM/dd/yyyy" format */
    public String getWeekKey() { return weekKey; }

    /** @return Weekly work value before deductions */
    public double getWeeklyWorkValue() { return weeklyWorkValue; }

    /**
     * @return Total benefits received for the week.
     */
    public double getTotalBenefits() {
        return totalBenefits;
    }
    
    /**
     * @return SSS contribution deducted for the week.
     */
    public double getSssDeduction() {
        return sssDeduction;
    }
    
    /**
     * @return PhilHealth contribution deducted for the week.
     */
    public double getPhilhealthDeduction() {
        return philhealthDeduction;
    }
    
    /**
     * @return Pag-IBIG contribution deducted for the week.
     */
    public double getPagibigDeduction() {
        return pagibigDeduction;
    }
    
    /**
     * @return Withholding tax deducted from salary for the week.
     */
    public double getWithholdingTax() {
        return withholdingTax;
    }
    
    /**
     * @return Total deductions applied to the employee's salary.
     */
    public double getTotalDeductions() {
        return totalDeductions;
    }
    
    /**
     * @return Gross salary before any deductions.
     */
    public double getGrossSalary() {
        return grossSalary;
    }
    
    /**
     * @return Net pay after deductions.
     */
    public double getNetPay() {
        return netPay;
    }
    
    /**
     * @return Rice subsidy benefit received.
     */
    public double getRiceSubsidy() {
        return riceSubsidy;
    }
    
    /**
     * @return Phone allowance benefit received.
     */
    public double getPhoneAllowance() {
        return phoneAllowance;
    }
    
    /**
     * @return Clothing allowance benefit received.
     */
    public double getClothingAllowance() {
        return clothingAllowance;
    }
    
    /**
     * @return Total normal hours worked during the week.
     */
    public double getNormalHours() {
        return normalHours;
    }
    
    /**
     * @return Total overtime hours worked during the week.
     */
    public double getOvertimeHours() {
        return overtimeHours;
    }
}