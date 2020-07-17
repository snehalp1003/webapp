/**
 * 
 */
package com.csye6225.cloudwebapp.api.rest.s3.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
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
@RequestMapping("/v1/fetchImagesFromS3/bookISBN/{bookISBN}/userLoggedIn/{userLoggedIn}")
public class FetchImagesFromS3 {

    private static final Logger logger = LoggerFactory.getLogger(FetchImagesFromS3.class);

    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private StatsDClient statsd;
    
    @Autowired
    private AmazonS3 amazonS3;
//    private AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
//            .withCredentials(new InstanceProfileCredentialsProvider(true))
//            .build();
    
//    @Value("${BUCKET_NAME}")
//    private String bucketName;
    
    private String bucketName = "webapp.snehal.patel";

    @GetMapping
    @ApiOperation(value = "Returns list of available images", notes = "Returns list of available images")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Returns list of available images."),
            @ApiResponse(code = 204, message = "No images available for this book."),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    public ResponseEntity fetchImagesFromS3(@PathVariable(value = "bookISBN") String bookISBN,
            @PathVariable(value = "userLoggedIn") String userLoggedIn) throws IOException {
        
        statsd.incrementCounter("fetchImagesFromS3Api");
        long start = System.currentTimeMillis();

        ArrayList<Image> availableImages = imageRepository.findAll();
        ArrayList<Image> validAvailableImages = new ArrayList<Image>();
        
        if (availableImages != null && availableImages.size() > 0) {
            for (Image image : availableImages) {
                if (image.getBookSoldBy().equals(userLoggedIn) && image.getBookISBN().equals(bookISBN)) {
                    validAvailableImages.add(image);
                }
            }
        }
        
        HashMap<String, String> returnAvailableImages = new HashMap<String,String>();
        
        if(validAvailableImages != null && validAvailableImages.size() > 0) {
            for (Image fetchImg : validAvailableImages) {
              long dbFetchImageFromS3Start = System.currentTimeMillis();
              S3Object s3Object = this.amazonS3.getObject(new GetObjectRequest(bucketName, fetchImg.getImageName()));
              long dbFetchImageFromS3End = System.currentTimeMillis();
              long dbFetchImageFromS3TimeElapsed = dbFetchImageFromS3End - dbFetchImageFromS3Start;
              statsd.recordExecutionTime("fetchImageFromS3Time", dbFetchImageFromS3TimeElapsed);
              String extension = s3Object.getObjectMetadata().getContentType();
              InputStream is = s3Object.getObjectContent();
              File f = new File((new Date()).toString() + s3Object.getKey());
              Files.copy(is, Paths.get(f.getPath()));
              FileInputStream fis = new FileInputStream(f);
              byte[] bytes = new byte[(int) s3Object.getObjectMetadata().getContentLength()];
              fis.read(bytes);
              String encodeBase64 = Base64.getEncoder().encodeToString(bytes);
              String image = "data:"+extension+";base64,"+encodeBase64;
              returnAvailableImages.put(fetchImg.getImageName(), image);
            }
        }
        
        if(returnAvailableImages != null && returnAvailableImages.size() > 0) {
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("fetchImagesFromS3ApiTime", timeElapsed);
            logger.info("**********Image fetched from S3 bucket successfully**********");
            return new ResponseEntity(returnAvailableImages, HttpStatus.OK);
        } else {
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("fetchImagesFromS3ApiTime", timeElapsed);
            logger.info("**********No images present in S3 bucket**********");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }
}
