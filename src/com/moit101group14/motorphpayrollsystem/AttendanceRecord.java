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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AttendanceRecord {
    private final String employeeNumber;
    private final LocalDate date;
    private final LocalTime logIn;
    private final LocalTime logOut;
    
    public AttendanceRecord(String[] data) {
        this.employeeNumber = data[0].trim();
        this.date = LocalDate.parse(data[3].trim(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        this.logIn = LocalTime.parse(data[4].trim(), DateTimeFormatter.ofPattern("H:mm"));
        this.logOut = LocalTime.parse(data[5].trim(), DateTimeFormatter.ofPattern("H:mm"));
    }
    
    public String getEmployeeNumber() {
        return employeeNumber;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public LocalTime getLogIn() {
        return logIn;
    }
    
    public LocalTime getLogOut() {
        return logOut;
    }
}