/**
 * 
 */
package com.csye6225.cloudwebapp.api.rest.user;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.csye6225.cloudwebapp.api.model.User;
import com.csye6225.cloudwebapp.datasource.repository.UserRepository;
import com.csye6225.cloudwebapp.utility.UtilityService;
import com.timgroup.statsd.StatsDClient;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Snehal Patel
 *
 */
@RestController
@RequestMapping("/v1/logoutUser/userEmailAddress/{userEmailAddress}")
public class LogoutUser {

    
    private static final Logger logger = LoggerFactory.getLogger(LogoutUser.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StatsDClient statsd;

    @PutMapping
    @ApiOperation(value = "Logout user", notes = "Logout user")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Logged user successfully."),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to update user details
    public ResponseEntity logoutUser(@PathVariable(value = "userEmailAddress") String userEmailAddress) throws IOException {

        User user = userRepository.findByUserEmailAddress(userEmailAddress);
        if (user != null && UtilityService.checkStringNotNull(user.getUuid())) {
            user.setUuid(null);
            userRepository.save(user);
            logger.info("**********User logged out successfully!**********");
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }



}
