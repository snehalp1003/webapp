/**
 * 
 */
package com.csye6225.cloudwebapp.api.rest.s3.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.csye6225.cloudwebapp.api.model.Image;
import com.csye6225.cloudwebapp.datasource.repository.ImageRepository;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Snehal Patel
 *
 */
@RestController
@RequestMapping("/v1/deleteImageFromS3/userLoggedIn/{userLoggedIn}/fileName/{fileName}")
public class DeleteImageFromS3 {

    private static final Logger logger = LoggerFactory.getLogger(DeleteImageFromS3.class);

    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private AmazonS3 amazonS3;
//    private AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
//            .withCredentials(new InstanceProfileCredentialsProvider(true))
//            .build();
    
    @Value("${BUCKET_NAME}")
    private String bucketName;
    
//    private String bucketName = "webapp.snehal.patel";

    @DeleteMapping
    @ApiOperation(value = "Deletes specified book image", notes = "Deletes specified book image")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Specified book image deleted successfully"),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    public ResponseEntity deleteBook(@PathVariable(value = "userLoggedIn") String userLoggedIn,
            @PathVariable(value = "fileName") String fileName) throws IOException {

        try {
            if (imageRepository.findByImageName(fileName) != null) {
                Image image = imageRepository.findByImageName(fileName);
                if (image.getBookSoldBy().equals(userLoggedIn)) {
                    amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));

                    imageRepository.delete(image);
                    return new ResponseEntity(HttpStatus.OK);
                }
            }
        } catch (AmazonServiceException ex) {
            logger.error("error [" + ex.getMessage() + "] occurred while removing [" + fileName + "] ");
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

}
