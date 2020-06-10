package com.csye6225.cloudwebapp;

import static org.junit.Assert.*;

import org.junit.Test;

import com.csye6225.cloudwebapp.utility.UtilityService;

/**
 * @author Snehal Patel
 *
 */
public class UtilityTest {

    @Test
    public void EmailUtilityTest() {
        String email = "snehalp1003@gmail.com";
        Boolean emailValid = UtilityService.checkIfValidEmail(email);
        assertEquals(true, emailValid);
    }
}
