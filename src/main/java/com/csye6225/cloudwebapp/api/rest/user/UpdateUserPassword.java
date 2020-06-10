/**
 * 
 */
package com.csye6225.cloudwebapp.api.rest.user;

import java.io.IOException;

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
@RequestMapping("/v1/updatePassword/userEmailAddress/{userEmailAddress}/oldPassword/{oldPassword}/newPassword/{newPassword}")
public class UpdateUserPassword {

    @Autowired
    private UserRepository userRepository;

    @PutMapping
    @ApiOperation(value = "Updates user's password", notes = "Updates user's password")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Updated user's password successfully."),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to update user details
    public ResponseEntity updateUserPassword(@PathVariable(value = "userEmailAddress") String userEmailAddress,
            @PathVariable(value = "oldPassword") String oldPassword,
            @PathVariable(value = "newPassword") String newPassword) throws IOException {

        User user = userRepository.findByUserEmailAddress(userEmailAddress);
        if (user != null) {
            if (UtilityService.checkPassword(oldPassword, user.getUserPassword())) {
                if (UtilityService.checkIfValidPassword(newPassword)) {
                    user.setUserPassword(UtilityService.hashPassword(newPassword));
                    userRepository.save(user);
                    return new ResponseEntity(user, HttpStatus.OK);
                } else {
                    return new ResponseEntity("Password in incorrect format !", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity("Incorrect old password !", HttpStatus.UNAUTHORIZED);
            }

        } else {
            return new ResponseEntity("User not found !", HttpStatus.NOT_FOUND);
        }

    }
}
