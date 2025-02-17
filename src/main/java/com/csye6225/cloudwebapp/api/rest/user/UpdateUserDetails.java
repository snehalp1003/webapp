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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Snehal Patel
 *
 */

@RestController
@RequestMapping("/v1/updatePersonalDetails/userEmailAddress/{userEmailAddress}/userFirstName/{userFirstName}/userLastName/{userLastName}")
public class UpdateUserDetails {
    
    private static final Logger logger = LoggerFactory.getLogger(UpdateUserDetails.class);

    @Autowired
    private UserRepository userRepository;

    @PutMapping
    @ApiOperation(value = "Updates user details", notes = "Updates user details")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Updated user details successfully."),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to update user details
    public ResponseEntity updateUserDetails(@PathVariable(value = "userEmailAddress") String userEmailAddress,
            @PathVariable(value = "userFirstName") String userFirstName,
            @PathVariable(value = "userLastName") String userLastName) throws IOException {

        User user = userRepository.findByUserEmailAddress(userEmailAddress);
        if (user == null || !UtilityService.checkStringNotNull(user.getUuid())) {
            logger.info("**********Session expired for user**********");
            return new ResponseEntity("Session expired for user.", HttpStatus.REQUEST_TIMEOUT);
        }
        
        if (user != null && UtilityService.checkStringNotNull(userFirstName)
                && UtilityService.checkStringNotNull(userLastName)) {
            user.setUserFirstName(userFirstName);
            user.setUserLastName(userLastName);
            userRepository.save(user);
            return new ResponseEntity(user, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }

}
