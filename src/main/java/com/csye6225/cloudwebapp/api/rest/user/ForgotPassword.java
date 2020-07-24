/**
 * 
 */
package com.csye6225.cloudwebapp.api.rest.user;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.csye6225.cloudwebapp.api.model.User;
import com.csye6225.cloudwebapp.aws.SQSClient;
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
@RequestMapping("/v1/forgotPassword/userEmailAddress/{userEmailAddress}")
public class ForgotPassword {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPassword.class);
    
    @Autowired
    private SQSClient sqsClient;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @ApiOperation(value = "Send password reset link", notes = "Send password reset link")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Sent password reset link successfully !"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to send password reset link
    public ResponseEntity forgotPassword(@PathVariable(value = "userEmailAddress") String userEmailAddress)
            throws IOException, InterruptedException {

        User user = userRepository.findByUserEmailAddress(userEmailAddress);
        if (user != null && UtilityService.checkStringNotNull(userEmailAddress)) {
            String generateToken = UUID.randomUUID().toString();
            sqsClient.sendEmail(userEmailAddress, generateToken);
            return new ResponseEntity(user, HttpStatus.OK);
        } else {
            return new ResponseEntity("User not found !", HttpStatus.NOT_FOUND);
        }

    }
}
