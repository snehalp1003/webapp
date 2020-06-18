/**
 * 
 */
package com.csye6225.cloudwebapp.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Snehal Patel
 *
 */

@Configuration
public class UtilityService {

    public static boolean checkStringNotNull(String in) {
        boolean output = false;
        if (in != null && !in.isEmpty() && !in.equals("") && in.trim().length() != 0) {
            output = true;
        }
        return output;
    }
    
    public static boolean checkIfValidPassword(String plainTextPassword) {
        boolean output = false;
        final String passwordRegex = "((?=.*[a-z])(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%!]).{8,40})";
        
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(plainTextPassword);
        output = matcher.matches();
        return output;
    }
    
    public static String hashPassword(String plainTextPassword){
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }
    
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (BCrypt.checkpw(plainPassword, hashedPassword))
            return true;
        else
            return false;
    }
    
    public static boolean checkIfValidEmail(String email) {
        boolean output = false;
        final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        output = matcher.matches();
        return output;
    }
    
    public static boolean checkIfValidBookPrice(Double bookPrice) {
        boolean output = false;
        if(bookPrice > 0.00 && bookPrice < 10000) {
            output = true;
        }
        return output;
    }
    
    public static boolean checkIfValidBookQuantity(Long bookQuantity) {
        boolean output = false;
        if(bookQuantity > -1 && bookQuantity < 1000) {
            output = true;
        }
        return output;
    }
}
