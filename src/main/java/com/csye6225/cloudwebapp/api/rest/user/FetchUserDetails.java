package com.csye6225.cloudwebapp.api.rest.user;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/v1/fetchUserDetails/userEmailAddress/{userEmailAddress}/userPassword/{userPassword}")
public class FetchUserDetails {
    
    private static final Logger logger = LoggerFactory.getLogger(FetchUserDetails.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StatsDClient statsd;

    @GetMapping
    @ApiOperation(value = "Fetches user details", notes = "Fetches user details")
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "Fetched user details successfully."),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to fetch user details
    public ResponseEntity fetchUserDetails(
            @PathVariable(value = "userEmailAddress") String userEmailAddress,
            @PathVariable(value = "userPassword") String userPassword) throws IOException {
        
        statsd.incrementCounter("fetchUserDetailsApi");
        long start = System.currentTimeMillis();

        User user = userRepository.findByUserEmailAddress(userEmailAddress);
        long dbEnd = System.currentTimeMillis();
        if (user != null && UtilityService.checkPassword(userPassword, user.getUserPassword())) {
            if(user.getUuid() == null || user.getUuid().isEmpty()) {
                String generateUUID = UUID.randomUUID().toString(); 
                user.setUuid(generateUUID);
                userRepository.save(user);
            }

            long end = System.currentTimeMillis();
            long dbTimeElapsed = dbEnd - start;
            long timeElapsed = end - start;
            statsd.recordExecutionTime("fetchUserFromDBTime", dbTimeElapsed);
            statsd.recordExecutionTime("fetchUserDetailsApiTime", timeElapsed);
            logger.info("**********User details fetched successfully !**********");
            HttpHeaders headers = new HttpHeaders();
            headers.add("session-id", user.getUuid());
            return ResponseEntity.ok().headers(headers).body(user);
        } else {
            long end = System.currentTimeMillis();
            long dbTimeElapsed = dbEnd - start;
            long timeElapsed = end - start;
            statsd.recordExecutionTime("fetchUserFromDBTime", dbTimeElapsed);
            statsd.recordExecutionTime("fetchUserDetailsApiTime", timeElapsed);
            logger.info("**********User not found !**********");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }
}
