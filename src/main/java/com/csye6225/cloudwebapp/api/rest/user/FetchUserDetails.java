package com.csye6225.cloudwebapp.api.rest.user;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/v1/fetchUserDetails/userEmailAddress/{userEmailAddress}/userPassword/{userPassword}")
public class FetchUserDetails {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @ApiOperation(value = "Fetches user details", notes = "Fetches user details")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "Fetched user details successfully."),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to insert user details
    public ResponseEntity fetchUserDetails(
            @PathVariable(value = "userEmailAddress") String userEmailAddress,
            @PathVariable(value = "userPassword") String userPassword) throws IOException {

        User user = userRepository.findByUserEmailAddress(userEmailAddress);
        if (user != null && UtilityService.checkPassword(userPassword, user.getUserPassword())) {
            return new ResponseEntity(user, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }
}
