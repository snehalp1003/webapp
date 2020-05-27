/**
 * 
 */
package com.csye6225.cloudwebapp.utility;

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
}
