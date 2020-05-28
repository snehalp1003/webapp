package com.csye6225.cloudwebapp.api.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.csye6225.cloudwebapp.api.model.User;
import com.csye6225.cloudwebapp.datasource.repository.UserRepository;
import com.csye6225.cloudwebapp.utility.UtilityService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v1/add/userEmailAddress/{userEmailAddress}/userPassword/{userPassword}/userFirstName/{userFirstName}/userLastName/{userLastName}")
public class CreateNewUser {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @ApiOperation(value = "Inserts/Updates user details", notes = "Inserts/Updates user details")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "User created successfully."),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to insert user details
    public ResponseEntity insertUserDetails(
            @PathVariable(value = "userEmailAddress") String userEmailAddress,
            @PathVariable(value = "userPassword") String userPassword,
            @PathVariable(value = "userFirstName") String userFirstName,
            @PathVariable(value = "userLastName") String userLastName) throws IOException {

        if (UtilityService.checkStringNotNull(userEmailAddress) && UtilityService.checkStringNotNull(userFirstName)
                && UtilityService.checkStringNotNull(userLastName) && UtilityService.checkStringNotNull(userPassword)) {
            User user = new User();
            user.setUserEmailAddress(userEmailAddress);
            user.setUserPassword(userPassword);
            user.setUserFirstName(userFirstName);
            user.setUserLastName(userLastName);
            userRepository.save(user);
            return new ResponseEntity(user, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }

}
