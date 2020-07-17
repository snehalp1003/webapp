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
import com.timgroup.statsd.StatsDClient;

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
    private StatsDClient statsd;
    
    @Autowired
    private AmazonS3 amazonS3;
    
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
    public ResponseEntity deleteImageFromS3(@PathVariable(value = "userLoggedIn") String userLoggedIn,
            @PathVariable(value = "fileName") String fileName) throws IOException {
        
        statsd.incrementCounter("deleteImageFromS3Api");
        long start = System.currentTimeMillis();

        try {
            if (imageRepository.findByImageName(fileName) != null) {
                Image image = imageRepository.findByImageName(fileName);
                if (image.getBookSoldBy().equals(userLoggedIn)) {
                    long dbBookImageDeleteFromS3Start = System.currentTimeMillis();
                    amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
                    long dbBookImageDeleteFromS3End = System.currentTimeMillis();
                    long dbBookImageDeleteFromS3TimeElapsed = dbBookImageDeleteFromS3End - dbBookImageDeleteFromS3Start;
                    statsd.recordExecutionTime("deleteBookImageFromS3Time", dbBookImageDeleteFromS3TimeElapsed);

                    imageRepository.delete(image);
                    
                    long end = System.currentTimeMillis();
                    long timeElapsed = end - start;
                    statsd.recordExecutionTime("deleteImageFromS3ApiTime", timeElapsed);
                    logger.info("**********Deleted image from S3 bucket successfully**********");
                    return new ResponseEntity(HttpStatus.OK);
                } else {
                    long end = System.currentTimeMillis();
                    long timeElapsed = end - start;
                    statsd.recordExecutionTime("deleteImageFromS3ApiTime", timeElapsed);
                    logger.info("**********User does not have permission to delete image !**********");
                    return new ResponseEntity(HttpStatus.FORBIDDEN);
                }
            } else {
                long end = System.currentTimeMillis();
                long timeElapsed = end - start;
                statsd.recordExecutionTime("deleteImageFromS3ApiTime", timeElapsed);
                logger.info("**********Image not found**********");
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        } catch (AmazonServiceException ex) {
            logger.error("error [" + ex.getMessage() + "] occurred while removing [" + fileName + "] ");
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("deleteImageFromS3ApiTime", timeElapsed);
            logger.info("**********Error deleting image from S3 bucket**********");
            return new ResponseEntity("Error inserting image to S3", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
