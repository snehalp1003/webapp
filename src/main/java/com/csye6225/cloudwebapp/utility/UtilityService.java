/**
 * 
 */
package com.csye6225.cloudwebapp.utility;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.annotation.Configuration;

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
    
    public static String hashPassword(String plainTextPassword){
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }
    
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (BCrypt.checkpw(plainPassword, hashedPassword))
            return true;
        else
            return false;
    }
}
